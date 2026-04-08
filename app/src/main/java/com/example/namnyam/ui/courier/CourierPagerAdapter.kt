package com.example.namnyam.ui.courier

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class CourierPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CourierAvailableOrdersFragment()
            else -> CourierMyOrdersFragment()
        }
    }
}