package com.fitsionary.momspt.presentation.workout.view

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.ActivityWorkoutStartBinding
import com.fitsionary.momspt.presentation.base.BaseActivity
import com.fitsionary.momspt.presentation.workout.viewmodel.WorkoutStartViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.dialog_player_control.*

class WorkoutStartActivity :
    BaseActivity<ActivityWorkoutStartBinding, WorkoutStartViewModel>(R.layout.activity_workout_start) {
    override val viewModel: WorkoutStartViewModel by lazy {
        ViewModelProvider(this).get(WorkoutStartViewModel::class.java)
    }
    var isFirst = true
    var isEnd = false

    private lateinit var mediaUrl: String

//    companion object {
//        private const val TAG = "MainActivity"
//
//        private const val OUTPUT_LANDMARKS_STREAM_NAME = "pose_landmarks"
//
//        // Flips the camera-preview frames vertically by default, before sending them into FrameProcessor
//        // to be processed in a MediaPipe graph, and flips the processed frames back when they are
//        // displayed. This maybe needed because OpenGL represents images assuming the image origin is at
//        // the bottom-left corner, whereas MediaPipe in general assumes the image origin is at the
//        // top-left corner.
//        // NOTE: use "flipFramesVertically" in manifest metadata to override this behavior.
//        private const val FLIP_FRAMES_VERTICALLY = true
//
//        // Number of output frames allocated in ExternalTextureConverter.
//        // NOTE: use "converterNumBuffers" in manifest metadata to override number of buffers. For
//        // example, when there is a FlowLimiterCalculator in the graph, number of buffers should be at
//        // least `max_in_flight + max_in_queue + 1` (where max_in_flight and max_in_queue are used in
//        // FlowLimiterCalculator options). That's because we need buffers for all the frames that are in
//        // flight/queue plus one for the next frame from the camera.
//        private const val NUM_BUFFERS = 2
//
//        init {
//            // Load all native libraries needed by the app.
//            System.loadLibrary("mediapipe_jni")
//            try {
//                System.loadLibrary("opencv_java3")
//            } catch (e: UnsatisfiedLinkError) {
//                // Some example apps (e.g. template matching) require OpenCV 4.
//                System.loadLibrary("opencv_java4")
//            }
//        }
//    }
//
//
//    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
//    // frames onto a {@link Surface}.
//    protected var processor: FrameProcessor? = null
//
//    // Handles camera access via the {@link CameraX} Jetpack support library.
//    private var cameraHelper: CameraXPreviewHelper? = null
//
//    // {@link SurfaceTexture} where the camera-preview frames can be accessed.
//    private var previewFrameTexture: SurfaceTexture? = null
//
//    // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
//    private var previewDisplayView: SurfaceView? = null
//
//    // Creates and manages an {@link EGLContext}.
//    private var eglManager: EglManager? = null
//
//    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
//    // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
//    private var converter: ExternalTextureConverter? = null

    private var player: SimpleExoPlayer? = null
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mediaUrl = getString(R.string.ex_video_3)
        binding.vm = viewModel
//        viewModel.timerCountDown.observe(this, Observer {
//            if (this::poseLandmarks.isInitialized) {
//                val poseRequest = PoseRequest(
//                    "TestVideo", viewHeight, viewWidth,
//                    trainingViewModel.timerCountUp.value!!, getPoseList(poseLandmarks)
//                )
//                viewmodel.sendPoseList(poseRequest)
//            }
//        })

//        previewDisplayView = SurfaceView(this)
//        setupPreviewDisplayView()
//
//        // Initialize asset manager so that MediaPipe native libraries can access the app assets, e.g.,
//        // binary graphs.
//        AndroidAssetUtil.initializeNativeAssetManager(this)
//        eglManager = EglManager(null)
//        processor = FrameProcessor(
//            this,
//            eglManager!!.nativeContext,
//            applicationInfo.metaData.getString("binaryGraphName"),
//            applicationInfo.metaData.getString("inputVideoStreamName"),
//            applicationInfo.metaData.getString("outputVideoStreamName")
//        )
//        processor!!
//            .videoSurfaceOutput
//            .setFlipY(
//                applicationInfo.metaData.getBoolean(
//                    "flipFramesVertically",
//                    FLIP_FRAMES_VERTICALLY
//                )
//            )
//        processor!!.addPacketCallback(
//            OUTPUT_LANDMARKS_STREAM_NAME
//        ) { packet: Packet ->
//            Log.v(TAG, "Received pose landmarks packet.")
//            try {
//                val landmarksRaw = PacketGetter.getProtoBytes(packet)
//                poseLandmarks = NormalizedLandmarkList.parseFrom(landmarksRaw)
//            } catch (exception: InvalidProtocolBufferException) {
//                Log.e(TAG, "Failed to get proto.", exception)
//            }
//        }
//        PermissionHelper.checkAndRequestCameraPermissions(this)
    }
