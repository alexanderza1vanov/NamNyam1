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
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.data.remote.dto.RestaurantDto
import com.example.namnyam.databinding.FragmentOwnerHomeBinding
import com.example.namnyam.utils.UiState
import java.util.Locale

class OwnerHomeFragment : Fragment(R.layout.fragment_owner_home) {

    private var _binding: FragmentOwnerHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: OwnerHomeViewModel
    private lateinit var ordersAdapter: OwnerOrdersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOwnerHomeBinding.bind(view)

        viewModel = ViewModelProvider(this)[OwnerHomeViewModel::class.java]

        setupToolbar()
        setupOrdersList()
        setupListeners()
        observeViewModel()

        viewModel.loadInitial()
    }

    override fun onResume() {
        super.onResume()

        val handle = findNavController().currentBackStackEntry?.savedStateHandle ?: return
        val created = handle.get<Boolean>("restaurant_created") ?: false

        if (created) {
            handle.remove<Boolean>("restaurant_created")
            Toast.makeText(requireContext(), "Ресторан создан", Toast.LENGTH_SHORT).show()
            viewModel.refreshAll()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Панель владельца"
    }

    private fun setupOrdersList() {
        ordersAdapter = OwnerOrdersAdapter(
            onOrderClick = { order -> openOrderDetails(order.id) },
            onConfirmClick = { order -> viewModel.confirmOrder(order.id) },
            onCookingClick = { order -> viewModel.startCooking(order.id) },
            onReadyClick = { order -> viewModel.markReady(order.id) },
            onCancelClick = { order -> viewModel.cancelOrder(order.id) }
        )

        binding.recyclerOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerOrders.adapter = ordersAdapter
        binding.recyclerOrders.isNestedScrollingEnabled = false
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshAll()
        }

        binding.btnCreateRestaurant.setOnClickListener {
            openCreateRestaurant()
        }
    }

    private fun observeViewModel() {
        viewModel.restaurantState = { state ->
            renderRestaurantState(state)
        }

        viewModel.ordersState = { state ->
            renderOrdersState(state)
        }

        viewModel.actionState = { state ->
            renderActionState(state)
        }
    }

    private fun renderRestaurantState(state: UiState<RestaurantDto>) {
        when (state) {
            UiState.Idle -> Unit

            UiState.Loading -> {
                binding.progressRestaurant.visibility = View.VISIBLE
                binding.cardRestaurant.visibility = View.GONE
                binding.layoutRestaurantEmpty.visibility = View.GONE
            }

            is UiState.Success -> {
                binding.progressRestaurant.visibility = View.GONE
                binding.layoutRestaurantEmpty.visibility = View.GONE
                binding.cardRestaurant.visibility = View.VISIBLE
                setOrdersSectionVisible(true)
                bindRestaurant(state.data)
            }

            is UiState.Error -> {
                binding.progressRestaurant.visibility = View.GONE
                binding.cardRestaurant.visibility = View.GONE
                binding.layoutRestaurantEmpty.visibility = View.VISIBLE
                binding.tvRestaurantEmpty.text =
                    state.message.ifBlank { "У вас пока нет ресторана" }

                ordersAdapter.submitList(emptyList())
                binding.swipeRefresh.isRefreshing = false
                setOrdersSectionVisible(false)
            }
        }
    }

    private fun bindRestaurant(restaurant: RestaurantDto) {
        binding.tvRestaurantName.text = restaurant.name
        binding.tvRestaurantAddress.text = restaurant.address
        binding.tvRestaurantPhone.text = "Телефон: ${restaurant.phone}"

        val metaParts = mutableListOf<String>()

        if (restaurant.isOpen) {
            metaParts.add("Открыт")
        } else {
            metaParts.add("Закрыт")
        }

        val cuisine = restaurant.cuisineType
        if (!cuisine.isNullOrBlank()) {
            metaParts.add(cuisine)
        }

        val deliveryTime = restaurant.deliveryTimeMin
        if (deliveryTime != null) {
            metaParts.add("Доставка ${deliveryTime} мин")
        }

        binding.tvRestaurantMeta.text = metaParts.joinToString(" • ")

        binding.tvRestaurantDelivery.text =
            "Доставка ${formatMoney(restaurant.deliveryFee)} • Мин. заказ ${formatMoney(restaurant.minOrderAmount)}"

        val description = restaurant.description
        binding.tvRestaurantDescription.text =
            if (description.isNullOrBlank()) {
                "Описание отсутствует"
            } else {
                description
            }
    }

    private fun renderOrdersState(state: UiState<List<OrderDto>>) {
        when (state) {
            UiState.Idle -> Unit

            UiState.Loading -> {
                binding.progressOrders.visibility = View.VISIBLE
                binding.recyclerOrders.visibility = View.GONE
                binding.tvOrdersEmpty.visibility = View.GONE
            }

            is UiState.Success -> {
                binding.progressOrders.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false

                val orders = state.data
                ordersAdapter.submitList(orders)

                if (orders.isEmpty()) {
                    binding.recyclerOrders.visibility = View.GONE
                    binding.tvOrdersEmpty.visibility = View.VISIBLE
                    binding.tvOrdersEmpty.text = "Пока нет заказов для этого ресторана"
                } else {
                    binding.recyclerOrders.visibility = View.VISIBLE
                    binding.tvOrdersEmpty.visibility = View.GONE
                }
            }

            is UiState.Error -> {
                binding.progressOrders.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                binding.recyclerOrders.visibility = View.GONE
                binding.tvOrdersEmpty.visibility = View.VISIBLE
                binding.tvOrdersEmpty.text =
                    state.message.ifBlank { "Не удалось загрузить заказы" }
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
                    "Статус заказа обновлён",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is UiState.Error -> {
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(
                    requireContext(),
                    state.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setOrdersSectionVisible(visible: Boolean) {
        val sectionVisibility = if (visible) View.VISIBLE else View.GONE
        binding.tvOrdersTitle.visibility = sectionVisibility

        if (!visible) {
            binding.progressOrders.visibility = View.GONE
            binding.recyclerOrders.visibility = View.GONE
            binding.tvOrdersEmpty.visibility = View.GONE
        }
    }

    private fun openCreateRestaurant() {
        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.ownerHomeFragment) return

        navController.navigate(R.id.action_ownerHomeFragment_to_createRestaurantFragment)
    }

    private fun openOrderDetails(orderId: Long) {
        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.ownerHomeFragment) return

        navController.navigate(
            R.id.orderDetailsFragment,
            bundleOf("orderId" to orderId)
        )
    }

    private fun formatMoney(value: Double): String {
        return String.format(Locale.getDefault(), "%.0f ₽", value)
    }

    override fun onDestroyView() {
        viewModel.restaurantState = null
        viewModel.ordersState = null
        viewModel.actionState = null
        _binding = null
        super.onDestroyView()
    }
    private var currentRestaurant: RestaurantDto? = null

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshAll()
        }

        binding.btnCreateRestaurant.setOnClickListener {
            findNavController().navigate(R.id.createRestaurantFragment)
        }

        binding.btnEditRestaurant.setOnClickListener {
            findNavController().navigate(R.id.editRestaurantFragment)
        }

        binding.btnManageProducts.setOnClickListener {
            findNavController().navigate(R.id.ownerProductsFragment)
        }

        binding.btnToggleRestaurant.setOnClickListener {
            val restaurant = currentRestaurant ?: return@setOnClickListener
            if (restaurant.isOpen) {
                viewModel.closeRestaurant()
            } else {
                viewModel.openRestaurant()
            }
        }
    }

    private fun bindRestaurant(restaurant: RestaurantDto) {
        currentRestaurant = restaurant

        binding.tvRestaurantName.text = restaurant.name
        binding.tvRestaurantAddress.text = restaurant.address
        binding.tvRestaurantPhone.text = "Телефон: ${restaurant.phone}"

        val metaParts = mutableListOf<String>()
        metaParts.add(if (restaurant.isOpen) "Открыт" else "Закрыт")

        restaurant.cuisineType?.takeIf { it.isNotBlank() }?.let {
            metaParts.add(it)
        }

        restaurant.deliveryTimeMin?.let {
            metaParts.add("Доставка $it мин")
        }

        binding.tvRestaurantMeta.text = metaParts.joinToString(" • ")
        binding.tvRestaurantDelivery.text =
            "Доставка ${formatMoney(restaurant.deliveryFee)} • Мин. заказ ${formatMoney(restaurant.minOrderAmount)}"

        binding.tvRestaurantDescription.text =
            restaurant.description?.takeIf { it.isNotBlank() } ?: "Описание отсутствует"

        binding.btnToggleRestaurant.text =
            if (restaurant.isOpen) "Закрыть ресторан" else "Открыть ресторан"
    }
}