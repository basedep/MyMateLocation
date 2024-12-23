package vaid.project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.annotations.Async
import vaid.project.R
import vaid.project.model.ParentItem
import vaid.project.model.User
import vaid.project.utils.MyDiffUtilGroup

class ParentRecyclerAdapter()
    : RecyclerView.Adapter<ParentRecyclerAdapter.ParentViewHolder>(){

    val differ = AsyncListDiffer(this, MyDiffUtilGroup)

     inner class ParentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val groupName: TextView = itemView.findViewById(R.id.group_name)
        val childRecyclerView: RecyclerView = itemView.findViewById(R.id.child_recycler_users)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.parent_recycler, parent, false)

        return ParentViewHolder(view)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parentItem = differ.currentList[position]

        holder.groupName.text = parentItem.title
        holder.childRecyclerView.setHasFixedSize(true)
        holder.childRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)

        val adapter = ChildRecyclerAdapter(parentItem.childItemList)
        holder.childRecyclerView.adapter = adapter

        adapter.setOnMessageClickListener {
            onMessageChildClickListener?.invoke(it)
        }

    }


    private var onMessageChildClickListener: ((User) -> Unit)? = null

    fun setOnMessageChildClickListener(listener: (User) -> Unit){
        onMessageChildClickListener = listener
    }
}