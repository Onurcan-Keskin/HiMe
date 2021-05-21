package com.huawei.hime.profile.profileevent.coverphoto

import java.io.File

class EventPickCoverPresenter constructor(private val contract : EventPickCoverContract){

	fun onViewsCreate(){
		contract.initViews()
	}
}

interface EventPickCoverContract{
	fun initViews()
	fun getImageTask(file : File)
}