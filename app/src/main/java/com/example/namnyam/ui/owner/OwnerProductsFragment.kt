package com.example.namnyam.ui.owner

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namnyam.R
import com.example.namnyam.databinding.FragmentOwnerProductsBinding
import com.example.namnyam.utils.UiState

class OwnerProductsFragment : Fragment(R.layout.fragment_owner_products) {

    private var _binding: FragmentOwnerProductsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: OwnerProductsViewModel
    private lateinit var adapter: OwnerProductsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOwnerProductsBinding.bind(view)

        viewModel = ViewModelProvider(this)[OwnerProductsViewModel::class.java]

        setupRecycler()
        setupClicks()
        observeVm()

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>("owner_products_refresh")
            ?.observe(viewLifecycleOwner) { shouldRefresh ->
                if (shouldRefresh == true) {
                    findNavController().currentBackStackEntry
                        ?.savedStateHandle
                        ?.remove<Boolean>("owner_products_refresh")
                    viewModel.refresh()
                }
            }

        viewModel.loadInitial()
    }

    private fun setupRecycler() {
        adapter = OwnerProductsAdapter(
            onEditClick = { product ->
                findNavController().navigate(
                    R.id.editProductFragment,
                    bundleOf("productId" to product.id)
                )
            },
            onDeleteClick = { product ->
                viewModel.deleteProduct(product.id)
            }
        )

        binding.recyclerProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerProducts.adapter = adapter
    }

    private fun setupClicks() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAddProduct.setOnClickListener {
            findNavController().navigate(
                R.id.editProductFragment,
                bundleOf("productId" to -1L)
            )
        }

        binding.swipeRefreshProducts.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeVm() {
        viewModel.productsState = { state ->
            when (state) {
                UiState.Idle -> Unit
                UiState.Loading -> {
                    binding.progressProducts.visibility = View.VISIBLE
                    binding.recyclerProducts.visibility = View.GONE
                    binding.tvProductsEmpty.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressProducts.visibility = View.GONE
                    binding.swipeRefreshProducts.isRefreshing = false

                    adapter.submitList(state.data)

                    if (state.data.isEmpty()) {
                        binding.recyclerProducts.visibility = View.GONE
                        binding.tvProductsEmpty.visibility = View.VISIBLE
                    } else {
                        binding.recyclerProducts.visibility = View.VISIBLE
                        binding.tvProductsEmpty.visibility = View.GONE
                    }
                }
                is UiState.Error -> {
                    binding.progressProducts.visibility = View.GONE
                    binding.swipeRefreshProducts.isRefreshing = false
                    binding.recyclerProducts.visibility = View.GONE
                    binding.tvProductsEmpty.visibility = View.VISIBLE
                    binding.tvProductsEmpty.text = state.message}
            }
        }

        viewModel.deleteState = { state ->
            when (state) {
                UiState.Idle -> Unit
                UiState.Loading -> {
                    binding.swipeRefreshProducts.isRefreshing = true
                }
                is UiState.Success -> {
                    binding.swipeRefreshProducts.isRefreshing = false
                    Toast.makeText(requireContext(), "Блюдо удалено", Toast.LENGTH_SHORT).show()
                }
                is UiState.Error -> {
                    binding.swipeRefreshProducts.isRefreshing = false
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        viewModel.productsState = null
        viewModel.deleteState = null
        _binding = null
        super.onDestroyView()
    }
}