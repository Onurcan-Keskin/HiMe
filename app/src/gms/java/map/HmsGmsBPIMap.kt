package map

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.here.sdk.core.Anchor2D
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.Point2D
import com.here.sdk.mapviewlite.*
import com.huawei.hime.R
import com.huawei.hime.ui.activities.BigPlayerActivity
import com.huawei.hime.ui.interfaces.IMap
import main.HiMeApp.Companion.context

class HmsGmsBPIMap constructor(context: BigPlayerActivity) : IMap {

    private var view: View = context.findViewById(R.id.map_layout)
    private var mapView: MapViewLite = view.findViewById(R.id.map_map) as MapViewLite
    private lateinit var geoCoordinates: GeoCoordinates
    private val mapMarkerList: MutableList<MapMarker> = ArrayList()

    override fun onInitMap() {
        try {
            mapView.mapScene.loadScene(
                MapStyle.NORMAL_DAY
            ) { errorCode ->
                if (errorCode == null) {
                    mapView.camera.target = GeoCoordinates(41.028710, 29.117660)
                    mapView.camera.zoomLevel = 14.0
                    mapView.camera.tilt = 60.0

                    setTapGestureHandler(mapView)
                    //setTapGestureHandler()
                } else {
                    Log.e("onLoadScene failed:", errorCode.toString())
                }
            }
        } catch (e: Exception) {
            Log.d("MapActivity: ", e.toString())
        }
    }

    override fun onMapReady() {

    }

    override fun onSavedInstanceBundle(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {

    }

    override fun onStartMap() {
    }

    override fun onPauseMap() {
        mapView.onPause()
    }

    override fun onResumeMap() {
        mapView.onResume()
    }

    override fun onDestroyMap() {
        mapView.onDestroy()
    }

    override fun onLowMemoryMap() {
        mapView.onLowMemory()
    }

    override fun ssCallback(id: String, push: String) {

    }

    override fun gotoCurrentLocation(lat: Double, long: Double) {
        mapView.camera.target = GeoCoordinates(lat, long)
        mapView.camera.zoomLevel = 20.5
        mapView.camera.tilt = 60.0

        /* Pin it with Marker */
        pinItWithMarker(context)
    }

    override fun getTappedLocation(lat: Double, long: Double) {
        mapView.gestures.setTapListener {
            val geo = mapView.camera.viewToGeoCoordinates(it)
            Log.d("Tap detected at: ", geo.latitude.toString() + ", " + geo.longitude.toString())
            pinItWithMarker(context)
        }
    }

    private fun clearMap() {
        for (mapMarker in mapMarkerList) {
            mapView.mapScene.removeMapMarker(mapMarker)
        }
        mapMarkerList.clear()
    }

    override fun listenCamera() {
        val cameraObserver =
            CameraObserver { cameraUpdate ->
                val mapCenter = cameraUpdate.target
                Log.d(
                    "Camera ",
                    "Current map center location: $mapCenter, Current zoom level: ${cameraUpdate.zoomLevel}"
                )
            }
       //mapView.camera.addObserver(cameraObserver)
    }

    override fun pinItWithMarker(context: Context) {
        val mapImage = MapImageFactory.fromResource(
            context.resources,
            R.drawable.pin
        ) //second parameter must be .png .jpg, .xml is not accepted
        geoCoordinates = mapView.camera.target
        val mapMarker = MapMarker(geoCoordinates)

        val mapMarkerStyle = MapMarkerImageStyle()
        mapMarkerStyle.anchorPoint = Anchor2D(0.5F, 1F)

        mapMarker.addImage(mapImage, MapMarkerImageStyle())
        mapView.mapScene.addMapMarker(mapMarker)
        mapMarkerList.add(mapMarker)
    }

    private fun setTapGestureHandler(mapView: MapViewLite) {
        mapView.gestures.setTapListener {
            val geo = mapView.camera.viewToGeoCoordinates(it)
            Log.d("Tap detected at: ", geo.latitude.toString() + ", " + geo.longitude.toString())
            // getAddressForCoordinates(geo)
            //pinItWithMarker(context)
            pickMapMarker(it)
        }
    }

    private fun pickMapMarker(touchPoint: Point2D) {
        val radiusInPixel = 2.0F
        mapView.pickMapItems(touchPoint, radiusInPixel, object : PickMapItemsCallback {
            override fun onMapItemsPicked(p0: PickMapItemsResult?) {
                if (p0 == null) {
                    return
                }
                val topMostMarker = p0.topmostMarker
                val TLat = topMostMarker!!.coordinates.latitude.toString()
                val TLong = topMostMarker.coordinates.longitude.toString()
                if (topMostMarker == null) {
                    return
                }
                Log.d("pickMapMarker: ", "$TLat, $TLong")
            }
        })
    }
}