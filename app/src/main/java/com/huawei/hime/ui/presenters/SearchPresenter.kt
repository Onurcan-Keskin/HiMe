package com.huawei.hime.ui.presenters

import com.huawei.hime.ui.interfaces.ISearch

class SearchPresenter constructor(private val searchContract: ISearch.ViewSearch){

    fun onViewsCreate(){
        searchContract.initViews()
        searchContract.setupViewPager()
    }
}