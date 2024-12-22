package vaid.project.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import vaid.project.R
import vaid.project.adapter.RecyclerAdapter
import vaid.project.model.Groups
import vaid.project.ui.activities.MainActivity
import vaid.project.utils.SessionUtil
import vaid.project.viewmodels.LocationViewModel

class SearchFragment : BaseFragment() {

    override var bottomNavigationVisibility: Int = View.VISIBLE

    private var recycler: RecyclerView? = null
    private var searchView: SearchView? = null
    private var recyclerAdapter: RecyclerAdapter? = null

    private lateinit var viewModel: LocationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        viewModel = (activity as MainActivity).viewModel
        recyclerAdapter = RecyclerAdapter()
        recycler = view.findViewById(R.id.recyclerListUsers)
        recycler?.adapter = recyclerAdapter
        recycler?.layoutManager = LinearLayoutManager(context)
        searchView = view.findViewById(R.id.top_search_bar)

        viewModel.getAllUsers(SessionUtil(requireContext()).getPreference("userId"))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.users.observe(viewLifecycleOwner){
            recyclerAdapter?.differ?.submitList(it)
        }

        val options = arrayListOf<String>()

        viewModel.parentItems.observe(viewLifecycleOwner){
           for(group in it){
               options.add(group.title)
           }
        }
        recyclerAdapter?.setOnButtonClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Выберете группу")
                .setItems(options.toTypedArray()) { _, which ->
                    val selectedOption = options[which]
                    lifecycleScope.launch {
                        val group = viewModel.getGroupByNameAndUserId(selectedOption, SessionUtil(requireContext()).getPreference("userId")).await()[0]
                        group.groupUsers?.add(it.id.toString())
                        viewModel.addUserToGroup(group.id, group)
                        Log.d("myLog", "onViewCreated: $group")
                    }
                }
                .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }
                .show()
        }
    }

}