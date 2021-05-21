package com.huawei.hime.search.singleuserpage.travel

class TravelPresenter constructor(private val travelContract: TravelContract) {

    fun onViewsCreate(){
        travelContract.initViews()
    }
}
interface TravelContract{
    fun initViews()
    fun initDB()
}