//
//    // Used to obtain the content view for this application. If you are extending this class, and
//    // have a custom layout, override this method and return the custom layout.
//    private val contentViewLayoutResId: Int
//        get() = R.layout.activity_training

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24 || player == null) {
            initializePlayer()
        }

//        converter = ExternalTextureConverter(
//            eglManager!!.context,
//            applicationInfo.metaData.getInt(
//                "converterNumBuffers",
//                NUM_BUFFERS
//            )
//        )
//        converter!!.setFlipY(
//            applicationInfo.metaData.getBoolean(
//                "flipFramesVertically",
//                FLIP_FRAMES_VERTICALLY
//            )
//        )
//        converter!!.setConsumer(processor)
//        if (PermissionHelper.cameraPermissionsGranted(this)) {
//            startCamera()
//        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }

//        converter?.close()
//        trainingViewModel.timerStop()
//
//        // Hide preview display until we re-open the camera again.
//        previewDisplayView!!.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<String>, grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
//
//    private fun onCameraStarted(surfaceTexture: SurfaceTexture?) {
//        previewFrameTexture = surfaceTexture
//        // Make the display view visible to start showing the preview. This triggers the
//        // SurfaceHolder.Callback added to (the holder of) previewDisplayView.
//        previewDisplayView!!.visibility = View.VISIBLE
//    }
//
//    private fun cameraTargetResolution(): Size? {
//        return null // No preference and let the camera (helper) decide.
//    }
//
//    private fun startCamera() {
//        cameraHelper = CameraXPreviewHelper()
//        cameraHelper!!.setOnCameraStartedListener { surfaceTexture -> onCameraStarted(surfaceTexture) }
//        val cameraFacing: CameraHelper.CameraFacing = if (applicationInfo.metaData.getBoolean(
//                "cameraFacingFront",
//                false
//            )
//        ) CameraHelper.CameraFacing.FRONT else CameraHelper.CameraFacing.BACK
//        cameraHelper!!.startCamera(
//            this, cameraFacing,  /*unusedSurfaceTexture=*/null, cameraTargetResolution()
//        )
//    }
//
//    private fun computeViewSize(width: Int, height: Int): Size {
//        return Size(width, height)
//    }
//
//    protected fun onPreviewDisplaySurfaceChanged(
//        width: Int, height: Int
//    ) {
//        // (Re-)Compute the ideal size of the camera-preview display (the area that the
//        // camera-preview frames get rendered onto, potentially with scaling and rotation)
//        // based on the size of the SurfaceView that contains the display.
//        val viewSize: Size = computeViewSize(width, height)
//        val displaySize: Size = cameraHelper!!.computeDisplaySizeFromViewSize(viewSize)
//        val isCameraRotated: Boolean = cameraHelper!!.isCameraRotated
//
//        converter!!.setRotation(1)
//        // Connect the converter to the camera-preview frames as its input (via
//        // previewFrameTexture), and configure the output width and height as the computed
//        // display size.
//        converter!!.setSurfaceTextureAndAttachToGLContext(
//            previewFrameTexture,
//            if (isCameraRotated) {
//                displaySize.height
//            } else {
//                displaySize.width
//            },
//            if (isCameraRotated) {
//                displaySize.width
//            } else {
//                displaySize.height
//            }
//        )
//        if (isCameraRotated) {
//            viewHeight = displaySize.height
//            viewWidth = displaySize.width
//        } else {
//            viewHeight = displaySize.width
//            viewWidth = displaySize.height
//        }
//    }
//
//    private fun setupPreviewDisplayView() {
//        runOnUiThread {
//            run {
//                previewDisplayView!!.visibility = View.GONE
//                val viewGroup = findViewById<ViewGroup>(R.id.preview_display_layout)
//                viewGroup.addView(previewDisplayView)
//                previewDisplayView!!
//                    .holder
//                    .addCallback(
//                        object : SurfaceHolder.Callback {
//                            override fun surfaceCreated(holder: SurfaceHolder) {
//                                processor?.videoSurfaceOutput!!.setSurface(holder.surface)
//                            }
//
//                            override fun surfaceChanged(
//                                holder: SurfaceHolder,
//                                format: Int,
//                                width: Int,
//                                height: Int
//                            ) {
//                                onPreviewDisplaySurfaceChanged(width, height)
//                            }
//
//                            override fun surfaceDestroyed(holder: SurfaceHolder) {
//                                processor!!.videoSurfaceOutput.setSurface(null)
//                            }
//                        })
//            }
//        }
//
//    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        binding.playerView.player = player
        val mediaItem = MediaItem.fromUri(mediaUrl)
        player!!.apply {
            playWhenReady = true
            setMediaItem(mediaItem)
            seekTo(currentWindow, playbackPosition)
            prepare()
        }

        binding.playerView.apply {
            controllerAutoShow = false
            useController = false
            videoSurfaceView?.setOnClickListener {
                val dlg = PlayerControlDialog(this@WorkoutStartActivity)
                dlg.setOnClickedListener {
                    if (!isEnd) {
                        if (player!!.playWhenReady) {
                            player!!.playWhenReady = false
                            viewModel.countDownTimerStop()
                            dlg.btn_control.setImageResource(R.drawable.exo_controls_play)

                        } else {
                            player!!.playWhenReady = true
                            viewModel.countDownTimerStart()
                            dlg.btn_control.setImageResource(R.drawable.exo_controls_pause)
                        }
                    }
                }
                dlg.start()
            }
        }

        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if (player!!.playWhenReady && state == Player.STATE_READY) {
                    // media actually playing
                    if (isFirst) {
                        viewModel.countDownTimerSet(player!!.contentDuration)
                        isFirst = false
                    }
                    viewModel.countDownTimerStart()
                } else if (player!!.playWhenReady) {
                    // might be idle (plays after prepare()),
                    // buffering (plays when data available)
                    // or ended (plays when seek away from end)
                    if (state == Player.STATE_ENDED) {
                        isEnd = true
                        val intent =
                            Intent(this@WorkoutStartActivity, WorkoutResultActivity::class.java)
                        intent.putExtra("result_cumulative_score", viewModel.cumulativeScore.value)
                        startActivity(intent)
                        finish()
                    } else {
                        viewModel.countDownTimerStop()
                    }
                } else {
                    // player paused in any state
                    viewModel.countDownTimerStop()
                }
            }
        })
    }

    private fun releasePlayer() {
        if (player != null) {
            player!!.playWhenReady = false
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.release()
            player = null
        }
    }
//
//    private fun getPoseLandmarksDebugString(poseLandmarks: NormalizedLandmarkList): String {
//        var poseLandmarkStr = """
//              Pose landmarks: ${poseLandmarks.landmarkCount}
//
//              """.trimIndent()
//        for ((landmarkIndex, landmark) in poseLandmarks.landmarkList.withIndex()) {
//            poseLandmarkStr += """	Landmark [$landmarkIndex]: (${landmark.x}, ${landmark.y}, ${landmark.z})
//"""
//        }
//        return poseLandmarkStr
//    }
//
//    private fun getPoseList(poseLandmarks: NormalizedLandmarkList): List<Pose> {
//        val poseList = mutableListOf<Pose>()
//        poseList.clear()
//
//        for ((landmarkIndex, landmark) in poseLandmarks.landmarkList.withIndex()) {
//            poseList.add(
//                Pose(
//                    "PoseLandmark." + PoseEnum.values()[landmarkIndex].name,
//                    Position(landmark.x, landmark.y, landmark.z),
//                    landmark.visibility
//                )
//            )
//        }
//        return poseList
//    }
}