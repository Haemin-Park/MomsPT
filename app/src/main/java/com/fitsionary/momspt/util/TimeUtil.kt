package com.fitsionary.momspt.util

object TimeUtil {
    fun makeTimerFormat(millis: Long): Pair<Long, Long> {
        val seconds = (millis / 1000) % 60
        val minutes = ((millis - seconds) / 1000) / 60
        return Pair(minutes, seconds)
    }
}