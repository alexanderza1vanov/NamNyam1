package com.example.namnyam.ui.client

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namnyam.R
import com.example.namnyam.databinding.FragmentClientHomeBinding
import com.example.namnyam.utils.UiState

class ClientHomeFragment : Fragment(R.layout.fragment_client_home) {

    private var _binding: FragmentClientHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ClientHomeViewModel
    private lateinit var adapter: RestaurantAdapter

    private var navigationInProgress = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentClientHomeBinding.bind(view)

        viewModel = ViewModelProvider(this)[ClientHomeViewModel::class.java]

        adapter = RestaurantAdapter { restaurant ->
            val navController = findNavController()

            if (navigationInProgress) return@RestaurantAdapter
            if (navController.currentDestination?.id != R.id.clientHomeFragment) return@RestaurantAdapter

            navigationInProgress = true

            val bundle = Bundle().apply {
                putLong("restaurantId", restaurant.id)
                putString("restaurantName", restaurant.name)
                putString("restaurantDescription", restaurant.description ?: "")
            }

            navController.navigate(
                R.id.action_clientHomeFragment_to_restaurantDetailsFragment,
                bundle
            )
        }

        binding.recyclerRestaurants.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRestaurants.adapter = adapter

        binding.toolbar.setOnMenuItemClickListener { item ->
            val navController = findNavController()

            when (item.itemId) {
                R.id.action_cart -> {
                    if (navController.currentDestination?.id == R.id.clientHomeFragment) {
                        navController.navigate(R.id.action_clientHomeFragment_to_cartFragment)
                    }
                    true
                }

                R.id.action_profile -> {
                    if (navController.currentDestination?.id == R.id.clientHomeFragment) {
                        navController.navigate(R.id.profileFragment)
                    }
                    true
                }

                else -> false
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadRestaurants(forceRefresh = true)
        }

        viewModel.state = { state ->
            when (state) {
                UiState.Idle -> Unit

                UiState.Loading -> {
                    if (!binding.swipeRefresh.isRefreshing) {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    binding.tvEmpty.visibility = View.GONE
                }

                is UiState.Success<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val restaurants = state.data as List<com.example.namnyam.data.remote.dto.RestaurantDto>

                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false

                    adapter.submitList(restaurants)

                    if (restaurants.isEmpty()) {
                        binding.tvEmpty.visibility = View.VISIBLE
                        binding.recyclerRestaurants.visibility = View.GONE
                    } else {
                        binding.tvEmpty.visibility = View.GONE
                        binding.recyclerRestaurants.visibility = View.VISIBLE
                    }
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.tvEmpty.text = state.message
                    binding.recyclerRestaurants.visibility = View.GONE
                }
            }
        }

        viewModel.loadRestaurants()
    }

    override fun onResume() {
        super.onResume()
        navigationInProgress = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}