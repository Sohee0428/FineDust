package com.example.finedust.presentation

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.finedust.App
import com.example.finedust.R

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

    @BindingAdapter("situationAlert")
    @JvmStatic
    fun situationAlert(view: TextView, grade: String?) {
        when (grade) {
            "1" -> view.text = "${App.instance.getString(R.string.perfect_air)}"
            "2" -> view.text = "${App.instance.getString(R.string.good_air)}"
            "3" -> view.text = "${App.instance.getString(R.string.soso_air)}"
            "4" -> view.text = "${App.instance.getString(R.string.bad_air)}"
            else -> view.text = "${App.instance.getString(R.string.null_air)}"
        }
    }
}