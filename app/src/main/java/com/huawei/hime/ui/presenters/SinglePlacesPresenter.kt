package com.huawei.hime.ui.presenters

import com.huawei.hime.ui.interfaces.ISinglePlaces

class SinglePlacesPresenter constructor(private val singlePlacesContract: ISinglePlaces.ViewSinglePlaces) {

    fun onViewsCreate(){
        singlePlacesContract.initViews()
        singlePlacesContract.setupPager()
    }
}