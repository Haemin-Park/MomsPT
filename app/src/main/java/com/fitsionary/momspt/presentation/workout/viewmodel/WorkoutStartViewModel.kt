package com.fitsionary.momspt.presentation.workout.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.request.PoseRequest
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.TimeUtil
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
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
                _timerCountDown.postValue(_timerCountDown.value!! - 500)
                val timerFormat = TimeUtil.makeTimerFormat(_timerCountDown.value!!)
                _timerMinutes.postValue(timerFormat.first!!)
                _timerSeconds.postValue(timerFormat.second!!)
            }
        }, 1000, 1000)
    }

    fun countDownTimerStop() {
        if (::timer.isInitialized) timer.cancel()
    }

    fun sendPoseList(poseRequest: PoseRequest) {
        addDisposable(
            NetworkService.api
                .sendPose(poseRequest)
                .applyNetworkScheduler()
                .subscribe({
                    Log.i(TAG, it.toString())
                    _score.value = it.score
                    _cumulativeScore.value?.plus(it.score)
                }, {
                    Log.i(TAG, it.message!!)
                })
        )
    }

    companion object {
        private val TAG = WorkoutStartViewModel::class.simpleName
    }
}