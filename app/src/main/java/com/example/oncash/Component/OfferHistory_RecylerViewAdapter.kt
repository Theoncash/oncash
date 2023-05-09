package com.example.oncash.Component

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.example.oncash.R
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oncash.DataType.SerializedDataType.OfferHistory.OfferHistoryRecord


class OfferHistory_RecylerViewAdapter : RecyclerView.Adapter<OfferHistory_RecylerViewAdapter.viewholder>() {
    var offerList : ArrayList<OfferHistoryRecord> = ArrayList()

    var context : Context?=null
    class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val status :TextView
        val price :TextView
        lateinit var  offerName :TextView
        init {
            status = itemView.findViewById(com.example.oncash.R.id.offer_status)
            price = itemView.findViewById(R.id.offerHistory_price)
            offerName = itemView.findViewById<TextView>(R.id.offerHistory_name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        context = parent.context
        val v = LayoutInflater.from(parent.context).inflate(R.layout.offer_history_recylerview,parent,false)
        return viewholder(v)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.offerName.text = offerList.get(position).fields.OfferName
        holder.status.text = offerList.get(position).fields.Status
        holder.price.text = offerList.get(position).fields.Payout // holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context , R.anim.offeranimation)

        var lastPosition = -1

        val animation = AnimationUtils.loadAnimation(
            context, if (position > lastPosition) {com.example.oncash.R.anim.offeranimation }else {com.example.oncash.R.anim.offeranimationdown}
        )
        holder.itemView.startAnimation(animation)
        lastPosition = position
    }

    override fun getItemCount(): Int {

        return offerList.size

    }

    override fun onViewDetachedFromWindow(holder: viewholder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list :ArrayList<OfferHistoryRecord>){
//        val result =  DiffUtil.calculateDiff( Diffutil(offerList , list))
        this.offerList.clear()
        this.offerList.addAll(list)
        notifyDataSetChanged()

//        result.dispatchUpdatesTo(this)
    }


}