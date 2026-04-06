package com.example.namnyam.ui.client

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namnyam.R
import com.example.namnyam.data.cart.CartManager
import com.example.namnyam.data.remote.dto.ProductDto
import com.example.namnyam.databinding.FragmentRestaurantDetailsBinding
import com.example.namnyam.utils.UiState

class RestaurantDetailsFragment : Fragment(R.layout.fragment_restaurant_details) {

    private var _binding: FragmentRestaurantDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RestaurantDetailsViewModel
    private lateinit var adapter: ProductAdapter
    private lateinit var cartManager: CartManager

    private var restaurantId: Long = -1L
    private var restaurantName: String = ""
    private var restaurantDescription: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRestaurantDetailsBinding.bind(view)

        restaurantId = arguments?.getLong("restaurantId") ?: -1L
        restaurantName = arguments?.getString("restaurantName").orEmpty()
        restaurantDescription = arguments?.getString("restaurantDescription").orEmpty()

        viewModel = ViewModelProvider(this)[RestaurantDetailsViewModel::class.java]
        cartManager = CartManager.getInstance()

        binding.toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back_24)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_cart -> {
                    findNavController().navigate(R.id.action_restaurantDetailsFragment_to_cartFragment)
                    true
                }
                else -> false
            }
        }

        binding.tvRestaurantName.text = restaurantName
        binding.tvRestaurantDescription.text =
            if (restaurantDescription.isBlank()) "Лучшие блюда ресторана"
            else restaurantDescription

        adapter = ProductAdapter { product ->
            cartManager.add(product.toCartItem())
            updateMiniCart()
        }

        binding.recyclerProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerProducts.adapter = adapter

        binding.miniCart.root.setOnClickListener {
            findNavController().navigate(R.id.action_restaurantDetailsFragment_to_cartFragment)
        }

        viewModel.state = { state ->
            when (state) {
                UiState.Idle -> Unit

                UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerProducts.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(state.data)

                    if (state.data.isEmpty()) {
                        binding.recyclerProducts.visibility = View.GONE
                        binding.tvEmpty.visibility = View.VISIBLE
                    } else {
                        binding.recyclerProducts.visibility = View.VISIBLE
                        binding.tvEmpty.visibility = View.GONE
                    }
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerProducts.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.tvEmpty.text = "Не удалось загрузить меню"
                }
            }
        }

        updateMiniCart()

        if (restaurantId != -1L) {
            viewModel.loadProducts(restaurantId)
        }
    }

    private fun updateMiniCart() {
        val count = cartManager.getTotalCount()
        val total = cartManager.getTotalPrice()

        if (count > 0) {
            binding.miniCart.root.visibility = View.VISIBLE
            binding.miniCart.tvCartCount.text = "$count шт."
            binding.miniCart.tvCartTotal.text = "${total.toInt()} ₽"
        } else {
            binding.miniCart.root.visibility = View.GONE
        }
    }

    private fun ProductDto.toCartItem() = com.example.namnyam.data.cart.CartItem(
        productId = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl,
        weightGrams = weightGrams,
        quantity = 1
    )

    override fun onResume() {
        super.onResume()
        updateMiniCart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}