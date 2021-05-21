package com.huawei.hime.profile.profilegallery.profilegalleryvideo

class ProfileGVideoPresenter constructor(private val profileGVideoContract: ProfileGVideoContract) {

    fun onViewsCreate(){
        profileGVideoContract.initViews()
        profileGVideoContract.initDB()
    }
}

interface ProfileGVideoContract{
    fun initViews()
    fun initDB()
}