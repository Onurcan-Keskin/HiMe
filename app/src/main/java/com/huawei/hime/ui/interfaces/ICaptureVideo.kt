package com.huawei.hime.ui.interfaces

import android.content.Context
import java.io.File

class ICaptureVideo {
	interface ViewCaptureVideo{
		fun initViews()
		fun initDB()
		fun toggleCamera()
		fun recordVideo()
		fun startRecording()
		fun stopRecording()
		fun initFrontCamera()
		fun initBackCamera()
	}

	interface PresenterCaptureVideo{
		fun getVideoTask(
			file: File,
			context: Context
		)
	}
}