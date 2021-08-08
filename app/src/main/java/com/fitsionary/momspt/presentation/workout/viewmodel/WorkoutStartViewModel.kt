package com.fitsionary.momspt.presentation.workout.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.TimeUtil
import java.util.*

class WorkoutStartViewModel : BaseViewModel() {
    private val _score = MutableLiveData<Int>()
    private val _cumulativeScore = MutableLiveData<Int>()
    private val _timerCountDown = MutableLiveData<Long>()
    private val _timerMinutes = MutableLiveData<Long>()
    private val _timerSeconds = MutableLiveData<Long>()
    private lateinit var timer: Timer

    val score: LiveData<Int>
        get() = _score
    val cumulativeScore: LiveData<Int>
        get() = _cumulativeScore
    val timerCountDown: LiveData<Long>
        get() = _timerCountDown
    val timerMinutes: LiveData<Long>
        get() = _timerMinutes
    val timerSeconds: LiveData<Long>
        get() = _timerSeconds

    init {
        _cumulativeScore.value = 0
    }

    fun countDownTimerSet(total: Long) {
        _timerCountDown.value = total
    }

    fun countDownTimerStart() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (_timerCountDown.value!! == 0L) {
                    timer.cancel()
                }
                _timerCountDown.postValue(_timerCountDown.value!! - 1000)
                val timerFormat = TimeUtil.makeTimerFormat(_timerCountDown.value ?: 0)
                _timerMinutes.postValue(timerFormat.first ?: 0)
                _timerSeconds.postValue(timerFormat.second ?: 0)
            }
        }, 1000, 1000)
    }

    fun countDownTimerStop() {
        if (::timer.isInitialized) timer.cancel()
    }

    fun setScore(score: Int) {
        Log.i(TAG, score.toString())
        _score.postValue(score)
        _cumulativeScore.postValue(_cumulativeScore.value?.plus(score))
    }

    companion object {
        private val TAG = WorkoutStartViewModel::class.simpleName
    }
}