package `in`.oncash.oncash.Component

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `in`.oncash.oncash.DataType.Instruction
import `in`.oncash.oncash.DataType.Step
import `in`.oncash.oncash.R


 class step_Adapter : RecyclerView.Adapter<`in`.oncash.oncash.Component.step_Adapter.viewholder>() {
    var InstructionList : ArrayList<Step> = ArrayList()

    var context : Context?=null
    class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val status : ImageView
        val description :TextView

        init {
            description = itemView.findViewById(`in`.oncash.oncash.R.id.stepName)
            status = itemView.findViewById(R.id.stepImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):viewholder {
        context = parent.context


        val v = LayoutInflater.from(parent.context).inflate(R.layout.custom_step,parent,false)


        return viewholder(
            v
        )



    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: viewholder, position: Int) {

        holder.description.text = InstructionList.get(position).instruction
        if(InstructionList.get(position).isCompleted){
            holder.status.setImageResource(R.drawable.completed)
        }else{
            holder.status.setImageResource(R.drawable.notcompleted)

        }

    }

    override fun getItemCount(): Int {

        return InstructionList.size

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list :ArrayList<Step>){
        this.InstructionList.clear()
        this.InstructionList.addAll(list)
        notifyDataSetChanged()
    }
}