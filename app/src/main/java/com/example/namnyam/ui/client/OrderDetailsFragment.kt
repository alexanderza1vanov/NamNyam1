package com.example.namnyam.ui.client

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namnyam.R
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.databinding.FragmentOrderDetailsBinding
import com.example.namnyam.utils.UiState

class OrderDetailsFragment : Fragment(R.layout.fragment_order_details) {

    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: OrderDetailsViewModel
    private lateinit var adapter: OrderItemsAdapter

    private var orderId: Long = -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrderDetailsBinding.bind(view)

        orderId = arguments?.getLong("orderId") ?: -1L

        viewModel = ViewModelProvider(this)[OrderDetailsViewModel::class.java]

        binding.toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back_24)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        adapter = OrderItemsAdapter()
        binding.recyclerOrderItems.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerOrderItems.adapter = adapter

        binding.btnRefresh.setOnClickListener {
            if (orderId != -1L) {
                viewModel.loadOrder(orderId)
            }
        }

        viewModel.state = { state ->
            when (state) {
                UiState.Idle -> Unit

                UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentScroll.visibility = View.GONE
                    binding.layoutError.visibility = View.GONE
                }

                is UiState.Success<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val order = state.data as OrderDto

                    binding.progressBar.visibility = View.GONE
                    binding.contentScroll.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE

                    renderOrder(order)
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentScroll.visibility = View.GONE
                    binding.layoutError.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                }
            }
        }

        if (orderId != -1L) {
            viewModel.loadOrder(orderId)
        }
    }

    private fun renderOrder(order: OrderDto) {
        binding.tvOrderNumber.text = "Заказ №${order.id}"
        binding.tvOrderStatus.text = viewModel.getStatusText(order.status)
        binding.tvStatusDescription.text = viewModel.getStatusDescription(order.status)

        binding.tvCreatedAt.text = viewModel.formatCreatedAt(order.createdAt)
        binding.tvDeliveryAddress.text = order.deliveryAddress
        binding.tvComment.text =
            if (order.comment.isNullOrBlank()) "Без комментария"
            else order.comment

        binding.tvDeliveryFee.text = "${order.deliveryFee.toInt()} ₽"
        binding.tvTotalPrice.text = "${order.totalPrice.toInt()} ₽"

        adapter.submitList(order.items)

        val step = viewModel.getProgressStep(order.status)
        renderProgress(step)
    }

    private fun renderProgress(step: Int) {
        val activeColor = ContextCompat.getColor(requireContext(), R.color.purple_500)
        val inactiveColor = ContextCompat.getColor(requireContext(), android.R.color.darker_gray)

        binding.step1.backgroundTintList = android.content.res.ColorStateList.valueOf(
            if (step >= 1) activeColor else inactiveColor
        )
        binding.step2.backgroundTintList = android.content.res.ColorStateList.valueOf(
            if (step >= 3) activeColor else inactiveColor
        )
        binding.step3.backgroundTintList = android.content.res.ColorStateList.valueOf(
            if (step >= 5) activeColor else inactiveColor
        )
        binding.step4.backgroundTintList = android.content.res.ColorStateList.valueOf(
            if (step >= 8) activeColor else inactiveColor
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}