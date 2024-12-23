package vaid.project.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import vaid.project.R
import vaid.project.model.Message
import vaid.project.utils.MyDiffUtilChat
import vaid.project.utils.SessionUtil
import java.text.SimpleDateFormat
import java.util.Locale

class ChatRecyclerAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val differ = AsyncListDiffer(this, MyDiffUtilChat)

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById<TextView>(R.id.message_text)
        val date = itemView.findViewById<TextView>(R.id.message_time)

        fun bind(message: Message) {
            text.text = message.messageText
            date.text = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(message.timestamp)
        }
    }

    inner class ChatSelfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById<TextView>(R.id.message_text_self)
        val date = itemView.findViewById<TextView>(R.id.message_time_self)

        fun bind(message: Message) {
            text.text = message.messageText
            date.text = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(message.timestamp)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return  if (differ.currentList[position].author == SessionUtil(context).getPreference("userId")) {
            0
        } else {
            1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return when(viewType){
            0 ->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message_self_item, parent, false)
                ChatSelfViewHolder(view)
            }
            1 ->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message_item, parent, false)
                ChatViewHolder(view)
            }
           else -> throw IllegalArgumentException("Invalid view type")
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = differ.currentList[position]
        when (holder) {
            is ChatViewHolder -> holder.bind(message)
            is ChatSelfViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}