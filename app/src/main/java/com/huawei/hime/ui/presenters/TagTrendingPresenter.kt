package com.huawei.hime.search.singletagpage.tagtrending

class TagTrendingPresenter constructor(private val tagTrendingContract: TagTrendingContract){

    fun onViewsCreate(){
        tagTrendingContract.initViews()
    }
}
interface TagTrendingContract{
    fun initViews()
    fun initDB()
}