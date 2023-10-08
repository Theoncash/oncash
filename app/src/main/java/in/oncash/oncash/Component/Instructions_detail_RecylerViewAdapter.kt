package `in`.oncash.oncash.Component

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.oncash.oncash.DataType.Instruction
import `in`.oncash.oncash.R


class Instructions_detail_RecylerViewAdapter : RecyclerView.Adapter<`in`.oncash.oncash.Component.Instructions_detail_RecylerViewAdapter.viewholder>() {
    var InstructionList : ArrayList<Instruction> = ArrayList<Instruction>()

    var context : Context?=null
    class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val serialNumber : TextView
        val description :TextView

        init {
            description = itemView.findViewById(`in`.oncash.oncash.R.id.withdrawalTransactionAmount)
             serialNumber = itemView.findViewById(R.id.withdrawalTransactionDate)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):viewholder {
        context = parent.context


        val v = LayoutInflater.from(parent.context).inflate(R.layout.offer_steps_listview,parent,false)

        return viewholder(
            v
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: viewholder, position: Int) {

        holder.serialNumber.text= (position + 1 ).toString()
        holder.description.text = InstructionList.get(position).instruction

    }

    override fun getItemCount(): Int {
        return InstructionList.size

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list :ArrayList<Instruction>){
        this.InstructionList.clear()
        this.InstructionList.addAll(list)
        notifyDataSetChanged()
    }
}