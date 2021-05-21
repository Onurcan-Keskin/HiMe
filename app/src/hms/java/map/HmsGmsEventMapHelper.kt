package map

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.huawei.hime.R
import com.huawei.hime.map.MapHelper
import com.huawei.hime.profile.profileevent.places.PlacesFragment
import com.huawei.hime.util.showSnackbar
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.CameraPosition
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.Marker

class HmsGmsEventMapHelper constructor(context: PlacesFragment) : MapHelper, OnMapReadyCallback {

	private var hMap: HuaweiMap? = null
	private var view = LayoutInflater.from(context.context).inflate(R.layout.fragment_places,null)
	private var mMapView: MapView? = view.findViewById(R.id.map_map) as MapView

	private var mMarker: Marker? = null

	private var mLat: Double = 0.0
	private var mLong: Double = 0.0

	private var pinLocation: String? = null
	private var fullName: String? = null
	private var countryName: String? = null

	companion object {
		private const val mapViewBundleKey = "MapViewBundleKey"
		private const val mTag = "HmsGmsEventMapHelper"
	}

	override fun onInitMap() {
	}

	override fun onMapReady() {

	}

	override fun onSavedInstanceBundle(savedInstanceState : Bundle?) {
		var mapViewBundle: Bundle? = null
		if (savedInstanceState != null) {
			mapViewBundle = savedInstanceState.getBundle(mapViewBundleKey)
		}
		mMapView!!.onCreate(mapViewBundle)
		mMapView!!.getMapAsync(this)
	}

	override fun onSaveInstanceState(outState : Bundle?) {

	}

	override fun onStartMap() {
		mMapView!!.onStart()
	}

	override fun onPauseMap() {
		mMapView!!.onPause()
	}

	override fun onResumeMap() {
		mMapView!!.onResume()
	}

	override fun onDestroyMap() {
		mMapView!!.onDestroy()
	}

	override fun onLowMemoryMap() {
		mMapView!!.onLowMemory()
	}

	override fun ssCallback(id : String, push : String) {

	}

	override fun gotoCurrentLocation(lat : Double, long : Double) {
		mLat = lat
		mLong = long
		val cameraPosition = CameraPosition.builder()
			.target(LatLng(lat, long))
			.zoom(10F)
			.bearing(45F)
			.tilt(20F)
			.build()
		val options = HuaweiMapOptions()

		Log.d("Location", "lat: $lat long: $long")

		options.mapType(HuaweiMap.MAP_TYPE_NORMAL)
			.camera(cameraPosition)
			.zoomControlsEnabled(false)
			.compassEnabled(true)
			.zoomGesturesEnabled(true)
			.scrollGesturesEnabled(true)
			.rotateGesturesEnabled(false)
			.tiltGesturesEnabled(true)
			.zOrderOnTop(true)
			.useViewLifecycleInFragment(true)

		val lng = LatLng(mLat, mLong)
		hMap!!.animateCamera(CameraUpdateFactory.newLatLng(lng))
	}

	override fun getTappedLocation(lat : Double, long : Double) {

	}

	override fun listenCamera() {

	}

	override fun pinItWithMarker(context : Context) {

	}

	override fun onMapReady(map : HuaweiMap?) {
		hMap = map
		hMap!!.isMyLocationEnabled = true
		hMap!!.uiSettings.isMyLocationButtonEnabled = true
		hMap!!.isBuildingsEnabled = true
		hMap!!.isIndoorEnabled = true
		hMap!!.isTrafficEnabled = true
		hMap!!.setOnMapLoadedCallback {
			Log.d(mTag, "onMapLoaded:successful")
			showSnackbar(view,"onMapLoaded:successful")
		}
		addMarkerListener()
		gotoCurrentLocation(mLat,mLong)
	}

	private fun addMarkerListener() {
		hMap!!.setOnMarkerDragListener(object : HuaweiMap.OnMarkerDragListener {
			override fun onMarkerDragStart(marker: Marker?) {
				Log.i(mTag, "onMarkerDragStart: ")

			}

			override fun onMarkerDrag(marker: Marker?) {
				Log.i(mTag, "onMarkerDrag: ")
			}

			override fun onMarkerDragEnd(marker: Marker?) {
				Log.i(mTag, "onMarkerDragEnd: ")
			}
		})
		hMap!!.setOnInfoWindowClickListener { marker ->

		}
		hMap!!.setOnInfoWindowCloseListener {
			Log.d(mTag, "infowindowclose")
		}
		hMap!!.setOnInfoWindowLongClickListener {
			Log.d(mTag, "onInfoWindowLongClick")
		}
	}
}