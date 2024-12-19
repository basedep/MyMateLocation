package vaid.project.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
        recyclerAdapter?.setOnButtonClickListener {
            /*lifecycleScope.launch {
                val user = viewModel.getUserData(SessionUtil(requireContext()).getPreference("userId")).await()
                if(!user[0].groupsIDs?.contains(it.id)!!){
                    Log.d("myLog", "onViewCreated: ${user[0].groupsIDs?.get(0)}")
                    viewModel.addUserToGroup(user[0].groupsIDs?.get(0) ?: "", user[0].id!!)
                }
            }*/

        }
    }

}