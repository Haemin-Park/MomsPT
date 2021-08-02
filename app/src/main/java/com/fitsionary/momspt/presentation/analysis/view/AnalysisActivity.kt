package com.fitsionary.momspt.presentation.analysis.view

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.daasuu.camerarecorder.CameraRecorder
import com.daasuu.camerarecorder.CameraRecorderBuilder
import com.daasuu.camerarecorder.LensFacing
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.ActivityAnalysisBinding
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisViewModel
import com.fitsionary.momspt.presentation.base.BaseActivity
import com.fitsionary.momspt.util.rx.ui
import io.reactivex.rxjava3.kotlin.addTo
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AnalysisActivity :
    BaseActivity<ActivityAnalysisBinding, AnalysisViewModel>(R.layout.activity_analysis) {
    override val viewModel: AnalysisViewModel by lazy {
        ViewModelProvider(this).get(AnalysisViewModel::class.java)
    }
    private var cameraRecorder: CameraRecorder? = null
    private var sampleGLView: GLSurfaceView? = null
    lateinit var path: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel

        binding.btnStart.setOnClickListener {
            path = getVideoFilePath()
            viewModel.countDownTimerStart()
            binding.tvCountDownTimer.visibility = View.VISIBLE
            binding.btnStart.visibility = View.GONE
            binding.btnStop.visibility = View.VISIBLE
        }
        binding.btnUpload.setOnClickListener {
            val file = File(path)
            viewModel.sendVideo(file)
            binding.btnUpload.visibility = View.GONE
        }
        binding.btnTestUpload.setOnClickListener {
            val test = getAndroidMoviesFolder().absolutePath
                .toString() + "/202107_29-230444cameraRecorder.mp4"
            viewModel.sendVideo(File(test))
        }

        viewModel.isLoading
            .observeOn(ui())
            .subscribe { if (it) showLoading() else hideLoading() }
            .addTo(compositeDisposable)

        viewModel.timerCountDownEnd.observe(this, {
            it.getContentIfNotHandled()?.let {
                binding.tvCountDownTimer.visibility = View.INVISIBLE
                binding.tvIntroduce.visibility = View.GONE
                binding.tvCountUpTimer.visibility = View.VISIBLE
                viewModel.countUpTimerStart()
                cameraRecorder?.start(path)
            }
        })

        viewModel.timerCountUpEnd.observe(this, {
            it.getContentIfNotHandled()?.let {
                cameraRecorder?.stop()
                exportMp4ToGallery(this, path)
                binding.tvCountUpTimer.visibility = View.GONE
                binding.btnStart.visibility = View.VISIBLE
                binding.btnStop.visibility = View.GONE
                binding.btnUpload.visibility = View.VISIBLE
                Toast.makeText(this, "$path 영상 저장이 완료되었습니다.", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        sampleGLView = GLSurfaceView(applicationContext)
        binding.wrapView.addView(sampleGLView)

        cameraRecorder = CameraRecorderBuilder(this, sampleGLView)
            .lensFacing(LensFacing.FRONT)
            .build()
    }

    override fun onPause() {
        super.onPause()

        cameraRecorder?.stop()
        cameraRecorder?.release()
        cameraRecorder = null

        binding.wrapView.removeView(sampleGLView)
        sampleGLView = null
    }

    private fun exportMp4ToGallery(context: Context, filePath: String) {
        val values = ContentValues(2)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Video.Media.DATA, filePath)
        context.contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            values
        )
        context.sendBroadcast(
            Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://$filePath")
            )
        )
    }

    @SuppressLint("SimpleDateFormat")
    fun getVideoFilePath(): String {
        return getAndroidMoviesFolder().absolutePath
            .toString() + "/" + SimpleDateFormat("yyyyMM_dd-HHmmss").format(Date()) + "cameraRecorder.mp4"
    }

    private fun getAndroidMoviesFolder(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
    }
}