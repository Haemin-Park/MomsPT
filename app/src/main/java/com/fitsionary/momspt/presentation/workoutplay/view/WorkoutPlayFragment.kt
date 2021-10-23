package com.fitsionary.momspt.presentation.workoutplay.view

import android.app.Application
import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.api.response.Landmark
import com.fitsionary.momspt.data.enum.CountingWorkoutEnum
import com.fitsionary.momspt.data.enum.EntirePoseEnum
import com.fitsionary.momspt.data.enum.WorkoutPoseEnum
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.databinding.FragmentWorkoutPlayBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.guide.view.GuideDialogFragment
import com.fitsionary.momspt.presentation.workoutplay.view.PlayerControlDialogFragment.Companion.PLAYER_CONTROL_DIALOG_FRAGMENT_TAG
import com.fitsionary.momspt.presentation.workoutplay.viewmodel.WorkoutPlayViewModel
import com.fitsionary.momspt.util.WorkoutAnalysisAlgorithm
import com.fitsionary.momspt.util.rx.ui
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
import kotlin.math.floor


class WorkoutPlayFragment :
    BaseFragment<FragmentWorkoutPlayBinding, WorkoutPlayViewModel>(R.layout.fragment_workout_play) {
    val safeArgs: WorkoutPlayFragmentArgs by navArgs()
    lateinit var application: Application
    override val viewModel: WorkoutPlayViewModel by lazy {
        ViewModelProvider(
            this,
            WorkoutPlayViewModel.ViewModelFactory(application, workoutItem.workoutCode)
        ).get(WorkoutPlayViewModel::class.java)
    }

    private lateinit var playerControlDialogFragment: PlayerControlDialogFragment
    var isFirst = true
    var isEnd = false
    private var playWhenReady = false
    private lateinit var workoutAnalysisAlgorithm: WorkoutAnalysisAlgorithm

    private lateinit var mediaUrl: String

    companion object {
        private val TAG = WorkoutPlayFragment::class.java.simpleName

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        application = requireNotNull(activity).application
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workoutItem = safeArgs.workout
        binding.vm = viewModel

        showWorkoutGuide()

        view.viewTreeObserver?.addOnWindowFocusChangeListener { _ ->
            binding.previewDisplayLayout.run {
                parentWidth = width
                parentHeight = height
            }
        }

        mediaUrl = workoutItem.video

        previewDisplayView = SurfaceView(application)
        setupPreviewDisplayView()

        // Initialize asset manager so that MediaPipe native libraries can access the app assets, e.g.,
        // binary graphs.
        AndroidAssetUtil.initializeNativeAssetManager(application)
        eglManager = EglManager(null)
        application.run {
            processor = FrameProcessor(
                this,
                eglManager!!.nativeContext,
                applicationInfo.metaData.getString("binaryGraphName"),
                applicationInfo.metaData.getString("inputVideoStreamName"),
                applicationInfo.metaData.getString("outputVideoStreamName")
            )
        }
        viewModel.workoutLandmarks.observe(viewLifecycleOwner, { landmarks ->
            if (!::workoutAnalysisAlgorithm.isInitialized) {
                Timber.i("Landmarks 로드 완료 " + landmarks.poseData.count())
                workoutAnalysisAlgorithm = WorkoutAnalysisAlgorithm(landmarks)
            }
        })
        viewModel.isGuide.observeOn(ui()).subscribe { if (it) showAiGuide() else hideAiGuide() }
        processor!!
            .videoSurfaceOutput
            .setFlipY(
                application.applicationInfo.metaData.getBoolean(
                    "flipFramesVertically",
                    FLIP_FRAMES_VERTICALLY
                )
            )

        var angle: Double?
        var up = false
        var down = false
        var reset = false
        var count = 0
        processor!!.addPacketCallback(
            OUTPUT_LANDMARKS_STREAM_NAME
        ) { packet: Packet ->
            try {
                if (isFirst) {
                    hideWorkoutGuide()
                    activity?.runOnUiThread {
                        isFirst = false
                        startPlayer()
                    }
                }
                if (playWhenReady && ::workoutAnalysisAlgorithm.isInitialized) {
                    val landmarksRaw = PacketGetter.getProtoBytes(packet)
                    poseLandmarks = NormalizedLandmarkList.parseFrom(landmarksRaw)
                    var idx = 0
                    val keyPoints = ArrayList<Landmark>()
                    for ((landmarkIndex, landmark) in poseLandmarks.landmarkList.withIndex()) {
                        if (WorkoutPoseEnum.values()
                                .any { it.name == EntirePoseEnum.values()[landmarkIndex].name }
                        ) {
                            keyPoints.add(
                                Landmark(
                                    EntirePoseEnum.values()[landmarkIndex].name,
                                    landmark.x.toDouble(),
                                    landmark.y.toDouble(),
                                    landmark.z.toDouble(),
                                    landmark.visibility.toDouble()
                                )
                            )
                            idx++
                        }
                    }

                    val workoutCode = workoutItem.workoutCode
                    val resultScore: Double
                    // 카운팅 대상 운동
                    if (workoutCode in CountingWorkoutEnum.values().map { it.name }) {
                        val angles = workoutAnalysisAlgorithm.calculateAngles(keyPoints)
                        val wState = workoutAnalysisAlgorithm.workoutSelector(
                            angles,
                            workoutCode,
                            up,
                            down,
                            reset,
                            count
                        )
                        angle = wState.angle
                        up = wState.up
                        down = wState.down
                        reset = wState.reset
                        count = wState.count
                        Log.e(TAG, "Angle : " + angle.toString() + " " + count.toString())
                        resultScore = count.toDouble()
                    } else { // 유사도 측정 후 점수화 운동
                        resultScore =
                            workoutAnalysisAlgorithm.pushKeyPoints(
                                keyPoints,
                                parentWidth,
                                parentHeight
                            )
                    }

                    if (resultScore > 0) {
                        viewModel.setScore(floor(resultScore).toInt())
                    }
                }
            } catch (exception: InvalidProtocolBufferException) {
                Log.e(TAG, "Failed to get proto.", exception)
            }
        }
        PermissionHelper.checkAndRequestCameraPermissions(activity)
    }

    override fun onResume() {
        super.onResume()
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }

        application.run {
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
        }

        converter!!.setConsumer(processor)
        if (PermissionHelper.cameraPermissionsGranted(activity)) {
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
            activity, cameraFacing, null, cameraTargetResolution()
        )
    }

    private fun computeViewSize(width: Int, height: Int): Size {
        return Size(width, height)
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
        requireActivity().runOnUiThread {
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
        player = SimpleExoPlayer.Builder(application).build()
        binding.playerView.player = player
        val mediaItem = MediaItem.fromUri(mediaUrl)
        player!!.apply {
            playWhenReady = false
            setMediaItem(mediaItem)
            seekTo(currentWindow, playbackPosition)
            prepare()
        }
        playWhenReady = false
    }

    private fun settingPlayer() {
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
                    childFragmentManager,
                    PLAYER_CONTROL_DIALOG_FRAGMENT_TAG
                )
            }
        }

        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if (playWhenReady && state == Player.STATE_READY) {
                    // media actually playing
                } else if (playWhenReady) {
                    // might be idle (plays after prepare()),
                    // buffering (plays when data available)
                    // or ended (plays when seek away from end)
                    if (state == Player.STATE_BUFFERING) {
                        viewModel.countDownTimerStop()
                    }
                } else {
                    // player paused in any state
                    //viewModel.countDownTimerStop()
                }
            }
        })

