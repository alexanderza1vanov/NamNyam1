package com.example.namnyam.ui.checkout

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.namnyam.R
import com.example.namnyam.data.remote.dto.DeliveryAddressDto
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.databinding.FragmentCheckoutBinding
import com.example.namnyam.ui.client.CartItemUi
import com.example.namnyam.utils.UiState

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CheckoutViewModel

    private var restaurantId: Long = -1L
    private var cartItems: ArrayList<CartItemUi> = arrayListOf()
    private var selectedAddress: DeliveryAddressDto? = null
    private var addresses: List<DeliveryAddressDto> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCheckoutBinding.bind(view)

        restaurantId = arguments?.getLong("restaurantId") ?: -1L
        cartItems = arguments?.getParcelableArrayList("cartItems") ?: arrayListOf()

        viewModel = ViewModelProvider(
            this,
            CheckoutViewModelFactory(requireContext())
        )[CheckoutViewModel::class.java]

        setupUi()
        observeViewModel()
        renderCartSummary()
        viewModel.loadAddresses()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAddresses()
    }

    private fun setupUi() {
        binding.toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back_24)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCreateOrder.setOnClickListener {
            submitOrder()
        }

        binding.btnAddAddress.setOnClickListener {
            findNavController().navigate(R.id.addAddressFragment)
        }

        binding.btnOpenAddresses.setOnClickListener {
            findNavController().navigate(R.id.addressesFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.addressesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Idle -> Unit

                UiState.Loading -> {
                    binding.progressAddresses.visibility = View.VISIBLE
                    binding.layoutAddressContent.visibility = View.GONE
                    binding.tvAddressError.visibility = View.GONE
                    binding.btnCreateOrder.isEnabled = false
                }

                is UiState.Success -> {
                    binding.progressAddresses.visibility = View.GONE
                    binding.layoutAddressContent.visibility = View.VISIBLE
                    binding.tvAddressError.visibility = View.GONE

                    addresses = state.data
                    bindAddresses(state.data)
                }

                is UiState.Error -> {
                    binding.progressAddresses.visibility = View.GONE
                    binding.layoutAddressContent.visibility = View.GONE
                    binding.tvAddressError.visibility = View.VISIBLE
                    binding.tvAddressError.text =
                        state.message.ifBlank { "Не удалось загрузить адреса доставки" }

                    selectedAddress = null
                    addresses = emptyList()
                    binding.btnCreateOrder.isEnabled = false

                    Toast.makeText(
                        requireContext(),
                        "Не удалось загрузить адреса. Добавьте адрес или проверьте сервер.",
                        Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.createOrderState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Idle -> Unit

                UiState.Loading -> {
                    binding.btnCreateOrder.isEnabled = false
                    binding.btnCreateOrder.text = "Оформляем..."
                }

                is UiState.Success -> {
                    binding.btnCreateOrder.isEnabled = true
                    binding.btnCreateOrder.text = "Оформить заказ"
                    openOrderDetails(state.data)
                    viewModel.resetCreateOrderState()
                }

                is UiState.Error -> {
                    binding.btnCreateOrder.isEnabled = true
                    binding.btnCreateOrder.text = "Оформить заказ"

                    Toast.makeText(
                        requireContext(),
                        state.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun bindAddresses(list: List<DeliveryAddressDto>) {
        if (list.isEmpty()) {
            selectedAddress = null
            binding.tvSelectedAddress.text = "У вас пока нет сохранённых адресов"
            binding.spinnerAddresses.visibility = View.GONE
            binding.btnCreateOrder.isEnabled = false
            return
        }

        binding.spinnerAddresses.visibility = View.VISIBLE
        binding.btnCreateOrder.isEnabled = true

        val titles = list.map { address ->
            "${address.title} — ${address.addressLine}"
        }

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            titles
        )
        binding.spinnerAddresses.adapter = spinnerAdapter

        val defaultAddress = list.firstOrNull { it.isDefault } ?: list.first()
        selectedAddress = defaultAddress
        binding.tvSelectedAddress.text = "${defaultAddress.title}\n${defaultAddress.addressLine}"

        val selectedIndex = list.indexOfFirst { it.id == defaultAddress.id }
        if (selectedIndex >= 0) {
            binding.spinnerAddresses.setSelection(selectedIndex, false)
        }

        binding.spinnerAddresses.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val address = list[position]
                    selectedAddress = address
                    binding.tvSelectedAddress.text = "${address.title}\n${address.addressLine}"
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
    }

    private fun renderCartSummary() {
        val itemsCount = cartItems.sumOf { it.quantity }
        val total = cartItems.sumOf { it.totalPrice }

        binding.tvItemsCount.text = "Товаров: $itemsCount"
        binding.tvTotalPrice.text = "Итого: %.0f ₽".format(total)
    }

    private fun submitOrder() {
        val address = selectedAddress

        if (restaurantId == -1L) {
            Toast.makeText(requireContext(), "Некорректный ресторан", Toast.LENGTH_SHORT).show()
            return
        }

        if (address == null) {
            Toast.makeText(requireContext(), "Выберите адрес доставки", Toast.LENGTH_SHORT).show()
            return
        }

        if (cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Корзина пуста", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.createOrder(
            restaurantId = restaurantId,
            addressId = address.id,
            comment = binding.etComment.text?.toString()?.trim(),
            cartItems = cartItems
        )
    }

    private fun openOrderDetails(order: OrderDto) {
        findNavController().navigate(
            R.id.orderDetailsFragment,
            bundleOf("orderId" to order.id)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}