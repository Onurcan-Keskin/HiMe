package com.huawei.hime.ui.presenters

import com.huawei.hime.ui.interfaces.ISingleTag

class SingleTagPresenter constructor(private val singleTagContract: ISingleTag.ViewSingleTag){

    fun onViewsCreate(){
        singleTagContract.initViews()
        singleTagContract.setupViewPager()
    }
}