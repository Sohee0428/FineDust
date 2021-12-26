package com.sohee.finedust.presentation

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.sohee.finedust.App
import com.sohee.finedust.R

object BindingAdapter {

    @BindingAdapter("changeGradeToString")
    @JvmStatic
    fun changeGradeToString(view: TextView, grade: String?) {
        when (grade) {
            null -> view.text = "로딩 중"
            "1" -> view.text = "좋음"
            "2" -> view.text = "보통"
            "3" -> view.text = "나쁨"
            "4" -> view.text = "매우나쁨"
            else -> view.text = "오류"
        }
    }

    @BindingAdapter("setImageForCAIGrade")
    @JvmStatic
    fun setImageForCAIGrade(view: ImageView, grade: String?) {
        when (grade) {
            null -> view.setImageResource(R.drawable.loading)
            "1" -> view.setImageResource(R.drawable.perfect_bearflower)
            "2" -> view.setImageResource(R.drawable.good_bearflower)
            "3" -> view.setImageResource(R.drawable.soso_bearflower)
            "4" -> view.setImageResource(R.drawable.bad_bearflower)
            else -> view.setImageResource(R.drawable.ic_baseline_error_24)
        }
    }

    @BindingAdapter("setImageForPM10Grade")
    @JvmStatic
    fun setImageForPM10Grade(view: ImageView, value: String?) {
        when (value?.toIntOrNull()) {
            in 0..30 -> view.setImageResource(R.drawable.perfect_bearflower)
            in 31..50 -> view.setImageResource(R.drawable.good_bearflower)
            in 51..100 -> view.setImageResource(R.drawable.soso_bearflower)
            null -> view.setImageResource(R.drawable.loading)
            else -> view.setImageResource(R.drawable.bad_bearflower)
        }
    }

    @BindingAdapter("setImageForPM25Grade")
    @JvmStatic
    fun setImageForPM25Grade(view: ImageView, value: String?) {
        when (value?.toIntOrNull()) {
            in 0..15 -> view.setImageResource(R.drawable.perfect_bearflower)
            in 16..25 -> view.setImageResource(R.drawable.good_bearflower)
            in 26..50 -> view.setImageResource(R.drawable.soso_bearflower)
            null -> view.setImageResource(R.drawable.loading)
            else -> view.setImageResource(R.drawable.bad_bearflower)
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