package com.huawei.hime.voipcaas

import android.content.Intent

class LiveChatPresenter constructor(
    private val liveChatContract: LiveChatContact,
    private val IVoip: IVoip
) {
    fun onBeforeCreateViews() {
        liveChatContract.initFullscreen()
    }

    fun onAfterCreateViews(){
        liveChatContract.managePermissions()
        liveChatContract.initViews()
    }

    fun onAcResult(){
        liveChatContract.addMediaView()
    }
}

interface LiveChatContact {
    fun initFullscreen()
    fun initViews()
    fun managePermissions()

    fun buttonClickEvent()
    fun startMedia()
    fun addMediaView()
    fun videoFilePath(intent: Intent)

    fun callShowClickEvent()
    fun videoControlClickEvent()
    fun callHideClickEvent()
}