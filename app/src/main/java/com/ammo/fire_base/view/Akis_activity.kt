package com.ammo.fire_base.view

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ammo.fire_base.model.Post
import com.ammo.fire_base.R
import com.ammo.fire_base.adapter.Akis_recycler_view_adapater
import com.ammo.fire_base.databinding.ActivityAkisBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import java.time.LocalDateTime


class akis_activity : AppCompatActivity() {


    private lateinit var binding: ActivityAkisBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var recyler_view_adapter : Akis_recycler_view_adapater
    var post_listesi = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityAkisBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        verileri_al()

            var layout_manager=LinearLayoutManager(this)
            binding.recyclerView.layoutManager=layout_manager
            recyler_view_adapter = Akis_recycler_view_adapater(post_listesi)
            binding.recyclerView.adapter=recyler_view_adapter

        binding.bottomNav.setOnItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.fofo_paylas -> {
                    val intent = Intent(this, fotograf_paylasma_aktivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.cıkıs_yap -> {

                    val builder = AlertDialog.Builder(this) //aler dialog oluşturduk
                    builder.setTitle("Çıkış")
                    builder.setMessage("Çıkış yapmak istiyor musunuz")

                    builder.setPositiveButton("evet") { dialog, which ->
                        auth.signOut() // firebaseden kontorl yaptıgmız ıcın cıkıs yapıcak
                        val intent  = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    builder.show()
                    true
                }
                else -> false
            }
        }

    }

    fun verileri_al(){


        database.collection("Post").orderBy("tarih",Query.Direction.DESCENDING).addSnapshotListener{snapshot,exception->
            if (exception != null){
                Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
            else{
                if(snapshot!=null){


                    if(!snapshot.isEmpty){

                        val documents = snapshot.documents
                        var eski_size = post_listesi.size
                        post_listesi.clear()

                        for(document in documents){

                            val kullanici_email = document.get("kullaniciemail") as String
                            val kullanici_yorumu = document.get("kullaniciyorum") as String
                            val gorsel_url = document.get("gorselurl") as String

                            val indirilen_post =
                                Post(
                                    kullanici_email,
                                    kullanici_yorumu,
                                    gorsel_url
                                )
                            post_listesi.add(indirilen_post)


                        }
                        val son_kullanici = documents.get(0).get("kullaniciemail") as String

                        recyler_view_adapter.notifyDataSetChanged()

                    }
                }
            }
        }


    }

    //fonk

}