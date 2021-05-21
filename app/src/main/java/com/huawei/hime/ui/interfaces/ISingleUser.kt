package com.huawei.hime.ui.interfaces

class ISingleUser {
	interface ViewSingleUser {
		fun initViews()
		fun setupViewPager()
		fun showLog(message : String)
		fun hitLovely()
		fun startSocializing(userID : String)
		fun populateInterestsGrid()
		fun setTooltip()
		fun setPrivacySelections()
		fun privacyMode()
		fun populateInterests()
		fun attachPhotoView(type : Int)
		fun detect()
	}
}