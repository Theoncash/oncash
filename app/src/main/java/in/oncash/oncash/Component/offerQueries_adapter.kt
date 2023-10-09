package `in`.oncash.oncash.Component

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.oncash.oncash.DataType.Instruction
import `in`.oncash.oncash.DataType.Step
import `in`.oncash.oncash.R


 class offerQueries_adapter : RecyclerView.Adapter<`in`.oncash.oncash.Component.offerQueries_adapter.viewholder>() {
    var InstructionList : ArrayList<Instruction> = ArrayList()

    var context : Context?=null
    class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val description :TextView
        var isClicked :Boolean = false
        val instruction :CardView
        val text:TextView
        init {
            description = itemView.findViewById(`in`.oncash.oncash.R.id.offerQuries_Name)
            instruction = itemView.findViewById(R.id.offerQuries_expandable)
            text = itemView.findViewById(R.id.offerQuries_TextView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):viewholder {
        context = parent.context


        val v = LayoutInflater.from(parent.context).inflate(R.layout.offerquries,parent,false)


        return viewholder(
            v
        )



    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: viewholder, position: Int) {

        holder.description.text = InstructionList.get(position).SerialNumber

        holder.itemView.setOnClickListener{
            if (!holder.isClicked){
                holder.instruction.visibility = View.VISIBLE
                holder.isClicked = true
                holder.text.text = InstructionList.get(position).instruction

            }else{
                holder.instruction.visibility = View.GONE
                holder.isClicked = false
            }
        }
    }

    override fun getItemCount(): Int {

        return InstructionList.size

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList( instructionlist:ArrayList<Instruction> )  {
        this.InstructionList.clear()
        this.InstructionList.addAll(instructionlist)

        notifyDataSetChanged()
    }
}