package com.fitsionary.momspt.presentation.workout.viewmodel

import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fitsionary.momspt.presentation.base.BaseViewModel
import java.util.*

class WorkoutPlayViewModel : BaseViewModel() {
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private val _cumulativeScore = MutableLiveData<Int>()
    val cumulativeScore: LiveData<Int>
        get() = _cumulativeScore

    private lateinit var timer: Timer

    private val _timerCountDown = MutableLiveData<Long>()
    private val timerCountDown: LiveData<Long>
        get() = _timerCountDown

    val formattedTimer = Transformations.map(timerCountDown) { time ->
        DateUtils.formatElapsedTime(time / 1000)
    }

    init {
        _score.value = 0
        _cumulativeScore.value = 0
    }

    fun countDownTimerSet(total: Long) {
        _timerCountDown.value = total
    }

    fun countDownTimerStart() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (_timerCountDown.value == 0L) {
                    timer.cancel()
                }
                _timerCountDown.postValue(_timerCountDown.value?.minus(1000))
            }
        }, 1000, 1000)
    }

    fun countDownTimerStop() {
        if (::timer.isInitialized) timer.cancel()
    }

    fun setScore(score: Int) {
        _score.postValue(score)
        _cumulativeScore.postValue(_cumulativeScore.value?.plus(score))
    }

    companion object {
        private val TAG = WorkoutPlayViewModel::class.simpleName
    }
}