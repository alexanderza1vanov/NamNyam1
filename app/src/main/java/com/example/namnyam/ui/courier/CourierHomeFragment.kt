package com.example.namnyam.ui.courier

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.namnyam.R
import com.example.namnyam.databinding.FragmentCourierHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class CourierHomeFragment : Fragment(R.layout.fragment_courier_home) {

    private var _binding: FragmentCourierHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCourierHomeBinding.bind(view)

        binding.toolbar.title = "Доставки"

        binding.viewPager.adapter = CourierPagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Свободные"
                else -> "Мои доставки"
            }
        }.attach()
    }

    override fun onDestroyView() {
        binding.viewPager.adapter = null
        _binding = null
        super.onDestroyView()
    }
}