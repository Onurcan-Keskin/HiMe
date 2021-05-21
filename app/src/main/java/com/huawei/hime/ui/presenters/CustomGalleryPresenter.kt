package com.huawei.hime.livestreamStreamaxia.pickphoto

class CustomGalleryPresenter constructor(private val customGalleryContract: CustomGalleryContract) {

    fun onViewsCreate() {
        customGalleryContract.isReadStoragePermissionGranted()
        customGalleryContract.initViews()
        customGalleryContract.setListeners()
        customGalleryContract.fetchGalleryImages()
        customGalleryContract.setUpGridView()
    }
}

interface CustomGalleryContract {
    fun isReadStoragePermissionGranted():Boolean
    fun initViews()
    fun setListeners()
    fun fetchGalleryImages()
    fun setUpGridView()
}