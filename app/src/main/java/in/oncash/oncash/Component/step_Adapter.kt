package `in`.oncash.oncash.Component

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `in`.oncash.oncash.DataType.Instruction
import `in`.oncash.oncash.DataType.Step
import `in`.oncash.oncash.R


 class step_Adapter : RecyclerView.Adapter<`in`.oncash.oncash.Component.step_Adapter.viewholder>() {
    var InstructionList : ArrayList<Step> = ArrayList()
     var Instruction : ArrayList<Instruction> = ArrayList()
     var ClosingInstruction : ArrayList<Instruction> = ArrayList()

    var context : Context?=null
    class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val status : ImageView
        val description :TextView
        var isClicked :Boolean = false
        val instruction :CardView
        val recylerView :RecyclerView

        init {
            description = itemView.findViewById(`in`.oncash.oncash.R.id.stepName)
            status = itemView.findViewById(R.id.stepImage)
            recylerView = itemView.findViewById(R.id.instruction_recylerview)
            instruction = itemView.findViewById(R.id.instructions_expandable)
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

        holder.itemView.setOnClickListener{
            if (!holder.isClicked){
                if(holder.description.text.contains("Register")){
                }
                if(holder.description.text.contains("Close")){
                }
                holder.instruction.visibility = View.VISIBLE
                holder.isClicked = true

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
    fun updateList(list :ArrayList<Step> , instructionlist:ArrayList<Instruction> , closingInstruction: ArrayList<Instruction>)  {
        this.InstructionList.clear()
        this.InstructionList.addAll(list)
        this.Instruction.clear()
        this.Instruction.addAll(instructionlist)
        this.ClosingInstruction.clear()
        this.ClosingInstruction.addAll(closingInstruction)
        notifyDataSetChanged()
    }
}