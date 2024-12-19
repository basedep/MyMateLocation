package vaid.project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import vaid.project.R
import vaid.project.model.User
import vaid.project.utils.MyDiffUtil

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.UsersViewHolder>() {

    val differ = AsyncListDiffer(this, MyDiffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerAdapter.UsersViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)

        return UsersViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.UsersViewHolder, position: Int) {
        val user = differ.currentList[position]

        holder.itemView.apply {
            val name = findViewById<TextView>(R.id.itemName)
            name.text = user.name

            val button = findViewById<Button>(R.id.itemAddUser)
            button.setOnClickListener {
                onButtonClickListener?.invoke(user)
            }
        }

    }

    override fun getItemCount(): Int = differ.currentList.size


    private var onButtonClickListener: ((User) -> Unit)? = null

    fun setOnButtonClickListener(listener: (User) -> Unit){
        onButtonClickListener = listener
    }


    inner class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}