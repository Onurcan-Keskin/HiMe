package com.huawei.hime.search.singleuserpage.gallery

class GalleryPresenter constructor(private val galleryContract: GalleryContract) {

    fun onViewsCreate(){
        galleryContract.initViews()
    }
}

interface GalleryContract{
    fun initViews()
    fun initDB()
}