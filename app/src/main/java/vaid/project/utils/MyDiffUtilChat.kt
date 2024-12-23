package vaid.project.utils

import androidx.recyclerview.widget.DiffUtil
import vaid.project.model.Message

object MyDiffUtilChat : DiffUtil.ItemCallback<Message>(){

    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }

}