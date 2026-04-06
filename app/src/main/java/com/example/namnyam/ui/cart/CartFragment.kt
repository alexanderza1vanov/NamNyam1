package com.example.namnyam.ui.cart

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namnyam.R
import com.example.namnyam.databinding.FragmentCartBinding

class CartFragment : Fragment(R.layout.fragment_cart) {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CartViewModel
    private lateinit var adapter: CartAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCartBinding.bind(view)

        viewModel = ViewModelProvider(this)[CartViewModel::class.java]

        binding.toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back_24)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        adapter = CartAdapter(
            onPlusClick = { item -> viewModel.increase(item.productId) },
            onMinusClick = { item -> viewModel.decrease(item.productId) },
            onRemoveClick = { item -> viewModel.remove(item.productId) }
        )

        binding.recyclerCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCart.adapter = adapter

        viewModel.state = { items, totalPrice, totalCount ->
            adapter.submitList(items)

            binding.tvTotalItems.text = "Товаров: $totalCount"
            binding.tvTotalPrice.text = "${totalPrice.toInt()} ₽"

            if (items.isEmpty()) {
                binding.recyclerCart.visibility = View.GONE
                binding.layoutBottom.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.recyclerCart.visibility = View.VISIBLE
                binding.layoutBottom.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
            }
        }

        binding.btnCheckout.setOnClickListener {
            // Пока просто переход-заглушка.
            // Следующим сообщением сделаем OrderCreateFragment / CheckoutFragment.
            findNavController().navigateUp()
        }

        viewModel.loadCart()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}