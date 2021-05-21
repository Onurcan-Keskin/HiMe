package com.huawei.hime.search.singleplacespage.placestrending

class PlacesTrendingPresenter constructor(private val placesTrendingContract: PlacesTrendingContract) {
    fun onViewsCreate(){
        placesTrendingContract.initViews()
    }
}

interface PlacesTrendingContract{
    fun initViews()
    fun initDB()
}