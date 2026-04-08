package com.example.namnyam.ui.client

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.namnyam.R
//import com.example.namnyam.data.cart.CartStore
import com.example.namnyam.data.local.SessionManager
import com.example.namnyam.data.storage.TokenManager
import com.example.namnyam.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var tokenManager: TokenManager

    private enum class AppRole {
        CLIENT, OWNER, COURIER
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        tokenManager = TokenManager(requireContext())

        setupToolbar()

        val role = setupUserInfo()
        setupRoleActions(role)
        setupCommonActions()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Профиль"
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }



    private fun setupUserInfo(): AppRole {
        val userName = sessionManager.getUserName().ifBlank { "Пользователь" }
        val userEmail = sessionManager.getUserEmail().ifBlank { "Email не указан" }

        val roleFromSession = sessionManager.getUserRole()
        val roleFromToken = tokenManager.getUserRole().orEmpty()
        val rawRole = if (roleFromSession.isNotBlank()) roleFromSession else roleFromToken

        val role = when (rawRole.uppercase()) {
            "OWNER" -> AppRole.OWNER
            "COURIER" -> AppRole.COURIER
            else -> AppRole.CLIENT
        }

        binding.tvUserName.text = userName
        binding.tvUserEmail.text = userEmail
        binding.tvUserRole.text = mapRole(role)

        return role
    }

    private fun setupRoleActions(role: AppRole) {
        when (role) {
            AppRole.CLIENT -> {
                binding.tvActionsTitle.text = "Разделы клиента"

                binding.itemPrimary.visibility = View.VISIBLE
                binding.itemSecondary.visibility = View.VISIBLE

                binding.tvPrimaryTitle.text = "История заказов"
                binding.tvPrimarySubtitle.text = "Просмотр оформленных заказов"

                binding.tvSecondaryTitle.text = "Мои адреса"
                binding.tvSecondarySubtitle.text = "Управление адресами доставки"

                binding.itemPrimary.setOnClickListener {
                    findNavController().navigate(R.id.ordersHistoryFragment)
                }

                binding.itemSecondary.setOnClickListener {
                    findNavController().navigate(R.id.addressesFragment)
                }
            }

            AppRole.OWNER -> {
                binding.tvActionsTitle.text = "Разделы владельца"

                binding.itemPrimary.visibility = View.VISIBLE
                binding.itemSecondary.visibility = View.VISIBLE

                binding.tvPrimaryTitle.text = "Панель владельца"
                binding.tvPrimarySubtitle.text = "Заказы, статус ресторана, управление"

                binding.tvSecondaryTitle.text = "Блюда ресторана"
                binding.tvSecondarySubtitle.text = "Добавление и редактирование меню"

                binding.itemPrimary.setOnClickListener {
                    findNavController().navigate(R.id.ownerHomeFragment)
                }

                binding.itemSecondary.setOnClickListener {
                    findNavController().navigate(R.id.ownerProductsFragment)
                }
            }

            AppRole.COURIER -> {
                binding.tvActionsTitle.text = "Разделы курьера"

                binding.itemPrimary.visibility = View.VISIBLE
                binding.itemSecondary.visibility = View.GONE

                binding.tvPrimaryTitle.text = "Панель курьера"
                binding.tvPrimarySubtitle.text = "Текущие и доступные доставки"

                binding.itemPrimary.setOnClickListener {
                    findNavController().navigate(R.id.courierHomeFragment)
                }
            }
        }
    }

    private fun setupCommonActions() {
        binding.itemLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        sessionManager.clearSession()
        tokenManager.clear()
        CartStore.clear(requireContext())

        Toast.makeText(
            requireContext(),
            "Вы вышли из аккаунта",
            Toast.LENGTH_SHORT
        ).show()

        findNavController().navigate(R.id.loginFragment)
    }

    private fun mapRole(role: AppRole): String {
        return when (role) {
            AppRole.CLIENT -> "Клиент"
            AppRole.OWNER -> "Владелец"
            AppRole.COURIER -> "Курьер"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}