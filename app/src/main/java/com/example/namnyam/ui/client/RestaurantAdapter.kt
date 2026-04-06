package com.example.namnyam.ui.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.namnyam.data.remote.dto.RestaurantDto
import com.example.namnyam.databinding.ItemRestaurantBinding

class RestaurantAdapter(
    private val onClick: (RestaurantDto) -> Unit
) : ListAdapter<RestaurantDto, RestaurantAdapter.RestaurantViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RestaurantViewHolder(
        private val binding: ItemRestaurantBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RestaurantDto) {
            binding.tvName.text = item.name
            binding.tvDescription.text = item.description ?: "Описание отсутствует"
            binding.tvDelivery.text =
                "Доставка ${item.deliveryTimeMin ?: 0} мин • ${item.deliveryFee.toInt()} ₽"
            binding.tvMinOrder.text = "Мин. заказ ${item.minOrderAmount.toInt()} ₽"

            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<RestaurantDto>() {
        override fun areItemsTheSame(oldItem: RestaurantDto, newItem: RestaurantDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RestaurantDto, newItem: RestaurantDto): Boolean {
            return oldItem == newItem
        }
    }
}