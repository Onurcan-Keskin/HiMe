package com.huawei.hime.voipcaas

import android.content.Context
import android.content.Intent

interface IVoip {
    fun setButtonClickEvent()
    fun setVideoFilePath(intent: Intent)
    fun startPlaying()

    fun onMediaPause()
    fun onMediaStop()
    fun onMediaResume()
    fun onMediaDestroy()
    fun addVideoView(context: Context)

    fun mVideoControlEvent()
    fun mCallShowEvent()
    fun mCallHideEvent()

    fun onHmsGmsBroadcastHelperCreate()
}