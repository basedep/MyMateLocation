package vaid.project.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import vaid.project.R
import vaid.project.ui.activities.MainActivity
import vaid.project.utils.SessionUtil
import vaid.project.viewmodels.LocationViewModel

class ProfileFragment : BaseFragment() {

    override var bottomNavigationVisibility: Int = View.VISIBLE

    private var userSession: SessionUtil? = null
    private lateinit var viewModel: LocationViewModel
    private lateinit var displayName: TextView
    private lateinit var displayEmail: TextView
    private lateinit var login: Button
    private lateinit var signup: Button
    private lateinit var exit: Button
    private lateinit var registerLinearLayout: LinearLayout
    private lateinit var exitLinearLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        viewModel = (activity as MainActivity).viewModel

        displayEmail = view.findViewById(R.id.user_email)
        displayName = view.findViewById(R.id.user_name)
        login = view.findViewById(R.id.profile_login_button)
        signup = view.findViewById(R.id.profile_signup_button)
        exit = view.findViewById(R.id.profile_exit_button)
        registerLinearLayout = view.findViewById(R.id.registerLinearLayout)
        exitLinearLayout = view.findViewById(R.id.exitLinearLayout)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userSession = SessionUtil(view.context)
        val name = userSession?.getPreference("userName")
        val email = userSession?.getPreference("userEmail")
        if (name!!.isNotBlank() && email!!.isNotBlank()) {
            displayName.text = name
            displayEmail.text = email
            registerLinearLayout.visibility = View.GONE
            exitLinearLayout.visibility = View.VISIBLE
        }

        login.setOnClickListener {
            findNavController().navigate(
                R.id.action_profileFragment_to_loginFragment,
                null
            )
        }

        signup.setOnClickListener {
            findNavController().navigate(
                R.id.action_profileFragment_to_signupFragment,
                null
            )
        }

        exit.setOnClickListener {

            setEnableButton(false)
            viewModel.deleteSession(SessionUtil(requireContext()).getPreference("sessionId"))
                .invokeOnCompletion {
                    SessionUtil(requireContext()).clearSessionPreferences()
                    Toast.makeText(context, "Выход из аккаунта", Toast.LENGTH_SHORT).show()
                    setEnableButton(true)
                }
        }

    }


    private fun setEnableButton(enable: Boolean){
        signup.isEnabled = enable
        login.isEnabled = enable
    }

}