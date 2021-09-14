package com.fitsionary.momspt.presentation.analysis.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentRecordPreviewBinding
import com.fitsionary.momspt.presentation.analysis.viewmodel.RecordPreviewViewModel
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory

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

    override fun onResume() {
        super.onResume()
        if (player == null) {
            initializePlayer()
        }
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val safeArgs: RecordPreviewFragmentArgs by navArgs()
        mediaUrl = safeArgs.filePath
    }

    private fun initializePlayer() {
        val extractorsFactory = DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true)
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