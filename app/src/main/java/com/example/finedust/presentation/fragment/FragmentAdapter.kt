package com.example.finedust.presentation.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    private var fragmentItem: ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int {
        return fragmentItem.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentItem[position]
    }

    fun addFragment(fragment: Fragment) {
        fragmentItem.add(fragment)
        notifyItemInserted(fragmentItem.size - 1)
    }

    fun removeFragment() {
        fragmentItem.removeAt(fragmentItem.size - 1)
        notifyItemInserted(fragmentItem.size)
    }

}