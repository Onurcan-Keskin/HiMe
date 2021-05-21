package com.huawei.hime.ui.interfaces

import android.net.Uri
import android.os.Bundle
import com.google.android.exoplayer2.source.MediaSource

class IBigPlayer {

	interface ViewBigPlayer{
		fun initViews()
		fun initDB()
		fun setBannerView()
		fun getLocationUpdate()
		fun setTypes(sis: Bundle?, postID: String)
	}

}