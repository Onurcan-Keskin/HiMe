package com.huawei.hime.trending

class TrendingPresenter constructor(private val trendingContract: TrendingContract) {

    fun onCreateViews(){
        trendingContract.initViews()
    }
}

interface TrendingContract{
    fun initViews()
    fun initDB()
}