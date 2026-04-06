package com.example.namnyam.ui.client

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.namnyam.R
import com.example.namnyam.data.local.SessionManager
import com.example.namnyam.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        sessionManager = SessionManager(requireContext())

        setupUserInfo()
        setupClicks()
    }

    private fun setupUserInfo() {
        val userName = sessionManager.getUserName().ifBlank { "Пользователь" }
        val userEmail = sessionManager.getUserEmail().ifBlank { "Email не указан" }
        val userRole = sessionManager.getUserRole().ifBlank { "CLIENT" }

        binding.tvUserName.text = userName
        binding.tvUserEmail.text = userEmail
        binding.tvUserRole.text = mapRole(userRole)
    }

    private fun setupClicks() {
        binding.itemOrders.setOnClickListener {
            findNavController().navigate(R.id.ordersHistoryFragment)
        }

        binding.itemAddresses.setOnClickListener {
            findNavController().navigate(R.id.addressesFragment)
        }

        binding.itemLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        sessionManager.clearSession()

        Toast.makeText(
            requireContext(),
            "Вы вышли из аккаунта",
            Toast.LENGTH_SHORT
        ).show()

        findNavController().navigate(R.id.loginFragment)
    }

    private fun mapRole(role: String): String {
        return when (role) {
            "CLIENT" -> "Клиент"
            "OWNER" -> "Владелец"
            "COURIER" -> "Курьер"
            else -> role
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}