package com.example.namnyam.ui.client

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namnyam.R
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.databinding.FragmentOrdersHistoryBinding
import com.example.namnyam.utils.UiState

class OrdersHistoryFragment : Fragment(R.layout.fragment_orders_history) {

    private var _binding: FragmentOrdersHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: OrdersHistoryViewModel
    private lateinit var adapter: OrdersHistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrdersHistoryBinding.bind(view)

        viewModel = ViewModelProvider(this)[OrdersHistoryViewModel::class.java]

        binding.toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back_24)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        adapter = OrdersHistoryAdapter(
            onOrderClick = { order ->
                val bundle = Bundle().apply {
                    putLong("orderId", order.id)
                }
                findNavController().navigate(
                    R.id.action_ordersHistoryFragment_to_orderDetailsFragment,
                    bundle
                )
            },
            statusFormatter = { status -> viewModel.getStatusText(status) },
            dateFormatter = { date -> viewModel.formatCreatedAt(date) },
            itemsFormatter = { order -> viewModel.getItemsText(order) }
        )

        binding.recyclerOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerOrders.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadOrders()
        }

        binding.btnRetry.setOnClickListener {
            viewModel.loadOrders()
        }

        viewModel.state = { state ->
            when (state) {
                UiState.Idle -> Unit

                UiState.Loading -> {
                    if (!binding.swipeRefresh.isRefreshing) {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    binding.recyclerOrders.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                    binding.layoutError.visibility = View.GONE
                }

                is UiState.Success<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val orders = state.data as List<OrderDto>

                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.layoutError.visibility = View.GONE

                    adapter.submitList(orders)

                    if (orders.isEmpty()) {
                        binding.recyclerOrders.visibility = View.GONE
                        binding.tvEmpty.visibility = View.VISIBLE
                    } else {
                        binding.recyclerOrders.visibility = View.VISIBLE
                        binding.tvEmpty.visibility = View.GONE
                    }
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.recyclerOrders.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                    binding.layoutError.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                }
            }
        }

        viewModel.loadOrders()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}