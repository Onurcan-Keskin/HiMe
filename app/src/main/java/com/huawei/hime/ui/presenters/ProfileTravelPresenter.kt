package com.huawei.hime.profile.profiletravel

class ProfileTravelPresenter constructor(
    private val profileTravelContractor: ProfileTravelContractor) {

    fun onCreateViews(){
        profileTravelContractor.initViews()
        profileTravelContractor.initDB()
    }
    fun addTravel(){
        profileTravelContractor.startAddingTravel()
    }
}

interface ProfileTravelContractor{
    fun initViews()
    fun initDB()
    fun setupViewsWithRecycler()
    fun startAddingTravel()
    fun setCountrySpinner()
}