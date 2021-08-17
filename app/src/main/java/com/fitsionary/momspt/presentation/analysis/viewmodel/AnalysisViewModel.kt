package com.fitsionary.momspt.presentation.analysis.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fitsionary.momspt.BASE_URL2
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.Event
import com.fitsionary.momspt.util.FormDataUtil
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import java.io.File
import java.util.*

class AnalysisViewModel : BaseViewModel() {
    private lateinit var timer: Timer

    private val _timerCountDown = MutableLiveData<Int>()
    val timerCountDown: LiveData<Int>
        get() = _timerCountDown

    private val _timerCountUp = MutableLiveData<Int>()
    val timerCountUp: LiveData<Int>
        get() = _timerCountUp

    val infoTvVisible = Transformations.map(status) {
        if (it == NONE || it == IS_RECORD_BTN_CLICKED) View.VISIBLE else View.INVISIBLE
    }

    val recordBtnVisible = Transformations.map(status) {
        if (it == NONE) View.VISIBLE else View.INVISIBLE
    }

    val timerCountDownTvVisible = Transformations.map(status) {
        if (it == IS_RECORD_BTN_CLICKED) View.VISIBLE else View.INVISIBLE
    }

    val timerCountUpTvVisible = Transformations.map(status) {
        if (it == IS_RECORDING) View.VISIBLE else View.INVISIBLE
    }

    val uploadBtnVisible = Transformations.map(status) {
        if (it == IS_RECORDED) View.VISIBLE else View.INVISIBLE
    }

    private val _event = MutableLiveData<Event<Pair<String, String>>>()
    val event: LiveData<Event<Pair<String, String>>>
        get() = _event

    fun countDownTimerStart() {
        status.value = IS_RECORD_BTN_CLICKED
        _timerCountDown.value = 5
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (_timerCountDown.value!! == 0) {
                    timer.cancel()
                    _event.postValue(Event(Pair(COUNT_DOWN_TIMER_END, "SUCCESS")))
                }
                _timerCountDown.postValue(_timerCountDown.value!! - 1)
            }
        }, 1000, 1000)
    }

    fun countUpTimerStart() {
        status.value = IS_RECORDING
        _timerCountUp.value = 0
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (_timerCountUp.value!! == 10) {
                    timer.cancel()
                    status.postValue(IS_RECORDED)
                    _event.postValue(Event(Pair(COUNT_UP_TIMER_END, "SUCCESS")))
                }
                _timerCountUp.postValue(_timerCountUp.value!! + 1)
            }
        }, 1000, 1000)
    }

    fun sendVideo(file: File) {
        addDisposable(
            NetworkService.api2.sendVideo(
                FormDataUtil.getVideoBody("file", file)
            ).applyNetworkScheduler()
                .doOnSubscribe { isLoading.onNext(true) }
                .doAfterTerminate { isLoading.onNext(false) }
                .subscribe({
                    Log.d(TAG, it.toString())
                    status.value = NONE
                    _event.value = Event(Pair(RESULT_URL, BASE_URL2 + it))

                }, {
                    Log.i(TAG, it.message!!)
                })
        )
    }

    fun downloadResult(url: String, fileName: String, parentFile: File) {
        val task = DownloadTask.Builder(url, parentFile)
            .setFilename(fileName)
            .build()

        val listener: DownloadListener = object : DownloadListener {
            override fun taskStart(task: DownloadTask) {
                isLoading.onNext(true)
            }

            override fun connectTrialStart(
                task: DownloadTask,
                requestHeaderFields: MutableMap<String, MutableList<String>>
            ) {
            }

            override fun connectTrialEnd(
                task: DownloadTask,
                responseCode: Int,
                responseHeaderFields: MutableMap<String, MutableList<String>>
            ) {
            }

            override fun downloadFromBeginning(
                task: DownloadTask,
                info: BreakpointInfo,
                cause: ResumeFailedCause
            ) {
            }

            override fun downloadFromBreakpoint(task: DownloadTask, info: BreakpointInfo) {
            }

            override fun connectStart(
                task: DownloadTask,
                blockIndex: Int,
                requestHeaderFields: MutableMap<String, MutableList<String>>
            ) {
            }

            override fun connectEnd(
                task: DownloadTask,
                blockIndex: Int,
                responseCode: Int,
                responseHeaderFields: MutableMap<String, MutableList<String>>
            ) {
            }

            override fun fetchStart(task: DownloadTask, blockIndex: Int, contentLength: Long) {
            }

            override fun fetchProgress(
                task: DownloadTask,
                blockIndex: Int,
                increaseBytes: Long
            ) {
            }

            override fun fetchEnd(task: DownloadTask, blockIndex: Int, contentLength: Long) {
            }

            override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?) {
                isLoading.onNext(false)
                _event.value =
                    Event(
                        Pair(
                            START_ANALYSIS_RESULT_ACTIVITY,
                            parentFile.absolutePath + "/" + fileName
                        )
                    )
            }
        }
        task.enqueue(listener)
    }

    companion object {
        private val TAG = AnalysisViewModel::class.simpleName
        const val COUNT_DOWN_TIMER_END = "COUNT_DOWN_TIMER_END"
        const val COUNT_UP_TIMER_END = "COUNT_UP_TIMER_END"
        const val RESULT_URL = "RESULT_URL"
        const val START_ANALYSIS_RESULT_ACTIVITY = "START_ANALYSIS_RESULT_ACTIVITY"

        // status
        const val NONE = "NONE"
        const val IS_RECORD_BTN_CLICKED = "IS_RECORD_BTN_CLICKED"
        const val IS_RECORDING = "IS_RECORDING"
        const val IS_RECORDED = "IS_RECORDED"

        private val status = MutableLiveData(NONE)
    }
}