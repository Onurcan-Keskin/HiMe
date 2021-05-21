package com.huawei.hime.livestreamStreamaxia.pickphoto

class PickPhotoPresenter constructor(private val pickPhotoContract: PickPhotoContract) {

    fun onViewsCreate(){
        pickPhotoContract.initViews()
        pickPhotoContract.initDB()
    }
}

interface PickPhotoContract{
    fun initViews()
    fun initDB()
}