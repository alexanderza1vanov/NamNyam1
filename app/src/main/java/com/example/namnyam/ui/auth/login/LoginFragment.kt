package com.example.namnyam.ui.auth.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.namnyam.R
import com.example.namnyam.data.storage.TokenManager
import com.example.namnyam.databinding.FragmentLoginBinding
import com.example.namnyam.utils.UiState

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LoginViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back_24)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Заполни email и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        binding.tvToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        viewModel.state = { state ->
            when (state) {
                UiState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }

                UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true

                    val response = state.data
                    TokenManager(requireContext()).saveUserRole(response.user.role)

                    when (response.user.role) {
                        "CLIENT" -> findNavController().navigate(R.id.action_login_to_client_home)
                        "OWNER" -> findNavController().navigate(R.id.action_login_to_owner_home)
                        "COURIER" -> findNavController().navigate(R.id.action_login_to_courier_home)
                        else -> Toast.makeText(
                            requireContext(),
                            "Неизвестная роль: ${response.user.role}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}