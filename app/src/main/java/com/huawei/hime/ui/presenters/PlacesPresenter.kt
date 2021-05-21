package com.huawei.hime.profile.profileevent.places

import com.huawei.hime.ui.interfaces.IMap

class PlacesPresenter constructor(
	private val mapContract : PlacesContract,
	private val IMap : IMap
) {
	fun onViewCreate() {
		mapContract.initViews()
		mapContract.getLocationUpdate()
	}

	fun onMapInteract() {
		IMap.onMapReady()
	}

	fun onCurrentLocation() {
		mapContract.getLocationUpdate()
	}
}

interface PlacesContract {
	fun initViews()
	fun onMapClick()
	fun getLocationUpdate()
}