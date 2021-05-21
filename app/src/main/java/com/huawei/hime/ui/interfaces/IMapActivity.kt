package com.huawei.hime.ui.interfaces

class IMapActivity {
	interface ViewMapActivity{
		fun initViews()
		fun onMapClick()
		fun showLogMessage(message: String)
		fun getLocationUpdate()
		fun takeMapSS()
	}
}