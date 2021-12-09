package com.example.finedust.data.date

import java.text.SimpleDateFormat
import java.util.*

class LocalDate {
    val long_now = System.currentTimeMillis()
    val t_date = Date(long_now)
    val t_dateFormat = SimpleDateFormat("yyyy-MM-dd E", Locale("ko", "KR"))
    val str_date = t_dateFormat.format(t_date)
}

fun main() {
    val date = LocalDate().str_date
    println("현재 날짜 :$date")
}