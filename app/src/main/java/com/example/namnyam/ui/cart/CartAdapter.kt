package com.example.namnyam.ui.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.namnyam.databinding.ItemCartBinding

class CartAdapter(
    private val onPlusClick: (CartItemUi) -> Unit,
    private val onMinusClick: (CartItemUi) -> Unit,
    private val onDeleteClick: (CartItemUi) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val items = mutableListOf<CartItemUi>()

    fun submitList(newItems: List<CartItemUi>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItemUi) {
            binding.tvProductName.text = item.productName
            binding.tvProductPrice.text = "${item.productPrice.toInt()} ₽"
            binding.tvQuantity.text = item.quantity.toString()
            binding.tvItemTotal.text = "${item.totalPrice.toInt()} ₽"

            binding.btnPlus.setOnClickListener {
                onPlusClick(item)
            }

            binding.btnMinus.setOnClickListener {
                onMinusClick(item)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }
}