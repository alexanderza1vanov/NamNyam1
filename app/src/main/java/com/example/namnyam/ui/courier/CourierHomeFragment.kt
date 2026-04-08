package com.example.namnyam.ui.courier

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namnyam.R
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.databinding.FragmentCourierHomeBinding
import com.example.namnyam.utils.UiState
import java.util.Locale

class CourierHomeFragment : Fragment(R.layout.fragment_courier_home) {

    private var _binding: FragmentCourierHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CourierHomeViewModel
    private lateinit var ordersAdapter: CourierOrdersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCourierHomeBinding.bind(view)

        viewModel = ViewModelProvider(this)[CourierHomeViewModel::class.java]

        setupToolbar()
        setupList()
        setupListeners()
        observeViewModel()

        viewModel.loadInitial()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Доставки"
    }

    private fun setupList() {
        ordersAdapter = CourierOrdersAdapter(
            onOrderClick = { order -> openOrderDetails(order.id) },
            onMainActionClick = { order -> handleMainAction(order) },
            onSecondaryActionClick = { order -> handleSecondaryAction(order) }
        )

        binding.recyclerOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerOrders.adapter = ordersAdapter
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshAll()
        }
    }

    private fun observeViewModel() {
        viewModel.ordersState = { state ->
            renderOrdersState(state)
        }

        viewModel.actionState = { state ->
            renderActionState(state)
        }
    }

    private fun renderOrdersState(state: UiState<List<OrderDto>>) {
        when (state) {
            UiState.Idle -> Unit

            UiState.Loading -> {
                binding.progressOrders.visibility = View.VISIBLE
                binding.recyclerOrders.visibility = View.GONE
                binding.tvEmpty.visibility = View.GONE
            }

            is UiState.Success -> {
                binding.progressOrders.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false

                val orders = state.data
                ordersAdapter.submitList(orders)

                if (orders.isEmpty()) {
                    binding.recyclerOrders.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.tvEmpty.text = "У вас пока нет назначенных доставок"
                } else {
                    binding.recyclerOrders.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                }
            }

            is UiState.Error -> {
                binding.progressOrders.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                binding.recyclerOrders.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
                binding.tvEmpty.text = state.message.ifBlank { "Не удалось загрузить доставки" }
            }
        }
    }

    private fun renderActionState(state: UiState<OrderDto>) {
        when (state) {
            UiState.Idle -> Unit

            UiState.Loading -> {
                binding.swipeRefresh.isRefreshing = true
            }

            is UiState.Success -> {
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(
                    requireContext(),
                    "Статус доставки обновлён",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is UiState.Error -> {
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(
                    requireContext(),
                    state.message.ifBlank { "Не удалось обновить статус" },
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handleMainAction(order: OrderDto) {
        when (order.status) {
            "READY_FOR_DELIVERY" -> viewModel.pickUpOrder(order.id)
            "PICKED_UP" -> viewModel.moveOnTheWay(order.id)
            "ON_THE_WAY" -> viewModel.deliverOrder(order.id)
        }
    }

    private fun handleSecondaryAction(order: OrderDto) {
        if (order.status == "ON_THE_WAY") {
            viewModel.failOrder(order.id)
        }
    }

    private fun openOrderDetails(orderId: Long) {
        findNavController().navigate(
            R.id.orderDetailsFragment,
            bundleOf("orderId" to orderId)
        )
    }

    fun formatMoney(value: Double): String {
        return String.format(Locale.getDefault(), "%.0f ₽", value)
    }

    override fun onDestroyView() {
        viewModel.ordersState = null
        viewModel.actionState = null
        _binding = null
        super.onDestroyView()
    }
}