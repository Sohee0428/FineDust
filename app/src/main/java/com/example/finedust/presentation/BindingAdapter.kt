package com.example.finedust.presentation

import android.widget.TextView
import androidx.databinding.BindingAdapter

object BindingAdapter {

    @BindingAdapter("changeGradeToString")
    @JvmStatic
    fun changeGradeToString(view: TextView, grade: String?) {
        when (grade) {
            "1" -> view.text = "좋음"
            "2" -> view.text = "보통"
            "3" -> view.text = "나쁨"
            "4" -> view.text = "매우나쁨"
            else -> view.text = "오류"
        }
    }
}