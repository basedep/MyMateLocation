package vaid.project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vaid.project.R
import vaid.project.model.User

class ChildRecyclerAdapter(private val listView: List<User>)
    : RecyclerView.Adapter<ChildRecyclerAdapter.ChildViewHolder>(){

    inner class ChildViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.itemNameAddedFriend)
        val message: ImageButton = itemView.findViewById(R.id.itemMessage)
        val dial: ImageButton = itemView.findViewById(R.id.itemDial)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_added_friend, parent, false)
        return ChildViewHolder(view)
    }

    override fun getItemCount(): Int = listView.size

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val childItem = listView[position]

        holder.name.text = childItem.name
        holder.message.setOnClickListener {
            onMessageClickListener?.invoke(childItem)
        }
    }

    private var onMessageClickListener: ((User) -> Unit)? = null

    fun setOnMessageClickListener(listener: (User) -> Unit){
        onMessageClickListener = listener
    }
}