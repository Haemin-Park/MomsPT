package com.fitsionary.momspt.presentation.analysis.view

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.daasuu.camerarecorder.CameraRecorder
import com.daasuu.camerarecorder.CameraRecorderBuilder
import com.daasuu.camerarecorder.LensFacing
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.ActivityAnalysisBinding
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisViewModel
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisViewModel.Companion.COUNT_DOWN_TIMER_END
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisViewModel.Companion.COUNT_UP_TIMER_END
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisViewModel.Companion.RESULT_URL
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisViewModel.Companion.START_ANALYSIS_RESULT_ACTIVITY
import com.fitsionary.momspt.presentation.base.BaseActivity
import com.fitsionary.momspt.util.DateUtil
import com.fitsionary.momspt.util.rx.ui
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.kotlin.addTo
import java.io.File
import java.util.*

class AnalysisActivity :
    BaseActivity<ActivityAnalysisBinding, AnalysisViewModel>(R.layout.activity_analysis) {
    override val viewModel: AnalysisViewModel by lazy {
        ViewModelProvider(this).get(AnalysisViewModel::class.java)
    }
    private var cameraRecorder: CameraRecorder? = null
    private var sampleGLView: GLSurfaceView? = null
    lateinit var parentFile: File
    private lateinit var defaultFileName: String
    private lateinit var videoPath: String
    private lateinit var rxPermissions: RxPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.vm = viewModel

        rxPermissions = RxPermissions(this)
        rxPermissions
            .request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
            )
            .subscribe { granted ->
                if (granted) {
                    parentFile = getParentFile(this)!!

                    binding.btnStart.setOnClickListener {
                        defaultFileName = makeFileName()
                        videoPath = getVideoFilePath()
                        viewModel.countDownTimerStart()
                        binding.tvCountDownTimer.visibility = View.VISIBLE
                        binding.btnStart.visibility = View.INVISIBLE
                    }

                    binding.btnUpload.setOnClickListener {
                        val file = File(videoPath)
                        viewModel.sendVideo(file)
                        binding.btnUpload.visibility = View.INVISIBLE
                    }

                    viewModel.isLoading
                        .observeOn(ui())
                        .subscribe { if (it) showLoading() else hideLoading() }
                        .addTo(compositeDisposable)

                    viewModel.event.observe(this, {
                        it.getContentIfNotHandled()?.let { event ->
                            when (event.first) {
                                COUNT_DOWN_TIMER_END -> {
                                    isRecording(true)
                                    viewModel.countUpTimerStart()
                                    cameraRecorder?.start(videoPath)
                                }
                                COUNT_UP_TIMER_END -> {
                                    isRecording(false)
                                    cameraRecorder?.stop()
                                    exportMp4(this, videoPath)
                                }
                                RESULT_URL -> {
                                    downloadResult(event.second)
                                }
                                START_ANALYSIS_RESULT_ACTIVITY -> {
                                    startAnalysisResultActivity(event.second)
                                }
                            }
                        }
                    })
                } else {
                    showToast("접근 권한이 부여되지 않으면 서비스를 이용하실 수 없습니다.")
                }
            }
    }

    override fun onResume() {
        super.onResume()
        if (rxPermissions.isGranted(Manifest.permission.CAMERA)) {
            sampleGLView = GLSurfaceView(applicationContext)
            binding.wrapView.addView(sampleGLView)

            cameraRecorder = CameraRecorderBuilder(this, sampleGLView)
                .lensFacing(LensFacing.FRONT)
                .build()
        }
    }

    override fun onPause() {
        super.onPause()
        if (rxPermissions.isGranted(Manifest.permission.CAMERA)) {
            cameraRecorder?.stop()
            cameraRecorder?.release()
            cameraRecorder = null

            binding.wrapView.removeView(sampleGLView)
            sampleGLView = null
        }
    }

    private fun exportMp4(context: Context, filePath: String) {
        val values = ContentValues(2)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Video.Media.DATA, filePath)
        context.contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            values
        )
        MediaScannerConnection.scanFile(
            context, arrayOf(File(filePath).toString()),
            null, null
        )
        if (File(filePath).exists()) {
            showToast("영상 촬영이 완료되었습니다.")
        }
    }

    private fun makeFileName() = DateUtil.getDateFormat()

    private fun getVideoFilePath(): String {
        return parentFile.absolutePath + "/" + defaultFileName + ".mp4"
    }

    private fun downloadResult(url: String) {
        val resultFileName = "$defaultFileName.glb"
        val resultFilePath = parentFile.absolutePath + "/" + resultFileName

        val file = File(resultFilePath)
        if (!file.exists()) {
            viewModel.downloadResult(url, resultFileName, parentFile)
        } else {
            startAnalysisResultActivity(resultFilePath)
            finish()
        }
    }

    private fun startAnalysisResultActivity(path: String) {
        startActivity(
            Intent(this@AnalysisActivity, AnalysisResultActivity::class.java).putExtra(
                PATH,
                path
            )
        )
    }

    private fun getParentFile(context: Context): File? {
        return context.externalCacheDir!!
    }

    private fun isRecording(status: Boolean) {
        if (status) {
            binding.ivRecord.visibility = View.VISIBLE
            binding.tvCountDownTimer.visibility = View.INVISIBLE
            binding.cardIntroduce.visibility = View.INVISIBLE
            binding.tvCountUpTimer.visibility = View.VISIBLE

        } else {
            binding.ivRecord.visibility = View.INVISIBLE
            binding.tvCountUpTimer.visibility = View.INVISIBLE
            binding.cardIntroduce.visibility = View.VISIBLE
            binding.btnStart.visibility = View.VISIBLE
            binding.btnUpload.visibility = View.VISIBLE
        }
    }

    companion object {
        private val TAG = AnalysisActivity::class.simpleName
        const val PATH = "PATH"
    }
}