package com.example.finedust.presentation.fragment

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.finedust.R

object FragmentBindingAdapter {
    @BindingAdapter("setImageForGradeFragment")
    @JvmStatic
    fun setImageForGradeFragment(view: ImageView, grade: String?) {
        when (grade) {
            "1" -> view.setImageResource(R.drawable.perfectbearflower)
            "2" -> view.setImageResource(R.drawable.goodbearflower)
            "3" -> view.setImageResource(R.drawable.sosobearflower)
            "4" -> view.setImageResource(R.drawable.badbearflower)
            else -> view.setImageResource(R.drawable.ic_baseline_error_24)
        }
    }
}