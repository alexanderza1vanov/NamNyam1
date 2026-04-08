package com.example.namnyam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.example.namnyam.data.local.SessionManager
import com.example.namnyam.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var navController: NavController

    private var currentMenuRes: Int? = null

    private val clientTopLevelDestinations = setOf(
        R.id.clientHomeFragment,
        R.id.cartFragment,
        R.id.ordersHistoryFragment,
        R.id.profileFragment
    )

    private val ownerTopLevelDestinations = setOf(
        R.id.ownerHomeFragment,
        R.id.ownerProductsFragment,
        R.id.profileFragment
    )

    private val courierTopLevelDestinations = setOf(
        R.id.courierHomeFragment,
        R.id.profileFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupBottomNavigation()
        observeNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            val currentDestinationId = navController.currentDestination?.id
            if (currentDestinationId == item.itemId) {
                return@setOnItemSelectedListener true
            }

            val options = navOptions {
                launchSingleTop = true
                restoreState = true
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
            }

            runCatching {
                navController.navigate(item.itemId, null, options)
            }

            true
        }

        binding.bottomNav.setOnItemReselectedListener {
            // ничего не делаем
        }
    }

    private fun observeNavigation() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val role = getCurrentRole()

            applyMenuForRole(role)

            val shouldShowBottomNav = destination.id in visibleDestinationsForRole(role)
            binding.bottomNav.isVisible = shouldShowBottomNav

            if (shouldShowBottomNav) {
                binding.bottomNav.menu.findItem(destination.id)?.isChecked = true
            }
        }
    }

    private fun applyMenuForRole(role: String) {
        val menuRes = when (role) {
            "OWNER" -> R.menu.menu_bottom_owner
            "COURIER" -> R.menu.menu_bottom_courier
            else -> R.menu.menu_bottom_client
        }

        if (currentMenuRes == menuRes) return

        binding.bottomNav.menu.clear()
        binding.bottomNav.inflateMenu(menuRes)
        currentMenuRes = menuRes
    }

    private fun visibleDestinationsForRole(role: String): Set<Int> {
        return when (role) {
            "OWNER" -> ownerTopLevelDestinations
            "COURIER" -> courierTopLevelDestinations
            else -> clientTopLevelDestinations
        }
    }

    private fun getCurrentRole(): String {
        return sessionManager.getUserRole()
            .uppercase(Locale.ROOT)
            .ifBlank { "CLIENT" }
    }
}