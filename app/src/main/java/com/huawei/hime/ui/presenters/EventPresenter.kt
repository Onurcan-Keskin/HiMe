package com.huawei.hime.search.singleuserpage.event

class EventPresenter constructor(private val eventContract: EventContract){

    fun onViewsCreate(){
        eventContract.initViews()
    }
}
interface EventContract{
    fun initViews()
    fun initDB()
}