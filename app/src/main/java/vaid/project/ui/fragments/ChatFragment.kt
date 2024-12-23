package vaid.project.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import vaid.project.R
import vaid.project.adapter.ChatRecyclerAdapter
import vaid.project.model.Chat
import vaid.project.model.Message
import vaid.project.model.User
import vaid.project.ui.activities.MainActivity
import vaid.project.utils.SessionUtil
import vaid.project.viewmodels.LocationViewModel
import java.util.Date
import java.util.UUID

class ChatFragment : BaseFragment() {

    override var bottomNavigationVisibility: Int = View.GONE

    private var chatName: TextView? = null
    private var recycler: RecyclerView? = null
    private var recyclerAdapter: ChatRecyclerAdapter? = null
    private var messageEditText: TextInputEditText? = null
    private var sendButton: Button? = null

    private lateinit var viewModel: LocationViewModel
    private val chat = mutableListOf<Chat>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_chat, container, false)

        val user = arguments?.getSerializable("user") as User

        viewModel = (activity as MainActivity).viewModel

        val userId = SessionUtil(requireContext()).getPreference("userId")

        lifecycleScope.launch {
            val result1 = viewModel.getChat(userId, user.id!!).await()
            val result2 = viewModel.getChat(user.id, userId).await()
            if (result1.isNotEmpty())
                chat.add(result1[0])
            else if (result2.isNotEmpty())
                chat.add(result2[0])
            else {
                val newChatId = UUID.randomUUID().toString()
                viewModel.createChat(newChatId, Chat(newChatId, userId, user.id, null))
            }
            Log.d("myLog", "result1: $result1")
            Log.d("myLog", "result2: $result2")
        }.invokeOnCompletion {
            viewModel.getAllChatMessages(chat[0].messagesIds!!)
        }

        chatName = view.findViewById(R.id.chat_name)
        recycler = view.findViewById(R.id.chat_recycler)
        messageEditText = view.findViewById(R.id.chat_message_edit_text)
        sendButton = view.findViewById(R.id.chat_button_send)
        recyclerAdapter = ChatRecyclerAdapter(requireContext())
        recycler?.layoutManager = LinearLayoutManager(context)
        recycler?.adapter = recyclerAdapter

        chatName?.text = user.name

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.messages.observe(viewLifecycleOwner){
            recyclerAdapter?.differ?.submitList(it)
        }

            sendButton?.setOnClickListener {
                val messageId = UUID.randomUUID().toString()

                viewModel.sendMessage(messageId, Message(messageId, SessionUtil(requireContext()).getPreference("userId"), messageEditText?.text.toString(), Date()))

                val newChatInstance = chat[0].copy()
                newChatInstance.messagesIds?.add(messageId)
                viewModel.updateChatMessages(newChatInstance)
                Log.d("myLog", "onViewCreated: $newChatInstance")
            }

    }


}