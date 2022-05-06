package com.sohee.finedust.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.sohee.finedust.R
import com.sohee.finedust.databinding.ActivityAppDescriptionBinding

class AppDescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppDescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_description)
    }

    fun close(view: View) {
        finish()
    }
}