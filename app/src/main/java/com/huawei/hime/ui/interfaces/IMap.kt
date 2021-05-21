package com.huawei.hime.ui.interfaces

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View

interface IMap {
    fun onInitMap()
    fun onMapReady()
    fun onSavedInstanceBundle(savedInstanceState: Bundle?) //For HereMaps
    fun onSaveInstanceState(outState: Bundle?) //For HMaps
    fun onStartMap()
    fun onPauseMap()
    fun onResumeMap()
    fun onDestroyMap()
    fun onLowMemoryMap()
    fun ssCallback(id: String,push:String)
    fun gotoCurrentLocation(lat:Double, long:Double)
    fun getTappedLocation(lat:Double,long:Double)
    fun listenCamera()
    fun pinItWithMarker(context: Context)
}