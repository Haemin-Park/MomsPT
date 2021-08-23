package com.fitsionary.momspt.presentation.analysis.viewmodel

import android.app.Application
import android.content.ContentValues
import android.media.MediaScannerConnection
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fitsionary.momspt.BASE_URL2
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseAndroidViewModel
import com.fitsionary.momspt.util.DateUtil
import com.fitsionary.momspt.util.Event
import com.fitsionary.momspt.util.FormDataUtil
import com.fitsionary.momspt.util.FormDataUtil.FILE
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import java.io.File
import java.util.*

class AnalysisViewModel(application: Application) : BaseAndroidViewModel(application) {
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

    private var parentFile = getParentFile(application)
    private lateinit var defaultFileName: String
    private lateinit var videoPath: String

    fun recordStart() {
        videoPath = getVideoFilePath()
        countDownTimerStart()
    }

    fun videoUpload() {
        val file = File(videoPath)
        sendVideo(file)
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
                    setCurrentStatus(IS_RECORDED, true)
                    _event.postValue(Event(Pair(COUNT_UP_TIMER_END, videoPath)))
                    exportVideo(getApplication(), videoPath)
                }
                _timerCountUp.postValue(_timerCountUp.value?.plus(1))
            }
        }, DEFAULT_DELAY, DEFAULT_PERIOD)
    }

    private fun downloadResult(url: String) {
        val resultFileName = "$defaultFileName.glb"
        val resultFilePath = parentFile.absolutePath + "/$resultFileName"

        val file = File(resultFilePath)
        if (!file.exists()) {
            downloadResult(url, resultFileName, parentFile)
        } else {
            _event.value =
                Event(
                    Pair(
                        START_ANALYSIS_RESULT_ACTIVITY,
                        resultFilePath
                    )
                )
        }
    }

    private fun sendVideo(file: File) {
        addDisposable(
            NetworkService.api2.sendVideo(
                FormDataUtil.getVideoBody(FILE, file)
            ).applyNetworkScheduler()
                .doOnSubscribe { isLoading.onNext(true) }
                .doAfterTerminate { isLoading.onNext(false) }
                .subscribe({
                    setCurrentStatus(NONE, false)
                    downloadResult(BASE_URL2 + it)
                }, {
                    Log.i(TAG, it.message!!)
                })
        )
    }

    private fun downloadResult(url: String, fileName: String, parentFile: File) {
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
                            parentFile.absolutePath + "/$fileName"
                        )
                    )
            }
        }
        task.enqueue(listener)
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

    private fun getParentFile(application: Application): File {
        return application.externalCacheDir!!
    }

    companion object {
        private val TAG = AnalysisViewModel::class.simpleName

        // record constant
        private const val COUNT_DOWN_TIME = 5
        private const val COUNT_UP_TIME = 10
        private const val DEFAULT_DELAY = 1000L
        private const val DEFAULT_PERIOD = 1000L

        // event type
        const val COUNT_DOWN_TIMER_END = "COUNT_DOWN_TIMER_END"
        const val COUNT_UP_TIMER_END = "COUNT_UP_TIMER_END"
        const val START_ANALYSIS_RESULT_ACTIVITY = "START_ANALYSIS_RESULT_ACTIVITY"

        // status type
        private const val NONE = "NONE"
        private const val IS_RECORD_BTN_CLICKED = "IS_RECORD_BTN_CLICKED"
        private const val IS_RECORDING = "IS_RECORDING"
        private const val IS_RECORDED = "IS_RECORDED"
    }
}