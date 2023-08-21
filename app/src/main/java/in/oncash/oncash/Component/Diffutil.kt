package `in`.oncash.oncash.Component

import androidx.recyclerview.widget.DiffUtil
import `in`.oncash.oncash.DataType.Offer

class Diffutil(val oldList : ArrayList<Offer> , val newList : ArrayList<Offer>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {

        return oldList.size
    }

    override fun getNewListSize(): Int {

      return  newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList.get(oldItemPosition).Image == newList.get(newItemPosition).Image
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList.get(oldItemPosition).Image == newList.get(newItemPosition).Image
    }
}