package com.huawei.hime.profile.profilegallery

class ProfileGalleryPresenter constructor(private val profileGalleryContract: ProfileGalleryContract) {

    fun onViewsCreate(){
        profileGalleryContract.initDB()
    }
}
interface ProfileGalleryContract{
    fun initDB()
    fun setupViewPager()
}