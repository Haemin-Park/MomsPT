package com.fitsionary.momspt.presentation.analysis.view

import android.Manifest
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daasuu.camerarecorder.CameraRecorder
import com.daasuu.camerarecorder.CameraRecorderBuilder
import com.daasuu.camerarecorder.LensFacing
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentAnalysisBinding
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisViewModel
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisViewModel.Companion.COUNT_DOWN_TIMER_END
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisViewModel.Companion.COUNT_UP_TIMER_END
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.util.rx.ui
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class AnalysisFragment :
    BaseFragment<FragmentAnalysisBinding, AnalysisViewModel>(R.layout.fragment_analysis) {
    override val viewModel: AnalysisViewModel by lazy {
        ViewModelProvider(this).get(AnalysisViewModel::class.java)
    }
    private var cameraRecorder: CameraRecorder? = null
    private var sampleGLView: GLSurfaceView? = null
    private lateinit var rxPermissions: RxPermissions

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeArgs: AnalysisFragmentArgs by navArgs()
        val direction = safeArgs.direction
        val signUpRequest = safeArgs.signUpRequest

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
                    viewModel.isLoading
                        .observeOn(ui())
                        .subscribe { if (it) showLoading() else hideLoading() }
                        .addTo(compositeDisposable)

                    findNavController().navigate(AnalysisFragmentDirections.actionAnalysisFragmentToCustomBodyAnalysisGuideDialog())

                    viewModel.event.observe(viewLifecycleOwner, {
                        it.getContentIfNotHandled()?.let { event ->
                            when (event.first) {
                                COUNT_DOWN_TIMER_END -> {
                                    cameraRecorder?.start(event.second)
                                }
                                COUNT_UP_TIMER_END -> {
                                    cameraRecorder?.stop()
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        showLoading()
                                        delay(3000L)
                                        launch(Dispatchers.Main) {
                                            if (File(event.second).exists()) {
                                                findNavController().navigate(
                                                    AnalysisFragmentDirections.actionAnalysisFragmentToRecordPreviewFragment(
                                                        direction, event.second, signUpRequest
                                                    )
                                                )
                                            }
                                        }
                                        hideLoading()
                                    }
                                }
                            }
                        }
                    })
                } else {
                    showToast("?????? ????????? ???????????? ????????? ???????????? ???????????? ??? ????????????.")
                }
            }
    }

    override fun onResume() {
        super.onResume()
        if (rxPermissions.isGranted(Manifest.permission.CAMERA)) {
            sampleGLView = GLSurfaceView(activity?.applicationContext)
            binding.wrapView.addView(sampleGLView)
            cameraRecorder = CameraRecorderBuilder(activity, sampleGLView)
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
}