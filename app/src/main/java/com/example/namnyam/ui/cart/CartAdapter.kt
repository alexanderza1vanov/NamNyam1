package com.example.namnyam.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.namnyam.data.cart.CartItem
import com.example.namnyam.databinding.ItemCartBinding

class CartAdapter(
    private val onPlusClick: (CartItem) -> Unit,
    private val onMinusClick: (CartItem) -> Unit,
    private val onRemoveClick: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.tvProductName.text = item.name
            binding.tvDescription.text = item.description ?: "Описание отсутствует"
            binding.tvWeight.text = item.weightGrams?.let { "$it г" } ?: ""
            binding.tvPrice.text = "${item.price.toInt()} ₽"
            binding.tvQuantity.text = item.quantity.toString()
            binding.tvTotal.text = "${item.totalPrice().toInt()} ₽"

            binding.btnPlus.setOnClickListener { onPlusClick(item) }
            binding.btnMinus.setOnClickListener { onMinusClick(item) }
            binding.btnRemove.setOnClickListener { onRemoveClick(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}