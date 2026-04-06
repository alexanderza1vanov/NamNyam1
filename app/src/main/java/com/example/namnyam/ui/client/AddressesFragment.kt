package com.example.namnyam.ui.client

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namnyam.R
import com.example.namnyam.databinding.FragmentAddressesBinding
import com.example.namnyam.utils.UiState

class AddressesFragment : Fragment(R.layout.fragment_addresses) {

    private var _binding: FragmentAddressesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AdressesViewModel
    private lateinit var adapter: AdressesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddressesBinding.bind(view)

        viewModel = ViewModelProvider(
            this,
            AdressesViewModelFactory(requireContext())
        )[AdressesViewModel::class.java]

        setupUi()
        observeState()

        viewModel.loadAddresses()
    }

    private fun setupUi() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        adapter = AdressesAdapter { address ->
            viewModel.deleteAddress(address.id)
        }

        binding.recyclerAddresses.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAddresses.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadAddresses()
        }

        binding.fabAddAddress.setOnClickListener {
            Toast.makeText(requireContext(), "Экран добавления адреса подключим следующим шагом", Toast.LENGTH_SHORT).show()
        }

        binding.btnRetry.setOnClickListener {
            viewModel.loadAddresses()
        }
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Idle -> Unit

                UiState.Loading -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerAddresses.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                    binding.layoutError.visibility = View.GONE
                }

                is UiState.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.progressBar.visibility = View.GONE
                    binding.layoutError.visibility = View.GONE

                    adapter.submitList(state.data)

                    if (state.data.isEmpty()) {
                        binding.recyclerAddresses.visibility = View.GONE
                        binding.tvEmpty.visibility = View.VISIBLE
                    } else {
                        binding.recyclerAddresses.visibility = View.VISIBLE
                        binding.tvEmpty.visibility = View.GONE
                    }
                }

                is UiState.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerAddresses.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                    binding.layoutError.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}