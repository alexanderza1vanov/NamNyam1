package com.example.namnyam.ui.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.namnyam.data.remote.dto.ProductDto
import com.example.namnyam.databinding.ItemProductBinding

class ProductAdapter(
    private val onAddClick: (ProductDto) -> Unit
) : ListAdapter<ProductDto, ProductAdapter.ProductViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductDto) {
            binding.tvName.text = item.name
            binding.tvDescription.text = item.description ?: "Описание отсутствует"
            binding.tvPrice.text = "${item.price.toInt()} ₽"
            binding.tvWeight.text = item.weightGrams?.let { "$it г" } ?: ""

            binding.btnAddToCart.setOnClickListener {
                onAddClick(item)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ProductDto>() {
        override fun areItemsTheSame(oldItem: ProductDto, newItem: ProductDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductDto, newItem: ProductDto): Boolean {
            return oldItem == newItem
        }
    }
}