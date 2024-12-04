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
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import vaid.project.R
import vaid.project.ui.activities.MainActivity
import vaid.project.viewmodels.LocationViewModel


class SignupFragment : BaseFragment() {

    override var bottomNavigationVisibility: Int = View.GONE


    private var name: EditText? = null
    private var email: EditText? = null
    private var password: EditText? = null
    private var signUpButton: Button? = null
    private var progressBar: ProgressBar? = null
    private lateinit var viewModel: LocationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        viewModel = (activity as MainActivity).viewModel

        name = view.findViewById(R.id.name)
        email = view.findViewById(R.id.email)
        password = view.findViewById(R.id.password)
        signUpButton = view.findViewById(R.id.signup_button)
        progressBar = view.findViewById(R.id.progressSignup)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signUpButton?.setOnClickListener {
            if (validateFields()) {

                enableFields(false)

                lifecycleScope.launch {
                    val jobSignUp = viewModel.signup(
                        name = name?.text!!.toString(),
                        email = email?.text!!.toString(),
                        password = password?.text!!.toString()
                    )
                    jobSignUp.join()
                    if (!jobSignUp.isCancelled) {
                        enableFields(true)
                        Toast.makeText(
                            requireContext(),
                            "Аккаунт успешно создан",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.action_signupFragment_to_profileFragment)
                    } else
                        enableFields(true)
                    Toast.makeText(
                        requireContext(),
                        "Такой пользователь уже существует",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }


    private fun enableFields(enable: Boolean) {
        signUpButton?.isEnabled = enable
        name?.isEnabled = enable
        email?.isEnabled = enable
        password?.isEnabled = enable
        progressBar?.visibility = if (enable) View.GONE else View.VISIBLE
    }

    private fun validateFields(): Boolean {
        return if (!(name?.text?.isNotEmpty()!! and name?.text?.isNotBlank()!! and
                    email?.text?.isNotEmpty()!! and email?.text?.isNotBlank()!! and
                    password?.text?.isNotEmpty()!! and password?.text?.isNotBlank()!!)
        ) {
            Toast.makeText(context, "Поля не заполнены", Toast.LENGTH_SHORT).show()
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email?.text.toString()).matches()) {
            Toast.makeText(context, "Неправильный email", Toast.LENGTH_SHORT).show()
            false
        } else if (password?.text?.length!! <= 8) {
            Toast.makeText(context, "Пароль должен быть не менее 8 символов", Toast.LENGTH_SHORT)
                .show()
            false
        } else true

    }

}