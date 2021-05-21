package com.huawei.hime.discover

class DiscoverPresenter constructor(private val discoverContract: DiscoverContract) {

    fun onViewsCreate() {
        discoverContract.initViews()
    }
}

interface DiscoverContract {
    fun initViews()
    fun initDB()
}