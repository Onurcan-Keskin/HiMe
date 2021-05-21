package com.huawei.hime.ui.interfaces

class IChat {
	interface ViewChat{
		fun initViews()
		fun populateViewsWithDB()
		fun loadMessages()
	}

}