package com.huawei.hime.models

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class ExoModel(context : Context) {

	private var player : SimpleExoPlayer? = null
	private var playWhenReady : Boolean = true
	private var currentWindow = 0
	private var playbackPosition : Long = 0
	private var cntx = context

	private fun buildMediaSource(uri : Uri) : MediaSource {
		val dataSourceFactory : DataSource.Factory =
			DefaultDataSourceFactory(cntx, "exoplayer-hime")
		return ProgressiveMediaSource.Factory(dataSourceFactory)
			.createMediaSource(uri)
	}

	fun initializeExo(string : String,mVideo:PlayerView) {
		player = SimpleExoPlayer.Builder(cntx).build()
		mVideo.player = player
		val mediaSource = buildMediaSource(Uri.parse(string))

		player!!.playWhenReady = playWhenReady
		player!!.seekTo(currentWindow, playbackPosition)
		player!!.prepare(mediaSource, false, false)
	}

	fun releasePlayer() {
		if (player != null) {
			playWhenReady = player!!.playWhenReady
			playbackPosition = player!!.currentPosition
			currentWindow = player!!.currentWindowIndex
			player!!.release()
			player = null
		}
	}
}
