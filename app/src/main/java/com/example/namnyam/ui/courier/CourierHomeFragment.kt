package com.example.namnyam.ui.courier

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.namnyam.R
import com.example.namnyam.databinding.FragmentCourierHomeBinding

class CourierHomeFragment : Fragment(R.layout.fragment_courier_home) {

    private var _binding: FragmentCourierHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCourierHomeBinding.bind(view)

        setupToolbar()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Панель курьера"
        binding.toolbar.menu.clear()
        binding.toolbar.inflateMenu(R.menu.menu_profile_only)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_profile -> {
                    val navController = findNavController()
                    if (navController.currentDestination?.id == R.id.courierHomeFragment) {
                        navController.navigate(R.id.profileFragment)
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}