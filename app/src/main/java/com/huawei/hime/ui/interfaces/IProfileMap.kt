package com.huawei.hime.profile.profiletravel

import android.content.Context
import android.os.Bundle

interface IProfileMap {
    fun onInitMap(lat: Double,long: Double)
    fun onMapReady()
    fun onSavedInstanceBundle(savedInstanceState: Bundle?) //For HereMaps
    fun onSaveInstanceState(outState: Bundle?) //For HMaps
    fun onStartMap()
    fun onPauseMap()
    fun onResumeMap()
    fun onDestroyMap()
    fun onLowMemoryMap()
    fun getTappedAndPin(lat: Double, long: Double)
    fun listenCamera()
    fun pinItWithMarker(context: Context)

}