package com.huawei.hime.profile.profiletravel

import android.os.Bundle
import android.os.PersistableBundle

class ProfileTravelRecyclerHelper constructor(
    private val profileTravelRecyclerContract: ProfileTravelRecyclerContract
) {

    fun onCreateRecycler(savedInstanceState: Bundle?) {
        profileTravelRecyclerContract.initViews(savedInstanceState)

    }
}

interface ProfileTravelRecyclerContract {
    fun initViews(savedInstanceState: Bundle?)
    fun onMapReadyCallback()
    fun getLocationUpdate(lat:Double,long:Double)
    fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle)
}