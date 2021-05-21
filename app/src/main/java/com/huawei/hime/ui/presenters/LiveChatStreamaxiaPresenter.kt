package com.huawei.hime.ui.presenters

class LiveChatStreamaxiaPresenter constructor(private val liveChatStreamaxiaContract: LiveChatStreamaxiaContract) {

    fun beforeViewsCreate(){
        liveChatStreamaxiaContract.initDB()
    }

    fun onViewsCreate() {
        liveChatStreamaxiaContract.initViews()
    }
}

interface LiveChatStreamaxiaContract {
    fun initViews()
    fun initDB()
    fun stopChronometer()
    fun handleException(e: Exception)
    fun setStreamerDefaultValues()
    fun stopStreaming()
    fun takeSnapshot()
    fun startStopStream()
    fun showDialog()
    fun uploadTask()
    fun liveDB()
}