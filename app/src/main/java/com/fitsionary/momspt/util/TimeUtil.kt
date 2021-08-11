package com.fitsionary.momspt.util

import kotlin.math.ceil

object TimeUtil {
    fun makeWorkoutTimeFormat(second: Int) = ceil(second / 60.0).toInt()
}