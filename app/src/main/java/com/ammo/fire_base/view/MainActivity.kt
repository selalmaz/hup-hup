package com.ammo.fire_base.view

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ammo.fire_base.R
import com.ammo.fire_base.databinding.ActivityMainBinding
import com.ammo.fire_base.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var progress_dialog: ProgressDialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // firebase ı kullanmak ve kutuphanelerini kullanmak icin gerekli kutuphaneleri gradle kısmınına ekledık

        auth = Firebase.auth  // objeyı tanımladık




        val guncell_kullanici=auth.currentUser

        if(guncell_kullanici != null){
            val intent = Intent(this, akis_activity::class.java)
            startActivity(intent)
            finish()
        }



        binding.forgotPassword.setOnClickListener{

            var password_forgot= LayoutInflater.from(this).inflate(R.layout.password_forgot,null)
            val alertDialog=AlertDialog.Builder(this)

            alertDialog.setView(password_forgot)
            alertDialog.show()
            println("forgot password tıklandı")

            val send_mail = password_forgot.findViewById<Button>(R.id.send_password_button)

            send_mail.setOnClickListener {
                println("send mail tıklandı")

                val email = password_forgot.findViewById<EditText>(R.id.forgot_email_text).text.toString()


                if(TextUtils.isEmpty(email))
                {
                    password_forgot.findViewById<TextView>(R.id.forgot_email_text).error="Eksik Bilgi"
                }

                else{
                    auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                        //asenkron
                        if(task.isSuccessful) {
                            //mail gonderildi
                            val intent = Intent(this,
                                MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }


            }
        }

        binding.createAcount.setOnClickListener{
       //ALERT diaglog olusturalaım

            var activity_uyeol= LayoutInflater.from(this).inflate(R.layout.activity_uyeol,null)
            val alertDialog=AlertDialog.Builder(this)

            alertDialog.setView(activity_uyeol)
            alertDialog.show()


            //yeni üye kaydı
            val kayit_ol_button = activity_uyeol.findViewById<TextView>(R.id.kayit_ol_button)

            kayit_ol_button.setOnClickListener{

                val email = activity_uyeol.findViewById<TextView>(R.id.email_text).text.toString()
                val sifre = activity_uyeol.findViewById<TextView>(R.id.password_text).text.toString()

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(sifre))
                {
                    activity_uyeol.findViewById<TextView>(R.id.email_text).error="Eksik Bilgi"
                    activity_uyeol.findViewById<TextView>(R.id.password_text).error="Eksik Bilgi"
                }

                else{

                    auth.createUserWithEmailAndPassword(email,sifre).addOnCompleteListener { task ->
                        //asenkron
                        if(task.isSuccessful) {
                            //diğer aktiviteye gidelim
                            val intent = Intent(this,
                                akis_activity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.girisButton.setOnClickListener{


            val email = binding.emailText.text.toString()
            val sifre = binding.passwordText.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(sifre)){
                binding.emailText.error="Eksik Bilgi"
                binding.passwordText.error="Eksik Bilgi"
            }

            else{
                auth.signInWithEmailAndPassword(email,sifre).addOnCompleteListener { task->
                    if(task.isSuccessful)
                    {
                        //giris basarılı
                        val guncel_kullanici=auth.currentUser?.email.toString()
                        Toast.makeText(this, "Hoşgeldin ${guncel_kullanici}", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, akis_activity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener {exception->
                    Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }
        }
    }



}