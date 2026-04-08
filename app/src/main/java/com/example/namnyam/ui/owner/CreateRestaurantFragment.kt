package com.example.namnyam.ui.owner

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.namnyam.R
import com.example.namnyam.data.remote.dto.CreateRestaurantRequest
import com.example.namnyam.databinding.FragmentCreateRestaurantBinding
import com.example.namnyam.utils.UiState

class CreateRestaurantFragment : Fragment(R.layout.fragment_create_restaurant) {

    private var _binding: FragmentCreateRestaurantBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CreateRestaurantViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateRestaurantBinding.bind(view)

        viewModel = ViewModelProvider(this)[CreateRestaurantViewModel::class.java]

        setupToolbar()
        setupListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Создать ресторан"
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupListeners() {
        binding.btnCreateRestaurant.setOnClickListener {
            submit()
        }
    }

    private fun observeViewModel() {
        viewModel.state = { state ->
            when (state) {
                UiState.Idle -> Unit

                UiState.Loading -> {
                    binding.progressCreateRestaurant.visibility = View.VISIBLE
                    binding.btnCreateRestaurant.isEnabled = false
                }

                is UiState.Success -> {
                    binding.progressCreateRestaurant.visibility = View.GONE
                    binding.btnCreateRestaurant.isEnabled = true

                    findNavController().previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("restaurant_created", true)

                    findNavController().popBackStack()
                }

                is UiState.Error -> {
                    binding.progressCreateRestaurant.visibility = View.GONE
                    binding.btnCreateRestaurant.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun submit() {
        clearErrors()

        val name = binding.etName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim().ifBlank { null }
        val address = binding.etAddress.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val cuisineType = binding.etCuisineType.text.toString().trim().ifBlank { null }
        val imageUrl = binding.etImageUrl.text.toString().trim().ifBlank { null }

        val deliveryTimeText = binding.etDeliveryTime.text.toString().trim()
        val deliveryFeeText = binding.etDeliveryFee.text.toString().trim()
        val minOrderAmountText = binding.etMinOrderAmount.text.toString().trim()

        if (name.isBlank()) {
            binding.etName.error = "Введите название"
            return
        }

        if (address.isBlank()) {
            binding.etAddress.error = "Введите адрес"
            return
        }

        if (phone.isBlank()) {
            binding.etPhone.error = "Введите телефон"
            return
        }

        val deliveryTimeMin = if (deliveryTimeText.isBlank()) {
            null
        } else {
            deliveryTimeText.toIntOrNull() ?: run {
                binding.etDeliveryTime.error = "Введите целое число"
                return
            }
        }

        val deliveryFee = if (deliveryFeeText.isBlank()) {
            0.0
        } else {
            parseDouble(deliveryFeeText) ?: run {
                binding.etDeliveryFee.error = "Введите число"
                return
            }
        }
        val minOrderAmount = if (minOrderAmountText.isBlank()) {
            0.0
        } else {
            parseDouble(minOrderAmountText) ?: run {
                binding.etMinOrderAmount.error = "Введите число"
                return
            }
        }

        val request = CreateRestaurantRequest(
            name = name,
            description = description,
            address = address,
            phone = phone,
            cuisineType = cuisineType,
            imageUrl = imageUrl,
            deliveryTimeMin = deliveryTimeMin,
            deliveryFee = deliveryFee,
            minOrderAmount = minOrderAmount
        )

        viewModel.createRestaurant(request)
    }

    private fun parseDouble(value: String): Double? {
        return value.replace(',', '.').toDoubleOrNull()
    }

    private fun clearErrors() {
        binding.etName.error = null
        binding.etAddress.error = null
        binding.etPhone.error = null
        binding.etDeliveryTime.error = null
        binding.etDeliveryFee.error = null
        binding.etMinOrderAmount.error = null
    }

    override fun onDestroyView() {
        viewModel.state = null
        _binding = null
        super.onDestroyView()
    }
}