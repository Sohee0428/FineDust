package com.sohee.finedust.ui.main

import android.view.View
import androidx.databinding.BindingAdapter
import com.sohee.finedust.R

object MainBindingAdapter {

    @BindingAdapter("setBackgroundForGradeMain")
    @JvmStatic
    fun setBackgroundForGradeMain(view: View, grade: String?) {
        when (grade) {
            "1" -> view.setBackgroundResource(R.drawable.perfect_background)
            "2" -> view.setBackgroundResource(R.drawable.good_background)
            "3" -> view.setBackgroundResource(R.drawable.soso_background)
            "4" -> view.setBackgroundResource(R.drawable.bad_background)
            else -> view.setBackgroundResource(R.drawable.background)
        }
    }
}