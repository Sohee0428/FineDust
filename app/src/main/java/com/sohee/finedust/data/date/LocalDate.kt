package com.sohee.finedust.data.date

import java.text.SimpleDateFormat
import java.util.*

class LocalDate {
    private val long_now = System.currentTimeMillis()
    private val t_date = Date(long_now)
    private val t_dateFormat = SimpleDateFormat("yyyy-MM-dd E", Locale("ko", "KR"))
    private val t_dateFormat_time = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("ko", "KR"))
    val str_date = t_dateFormat.format(t_date)
    val str_date_time = t_dateFormat_time.format(t_date)
}

fun main() {
    val date = LocalDate().str_date_time
    println("현재 날짜 :$date")
}