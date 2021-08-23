package com.fitsionary.momspt.presentation.analysis.view

import android.Manifest
import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.daasuu.camerarecorder.CameraRecorder
import com.daasuu.camerarecorder.CameraRecorderBuilder
import com.daasuu.camerarecorder.LensFacing
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.ActivityAnalysisBinding
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisViewModel
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisViewModel.Companion.COUNT_DOWN_TIMER_END
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisViewModel.Companion.COUNT_UP_TIMER_END
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisViewModel.Companion.START_ANALYSIS_RESULT_ACTIVITY
import com.fitsionary.momspt.presentation.base.BaseActivity
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
                    binding.btnStart.setOnClickListener {
                        viewModel.recordStart()
                    }

                    binding.btnUpload.setOnClickListener {
                        viewModel.videoUpload()
                    }

                    viewModel.isLoading
                        .observeOn(ui())
                        .subscribe { if (it) showLoading() else hideLoading() }
                        .addTo(compositeDisposable)

                    viewModel.event.observe(this, {
                        it.getContentIfNotHandled()?.let { event ->
                            when (event.first) {
                                COUNT_DOWN_TIMER_END -> {
                                    cameraRecorder?.start(event.second)
                                }
                                COUNT_UP_TIMER_END -> {
                                    cameraRecorder?.stop()
                                    if (File(event.second).exists()) {
                                        showToast("영상촬영이 완료되었습니다.")
                                    }
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

    private fun startAnalysisResultActivity(path: String) {
        startActivity(
            Intent(this@AnalysisActivity, AnalysisResultActivity::class.java).putExtra(PATH, path)
        )
    }

    companion object {
        private val TAG = AnalysisActivity::class.simpleName
        const val PATH = "PATH"
    }
}