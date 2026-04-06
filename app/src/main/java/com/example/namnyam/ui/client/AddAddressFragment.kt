package com.example.namnyam.ui.client

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.namnyam.R
import com.example.namnyam.databinding.FragmentAddAddressBinding
import com.example.namnyam.utils.UiState

class AddAddressFragment : Fragment(R.layout.fragment_add_address) {

    private var _binding: FragmentAddAddressBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddAddressViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddAddressBinding.bind(view)

        viewModel = ViewModelProvider(this)[AddAddressViewModel::class.java]

        binding.toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back_24)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            submit()
        }

        observeState()
    }

    private fun observeState() {
        viewModel.state = { state ->
            when (state) {
                is UiState.Idle -> Unit

                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSave.isEnabled = false
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    Toast.makeText(requireContext(), "Адрес добавлен", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun submit() {
        val title = binding.etTitle.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val entrance = binding.etEntrance.text.toString().trim()
        val floor = binding.etFloor.text.toString().trim()
        val apartment = binding.etApartment.text.toString().trim()
        val comment = binding.etComment.text.toString().trim()
        val isDefault = binding.checkboxDefault.isChecked

        if (title.isBlank()) {
            binding.etTitle.error = "Введите название"
            return
        }

        if (address.isBlank()) {
            binding.etAddress.error = "Введите адрес"
            return
        }

        viewModel.createAddress(
            title = title,
            addressLine = address,
            entrance = entrance,
            floor = floor,
            apartment = apartment,
            comment = comment,
            isDefault = isDefault
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}