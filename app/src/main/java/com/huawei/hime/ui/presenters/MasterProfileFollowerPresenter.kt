package com.huawei.hime.ui.presenters

import com.huawei.hime.ui.interfaces.IMasterProfileFollower

class MasterProfileFollowerPresenter constructor(private val masterProfileFollowerContract: IMasterProfileFollower.ViewMasterProfileFollower) {

    fun onViewsCreate(){
        masterProfileFollowerContract.initDB()
        masterProfileFollowerContract.setupViewPager()
    }
}