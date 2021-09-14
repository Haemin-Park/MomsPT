package com.fitsionary.momspt.presentation.analysis.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Choreographer
import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.ActivityAnalysisResultBinding
import com.fitsionary.momspt.presentation.analysis.view.AnalysisFragment.Companion.PATH
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisResultViewModel
import com.fitsionary.momspt.presentation.base.BaseActivity
import com.google.android.filament.Engine
import com.google.android.filament.Fence
import com.google.android.filament.Skybox
import com.google.android.filament.utils.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.Buffer
import java.nio.ByteBuffer

class AnalysisResultActivity
    :
    BaseActivity<ActivityAnalysisResultBinding, AnalysisResultViewModel>(R.layout.activity_analysis_result) {
    override val viewModel: AnalysisResultViewModel by lazy {
        ViewModelProvider(this).get(AnalysisResultViewModel::class.java)
    }

    companion object {
        init {
            Utils.init()
        }

        private val kDefaultObjectPosition = Float3(0.0f, 0.0f, -300.0f)
        private val TAG = AnalysisResultActivity::class.simpleName
    }

    private lateinit var choreographer: Choreographer
    private val frameScheduler = FrameCallback()
    private lateinit var modelViewer: ModelViewer
    private var loadStartTime = 0L
    private var loadStartFence: Fence? = null

    private lateinit var engine: Engine

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        choreographer = Choreographer.getInstance()

        engine = Engine.create()
        val cameraManipulator = Manipulator.Builder()
            .targetPosition(
                kDefaultObjectPosition.x,
                kDefaultObjectPosition.y,
                kDefaultObjectPosition.z
            )
            .viewport(binding.surface.width, binding.surface.height)
            .build(Manipulator.Mode.ORBIT)
        modelViewer = ModelViewer(binding.surface, engine, cameraManipulator)

        binding.surface.setOnTouchListener(modelViewer)

        val path = intent.getStringExtra(PATH)
        if (path != null) {
            val byte = FileUtils.readFileToByteArray(File(path))
            val buffer = ByteBuffer.wrap(byte)

            createModel(buffer)
            createIndirectLight()
        } else {
            showToast("모델을 로드할 수 없습니다.")
        }

        modelViewer.view.apply {
            dynamicResolutionOptions = dynamicResolutionOptions.apply {
                enabled = true
            }
            ambientOcclusionOptions = ambientOcclusionOptions.apply {
                enabled = true
            }
            bloomOptions = bloomOptions.apply {
                enabled = true
            }
        }
    }

    private fun createModel(buffer: Buffer) {
        modelViewer.loadModelGlb(buffer)
        modelViewer.transformToUnitCube(kDefaultObjectPosition)
    }

    private fun createIndirectLight() {
        val scene = modelViewer.scene
        val ibl = "default_env"
        readCompressedAsset("env/$ibl/${ibl}_ibl.ktx").let {
            scene.indirectLight = KtxLoader.createIndirectLight(engine, it)
            scene.indirectLight!!.intensity = 50_00.0f
        }
        scene.skybox = Skybox.Builder().build(engine)
        scene.skybox!!.setColor(0.0f, 0.0f, 0.0f, 0.0f)
    }

    private fun readCompressedAsset(assetName: String): ByteBuffer {
        val input = assets.open(assetName)
        val bytes = ByteArray(input.available())
        input.read(bytes)
        return ByteBuffer.wrap(bytes)
    }

    override fun onResume() {
        super.onResume()
        choreographer.postFrameCallback(frameScheduler)
    }

    override fun onPause() {
        super.onPause()
        choreographer.removeFrameCallback(frameScheduler)
    }

    override fun onDestroy() {
        super.onDestroy()
        choreographer.removeFrameCallback(frameScheduler)
        modelViewer.destroyModel()

    }

    inner class FrameCallback : Choreographer.FrameCallback {
        private val startTime = System.nanoTime()
        override fun doFrame(frameTimeNanos: Long) {
            choreographer.postFrameCallback(this)

            loadStartFence?.let {
                if (it.wait(Fence.Mode.FLUSH, 0) == Fence.FenceStatus.CONDITION_SATISFIED) {
                    val end = System.nanoTime()
                    val total = (end - loadStartTime) / 1_000_000
                    Log.i(TAG, "The Filament backend took $total ms to load the model geometry.")
                    modelViewer.engine.destroyFence(it)
                    loadStartFence = null
                }
            }

            modelViewer.animator?.apply {
                if (animationCount > 0) {
                    val elapsedTimeSeconds = (frameTimeNanos - startTime).toDouble() / 1_000_000_000
                    applyAnimation(0, elapsedTimeSeconds.toFloat())
                }
                updateBoneMatrices()
            }

            modelViewer.render(frameTimeNanos)
        }
    }
}
