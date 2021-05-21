package com.huawei.hime.gdpr

class GdprPresenter constructor(private val contract: GdprContract) {

    fun onViewCreate() {
        contract.initViews()
    }
}


interface GdprContract{
    fun initViews()
}