package com.example.finedust.data.date

import java.text.SimpleDateFormat
import java.util.*

    fun main() {

        val long_now = System.currentTimeMillis()
        val t_date = Date(long_now)
        val t_dateFormat = SimpleDateFormat("yyyy-mm-dd kk:mm E", Locale("ko", "KR"))

        val str_date = t_dateFormat.format(t_date)
        println("현재 시간 :$str_date")
    }