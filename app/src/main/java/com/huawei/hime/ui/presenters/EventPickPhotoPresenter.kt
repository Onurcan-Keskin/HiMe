package com.huawei.hime.profile.profileevent.pickPhoto

import java.io.File

class EventPickPhotoPresenter constructor(private val contract : EventPickPhotoContract){

	fun onViewsCreate(){
		contract.initViews()
	}
}

interface EventPickPhotoContract{
	fun initViews()
	fun getImageTask(string : String)
}