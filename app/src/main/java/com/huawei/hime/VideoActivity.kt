package com.huawei.hime

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.streamaxia.player.StreamaxiaPlayer
import com.streamaxia.player.listener.StreamaxiaPlayerState
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : AppCompatActivity(), StreamaxiaPlayerState {

    private var uri: Uri? = null
    private var rtmpTest =
        "rtmp://rtmp.streamaxia.com/streamaxia/himeLiveChatBy_yFAJzmMMaReIaN9DYGc7hbecqR32"

    private val mStreamaxiaPlayer = StreamaxiaPlayer()

    var hide = Runnable { streamaxia_play.visibility = View.GONE }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        getExtrasm()

        streamaxia_play.tag = "play"
        streamaxia_mute.tag = "mute"
        streamaxia_small.tag = "small"
        streamaxia_progress_bar.visibility = View.GONE
        initRTMPEExoPlayer()
        streamaxia_url_edit_text.setText(uri.toString())

        streamaxia_play.setOnClickListener {
            setPlayBtn()
        }

        streamaxia_mute.setOnClickListener {
            setMute()
        }

        streamaxia_small.setOnClickListener {
            setScreenSize()
        }
    }

    private fun getExtrasm() {
        uri = Uri.parse(rtmpTest)
    }

    fun setPlayBtn() {
        streamaxia_play.postDelayed(hide, 1000)
        if (streamaxia_play.tag == "play") {
            mStreamaxiaPlayer.play(uri, StreamaxiaPlayer.TYPE_RTMP)
            streamaxia_surface_view.setBackgroundColor(Color.TRANSPARENT)
            streamaxia_progress_bar.visibility = View.GONE
            streamaxia_play.tag = "pause"
            streamaxia_play.setImageResource(R.drawable.ic_pause)
            setAspectRatioFrameLayoutOnClick()
        } else {
            mStreamaxiaPlayer.pause()
            streamaxia_progress_bar.visibility = View.GONE
            streamaxia_play.tag = "play"
            streamaxia_play.setImageResource(R.drawable.ic_play)
        }
    }

    fun setMute() {
        if (streamaxia_mute.tag == "mute") {
            mStreamaxiaPlayer.setMute()
            streamaxia_mute.tag = "muted"
            streamaxia_mute.setImageResource(R.drawable.ic_volume_off)
        } else {
            mStreamaxiaPlayer.setMute()
            streamaxia_mute.tag = "mute"
            streamaxia_mute.setImageResource(R.drawable.ic_volume_up)
        }
    }

    fun setScreenSize() {
        if (streamaxia_small.tag == "small") {
            mStreamaxiaPlayer.setVideoSize(300, 300)
            streamaxia_small.tag = "big"
            streamaxia_small.setImageResource(R.drawable.ic_fullscreen_exit)
        } else {
            mStreamaxiaPlayer.setVideoSize(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            streamaxia_small.tag = "small"
            streamaxia_small.setImageResource(R.drawable.ic_fullscreen)
        }
    }

    private fun setAspectRatioFrameLayoutOnClick() {
        streamaxia_video_frame.setOnClickListener {
            streamaxia_play.visibility = View.VISIBLE
            streamaxia_play.postDelayed(hide, 1000)
        }
    }

    private fun initRTMPEExoPlayer() {
        mStreamaxiaPlayer.initStreamaxiaPlayer(
            streamaxia_surface_view,
            streamaxia_video_frame,
            streamaxia_player_state_view,
            this,
            this,
            uri
        )
    }

    override fun stateENDED() {
        streamaxia_progress_bar.visibility = View.GONE
        streamaxia_play.setImageResource(R.drawable.ic_play)
    }

    override fun stateBUFFERING() {
        streamaxia_progress_bar.visibility = View.VISIBLE
    }

    override fun stateIDLE() {
        streamaxia_progress_bar.visibility = View.VISIBLE
    }

    override fun statePREPARING() {
        streamaxia_progress_bar.visibility = View.VISIBLE
    }

    override fun stateREADY() {
        streamaxia_progress_bar.visibility = View.GONE
    }

    override fun stateUNKNOWN() {
        streamaxia_progress_bar.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        mStreamaxiaPlayer.stop()
    }

}
