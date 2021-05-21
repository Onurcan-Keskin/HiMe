package com.huawei.hime.fresh

class FreshPresenter constructor(private val freshContract: FreshContract){

    fun onViewsCreate(){
        freshContract.initViews()
    }
}

interface FreshContract{
    fun initViews()
    fun initDB()
}