package com.sohee.finedust

import android.content.Context
import android.util.Log
import android.widget.Toast

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun logHelper(title: String, content: String) {
    Log.d(title, content)
}

fun logHelper(content: String) {
    Log.d("LogHelper", content)
}