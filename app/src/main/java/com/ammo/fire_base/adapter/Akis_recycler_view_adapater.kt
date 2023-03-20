package com.ammo.fire_base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ammo.fire_base.R
import com.ammo.fire_base.databinding.RecyclerRowBinding
import com.ammo.fire_base.model.Post
import com.squareup.picasso.Picasso

class Akis_recycler_view_adapater(val postList :ArrayList<Post>):
    RecyclerView.Adapter<Akis_recycler_view_adapater.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row,parent,false)
        return ViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val index = postList[position]
        holder.x.text=index.kullanici_yorum
        holder.y.text=index.kullanici_email

        Picasso.get().load(index.gorsel_url).into(holder.z)

    }

    override fun getItemCount(): Int {

        return postList.size
    }

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {

        val x : TextView = itemView.findViewById(R.id.recycler_view_yorum)
        val y : TextView = itemView.findViewById(R.id.recycler_view_email)
        val z: ImageView = itemView.findViewById(R.id.recycler_view_gorsel)
    }


}