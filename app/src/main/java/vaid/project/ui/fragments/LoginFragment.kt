package vaid.project.ui.fragments

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import io.appwrite.extensions.toJson
import kotlinx.coroutines.launch
import vaid.project.R
import vaid.project.ui.activities.MainActivity
import vaid.project.utils.SessionUtil
import vaid.project.viewmodels.LocationViewModel


class LoginFragment : BaseFragment() {

    override var bottomNavigationVisibility: Int = View.GONE

    private var email: EditText? = null
    private var password: EditText? = null
    private var loginButton: Button? = null
    private var progressBar: ProgressBar? = null
    private lateinit var viewModel: LocationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        viewModel = (activity as MainActivity).viewModel

        email = view.findViewById(R.id.emailLogin)
        password = view.findViewById(R.id.passwordLogin)
        loginButton = view.findViewById(R.id.login_button)
        progressBar = view.findViewById(R.id.progressLogin)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton?.setOnClickListener {
            if (validateFields()) {

                enableFields(false)

                lifecycleScope.launch {
                    try {
                        val respond = viewModel.login(email?.text.toString(), password?.text.toString()).await()
                        val user = viewModel.getUser().await()

                        if (user.containsKey("name") and user.containsKey("email")){
                            SessionUtil(requireContext()).saveSessionPreferences(respond.id, respond.userId, user["name"]!! ,user["email"]!!)
                            Log.d("myLog", "onViewCreated: ${user.values}")
                            enableFields(true)
                            findNavController().navigate(R.id.action_loginFragment_to_profileFragment)
                        }

                    } catch (e: Exception) {
                        enableFields(true)
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun enableFields(enable: Boolean){
        loginButton?.isEnabled = enable
        email?.isEnabled = enable
        password?.isEnabled = enable
        progressBar?.visibility = if (enable) View.GONE else View.VISIBLE
    }

    private fun validateFields() : Boolean{
        return if (!(email?.text?.isNotEmpty()!! and email?.text?.isNotBlank()!! and
                    password?.text?.isNotEmpty()!! and password?.text?.isNotBlank()!!)) {
            Toast.makeText(context, "Поля не заполнены", Toast.LENGTH_SHORT).show()
            false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email?.text.toString()).matches()){
            Toast.makeText(context, "Неправильный email", Toast.LENGTH_SHORT).show()
            false
        }else true

    }

}