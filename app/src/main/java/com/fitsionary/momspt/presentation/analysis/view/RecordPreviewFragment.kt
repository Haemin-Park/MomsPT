package com.fitsionary.momspt.presentation.analysis.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.enum.DirectionEnum
import com.fitsionary.momspt.databinding.FragmentRecordPreviewBinding
import com.fitsionary.momspt.presentation.analysis.viewmodel.RecordPreviewViewModel
import com.fitsionary.momspt.presentation.analysis.viewmodel.RecordPreviewViewModel.Companion.START_ANALYSIS_RESULT_ACTIVITY
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.util.Util

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
        val safeArgs: RecordPreviewFragmentArgs by navArgs()
        val direction = safeArgs.direction
        mediaUrl = safeArgs.filePath

        viewModel.event.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { event ->
                when (event.first) {
                    START_ANALYSIS_RESULT_ACTIVITY -> {
                        showResult(direction)
                    }
                }
            }
        })

        binding.btnReRecord.setOnClickListener {
            findNavController().navigateUp()
        }
        // for test
        binding.btnSend.setOnClickListener {
            showResult(direction)
        }
    }

    private fun showResult(direction: DirectionEnum) {
        when (direction) {
            DirectionEnum.TO_MAIN -> {
                findNavController().navigate(
                    RecordPreviewFragmentDirections.actionRecordPreviewFragmentToAnalysisResultFragmentInSignInScenario(
                        direction, ""
                    )
                )
            }
            DirectionEnum.TO_DAILY -> {
                findNavController().navigate(
                    RecordPreviewFragmentDirections.actionRecordPreviewFragmentToAnalysisResultFragmentInMainScenario(
                        direction, ""
                    )
                )
            }
        }
    }

    private fun initializePlayer() {
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