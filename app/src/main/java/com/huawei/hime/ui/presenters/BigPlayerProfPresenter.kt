package com.huawei.hime.ui.presenters

import android.net.Uri
import com.google.android.exoplayer2.source.MediaSource
import com.huawei.hime.ui.interfaces.IBigPlayerProf

class BigPlayerProfPresenter (private val bigPlayerProfContract: IBigPlayerProf.ViewBigPlayerProf){

    fun onViewsCreate(){
        bigPlayerProfContract.initViews()
        bigPlayerProfContract.initDB()
    }
}

