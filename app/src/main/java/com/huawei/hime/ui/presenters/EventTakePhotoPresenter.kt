package com.huawei.hime.profile.profileevent.takePhoto

import android.view.MotionEvent
import java.io.File

class EventTakePhotoPresenter (private val eventTakePhotoContract : EventTakePhotoContract) {

	fun onViewsCreate(){
		eventTakePhotoContract.initViews()
	}
}

interface EventTakePhotoContract{
	fun initViews()
	fun getImageTask(file: File)
	fun onTouchEvent(event: MotionEvent): Boolean
	fun startCameraFront()
	fun startCameraBack()
	fun toggleTorch()
	fun startZoomDialog(type : Int)
	fun takePicture()
}