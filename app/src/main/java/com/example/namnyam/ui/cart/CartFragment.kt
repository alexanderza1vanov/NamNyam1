package com.example.namnyam.ui.cart

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namnyam.R
import com.example.namnyam.databinding.FragmentCartBinding
import com.example.namnyam.ui.client.CartAdapter
import com.example.namnyam.ui.client.CartItemUi
import com.example.namnyam.ui.client.CartStore
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CartFragment : Fragment(R.layout.fragment_cart) {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CartAdapter

    private val cartItems = mutableListOf<CartItemUi>()
    private var deliveryFee: Double = 199.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCartBinding.bind(view)

        deliveryFee = arguments?.getDouble("deliveryFee") ?: 199.0

        setupToolbar()
        setupRecycler()
        setupActions()
        loadCart()
        renderCart()
    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back_24)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecycler() {
        adapter = CartAdapter(
            onPlusClick = { item -> increaseQuantity(item) },
            onMinusClick = { item -> decreaseQuantity(item) },
            onDeleteClick = { item -> confirmDelete(item) }
        )

        binding.recyclerCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCart.adapter = adapter
    }

    private fun setupActions() {
        binding.btnCheckout.setOnClickListener {
            openCheckout()
        }
    }

    private fun loadCart() {
        cartItems.clear()
        cartItems.addAll(CartStore.load(requireContext()))
    }

    private fun saveCart() {
        CartStore.save(requireContext(), cartItems)
    }

    private fun increaseQuantity(item: CartItemUi) {
        val index = cartItems.indexOfFirst { it.productId == item.productId }
        if (index == -1) return

        val old = cartItems[index]
        cartItems[index] = old.copy(quantity = old.quantity + 1)

        saveCart()
        renderCart()
    }

    private fun decreaseQuantity(item: CartItemUi) {
        val index = cartItems.indexOfFirst { it.productId == item.productId }
        if (index == -1) return

        val old = cartItems[index]
        if (old.quantity <= 1) {
            cartItems.removeAt(index)
        } else {
            cartItems[index] = old.copy(quantity = old.quantity - 1)
        }

        saveCart()
        renderCart()
    }

    private fun confirmDelete(item: CartItemUi) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить товар?")
            .setMessage("Удалить \"${item.name}\" из корзины?")
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Удалить") { _, _ ->
                deleteItem(item)
            }
            .show()
    }

    private fun deleteItem(item: CartItemUi) {
        val index = cartItems.indexOfFirst { it.productId == item.productId }
        if (index == -1) return

        cartItems.removeAt(index)
        saveCart()
        renderCart()

        Toast.makeText(requireContext(), "Товар удалён", Toast.LENGTH_SHORT).show()
    }

    private fun renderCart() {
        adapter.submitList(cartItems.toList())

        val itemsTotal = cartItems.sumOf { it.totalPrice }
        val finalTotal = if (cartItems.isEmpty()) 0.0 else itemsTotal + deliveryFee

        binding.tvItemsTotal.text = "${itemsTotal.toInt()} ₽"
        binding.tvDeliveryFee.text = if (cartItems.isEmpty()) "0 ₽" else "${deliveryFee.toInt()} ₽"
        binding.tvFinalTotal.text = "${finalTotal.toInt()} ₽"

        val isEmpty = cartItems.isEmpty()
        binding.recyclerCart.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.layoutSummary.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.tvEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.btnCheckout.isEnabled = !isEmpty
    }

    private fun openCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Корзина пуста", Toast.LENGTH_SHORT).show()
            return
        }

        val resolvedRestaurantId = cartItems.firstOrNull()?.restaurantId ?: -1L
        if (resolvedRestaurantId == -1L) {
            Toast.makeText(requireContext(), "Не удалось определить ресторан", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val items = ArrayList(cartItems)
        val totalPrice = cartItems.sumOf { it.totalPrice }

        val bundle = Bundle().apply {
            putLong("restaurantId", resolvedRestaurantId)
            putDouble("totalPrice", totalPrice)
            putDouble("deliveryFee", deliveryFee)
            putParcelableArrayList("cartItems", items)
        }

        findNavController().navigate(R.id.checkoutFragment, bundle)
    }

    override fun onResume() {
        super.onResume()
        loadCart()
        renderCart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}