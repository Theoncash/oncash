package `in`.oncash.oncash.Component

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import `in`.oncash.oncash.R
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `in`.oncash.oncash.DataType.Offer
import `in`.oncash.oncash.DataType.OfferList
import `in`.oncash.oncash.DataType.SerializedDataType.Blacklist.Blacklist
import `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.Fields
import `in`.oncash.oncash.DataType.SerializedDataType.OfferHistory.OfferHistoryRecord


class OfferHistory_RecylerViewAdapter : RecyclerView.Adapter<OfferHistory_RecylerViewAdapter.viewholder>() {
    var offerList : ArrayList<Fields> = ArrayList()
    var offers : ArrayList<Offer> = ArrayList()
    var context : Context?=null
    class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val status :TextView
        val price :TextView
        lateinit var  offerName :TextView
        var offerImage:ImageView
        init {
            status = itemView.findViewById(`in`.oncash.oncash.R.id.offer_status)
            price = itemView.findViewById(R.id.offerHistory_price)
            offerName = itemView.findViewById<TextView>(R.id.offerHistory_name)
            offerImage = itemView.findViewById<ImageView>(R.id.offer_imageview)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        context = parent.context
        val v = LayoutInflater.from(parent.context).inflate(R.layout.offer_history_recylerview,parent,false)
        return viewholder(v)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.offerName.text = " "
        holder.status.text = offerList.get(position).Status
        holder.price.text = offerList.get(position).Payout // holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context , R.anim.offeranimation)
        Glide.with(holder.itemView).load(offers[offerList[position].OfferId].Image).into(holder.offerImage)

        var lastPosition = -1

        val animation = AnimationUtils.loadAnimation(
            context, if (position > lastPosition) {`in`.oncash.oncash.R.anim.offeranimation }else {`in`.oncash.oncash.R.anim.offeranimationdown}
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
    fun updateList(list :ArrayList<Fields> , offerList :ArrayList<Offer>){
//        val result =  DiffUtil.calculateDiff( Diffutil(offerList , list))
        this.offers.clear()
        this.offers.addAll(offerList)
        this.offerList.clear()
        this.offerList.addAll(list)
        notifyDataSetChanged()

//        result.dispatchUpdatesTo(this)
    }


}