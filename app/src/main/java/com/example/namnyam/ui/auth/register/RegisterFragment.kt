package com.example.namnyam.ui.auth.register

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.namnyam.R
import com.example.namnyam.data.storage.TokenManager
import com.example.namnyam.databinding.FragmentRegisterBinding
import com.example.namnyam.utils.UiState

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RegisterViewModel

    private val roles = listOf("CLIENT", "OWNER", "COURIER")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

//        binding.toolbar.navigationIcon =
//            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back_24)
//
//        binding.toolbar.setNavigationOnClickListener {
//            findNavController().navigateUp()
//        }

        binding.spRole.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            roles
        )

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val role = binding.spRole.selectedItem.toString()

            if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Заполни все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(
                name = name,
                email = email,
                phone = phone,
                password = password,
                role = role
            )
        }

        binding.tvToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }

        viewModel.state = { state ->
            when (state) {
                UiState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                }

                UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true

                    val response = state.data
                    TokenManager(requireContext()).saveUserRole(response.user.role)

                    when (response.user.role) {
                        "CLIENT" -> findNavController().navigate(R.id.action_register_to_client_home)
                        "OWNER" -> findNavController().navigate(R.id.action_register_to_owner_home)
                        "COURIER" -> findNavController().navigate(R.id.action_register_to_courier_home)
                        else -> Toast.makeText(
                            requireContext(),
                            "Неизвестная роль: ${response.user.role}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
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