package vaid.project.utils

import androidx.recyclerview.widget.DiffUtil
import vaid.project.model.ParentItem
import vaid.project.model.User

object MyDiffUtilGroup : DiffUtil.ItemCallback<ParentItem>(){

    override fun areItemsTheSame(oldItem: ParentItem, newItem: ParentItem): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: ParentItem, newItem: ParentItem): Boolean {
        return oldItem == newItem
    }

}