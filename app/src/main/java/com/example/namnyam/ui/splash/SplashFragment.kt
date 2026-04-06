package com.example.namnyam.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.namnyam.R
import com.example.namnyam.data.storage.TokenManager

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tokenManager = TokenManager(requireContext())
        val token = tokenManager.getToken()
        val role = tokenManager.getUserRole()

        val navController = findNavController()

        if (token.isNullOrBlank()) {
            navController.navigate(R.id.action_splashFragment_to_loginFragment)
            return
        }

        when (role) {
            "CLIENT" -> navController.navigate(R.id.action_splashFragment_to_clientHomeFragment)
            "OWNER" -> navController.navigate(R.id.action_splashFragment_to_ownerHomeFragment)
            "COURIER" -> navController.navigate(R.id.action_splashFragment_to_courierHomeFragment)
            else -> navController.navigate(R.id.action_splashFragment_to_loginFragment)
        }
    }
}