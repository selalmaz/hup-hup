package com.ammo.fire_base.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ammo.fire_base.databinding.ActivityFotografPaylasmaAktivityBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class fotograf_paylasma_aktivity : AppCompatActivity() {

    private lateinit var binding: ActivityFotografPaylasmaAktivityBinding
    var secilen_gorsel : Uri? = null
    var secilen_bitmap : Bitmap? = null

    private lateinit var storage : FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFotografPaylasmaAktivityBinding.inflate(layoutInflater)
        setContentView(binding.root)





        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        binding.PaylasButton.setOnClickListener{
            //depo işlemleri

            val reference =storage.reference
            val gorsel_id=UUID.randomUUID()


            val gorselReference = reference.child("images").child("${gorsel_id}.jpg")
            if(secilen_gorsel != null)
            {
                gorselReference.putFile(secilen_gorsel!!).addOnSuccessListener { taskSnapshot->
                       val yuklenen_gorsel_reference = FirebaseStorage.getInstance().reference.child("images").child("${gorsel_id}.jpg")
                        // yuklenen resmib url sini aldık
                        yuklenen_gorsel_reference.downloadUrl.addOnSuccessListener { uri->
                            val dowload_url=uri.toString()  // yuklenen resmin url si
                            val guncel_kullanici_email = auth.currentUser!!.email.toString()  // kullanıcının maili
                            val kullanıcı_yorumu = binding.yorumEditText.text.toString()  // kullanıcının yorumu
                            var date = Timestamp.now()// tarihi aldık



                            println("gorsel secildi")

                            //veritabanı işlemleri && veritabanına kaydetmek

                            val post_hasp_map = hashMapOf<String,Any>()
                            post_hasp_map.put("gorselurl",dowload_url)
                            post_hasp_map.put("kullaniciemail",guncel_kullanici_email)
                            post_hasp_map.put("kullaniciyorum",kullanıcı_yorumu)
                            post_hasp_map.put("tarih",date)


                            // firebase databasesine  hasp map i yukledık
                            database.collection("Post").add(post_hasp_map).addOnCompleteListener { task->
                            
                                if (task.isSuccessful){
                                    finish()
                                    println("gorsel data baseye yuklendi")

                                }
                            }.addOnFailureListener { exception->
                                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                                println("gorsel data baseye yuklenemedi")
                            }


                        }.addOnFailureListener { exception->
                            println("gorsel secilemedi")
                            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                        }
                }
            }


        }

        binding.fotoSecImageView.setOnClickListener{

            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //izni almamışız
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            } else {
                //izin zaten varsa
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //izin verilince yapılacaklar
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)

            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {

            secilen_gorsel = data.data

            if (secilen_gorsel != null) {

                if(Build.VERSION.SDK_INT >= 28) {

                    val source = ImageDecoder.createSource(this.contentResolver,secilen_gorsel!!)
                    secilen_bitmap = ImageDecoder.decodeBitmap(source)
                    binding.fotoSecImageView.setImageBitmap(secilen_bitmap)

                } else {
                    secilen_bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,secilen_gorsel)
                    binding.fotoSecImageView.setImageBitmap(secilen_bitmap)


                }


            }


        }


        super.onActivityResult(requestCode, resultCode, data)
    }

}