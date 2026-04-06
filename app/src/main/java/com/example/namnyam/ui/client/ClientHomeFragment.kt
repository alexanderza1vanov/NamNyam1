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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentClientHomeBinding.bind(view)

        viewModel = ViewModelProvider(this)[ClientHomeViewModel::class.java]

        adapter = RestaurantAdapter { restaurant ->
            val bundle = Bundle().apply {
                putLong("restaurantId", restaurant.id)
                putString("restaurantName", restaurant.name)
                putString("restaurantDescription", restaurant.description ?: "")
            }
            findNavController().navigate(
                R.id.action_clientHomeFragment_to_restaurantDetailsFragment,
                bundle
            )
        }

        binding.recyclerRestaurants.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRestaurants.adapter = adapter

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_cart -> {
                    findNavController().navigate(R.id.action_clientHomeFragment_to_cartFragment)
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

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    adapter.submitList(state.data)

                    if (state.data.isEmpty()) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}