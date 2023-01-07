package com.example.oncash.Component

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.oncash.R
import com.example.oncash.View.Info
import com.example.oncash.DataType.Offer


class Offer_RecylerViewAdapter : RecyclerView.Adapter<Offer_RecylerViewAdapter.viewholder>() {
    var offerList : ArrayList<Offer> = ArrayList<Offer>()

    var context : Context?=null
    class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val image : ImageView
        val name : TextView
        val description :TextView
        val price :TextView
        lateinit var  offerId :String
        init {
            image = itemView.findViewById(R.id.offer_image)
            name = itemView.findViewById(R.id.offer_name)
            description = itemView.findViewById(R.id.offer_desciption)
            price = itemView.findViewById(R.id.offer_price)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        context = parent.context


        val v = LayoutInflater.from(parent.context).inflate(R.layout.offer_recyerview,parent,false)


        return viewholder(v)



    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.offerId = offerList.get(position).OfferId!!
        holder.name.text= offerList.get(position).Name
        holder.description.text = offerList.get(position).Description
        holder.price.text = offerList.get(position).Price
        Glide.with(holder.itemView.context).load(offerList.get(position).Image).into(holder.image)

        holder.itemView.setOnClickListener {
            val offer_information : Offer = offerList.get(holder.offerId.toInt()-1)
            val intent = Intent(
                holder.itemView.context,
                Info::class.java
            ).putExtra("OfferId",offer_information.OfferId )
                .putExtra("OfferName",offer_information.Name)
                .putExtra("OfferImage",offer_information.Image)
                .putExtra("OfferPrice",offer_information.Price)
                .putExtra("OfferDescription",offer_information.Description)
                .putExtra("OfferLink",offer_information.Link)


            holder.itemView.context.startActivity(
                intent

            )
        }

    }

    override fun getItemCount(): Int {

        return offerList.size

    }

    fun updateList(list :ArrayList<Offer>){
        val result =  DiffUtil.calculateDiff( Diffutil(offerList , list))
        this.offerList.clear()
        this.offerList.addAll(list)
        result.dispatchUpdatesTo(this)
    }
}