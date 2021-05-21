package com.huawei.hime.ui.presenters

import com.huawei.hime.ui.interfaces.IMap
import com.huawei.hime.ui.interfaces.IBigPlayer

class BigPlayerPresenter constructor(private val bigPlayerContract: IBigPlayer.ViewBigPlayer,
                                     private val IMap: IMap
) {

    fun onViewsCreate() {
        bigPlayerContract.initViews()
        bigPlayerContract.initDB()
    }

    fun onMapInteract(){
        IMap.onMapReady()
    }

    fun onCurrentLocation(){
        bigPlayerContract.getLocationUpdate()
    }
}