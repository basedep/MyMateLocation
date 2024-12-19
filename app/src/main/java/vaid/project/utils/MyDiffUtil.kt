package vaid.project.utils

import androidx.recyclerview.widget.DiffUtil
import vaid.project.model.User


object MyDiffUtil : DiffUtil.ItemCallback<User>(){

    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }

}