package com.example.namnyam.ui.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.namnyam.data.remote.dto.DeliveryAddressDto
import com.example.namnyam.databinding.ItemAddressBinding

class AdressesAdapter(
    private val onDeleteClick: (DeliveryAddressDto) -> Unit
) : RecyclerView.Adapter<AdressesAdapter.AddressViewHolder>() {

    private val items = mutableListOf<DeliveryAddressDto>()

    fun submitList(list: List<DeliveryAddressDto>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class AddressViewHolder(
        private val binding: ItemAddressBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DeliveryAddressDto) {
            binding.tvTitle.text = item.title
            binding.tvAddress.text = buildAddressText(item)

            binding.tvDefault.visibility =
                if (item.isDefault) android.view.View.VISIBLE else android.view.View.GONE

            binding.btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }

        private fun buildAddressText(item: DeliveryAddressDto): String {
            val parts = mutableListOf<String>()
            parts += item.addressLine

            item.entrance?.takeIf { it.isNotBlank() }?.let { parts += "Подъезд $it" }
            item.floor?.takeIf { it.isNotBlank() }?.let { parts += "Этаж $it" }
            item.apartment?.takeIf { it.isNotBlank() }?.let { parts += "Кв. $it" }
            item.comment?.takeIf { it.isNotBlank() }?.let { parts += it }

            return parts.joinToString(", ")
        }
    }
}