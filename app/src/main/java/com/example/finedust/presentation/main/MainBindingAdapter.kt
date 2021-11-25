package com.example.finedust.presentation.main

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.finedust.R

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

    @BindingAdapter("setImageForCAIGradeMain")
    @JvmStatic
    fun setImageForCAIGradeMain(view: ImageView, grade: String?) {
        when (grade) {
            "1" -> view.setImageResource(R.drawable.perfect_bearflower)
            "2" -> view.setImageResource(R.drawable.good_bearflower)
            "3" -> view.setImageResource(R.drawable.soso_bearflower)
            "4" -> view.setImageResource(R.drawable.bad_bearflower)
            else -> view.setImageResource(R.drawable.ic_baseline_error_24)
        }
    }

    @BindingAdapter("setImageForPM10GradeMain")
    @JvmStatic
    fun setImageForPM10GradeMain(view: ImageView, value: String?) {
        when (value?.toInt()) {
            in 0..30 -> view.setImageResource(R.drawable.perfect_bearflower)
            in 31..50 -> view.setImageResource(R.drawable.good_bearflower)
            in 51..100 -> view.setImageResource(R.drawable.soso_bearflower)
            null -> view.setImageResource(R.drawable.loading)
            else -> view.setImageResource(R.drawable.bad_bearflower)
        }
    }

    @BindingAdapter("setImageForPM25GradeMain")
    @JvmStatic
    fun setImageForPM25GradeMain(view: ImageView, value: String?) {
        when (value?.toInt()) {
            in 0..15 -> view.setImageResource(R.drawable.perfect_bearflower)
            in 16..25 -> view.setImageResource(R.drawable.good_bearflower)
            in 26..50 -> view.setImageResource(R.drawable.soso_bearflower)
            null -> view.setImageResource(R.drawable.loading)
            else -> view.setImageResource(R.drawable.bad_bearflower)
        }
    }
}