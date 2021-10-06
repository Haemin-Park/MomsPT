package com.fitsionary.momspt.presentation.workout.view

import android.content.Intent
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.api.response.Landmark
import com.fitsionary.momspt.data.enum.PoseEnum
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.databinding.ActivityWorkoutPlayBinding
import com.fitsionary.momspt.presentation.base.BaseActivity
import com.fitsionary.momspt.presentation.workout.view.PlayerControlDialogFragment.Companion.PLAYER_CONTROL_DIALOG_FRAGMENT_TAG
import com.fitsionary.momspt.presentation.workout.view.WorkoutResultActivity.Companion.RESULT_CUMULATIVE_SCORE
import com.fitsionary.momspt.presentation.workout.viewmodel.WorkoutPlayViewModel
import com.fitsionary.momspt.util.ScoringAlgorithm
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import com.google.mediapipe.components.*
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList
import com.google.mediapipe.framework.AndroidAssetUtil
import com.google.mediapipe.framework.Packet
import com.google.mediapipe.framework.PacketGetter
import com.google.mediapipe.glutil.EglManager
import com.google.protobuf.InvalidProtocolBufferException
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.floor


class WorkoutPlayActivity :
    BaseActivity<ActivityWorkoutPlayBinding, WorkoutPlayViewModel>(R.layout.activity_workout_play) {
    override val viewModel: WorkoutPlayViewModel by lazy {
        ViewModelProvider(
            this,
            WorkoutPlayViewModel.ViewModelFactory(application, workoutItem.workoutCode + ".mp4")
        ).get(WorkoutPlayViewModel::class.java)
    }

    private lateinit var playerControlDialogFragment: PlayerControlDialogFragment
    var isFirst = true
    var isEnd = false
    private var playWhenReady = false
    private lateinit var scoreAlgorithm: ScoringAlgorithm

    private lateinit var mediaUrl: String

    companion object {
        private val TAG = WorkoutPlayActivity::class.java.simpleName

        val Poses = listOf(
            "PoseLandmark.NOSE",
            "PoseLandmark.LEFT_EYE",
            "PoseLandmark.RIGHT_EYE",
            "PoseLandmark.LEFT_EAR",
            "PoseLandmark.RIGHT_EAR",
            "PoseLandmark.LEFT_SHOULDER",
            "PoseLandmark.RIGHT_SHOULDER",
            "PoseLandmark.LEFT_ELBOW",
            "PoseLandmark.RIGHT_ELBOW",
            "PoseLandmark.LEFT_WRIST",
            "PoseLandmark.RIGHT_WRIST",
            "PoseLandmark.LEFT_HIP",
            "PoseLandmark.RIGHT_HIP",
            "PoseLandmark.LEFT_KNEE",
            "PoseLandmark.RIGHT_KNEE",
            "PoseLandmark.LEFT_ANKLE",
            "PoseLandmark.RIGHT_ANKLE"
        )

        private const val OUTPUT_LANDMARKS_STREAM_NAME = "pose_landmarks"

        // Flips the camera-preview frames vertically by default, before sending them into FrameProcessor
        // to be processed in a MediaPipe graph, and flips the processed frames back when they are
        // displayed. This maybe needed because OpenGL represents images assuming the image origin is at
        // the bottom-left corner, whereas MediaPipe in general assumes the image origin is at the
        // top-left corner.
        // NOTE: use "flipFramesVertically" in manifest metadata to override this behavior.
        private const val FLIP_FRAMES_VERTICALLY = true

        // Number of output frames allocated in ExternalTextureConverter.
        // NOTE: use "converterNumBuffers" in manifest metadata to override number of buffers. For
        // example, when there is a FlowLimiterCalculator in the graph, number of buffers should be at
        // least `max_in_flight + max_in_queue + 1` (where max_in_flight and max_in_queue are used in
        // FlowLimiterCalculator options). That's because we need buffers for all the frames that are in
        // flight/queue plus one for the next frame from the camera.
        private const val NUM_BUFFERS = 2

        init {
            // Load all native libraries needed by the app.
            System.loadLibrary("mediapipe_jni")
            try {
                System.loadLibrary("opencv_java3")
            } catch (e: UnsatisfiedLinkError) {
                // Some example apps (e.g. template matching) require OpenCV 4.
                System.loadLibrary("opencv_java4")
            }
        }
    }


    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
    // frames onto a {@link Surface}.
    var processor: FrameProcessor? = null

    // Handles camera access via the {@link CameraX} Jetpack support library.
    private var cameraHelper: CameraXPreviewHelper? = null

    // {@link SurfaceTexture} where the camera-preview frames can be accessed.
    private var previewFrameTexture: SurfaceTexture? = null

    // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
    private var previewDisplayView: SurfaceView? = null

    // Creates and manages an {@link EGLContext}.
    private var eglManager: EglManager? = null

    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
    // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
    private var converter: ExternalTextureConverter? = null

    private var player: SimpleExoPlayer? = null
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    // DTW에 이용할 카메라 가로, 세로 크기
    private var parentHeight = 0
    private var parentWidth = 0

    private lateinit var poseLandmarks: NormalizedLandmarkList
    private lateinit var workoutItem: WorkoutModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val safeArgs: WorkoutPlayActivityArgs by navArgs()
        workoutItem = safeArgs.workout
        mediaUrl = workoutItem.video

        binding.vm = viewModel

        previewDisplayView = SurfaceView(this)
        setupPreviewDisplayView()

        // Initialize asset manager so that MediaPipe native libraries can access the app assets, e.g.,
        // binary graphs.
        AndroidAssetUtil.initializeNativeAssetManager(this)
        eglManager = EglManager(null)
        processor = FrameProcessor(
            this,
            eglManager!!.nativeContext,
            applicationInfo.metaData.getString("binaryGraphName"),
            applicationInfo.metaData.getString("inputVideoStreamName"),
            applicationInfo.metaData.getString("outputVideoStreamName")
        )

        viewModel.workoutLandmarks.observe(this, { landmarks ->
            if (!::scoreAlgorithm.isInitialized) {
                Timber.i("Landmarks 로드 완료 " + landmarks.poseData.count())
                scoreAlgorithm = ScoringAlgorithm(landmarks)
            }
        })
        processor!!
            .videoSurfaceOutput
            .setFlipY(
                applicationInfo.metaData.getBoolean(
                    "flipFramesVertically",
                    FLIP_FRAMES_VERTICALLY
                )
            )
        processor!!.addPacketCallback(
            OUTPUT_LANDMARKS_STREAM_NAME
        ) { packet: Packet ->
            try {
                if (playWhenReady && ::scoreAlgorithm.isInitialized) {
                    val landmarksRaw = PacketGetter.getProtoBytes(packet)
                    poseLandmarks = NormalizedLandmarkList.parseFrom(landmarksRaw)
                    var idx = 0
                    val keyPoints = ArrayList<Landmark>()
                    for ((landmarkIndex, landmark) in poseLandmarks.landmarkList.withIndex()) {
                        if (Poses.any { it == "PoseLandmark." + PoseEnum.values()[landmarkIndex].name }) {
                            keyPoints.add(
                                Landmark(
                                    "PoseLandmark." + PoseEnum.values()[landmarkIndex].name,
                                    landmark.x.toDouble(),
                                    landmark.y.toDouble(),
                                    landmark.z.toDouble(),
                                    landmark.visibility.toDouble()
                                )
                            )
                            idx++
                        }
                    }
                    val resultScore =
                        scoreAlgorithm.pushKeyPoints(keyPoints, parentWidth, parentHeight)

                    if (resultScore > 0) {
                        viewModel.setScore(floor(resultScore).toInt())
                    }
                }
            } catch (exception: InvalidProtocolBufferException) {
                Log.e(TAG, "Failed to get proto.", exception)
            }
        }
        PermissionHelper.checkAndRequestCameraPermissions(this)
    }

    override fun onResume() {
        super.onResume()
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }

        converter = ExternalTextureConverter(
            eglManager!!.context,
            applicationInfo.metaData.getInt(
                "converterNumBuffers",
                NUM_BUFFERS
            )
        )
        converter!!.setFlipY(
            applicationInfo.metaData.getBoolean(
                "flipFramesVertically",
                FLIP_FRAMES_VERTICALLY
            )
        )
        converter!!.setConsumer(processor)
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            startCamera()
        }
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
        converter?.close()
        viewModel.countDownTimerStop()

        // Hide preview display until we re-open the camera again.
        previewDisplayView!!.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun onCameraStarted(surfaceTexture: SurfaceTexture?) {
        previewFrameTexture = surfaceTexture
        // Make the display view visible to start showing the preview. This triggers the
        // SurfaceHolder.Callback added to (the holder of) previewDisplayView.
        previewDisplayView!!.visibility = View.VISIBLE
    }

    private fun cameraTargetResolution(): Size? {
        return null // No preference and let the camera (helper) decide.
    }

    private fun startCamera() {
        cameraHelper = CameraXPreviewHelper()
        cameraHelper!!.setOnCameraStartedListener { surfaceTexture ->
            onCameraStarted(surfaceTexture)
        }
        val cameraFacing: CameraHelper.CameraFacing = CameraHelper.CameraFacing.FRONT

        cameraHelper!!.startCamera(
            this, cameraFacing, null, cameraTargetResolution()
        )
    }

    private fun computeViewSize(width: Int, height: Int): Size {
        return Size(width, height)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        parentWidth = binding.previewDisplayLayout.width
        parentHeight = binding.previewDisplayLayout.height
    }

    fun onPreviewDisplaySurfaceChanged(
        width: Int, height: Int
    ) {
        // (Re-)Compute the ideal size of the camera-preview display (the area that the
        // camera-preview frames get rendered onto, potentially with scaling and rotation)
        // based on the size of the SurfaceView that contains the display.
        val viewSize: Size = computeViewSize(width, height)
        val displaySize: Size = cameraHelper!!.computeDisplaySizeFromViewSize(viewSize)
        converter!!.setRotation(1)
        // Connect the converter to the camera-preview frames as its input (via
        // previewFrameTexture), and configure the output width and height as the computed
        // display size.
        converter!!.setSurfaceTextureAndAttachToGLContext(
            previewFrameTexture, displaySize.height, displaySize.width
        )
    }

    private fun setupPreviewDisplayView() {
        runOnUiThread {
            run {
                previewDisplayView!!.visibility = View.GONE
                val viewGroup = binding.previewDisplayLayout
                viewGroup.addView(previewDisplayView)
                previewDisplayView!!
                    .holder
                    .addCallback(
                        object : SurfaceHolder.Callback {
                            override fun surfaceCreated(holder: SurfaceHolder) {
                                processor?.videoSurfaceOutput!!.setSurface(holder.surface)
                            }

                            override fun surfaceChanged(
                                holder: SurfaceHolder,
                                format: Int,
                                width: Int,
                                height: Int
                            ) {
                                onPreviewDisplaySurfaceChanged(width, height)
                            }

                            override fun surfaceDestroyed(holder: SurfaceHolder) {
                                processor!!.videoSurfaceOutput.setSurface(null)
                            }
                        })
            }
        }
    }

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
        playWhenReady = true

        binding.playerView.apply {
            controllerAutoShow = false
            useController = false
            videoSurfaceView?.setOnClickListener {
                playerControlDialogFragment =
                    PlayerControlDialogFragment.newInstance(playWhenReady)
                playerControlDialogFragment.setOnClickedListener {
                    if (!isEnd) {
                        if (playWhenReady) {
                            player!!.playWhenReady = false
                            playWhenReady = false
                            viewModel.countDownTimerStop()
                            playerControlDialogFragment.setState(isPlaying = false)

                        } else {
                            player!!.playWhenReady = true
                            playWhenReady = true
                            viewModel.countDownTimerStart()
                            playerControlDialogFragment.setState(isPlaying = true)
                        }
                    }
                }
                playerControlDialogFragment.show(
                    supportFragmentManager,
                    PLAYER_CONTROL_DIALOG_FRAGMENT_TAG
                )
            }
        }

        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if (playWhenReady && state == Player.STATE_READY) {
                    // media actually playing
                    if (isFirst) {
                        viewModel.countDownTimerSet(player!!.contentDuration)
                        isFirst = false
                    }
                    viewModel.countDownTimerStart()
                } else if (playWhenReady) {
                    // might be idle (plays after prepare()),
                    // buffering (plays when data available)
                    // or ended (plays when seek away from end)
                    viewModel.countDownTimerStop()
                    if (state == Player.STATE_ENDED) {
                        isEnd = true
                        val intent =
                            Intent(this@WorkoutPlayActivity, WorkoutResultActivity::class.java)
                        intent.putExtra(RESULT_CUMULATIVE_SCORE, viewModel.cumulativeScore.value)
                        startActivity(intent)
                        finish()
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
            playWhenReady = false
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.release()
            player = null
        }
    }
}
