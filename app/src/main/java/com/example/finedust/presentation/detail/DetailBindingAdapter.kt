package com.example.finedust.presentation.detail

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.finedust.R

object DetailBindingAdapter {

    @BindingAdapter("setImageForGradeDetail")
    @JvmStatic
    fun setImageForGradeDetail(view: ImageView, grade: String?) {
        when (grade) {
            "01" -> view.setImageResource(R.drawable.loading)
            "1" -> view.setImageResource(R.drawable.perfect_bear)
            "2" -> view.setImageResource(R.drawable.good_bear)
            "3" -> view.setImageResource(R.drawable.soso_bear)
            "4" -> view.setImageResource(R.drawable.bad_bear)
            else -> view.setImageResource(R.drawable.ic_baseline_error_24)
        }
    }

    @BindingAdapter("setCircleForGradeDetail")
    @JvmStatic
    fun setCircleForGradeDetail(view: ImageView, grade: String?) {
        when (grade) {
            "01" -> view.setImageResource(R.drawable.loading)
            "1" -> view.setImageResource(R.drawable.perfect_circle_24)
            "2" -> view.setImageResource(R.drawable.good_circle_24)
            "3" -> view.setImageResource(R.drawable.soso_circle_24)
            "4" -> view.setImageResource(R.drawable.bad_circle_24)
            else -> view.setImageResource(R.drawable.ic_baseline_error_24)
        }
    }
}