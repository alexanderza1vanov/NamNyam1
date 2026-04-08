package com.example.namnyam.ui.courier

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namnyam.R
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.databinding.FragmentCourierOrdersListBinding
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class CourierMyOrdersFragment : Fragment(R.layout.fragment_courier_orders_list) {

    private var _binding: FragmentCourierOrdersListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CourierOrdersViewModel
    private lateinit var adapter: CourierOrdersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCourierOrdersListBinding.bind(view)

        viewModel = ViewModelProvider(requireParentFragment())[CourierOrdersViewModel::class.java]

        setupList()
        observeData()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshMyOrders()
        }

        viewModel.loadMyOrders()
    }

    private fun setupList() {
        adapter = CourierOrdersAdapter(
            onOrderClick = { order ->
                openOrderDetails(order.id)
            },
            onMainActionClick = { order ->
                when (order.status) {
                    "ASSIGNED_TO_COURIER" -> viewModel.pickUpOrder(order.id)
                    "PICKED_UP" -> viewModel.moveOnTheWay(order.id)
                    "ON_THE_WAY" -> viewModel.deliverOrder(order.id)
                }
            },
            onSecondaryActionClick = { order ->
                if (order.status in setOf("ASSIGNED_TO_COURIER", "PICKED_UP", "ON_THE_WAY")) {
                    viewModel.failOrder(order.id)
                }
            }
        )

        binding.recyclerOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerOrders.adapter = adapter
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.myOrdersState.collect { state ->
                        renderOrdersState(state)
                    }
                }

                launch {
                    viewModel.actionEvents.collect { event ->
                        when (event) {
                            CourierActionEvent.Loading -> {
                                binding.swipeRefresh.isRefreshing = true
                            }
                            is CourierActionEvent.Success -> {
                                binding.swipeRefresh.isRefreshing = false
                                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                            }
                            is CourierActionEvent.Error -> {
                                binding.swipeRefresh.isRefreshing = false
                                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun renderOrdersState(state: UiState<List<OrderDto>>) {
        when (state) {
            UiState.Idle -> Unit

            UiState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerOrders.visibility = View.GONE
                binding.tvEmpty.visibility = View.GONE
            }

            is UiState.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false

                val orders = state.data
                adapter.submitList(orders)

                if (orders.isEmpty()) {
                    binding.recyclerOrders.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.tvEmpty.text = "У вас пока нет активных доставок"
                } else {
                    binding.recyclerOrders.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                }
            }

            is UiState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                binding.recyclerOrders.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
                binding.tvEmpty.text = state.message.ifBlank {
                    "Не удалось загрузить мои доставки"
                }
            }
        }
    }

    private fun openOrderDetails(orderId: Long) {
        findNavController().navigate(
            R.id.orderDetailsFragment,
            bundleOf("orderId" to orderId)
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}