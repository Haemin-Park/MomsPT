package com.fitsionary.momspt.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    @SuppressLint("SimpleDateFormat")
    fun getDateFormat(): String = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Date())
}