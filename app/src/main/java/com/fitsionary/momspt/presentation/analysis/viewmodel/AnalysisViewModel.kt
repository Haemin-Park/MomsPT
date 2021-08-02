package com.fitsionary.momspt.presentation.analysis.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.BASE_URL2
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.Event
import com.fitsionary.momspt.util.FormDataUtil
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import java.io.File
import java.util.*

class AnalysisViewModel : BaseViewModel() {

    private val _timerCountDown = MutableLiveData<Int>()
    private val _timerCountUp = MutableLiveData<Int>()
    private val _timerCountDownEnd = MutableLiveData<Event<Boolean>>()
    private val _timerCountUpEnd = MutableLiveData<Event<Boolean>>()
    private val _resultUrl = MutableLiveData<Event<String>>()
    private lateinit var timer: Timer

    val timerCountDown: LiveData<Int>
        get() = _timerCountDown
    val timerCountUp: LiveData<Int>
        get() = _timerCountUp
    val timerCountDownEnd: LiveData<Event<Boolean>>
        get() = _timerCountDownEnd
    val timerCountUpEnd: LiveData<Event<Boolean>>
        get() = _timerCountUpEnd
    val resultUrl: LiveData<Event<String>>
        get() = _resultUrl

    fun countDownTimerStart() {
        _timerCountDown.value = 5
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (_timerCountDown.value!! == 0) {
                    timer.cancel()
                    _timerCountDownEnd.postValue(Event(true))
                }
                _timerCountDown.postValue(_timerCountDown.value!! - 1)
            }
        }, 1000, 1000)
    }

    fun countUpTimerStart() {
        _timerCountUp.value = 0
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (_timerCountUp.value!! == 10) {
                    timer.cancel()
                    _timerCountUpEnd.postValue(Event(true))
                }
                _timerCountUp.postValue(_timerCountUp.value!! + 1)
            }
        }, 1000, 1000)
    }

    fun sendVideo(file: File) {
        addDisposable(
            NetworkService.api.sendVideo(
                FormDataUtil.getVideoBody("file", file)
            ).applyNetworkScheduler()
                .doOnSubscribe { isLoading.onNext(true) }
                .doAfterTerminate { isLoading.onNext(false) }
                .subscribe({
                    Log.d(TAG, it.toString())
                    _resultUrl.value = Event(BASE_URL2 + it)

                }, {
                    Log.i(TAG, it.message!!)
                })
        )
    }

    companion object {
        private val TAG = AnalysisViewModel::class.simpleName
    }
}