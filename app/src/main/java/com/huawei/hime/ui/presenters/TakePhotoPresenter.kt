package com.huawei.hime.ui.presenters

class TakePhotoPresenter constructor(private val takePhotoContract : TakePhotoContract) {

	fun onCreateViews() {
		takePhotoContract.initViews()
		takePhotoContract.initDB()
	}
}

interface TakePhotoContract {
	fun initViews()
	fun initDB()
}