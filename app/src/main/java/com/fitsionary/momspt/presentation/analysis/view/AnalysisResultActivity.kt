package com.fitsionary.momspt.presentation.analysis.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.ActivityAnalysisResultBinding
import com.fitsionary.momspt.presentation.analysis.viewmodel.AnalysisResultViewModel
import com.fitsionary.momspt.presentation.base.BaseActivity
import com.google.ar.core.Anchor
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import kotlinx.android.synthetic.main.activity_analysis_result.*
import java.util.concurrent.CompletionException


open class AnalysisResultActivity
    : BaseActivity<ActivityAnalysisResultBinding, AnalysisResultViewModel>(R.layout.activity_analysis_result) {
    override val viewModel: AnalysisResultViewModel by lazy {
        ViewModelProvider(this).get(AnalysisResultViewModel::class.java)
    }
    private var remoteModelUrl =
        "https://poly.googleusercontent.com/downloads/0BnDT3T1wTE/85QOHCZOvov/Mesh_Beagle.gltf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        renderRemoteObject()
    }

    private fun renderRemoteObject() {

        skuProgressBar.visibility = View.VISIBLE
        ModelRenderable.builder()
            .setSource(
                this, RenderableSource.Builder().setSource(
                    this,
                    Uri.parse(remoteModelUrl),
                    RenderableSource.SourceType.GLTF2
                ).setScale(0.01f)
                    .setRecenterMode(RenderableSource.RecenterMode.CENTER)
                    .build()
            )
            .setRegistryId(remoteModelUrl)
            .build()
            .thenAccept { modelRenderable: ModelRenderable ->
                skuProgressBar.visibility = View.GONE
                addNodeToScene(modelRenderable)
            }
            .exceptionally { throwable: Throwable? ->
                var message: String?
                message = if (throwable is CompletionException) {
                    skuProgressBar.setVisibility(View.GONE)
                    "Internet is not working"
                } else {
                    skuProgressBar.setVisibility(View.GONE)
                    "Can't load Model"
                }
                val mainHandler = Handler(Looper.getMainLooper())
                val finalMessage: String = message
                val myRunnable = Runnable {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage(finalMessage + "")
                        .setPositiveButton("Retry") { dialogInterface: DialogInterface, _: Int ->
                            renderRemoteObject()
                            dialogInterface.dismiss()
                        }
                        .setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
                        .show()
                }
                mainHandler.post(myRunnable)
                null
            }
    }

    override fun onPause() {
        super.onPause()
        sceneView.pause()
    }

    private fun addNodeToScene(model: ModelRenderable) {
        if (sceneView != null) {
            val transformationSystem = makeTransformationSystem()
            val dragTransformableNode = DragTransformableNode(1f, transformationSystem)
            dragTransformableNode.renderable = model
            sceneView.scene.addChild(dragTransformableNode)
            dragTransformableNode.select()
            sceneView.scene
                .addOnPeekTouchListener { hitTestResult: HitTestResult?, motionEvent: MotionEvent? ->
                    transformationSystem.onTouch(
                        hitTestResult,
                        motionEvent
                    )
                }
        }
    }

    private fun makeTransformationSystem(): TransformationSystem {
        val footprintSelectionVisualizer = FootprintSelectionVisualizer()
        return TransformationSystem(resources.displayMetrics, footprintSelectionVisualizer)
    }

    override fun onResume() {
        super.onResume()
        try {
            sceneView.resume()
        } catch (e: CameraNotAvailableException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            sceneView.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}