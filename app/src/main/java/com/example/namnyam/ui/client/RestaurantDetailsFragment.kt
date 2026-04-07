package com.example.namnyam.ui.client

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namnyam.R
import com.example.namnyam.data.remote.dto.ProductDto
import com.example.namnyam.databinding.FragmentRestaurantDetailsBinding
import com.example.namnyam.utils.UiState

class RestaurantDetailsFragment : Fragment(R.layout.fragment_restaurant_details) {

    private var _binding: FragmentRestaurantDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RestaurantDetailsViewModel
    private lateinit var adapter: ProductAdapter

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

                R.id.action_profile -> {
                    findNavController().navigate(R.id.profileFragment)
                    true
                }

                else -> false
            }
        }

        binding.tvRestaurantName.text = restaurantName
        binding.tvRestaurantDescription.text =
            if (restaurantDescription.isBlank()) {
                "Лучшие блюда ресторана"
            } else {
                restaurantDescription
            }

        adapter = ProductAdapter { product ->
            addProductToCart(product)
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

                is UiState.Success<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val products = state.data as List<ProductDto>

                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(products)

                    if (products.isEmpty()) {
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

    private fun addProductToCart(product: ProductDto) {
        val currentItems = CartStore.load(requireContext())

        val hasAnotherRestaurant = currentItems.any { it.restaurantId != restaurantId }
        if (hasAnotherRestaurant) {
            Toast.makeText(
                requireContext(),
                "В корзине уже есть товары из другого ресторана",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val index = currentItems.indexOfFirst { it.productId == product.id }

        if (index >= 0) {
            val oldItem = currentItems[index]
            currentItems[index] = oldItem.copy(quantity = oldItem.quantity + 1)
        } else {
            currentItems.add(
                CartItemUi(
                    productId = product.id,
                    restaurantId = restaurantId,
                    name = product.name,
                    price = product.price,
                    quantity = 1,
                    imageUrl = product.imageUrl?.trim()
                )
            )
        }

        CartStore.save(requireContext(), currentItems)
        updateMiniCart()

        Toast.makeText(
            requireContext(),
            "Добавлено: ${product.name}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateMiniCart() {
        val items = CartStore.load(requireContext())
        val count = items.sumOf { it.quantity }
        val total = items.sumOf { it.totalPrice }

        if (count > 0) {
            binding.miniCart.root.visibility = View.VISIBLE
            binding.miniCart.tvCartCount.text = "$count шт."
            binding.miniCart.tvCartTotal.text = "${total.toInt()} ₽"
        } else {
            binding.miniCart.root.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        updateMiniCart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}