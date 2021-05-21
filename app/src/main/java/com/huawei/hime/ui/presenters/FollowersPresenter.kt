package com.huawei.hime.masterfollowers.masterU.followers

class FollowersPresenter constructor(private val followerContract: FollowerContract) {

    fun onViewsCreate() {
        followerContract.initViews()
        followerContract.initDB()
    }
}

interface FollowerContract {
    fun initViews()
    fun initDB()
    fun setupRecycler()
}