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
import `in`.oncash.oncash.DataType.Fields
import `in`.oncash.oncash.DataType.Offer
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.View.Info
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class referral_RecylerViewAdapter() : RecyclerView.Adapter<referral_RecylerViewAdapter.viewholder>() {
    var offerList : ArrayList<Fields> = ArrayList<Fields>()
    class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val number : TextView
        val total_bal :TextView
        val serial_no : TextView
        init {
            number = itemView.findViewById(R.id.user_number)
            total_bal = itemView.findViewById(R.id.total_bal)
            serial_no = itemView.findViewById(R.id.serial_num)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        parent.context

        val v = LayoutInflater.from(parent.context).inflate(R.layout.leader_board_recyclerview,parent,false)

        return viewholder(v)



    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        holder.serial_no.text = (position + 1).toString()
            holder.number.text=  offerList[position].UserPhone.toString()

        val price = ((offerList[position].Total_Bal * 20 ) / 100 )
        holder.total_bal.text = "₹ $price "

    }

    override fun getItemCount(): Int {

        return offerList.size

    }

    override fun onViewDetachedFromWindow(holder: viewholder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list :ArrayList<Fields> ){
        this.offerList.clear()
        this.offerList.addAll(list)
        notifyDataSetChanged()

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