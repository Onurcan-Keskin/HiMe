package com.huawei.hime.splash

class SplashPresenter constructor(private val splashConstructor: SplashConstructor){
    fun onViewCreate(){
        splashConstructor.initViews()
        splashConstructor.startFullScreen()
        splashConstructor.doWork()
        splashConstructor.startApp()
    }
}

interface SplashConstructor{
    fun initViews()
    fun startFullScreen()
    fun doWork()
    fun startApp()
}