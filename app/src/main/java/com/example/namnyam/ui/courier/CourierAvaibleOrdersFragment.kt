package com.example.namnyam.ui.courier

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namnyam.R
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.databinding.FragmentCourierOrdersListBinding
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class CourierAvailableOrdersFragment : Fragment(R.layout.fragment_courier_orders_list) {

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
            viewModel.refreshAvailableOrders()
        }

        viewModel.loadAvailableOrders()
    }

    private fun setupList() {
        adapter = CourierOrdersAdapter(
            onOrderClick = {},
            onMainActionClick = { order ->
                if (order.status == "READY_FOR_DELIVERY" && order.courierId == null) {
                    viewModel.takeOrder(order.id)
                }
            },
            onSecondaryActionClick = {}
        )

        binding.recyclerOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerOrders.adapter = adapter
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.availableOrdersState.collect { state ->
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
                    binding.tvEmpty.text = "Сейчас нет свободных заказов"
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
                    "Не удалось загрузить свободные заказы"
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}