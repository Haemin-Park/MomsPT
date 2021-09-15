package com.fitsionary.momspt.presentation.analysis.viewmodel

import android.app.Application
import android.content.ContentValues
import android.media.MediaScannerConnection
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fitsionary.momspt.presentation.base.BaseAndroidViewModel
import com.fitsionary.momspt.util.DateUtil
import com.fitsionary.momspt.util.Event
import java.io.File
import java.util.*

class AnalysisViewModel(application: Application) : BaseAndroidViewModel(application) {
    // record constant
    private val COUNT_DOWN_TIME = 5
    private val COUNT_UP_TIME = 10
    private val DEFAULT_DELAY = 1000L
    private val DEFAULT_PERIOD = 1000L

    // status type
    private val NONE = "NONE"
    private val IS_RECORD_BTN_CLICKED = "IS_RECORD_BTN_CLICKED"
    private val IS_RECORDING = "IS_RECORDING"

    companion object {
        // event type
        val COUNT_DOWN_TIMER_END = "COUNT_DOWN_TIMER_END"
        val COUNT_UP_TIMER_END = "COUNT_UP_TIMER_END"
    }

    private lateinit var timer: Timer
    private var status: MutableLiveData<String> = MutableLiveData(NONE)

    private val _timerCountDown = MutableLiveData(COUNT_DOWN_TIME)
    val timerCountDown: LiveData<Int>
        get() = _timerCountDown

    private val _timerCountUp = MutableLiveData(0)
    val timerCountUp: LiveData<Int>
        get() = _timerCountUp

    private val _event = MutableLiveData<Event<Pair<String, String>>>()
    val event: LiveData<Event<Pair<String, String>>>
        get() = _event

    val recordBtnVisible = Transformations.map(status) {
        if (it == NONE) View.VISIBLE else View.INVISIBLE
    }

    val timerCountDownTvVisible = Transformations.map(status) {
        if (it == IS_RECORD_BTN_CLICKED) View.VISIBLE else View.INVISIBLE
    }

    val timerCountUpTvVisible = Transformations.map(status) {
        if (it == IS_RECORDING) View.VISIBLE else View.INVISIBLE
    }

    private var parentFile = getParentFile(application)
    private lateinit var defaultFileName: String
    private lateinit var videoPath: String

    fun recordStart() {
        videoPath = getVideoFilePath()
        countDownTimerStart()
    }

    private fun countDownTimerStart() {
        setCurrentStatus(IS_RECORD_BTN_CLICKED, false)
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (_timerCountDown.value == 0) {
                    timer.cancel()
                    _event.postValue(Event(Pair(COUNT_DOWN_TIMER_END, videoPath)))
                    countUpTimerStart()
                }
                _timerCountDown.postValue(_timerCountDown.value?.minus(1))
            }
        }, DEFAULT_DELAY, DEFAULT_PERIOD)
    }

    fun countUpTimerStart() {
        setCurrentStatus(IS_RECORDING, true)
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (_timerCountUp.value == COUNT_UP_TIME) {
                    timer.cancel()
                    setCurrentStatus(NONE, true)
                    initTimer(true)
                    _event.postValue(Event(Pair(COUNT_UP_TIMER_END, videoPath)))
                    exportVideo(getApplication(), videoPath)
                }
                _timerCountUp.postValue(_timerCountUp.value?.plus(1))
            }
        }, DEFAULT_DELAY, DEFAULT_PERIOD)
    }


    private fun setCurrentStatus(currentStatus: String, isBackgroundThread: Boolean) {
        if (!isBackgroundThread)
            status.value = currentStatus
        else
            status.postValue(currentStatus)
    }

    private fun makeFileName() = DateUtil.getDateFormat()

    private fun getVideoFilePath(): String {
        defaultFileName = makeFileName()
        return parentFile.absolutePath + "/$defaultFileName.mp4"
    }

    private fun exportVideo(application: Application, videoPath: String) {
        val values = ContentValues(2)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Video.Media._ID, videoPath)
        application.contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            values
        )
        MediaScannerConnection.scanFile(
            application, arrayOf(File(videoPath).toString()),
            null, null
        )
    }

    private fun initTimer(isBackgroundThread: Boolean) {
        if (!isBackgroundThread) {
            _timerCountDown.value = COUNT_DOWN_TIME
            _timerCountUp.value = 0
        } else {
            _timerCountDown.postValue(COUNT_DOWN_TIME)
            _timerCountUp.postValue(0)
        }
    }

    private fun getParentFile(application: Application): File {
        return application.externalCacheDir!!
    }
}