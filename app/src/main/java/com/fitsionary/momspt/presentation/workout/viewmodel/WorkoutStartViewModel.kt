package com.fitsionary.momspt.presentation.workout.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fitsionary.momspt.data.api.request.PoseRequest
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.concurrent.TimeUnit

class WorkoutStartViewModel : BaseViewModel() {
    private val _score = MutableLiveData<Int>()
    private val _cumulativeScore = MutableLiveData<Int>()
    private val _timerCountDown = MutableLiveData<Long>()
    private val _timerMinutes = MutableLiveData<Long>()
    private val _timerSeconds = MutableLiveData<Long>()
    private lateinit var timerJob: Job

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

    fun timerSet(total: Long) {
        _timerCountDown.value = total
    }

    fun timerStart() {
        if (::timerJob.isInitialized) timerJob.cancel()

        timerJob = viewModelScope.launch {
            while (_timerCountDown.value!! >= 1) {
                delay(500)
                _timerCountDown.value = _timerCountDown.value!! - 500
                makeTimerFormat(_timerCountDown.value!!)
            }
        }
    }

    fun timerStop() {
        if (::timerJob.isInitialized) timerJob.cancel()
    }

    fun sendPoseList(poseRequest: PoseRequest) {
        addDisposable(
            NetworkService.api
                .sendPose(poseRequest)
                .applyNetworkScheduler()
                .subscribe({
                    _score.value = it.score
                    _cumulativeScore.value?.plus(it.score)
                }, {
                    Log.d("네트워크 에러", it.localizedMessage)
                })
        )
    }

    private fun makeTimerFormat(millis: Long) {
        _timerSeconds.value = (millis / 1000) % 60
        _timerMinutes.value = ((millis - _timerSeconds.value!!) / 1000) / 60
    }
}