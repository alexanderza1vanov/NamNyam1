package com.example.namnyam.ui.owner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.namnyam.data.remote.dto.ProductDto
import com.example.namnyam.databinding.ItemOwnerProductBinding
import java.util.Locale

class OwnerProductsAdapter(
    private val onEditClick: (ProductDto) -> Unit,
    private val onDeleteClick: (ProductDto) -> Unit
) : ListAdapter<ProductDto, OwnerProductsAdapter.ProductViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemOwnerProductBinding.inflate(
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
        private val binding: ItemOwnerProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductDto) {
            binding.tvProductName.text = item.name
            binding.tvProductPrice.text = formatMoney(item.price)

            val meta = buildList {
                item.weightGrams?.let { add("${it} г") }
                add(if (item.isAvailable) "Доступно" else "Недоступно")
            }.joinToString(" • ")

            binding.tvProductMeta.text = meta
            binding.tvProductDescription.text = item.description ?: "Без описания"
            binding.tvProductIngredients.text = item.ingredients ?: "Ингредиенты не указаны"

            binding.btnEditProduct.setOnClickListener { onEditClick(item) }
            binding.btnDeleteProduct.setOnClickListener { onDeleteClick(item) }

            binding.btnToggleAvailability.text =
                if (item.isAvailable) "Скрыть" else "Показать"
            binding.btnToggleAvailability.visibility = View.GONE
        }
    }

    private fun formatMoney(value: Double): String {
        return String.format(Locale.getDefault(), "%.0f ₽", value)
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