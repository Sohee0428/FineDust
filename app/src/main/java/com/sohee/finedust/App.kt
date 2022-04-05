package com.sohee.finedust

import android.app.Application
import android.content.Context

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        context = this
    }

    companion object {
        lateinit var instance: App
        lateinit var context: Context
    }
}