package com.example.finedust

import android.app.Application

class App: Application() {
//    string으로 된 글자를 적용해주기 위해 사용

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: App
    }
}