package com.example.namnyam.ui.owner

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.namnyam.R
import com.example.namnyam.databinding.FragmentEditProductBinding
import com.example.namnyam.utils.UiState

class EditProductFragment : Fragment(R.layout.fragment_edit_product) {

    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: EditProductViewModel

    private val productId: Long
        get() = arguments?.getLong("productId", -1L) ?: -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditProductBinding.bind(view)

        viewModel = ViewModelProvider(this)[EditProductViewModel::class.java]

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSaveProduct.setOnClickListener {
            save()
        }

        observeVm()

        if (productId != -1L) {
            binding.toolbar.title = "Редактировать блюдо"
            viewModel.loadProduct(productId)
        } else {
            binding.toolbar.title = "Новое блюдо"
        }
    }

    private fun observeVm() {
        viewModel.productState = { state ->
            when (state) {
                UiState.Idle -> Unit
                UiState.Loading -> {
                    binding.progressSaveProduct.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    binding.progressSaveProduct.visibility = View.GONE

                    val product = state.data
                    binding.etName.setText(product.name)
                    binding.etDescription.setText(product.description.orEmpty())
                    binding.etPrice.setText(product.price.toString())
                    binding.etWeight.setText(product.weightGrams?.toString().orEmpty())
                    binding.etIngredients.setText(product.ingredients.orEmpty())
                    binding.etImageUrl.setText(product.imageUrl.orEmpty())
                    binding.switchAvailable.isChecked = product.isAvailable
                }
                is UiState.Error -> {
                    binding.progressSaveProduct.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.saveState = { state ->
            when (state) {
                UiState.Idle -> Unit
                UiState.Loading -> {
                    binding.btnSaveProduct.isEnabled = false
                    binding.progressSaveProduct.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    binding.btnSaveProduct.isEnabled = true
                    binding.progressSaveProduct.visibility = View.GONE

                    findNavController()
                        .previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("owner_products_refresh", true)

                    Toast.makeText(requireContext(), "Сохранено", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is UiState.Error -> {
                    binding.btnSaveProduct.isEnabled = true
                    binding.progressSaveProduct.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun save() {
        val name = binding.etName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim().ifBlank { null }
        val price = binding.etPrice.text.toString().trim().replace(',', '.').toDoubleOrNull()
        val weight = binding.etWeight.text.toString().trim().toIntOrNull()
        val ingredients = binding.etIngredients.text.toString().trim().ifBlank { null }
        val imageUrl = binding.etImageUrl.text.toString().trim().ifBlank { null }
        val isAvailable = binding.switchAvailable.isChecked

        binding.etName.error = null
        binding.etPrice.error = null

        if (name.isBlank()) {
            binding.etName.error = "Введите название"
            return
        }

        if (price == null) {
            binding.etPrice.error = "Введите цену"
            return
        }

        if (productId == -1L) {
            viewModel.createProduct(
                name = name,
                description = description,
                price = price,
                weightGrams = weight,
                ingredients = ingredients,
                imageUrl = imageUrl,
                isAvailable = isAvailable
            )
        } else {
            viewModel.updateProduct(
                productId = productId,
                name = name,
                description = description,
                price = price,
                weightGrams = weight,
                ingredients = ingredients,
                imageUrl = imageUrl,
                isAvailable = isAvailable
            )
        }
    }

    override fun onDestroyView() {
        viewModel.productState = null
        viewModel.saveState = null
        _binding = null
        super.onDestroyView()
    }
}