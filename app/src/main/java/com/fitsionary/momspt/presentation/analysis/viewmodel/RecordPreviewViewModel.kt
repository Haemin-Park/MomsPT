package com.fitsionary.momspt.presentation.analysis.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.request.SignUpRequest
import com.fitsionary.momspt.data.api.response.BodyAnalysisResultResponse
import com.fitsionary.momspt.data.model.BodyAnalysisResultModel
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseAndroidViewModel
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
        val SIGN_UP_FINISH = "SIGN_UP_FINISH"
        val SHOW_ANALYSIS_RESULT = "START_ANALYSIS_RESULT_ACTIVITY"
    }

    private var parentFile = getParentFile(application)
    private lateinit var resultFileName: String

    private val _event = MutableLiveData<Event<Pair<String, Any>>>()
    val event: LiveData<Event<Pair<String, Any>>>
        get() = _event

    fun signUp(signUpRequest: SignUpRequest) {
        addDisposable(
            NetworkService.api.signUp(
                signUpRequest
            ).subscribe({
                Timber.i("회원가입 $it.message")
                if (it.success)
                    _event.value = Event(Pair(SIGN_UP_FINISH, ""))
            }, {})
        )
    }

    fun sendVideo(videoPath: String) {
        val file = File(videoPath)
        resultFileName = "${file.nameWithoutExtension}.glb"
        addDisposable(
            NetworkService.api.sendVideo(
                FormDataUtil.getVideoBody(FormDataUtil.FILE, file)
            ).applyNetworkScheduler()
                .doOnSubscribe { isLoading.onNext(true) }
                .doAfterTerminate { isLoading.onNext(false) }
                .subscribe({
                    Timber.i(it.toString())
                    if (it.glbURL != null)
                        downloadResult(it, resultFileName, parentFile)
                }, {
                    Timber.e(it.message!!)
                })
        )
    }

    private fun downloadResult(
        analysisResult: BodyAnalysisResultResponse,
        fileName: String,
        parentFile: File
    ) {
        val task = DownloadTask.Builder(analysisResult.glbURL!!, parentFile)
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
                val result = BodyAnalysisResultModel(
                    bodyComment = analysisResult.bodyComment,
                    workoutComment = analysisResult.workoutComment,
                    filePath = parentFile.absolutePath + "/$fileName"
                )
                _event.value =
                    Event(
                        Pair(
                            SHOW_ANALYSIS_RESULT,
                            result
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