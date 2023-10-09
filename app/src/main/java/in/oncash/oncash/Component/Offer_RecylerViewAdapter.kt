package `in`.oncash.oncash.Component

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import `in`.oncash.oncash.R
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `in`.oncash.oncash.DataType.Offer
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.View.Info
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class Offer_RecylerViewAdapter(val userData :userData ) : RecyclerView.Adapter<Offer_RecylerViewAdapter.viewholder>() {
    var offerList : ArrayList<Offer> = ArrayList<Offer>()
    var lastPosition = -1
    var offer = 100
    var context : Context?=null
    class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name : TextView
        val price :TextView
        val DaysLeft : TextView
        val background : ImageView
        lateinit var  offerId :String
        init {
            name = itemView.findViewById(`in`.oncash.oncash.R.id.offerHistory_name)
            price = itemView.findViewById(R.id.offerHistory_price)
            background = itemView.findViewById(R.id.offer_imageview)
            DaysLeft = itemView.findViewById(R.id.offer_timer)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        context = parent.context


        val v = LayoutInflater.from(parent.context).inflate(R.layout.offer_recyerview,parent,false)


        return viewholder(v)



    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: viewholder, position: Int) {
        val price :Int = offerList[position].Price!!.toInt()

        holder.offerId = offerList[position].OfferId!!
        holder.name.text= offerList[position].Name
        val text =  (offerList[position].Price!!.toInt() * offer ) /100
        holder.price.text = "₹ $text "
        Glide.with(holder.itemView.context).load(offerList[position].Image).into(holder.background)
        holder.DaysLeft.text = "Only ${offerList[position].cap.toString()} Offer  Left "
        val url :URL = URL( offerList[position].Image )

        var colour  :String = ""


//
//        val animation = AnimationUtils.loadAnimation(
//            context, if (position > lastPosition) {com.example.oncash.R.anim.offeranimation }else {com.example.oncash.R.anim.offeranimationdown}
//        )
//        holder.itemView.startAnimation(animation)
//        lastPosition = position

//            holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context , R.anim.offeranimation)


        holder.itemView.setOnClickListener {
            val offer_information : Offer = offerList.get(holder.offerId.toInt()-1)
            val intent = Intent(
                holder.itemView.context,
                Info::class.java
            )
                .putExtra("OfferId",offer_information.OfferId )
                .putExtra("OfferName",offer_information.Name)
                .putExtra("OfferImage",offer_information.Image)
                .putExtra("OfferPrice",offer_information.Price)
                .putExtra("OfferLink",offer_information.Link)
                .putExtra("subid" , offer_information.subid)
                .putExtra("subid2" , offer_information.payout)
                .putExtra("wallet" , userData.userNumber)
                .putExtra("videoId" , offer_information.VideoId)
                .putExtra("number" , userData.userNumber.toString())
                .putExtra("noOfSteps" , offer_information.noOfSteps.toString())
                .putExtra("appName" , offer_information.appName)
                .putExtra("regSMS" , offer_information.regSMS)

            holder.itemView.context.startActivity(
                intent

            )
        }

    }

    override fun getItemCount(): Int {

        return offerList.size

    }

    override fun onViewDetachedFromWindow(holder: viewholder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list :ArrayList<Offer> , offer: Int){
//        val result =  DiffUtil.calculateDiff( Diffutil(offerList , list))
        this.offer = offer
        this.offerList.clear()
        this.offerList.addAll(list)
        notifyDataSetChanged()

//        result.dispatchUpdatesTo(this)
    }

    fun getDominantColor(bitmapp: Bitmap?): Int {
        val newBitmap = Bitmap.createScaledBitmap(bitmapp!!, 10, 10, true)
        val color = newBitmap.getPixel(9, 9)
        newBitmap.recycle()
        return color
    }

    fun linearGradientDrawable(color: String): GradientDrawable {
        return GradientDrawable().apply {
            if (color.contains("fff"))
            {
                colors = intArrayOf(
                    Color.parseColor("#E6E3D3"),
                    Color.parseColor("#$color")

                )
            }else{
                colors = intArrayOf(
                    Color.parseColor("#$color"),
                    Color.parseColor("#ffffff")

                )
            }

            gradientType = GradientDrawable.LINEAR_GRADIENT
            shape = GradientDrawable.RECTANGLE
            orientation = GradientDrawable.Orientation.BL_TR

            // border around drawable
           // setStroke(5,Color.parseColor("#4B5320"))
        }
    }

}