package com.huawei.hime.search.singletagpage.tagfresh

class TagFreshPresenter constructor(private val tagFreshContract: TagFreshContract){

    fun onViewsCreate(){
        tagFreshContract.initViews()
    }
}
interface  TagFreshContract{
    fun initDb()
    fun initViews()
}