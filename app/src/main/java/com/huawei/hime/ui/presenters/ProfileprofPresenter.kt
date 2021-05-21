package com.huawei.hime.ui.presenters

class ProfilePresenter constructor(private val profileContract : ProfileContract) {

	fun onViewsCreate() {
		profileContract.initDB()
		profileContract.initViews()
	}
}

interface ProfileContract {
	fun initDB()
	fun initViews()
	fun startFollowerActivity()
	fun setNameMemoDialog(type : Int)
	fun attachPhotoView(type : Int)
}