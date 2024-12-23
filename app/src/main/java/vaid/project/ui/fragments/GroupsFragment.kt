package vaid.project.ui.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.setPadding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vaid.project.R
import vaid.project.adapter.ParentRecyclerAdapter
import vaid.project.adapter.RecyclerAdapter
import vaid.project.model.Groups
import vaid.project.ui.activities.MainActivity
import vaid.project.utils.SessionUtil
import vaid.project.viewmodels.LocationViewModel
import java.util.UUID

class GroupsFragment : BaseFragment() {

    override var bottomNavigationVisibility: Int = View.VISIBLE

    private var recycler: RecyclerView? = null
    private var toolbar: Toolbar? = null
    private var recyclerAdapter: ParentRecyclerAdapter? = null

    private lateinit var viewModel: LocationViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groups, container, false)
        viewModel = (activity as MainActivity).viewModel
        recycler = view.findViewById(R.id.recycler_groups)
        recyclerAdapter = ParentRecyclerAdapter()
        recycler?.adapter = recyclerAdapter
        recycler?.layoutManager = LinearLayoutManager(context)
        toolbar = view.findViewById(R.id.toolbar_groups)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerAdapter?.setOnMessageChildClickListener {
            val bundle = Bundle()
            bundle.putSerializable("user", it)
            findNavController().navigate(R.id.action_groupsFragment_to_chatFragment, bundle)
        }

        viewModel.parentItems.observe(viewLifecycleOwner) {
            recyclerAdapter?.differ?.submitList(it)
        }

        toolbar?.inflateMenu(R.menu.groups_toolbar_menu)
        toolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_add_item -> {

                    val editText = EditText(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        setTextColor(Color.BLACK)
                    }

                    AlertDialog.Builder(requireContext())
                        .setTitle("Введите название группы")
                        .setView(editText)
                        .setPositiveButton("OK") { _, _->
                            val inputText = editText.text.toString()

                            if (inputText.isNotBlank()) {
                                val id = UUID.randomUUID().toString()
                                val group = Groups(id, SessionUtil(requireContext()).getPreference("userId"), null, inputText)
                                viewModel.addGroup(id, group)
                                Toast.makeText(requireContext(), "Группа добавлена", Toast.LENGTH_SHORT).show()
                            }else
                                Toast.makeText(requireContext(), "Пустое поле", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }
                        .show()

                    true
                }

                else -> false
            }
        }

    }


}