package `in`.oncash.oncash.Component

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.opengl.Visibility
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
import `in`.oncash.oncash.DataType.SerializedDataType.Fields1
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.View.Info
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class leaderBoard_RecylerViewAdapter() : RecyclerView.Adapter<leaderBoard_RecylerViewAdapter.viewholder>() {
    var offerList : ArrayList<Fields1> = ArrayList<Fields1>()
    class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val number : TextView
        val total_bal :TextView
        val serial_no : TextView
        val image : ImageView
        init {
            number = itemView.findViewById(R.id.user_number)
            total_bal = itemView.findViewById(R.id.total_bal)
            serial_no = itemView.findViewById(R.id.serial_num)
            image = itemView.findViewById(R.id.image)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        parent.context


        val v = LayoutInflater.from(parent.context).inflate(R.layout.leader_board_recyclerview,parent,false)


        return viewholder(v)



    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: viewholder, position: Int) {

        holder.serial_no.text = (position + 1).toString()
        if(offerList[position].Name==null || offerList[position].Name == ""){
            holder.number.text=   offerList[position].UserPhone.toString().slice(0..3) + "XXX"
        }else{
            holder.number.text=   offerList[position].Name
        }
        if(position == 0){
            holder.serial_no.visibility = View.GONE
            holder.image.visibility = View.VISIBLE
            Glide.with(holder.itemView.context).load("https://static.vecteezy.com/system/resources/previews/018/842/631/non_2x/3d-badge-winner-for-1st-first-place-winner-awards-and-champion-prizes-3d-rendering-free-png.png").into(holder.image)
        }
        if(position == 1){
            holder.serial_no.visibility = View.GONE
            holder.image.visibility = View.VISIBLE
            Glide.with(holder.itemView.context).load("https://static.vecteezy.com/system/resources/previews/018/842/766/original/3d-badge-winner-for-2nd-second-place-winner-awards-and-champion-prizes-3d-rendering-free-png.png").into(holder.image)
        }
        if(position ==2){
            holder.serial_no.visibility = View.GONE
            holder.image.visibility = View.VISIBLE
            Glide.with(holder.itemView.context).load("https://static.vecteezy.com/system/resources/previews/018/842/778/non_2x/3d-badge-winner-for-3rd-third-place-winner-awards-and-champion-prizes-3d-rendering-free-png.png").into(holder.image)
        }

        val price = offerList[position].Total_Bal.toString()
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
    fun updateList(list :ArrayList<Fields1> ){
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