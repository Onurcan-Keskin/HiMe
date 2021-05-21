package map

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.bigplayer.BigPlayerActivity
import com.huawei.hime.map.MapHelper
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.CameraPosition
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.Marker

class HmsGmsBPMapHelper constructor(context: BigPlayerActivity) : MapHelper,
    OnMapReadyCallback {

    private var hMap: HuaweiMap? = null
    private val cntx = context

    private var view: View = context.findViewById(R.id.map_layout)
    private var mMapView: MapView? = view.findViewById(R.id.map_map) as MapView

    private var mMarker: Marker? = null

    private var mLat: Double = 0.0
    private var mLong: Double = 0.0

    private var pinLocation: String? = null
    private var fullName: String? = null
    private var countryName: String? = null

    private val id = AppUser.getUserId()

    private var markerList: MutableList<Marker> = ArrayList()

    companion object {
        private const val mapViewBundleKey = "MapViewBundleKey"
        private const val mTag = "HmsGmsBPMapHelper"
    }

    override fun onInitMap() {
    }

    override fun onMapReady() {
    }

    override fun onSavedInstanceBundle(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(mapViewBundleKey)
        }
        mMapView!!.onCreate(mapViewBundle)
        mMapView!!.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle?) {

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

    override fun ssCallback(id: String, push: String) {
    }

    override fun gotoCurrentLocation(lat: Double, long: Double) {
        mLat = lat
        mLong = long

        val cameraPosition = CameraPosition.builder()
            .target(LatLng(lat, long))
            .zoom(10F)
            .bearing(31.5F)
            .tilt(2.2F)
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

        val lng = LatLng(lat, long)
        val cu = CameraUpdateFactory.newCameraPosition(cameraPosition)
        //hMap!!.animateCamera(cu)
        //hMap!!.animateCamera(CameraUpdateFactory.newLatLng(lng))
    }

    override fun getTappedLocation(lat: Double, long: Double) {

    }

    override fun listenCamera() {

    }

    override fun pinItWithMarker(context: Context) {

    }

    override fun onMapReady(map: HuaweiMap?) {
        hMap = map
        hMap!!.isMyLocationEnabled = true
        hMap!!.uiSettings.isMyLocationButtonEnabled = true
        hMap!!.isBuildingsEnabled = true
        hMap!!.isIndoorEnabled = true
        hMap!!.isTrafficEnabled = true
        hMap!!.setOnMapLoadedCallback {
            Log.d(mTag, "onMapLoaded:successful")
        }
        addMarkerListener()

        gotoCurrentLocation(mLat, mLong)
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

    fun addMarker() {
        mMarker!!.isDraggable = true

        hMap!!.setOnMarkerClickListener {
            val clusterable = mMarker!!.isClusterable
            true
        }

        hMap!!.setOnMarkerDragListener(object : HuaweiMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker?) {
                Log.d(mTag, "onMarkerDragStart: ")
            }

            override fun onMarkerDrag(marker: Marker?) {
                Log.d(mTag, "onMarkerDrag: ")
            }

            override fun onMarkerDragEnd(marker: Marker?) {
                Log.d(mTag, "onMarkerDragEnd: ")
            }
        })
    }
}