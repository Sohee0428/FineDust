package com.example.finedust.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.finedust.R
import com.example.finedust.databinding.ActivityAppDescriptionBinding

class AppDescriptionActivity : AppCompatActivity() {

    lateinit var binding: ActivityAppDescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_description)

        binding.cancelImage.setOnClickListener {
            finish()
        }
    }
}