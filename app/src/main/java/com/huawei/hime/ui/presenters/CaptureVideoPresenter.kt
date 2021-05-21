package com.huawei.hime.ui.presenters

import android.content.Context
import com.huawei.hime.ui.interfaces.ICaptureVideo
import java.io.File

class CaptureVideoPresenter constructor(private val contract: ICaptureVideo.ViewCaptureVideo) {

	fun onCreateViews(){
		contract.initViews()
		contract.initViews()
	}


}