//        viewModel.timerCountDown.observe(viewLifecycleOwner, {
//            it?.let {
//                if (it <= 0L) {
//                    isEnd = true
//                    val resultScore = if (viewModel.cumulativeScore.value?.toInt() != 0) {
//                        viewModel.cumulativeScore.value!!.div(viewModel.cnt.value!!).toInt()
//                    } else
//                        0
//                    findNavController().navigate(
//                        WorkoutPlayFragmentDirections.actionWorkoutPlayFragmentToWorkoutResultFragment(
//                            workoutItem, resultScore
//                        )
//                    )
//                }
//            }
//        })
        viewModel.isWorkoutEnd.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    isEnd = true
                    val resultScore = if (viewModel.cumulativeScore.value?.toInt() != 0) {
                        viewModel.cumulativeScore.value!!.div(viewModel.cnt.value!!).toInt()
                    } else
                        0
                    findNavController().navigate(
                        WorkoutPlayFragmentDirections.actionWorkoutPlayFragmentToWorkoutResultFragment(
                            workoutItem, resultScore
                        )
                    )
                }
            }
        })
    }

    private fun startPlayer() {
        playWhenReady = true
        player!!.playWhenReady = true
        viewModel.timerSet(player!!.contentDuration, workoutItem.aiStartTime)
        viewModel.countDownTimerStart()
        settingPlayer()
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

    private var guideDialogFragment: GuideDialogFragment? = null

    private fun showAiGuide() {
        activity?.runOnUiThread {
            if (guideDialogFragment != null || (guideDialogFragment?.dialog)?.isShowing == true) {
                return@runOnUiThread
            } else {
                guideDialogFragment = GuideDialogFragment.GuideDialogBuilder().create()
                guideDialogFragment?.show(
                    childFragmentManager,
                    GuideDialogFragment.AI_GUIDE_DIALOG_FRAGMENT_TAG
                )
            }
        }
    }

    private fun hideAiGuide() {
        activity?.runOnUiThread {
            (childFragmentManager.findFragmentByTag(GuideDialogFragment.AI_GUIDE_DIALOG_FRAGMENT_TAG) as? GuideDialogFragment)?.dismiss()
            guideDialogFragment = null
        }
    }

    private fun showWorkoutGuide() {
        activity?.runOnUiThread {
            if (guideDialogFragment != null || (guideDialogFragment?.dialog)?.isShowing == true) {
                return@runOnUiThread
            } else {
                guideDialogFragment =
                    GuideDialogFragment.GuideDialogBuilder()
                        .setGuideText(getString(R.string.workout_guide)).create()
                guideDialogFragment?.show(
                    childFragmentManager,
                    GuideDialogFragment.WORKOUT_GUIDE_DIALOG_FRAGMENT_TAG
                )
            }
        }
    }

    private fun hideWorkoutGuide() {
        activity?.runOnUiThread {
            (childFragmentManager.findFragmentByTag(GuideDialogFragment.WORKOUT_GUIDE_DIALOG_FRAGMENT_TAG) as? GuideDialogFragment)?.dismiss()
            guideDialogFragment = null
        }
    }
}
