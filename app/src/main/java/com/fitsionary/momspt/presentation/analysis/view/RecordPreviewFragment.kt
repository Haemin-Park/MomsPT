package com.fitsionary.momspt.presentation.analysis.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fitsionary.momspt.MomsPTApplication
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.enum.DirectionEnum
import com.fitsionary.momspt.data.model.BodyAnalysisResultModel
import com.fitsionary.momspt.databinding.FragmentRecordPreviewBinding
import com.fitsionary.momspt.presentation.analysis.viewmodel.RecordPreviewViewModel
import com.fitsionary.momspt.presentation.analysis.viewmodel.RecordPreviewViewModel.Companion.SHOW_ANALYSIS_RESULT
import com.fitsionary.momspt.presentation.analysis.viewmodel.RecordPreviewViewModel.Companion.SIGN_UP_FINISH
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.util.CurrentUser
import com.fitsionary.momspt.util.rx.ui
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.util.Util
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordPreviewFragment :
    BaseFragment<FragmentRecordPreviewBinding, RecordPreviewViewModel>(R.layout.fragment_record_preview) {
    override val viewModel: RecordPreviewViewModel by lazy {
        ViewModelProvider(this).get(RecordPreviewViewModel::class.java)
    }
    private var playWhenReady = false

    private lateinit var mediaUrl: String
    private var player: SimpleExoPlayer? = null
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    val safeArgs: RecordPreviewFragmentArgs by navArgs()
    private lateinit var filePath: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        player = null
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val direction = safeArgs.direction
        val signUpRequest = safeArgs.signUpRequest
        filePath = safeArgs.filePath

        viewModel.event.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { event ->
                when (event.first) {
                    SIGN_UP_FINISH -> {
                        showToast(getString(R.string.sign_in_success))
                        viewLifecycleOwner.lifecycleScope.launch {
                            MomsPTApplication.getInstance().getTokenDataStore().saveToken(
                                CurrentUser.token
                            )
                            viewModel.sendVideo(filePath)
                        }
                    }
                    SHOW_ANALYSIS_RESULT -> {
                        showResult(direction, event.second as BodyAnalysisResultModel)
                    }
                }
            }
        })

        binding.btnReRecord.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSend.setOnClickListener {
            if (signUpRequest == null) {
                viewModel.sendVideo(filePath)
            } else {
                viewModel.signUp(signUpRequest)
            }
        }

        viewModel.isLoading
            .observeOn(ui())
            .subscribe { if (it) showLoading() else hideLoading() }
            .addTo(compositeDisposable)
    }

    private fun showResult(direction: DirectionEnum, bodyAnalysisResult: BodyAnalysisResultModel) {
        when (direction) {
            DirectionEnum.TO_MAIN -> {
                findNavController().navigate(
                    RecordPreviewFragmentDirections.actionRecordPreviewFragmentToAnalysisResultFragmentInSignInScenario(
                        direction, bodyAnalysisResult
                    )
                )
            }
            DirectionEnum.TO_DAILY -> {
                findNavController().navigate(
                    RecordPreviewFragmentDirections.actionRecordPreviewFragmentToAnalysisResultFragmentInMainScenario(
                        direction, bodyAnalysisResult
                    )
                )
            }
        }
    }

    private fun initializePlayer() {
        mediaUrl = filePath
        val extractorsFactory =
            DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true)
        player = SimpleExoPlayer.Builder(requireContext(), extractorsFactory).build()
        binding.playerView.player = player
        val mediaItem = MediaItem.fromUri(mediaUrl)
        player!!.apply {
            playWhenReady = true
            setMediaItem(mediaItem)
            seekTo(currentWindow, playbackPosition)
            prepare()
        }
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