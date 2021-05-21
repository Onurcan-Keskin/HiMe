package com.huawei.hime.voipcaas

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.hime.R
import kotlinx.android.synthetic.main.activity_live_chat_voip.*
import liveChat.HmsGmsBroadcastI
import main.HiMeApp.Companion.context


class VoipActivity : AppCompatActivity(), LiveChatContact {

    private lateinit var mVideoControl: Button
    private lateinit var mCallShow: Button
    private lateinit var mCallHide: Button
    private lateinit var mVideoPick: Button

    private val hmsGmsBroadcastHelper: HmsGmsBroadcastI by lazy {
        HmsGmsBroadcastI(this)
    }

    private val liveChatPresenter: LiveChatPresenter by lazy {
        LiveChatPresenter(this, hmsGmsBroadcastHelper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        liveChatPresenter.onBeforeCreateViews()
        setContentView(R.layout.activity_live_chat_voip)
        supportActionBar?.hide()
        liveChatPresenter.onAfterCreateViews()
        hmsGmsBroadcastHelper.onHmsGmsBroadcastHelperCreate()

        mVideoPick.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_MEDIA_DATA)
        }

        mVideoControl.setOnClickListener {
            videoControlClickEvent()
            hmsGmsBroadcastHelper.mVideoControlEvent()
        }
        mCallShow.setOnClickListener {
            callShowClickEvent()
            hmsGmsBroadcastHelper.mCallShowEvent()
        }
        mCallHide.setOnClickListener {
            callHideClickEvent()
            hmsGmsBroadcastHelper.mCallHideEvent()
        }
    }

    override fun initFullscreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    override fun initViews() {
        //mVideoView = findViewById(R.id.lv_broadcast)
        mVideoControl = findViewById(R.id.live_chat_video_control)
        mCallShow = findViewById(R.id.live_chat_call_show)
        mCallHide = findViewById(R.id.live_chat_call_hide)
        mVideoPick = findViewById(R.id.live_chat_video_pick)
    }

    override fun managePermissions() {
        val permission = ActivityCompat.checkSelfPermission(
            application,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_EXTERNAL_STORAGE
            )
        } else {
            hmsGmsBroadcastHelper.setButtonClickEvent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_DATA) {
            if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                videoFilePath(data)
                addMediaView()
                liveChatPresenter.onAcResult()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> if (grantResults[0] == 0) {
                buttonClickEvent()
            } else {
                finish()
            }
            else -> {
            }
        }
    }

    override fun buttonClickEvent() {
        live_chat_video_pick.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_MEDIA_DATA)
        }
        hmsGmsBroadcastHelper.setButtonClickEvent()
    }

    override fun startMedia() {
        hmsGmsBroadcastHelper.startPlaying()
    }

    override fun videoFilePath(intent: Intent) {
        hmsGmsBroadcastHelper.setVideoFilePath(intent)
    }

    override fun callShowClickEvent() {
        hmsGmsBroadcastHelper.mCallShowEvent()
    }

    override fun videoControlClickEvent() {
        hmsGmsBroadcastHelper.mVideoControlEvent()
    }

    override fun callHideClickEvent() {
        hmsGmsBroadcastHelper.mCallHideEvent()
    }

    override fun addMediaView() {
        hmsGmsBroadcastHelper.addVideoView(context)
    }

    override fun onPause() {
        hmsGmsBroadcastHelper.onMediaPause()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        hmsGmsBroadcastHelper.onMediaStop()
    }

    override fun onResume() {
        super.onResume()
        hmsGmsBroadcastHelper.onMediaResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        hmsGmsBroadcastHelper.onMediaDestroy()
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private const val REQUEST_MEDIA_DATA = 1
    }
}
