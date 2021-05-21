package com.huawei.hime.ui.interfaces

import android.content.Context
import java.io.File

interface ITakePhoto {
    fun takePicture()
    fun setTorchStateObserver()
    fun getImageTask(file:File)
    fun toggleTorch()
    fun startCameraBack()
    fun startCameraFront()
    fun initProvider(context: Context)
    fun initDB()
    fun initViewsCreate()
    fun onViewResume()
    fun onViewPause()
    fun onViewDestroy()
}