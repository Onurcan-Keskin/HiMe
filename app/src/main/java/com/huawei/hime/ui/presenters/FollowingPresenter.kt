             package com.huawei.hime.masterfollowers.masterU.following

class FollowingPresenter constructor(private val followingContract: FollowingContract) {

    fun onViewsCreate(){
        followingContract.initViews()
        followingContract.initDB()
    }
}

interface FollowingContract{
    fun initViews()
    fun initDB()
    fun setupRecycler()
}