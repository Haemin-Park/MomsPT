package com.fitsionary.momspt.presentation.analysis.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseAndroidViewModel
import com.fitsionary.momspt.util.BASE_URL2
import com.fitsionary.momspt.util.Event
import com.fitsionary.momspt.util.FormDataUtil
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import timber.log.Timber
import java.io.File

class RecordPreviewViewModel(application: Application) : BaseAndroidViewModel(application) {
    companion object {
        // event type
        val START_ANALYSIS_RESULT_ACTIVITY = "START_ANALYSIS_RESULT_ACTIVITY"
    }

    private var parentFile = getParentFile(application)
    private lateinit var defaultFileName: String
    private lateinit var videoPath: String

    private val _event = MutableLiveData<Event<Pair<String, String>>>()
    val event: LiveData<Event<Pair<String, String>>>
        get() = _event

    fun sendVideo() {
        val file = File(videoPath)
        addDisposable(
            NetworkService.api2.sendVideo(
                FormDataUtil.getVideoBody(FormDataUtil.FILE, file)
            ).applyNetworkScheduler()
                .doOnSubscribe { isLoading.onNext(true) }
                .doAfterTerminate { isLoading.onNext(false) }
                .subscribe({
                    downloadResult(BASE_URL2 + it)
                }, {
                    Timber.e(it.message!!)
                })
        )
    }

    private fun downloadResult(url: String) {
        val resultFileName = "$defaultFileName.glb"
        val resultFilePath = parentFile.absolutePath + "/$resultFileName"

        val file = File(resultFilePath)
        if (!file.exists()) {
            downloadResult(url, resultFileName, parentFile)
        } else {
            _event.value =
                Event(Pair(START_ANALYSIS_RESULT_ACTIVITY, resultFilePath))
        }
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

    private fun getParentFile(application: Application): File {
        return application.externalCacheDir!!
    }
}