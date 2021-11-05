package com.fitsionary.momspt.presentation.analysis.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Choreographer
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.enum.DirectionEnum
import com.fitsionary.momspt.databinding.FragmentAnalysisResultBinding
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisResultViewModel
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.google.android.filament.Engine
import com.google.android.filament.Fence
import com.google.android.filament.Skybox
import com.google.android.filament.utils.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.Buffer
import java.nio.ByteBuffer

class AnalysisResultFragment
    :
    BaseFragment<FragmentAnalysisResultBinding, AnalysisResultViewModel>(R.layout.fragment_analysis_result) {
    override val viewModel: AnalysisResultViewModel by lazy {
        ViewModelProvider(this).get(AnalysisResultViewModel::class.java)
    }

    companion object {
        init {
            Utils.init()
        }

        private val kDefaultObjectPosition = Float3(-300.0f, 0.0f, 0.0f)
        private val TAG = AnalysisResultFragment::class.simpleName
    }

    private var isSuccess = false
    private lateinit var choreographer: Choreographer
    private val frameScheduler = FrameCallback()
    private lateinit var modelViewer: ModelViewer
    private var loadStartTime = 0L
    private var loadStartFence: Fence? = null

    private lateinit var engine: Engine

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        val safeArgs: AnalysisResultFragmentArgs by navArgs()
        val bodyAnalysisResult = safeArgs.bodyAnalysisResultModel
        binding.bodyAnalysisResult = bodyAnalysisResult

        val direction = safeArgs.direction
        val path = bodyAnalysisResult.filePath

        if (File(path).exists()) {
            isSuccess = true

            val byte = FileUtils.readFileToByteArray(File(path))
            val buffer = ByteBuffer.wrap(byte)

            createModel(buffer)
            createIndirectLight()

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
        } else {
            showToast("파일을 로드할 수 없습니다.")
        }

        binding.btnGoMain.setOnClickListener {
            when (direction) {
                DirectionEnum.TO_MAIN -> findNavController().navigate(
                    AnalysisResultFragmentDirections.actionAnalysisResultFragmentToMainHome()
                )
                DirectionEnum.TO_DAILY -> findNavController().navigate(
                    AnalysisResultFragmentDirections.actionAnalysisResultFragmentToMainDaily()
                )
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
        val input = requireContext().assets.open(assetName)
        val bytes = ByteArray(input.available())
        input.read(bytes)
        return ByteBuffer.wrap(bytes)
    }

    override fun onResume() {
        super.onResume()
        if (isSuccess)
            choreographer.postFrameCallback(frameScheduler)
    }

    override fun onPause() {
        super.onPause()
        if (isSuccess)
            choreographer.removeFrameCallback(frameScheduler)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isSuccess) {
            choreographer.removeFrameCallback(frameScheduler)
            modelViewer.destroyModel()
        }
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
