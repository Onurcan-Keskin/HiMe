package com.huawei.hime.ui.interfaces

import android.net.Uri
import com.google.android.exoplayer2.source.MediaSource

class IPostMessage {
	interface ViewPostMessage{
		fun initViews()
		fun initDB()
		fun sendComments()
	}
}