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
import androidx.recyclerview.widget.RecyclerView.LayoutManager
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
        val recyclerView :RecyclerView

        init {
            description = itemView.findViewById(`in`.oncash.oncash.R.id.stepName)
            status = itemView.findViewById(R.id.stepImage)
            instruction = itemView.findViewById(R.id.instructions_expandable)
            recyclerView = itemView.findViewById(R.id.instruction_recyclerview)
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
                Log.i("instructionData" , Instruction.toString())
                holder.instruction.visibility = View.VISIBLE
                holder.isClicked = true
                if(holder.description.text.contains("Register")){
                    Log.i("instructionData" , Instruction.toString())

                    val adapter =  Instructions_RecylerViewAdapter()
                    holder.recyclerView.adapter  = adapter
                    holder.recyclerView.layoutManager =LinearLayoutManager(holder.itemView.context , LinearLayoutManager.VERTICAL , false)
                    adapter.updateList(Instruction)
                }
                if(holder.description.text.contains("Close")){
                    val adapter =  Instructions_RecylerViewAdapter()
                    holder.recyclerView.adapter  = adapter
                    holder.recyclerView.layoutManager =LinearLayoutManager(holder.itemView.context , LinearLayoutManager.VERTICAL , false)
                    adapter.updateList(ClosingInstruction)

                }


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