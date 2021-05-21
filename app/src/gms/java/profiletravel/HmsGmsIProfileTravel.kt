package profiletravel

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.here.sdk.core.Anchor2D
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapviewlite.*
import com.huawei.hime.R
import com.huawei.hime.profile.profiletravel.IProfileMap
import com.huawei.hime.profile.profiletravel.ProfileTravelViewHolder

class HmsGmsIProfileTravel constructor(context: ProfileTravelViewHolder) :
    IProfileMap {

    private var view: View = context.parent
    private var ui: View = view.findViewById(R.id.map_layout)

    private var mapView: MapViewLite = ui.findViewById(R.id.posted_single_map) as MapViewLite
    private lateinit var geoCoordinates: GeoCoordinates

    override fun onInitMap(lat: Double, long: Double) {
        try {
            mapView.mapScene.loadScene(
                MapStyle.NORMAL_DAY
            ) { errorCode ->
                if (errorCode == null) {
                    mapView.isClickable = false
                    mapView.isEnabled = false
                    mapView.camera.target = GeoCoordinates(lat, long)
                    mapView.camera.zoomLevel = 14.0
                    mapView.camera.tilt = 60.0

                    setTapGestureHandler(mapView)
                    getTappedAndPin(lat, long)
                } else {
                    Log.e("onLoadScene failed: ", errorCode.toString())
                }
            }
        } catch (e: Exception) {
            Log.d("HmsGmsProfileTravel: ", e.toString())
        }
    }

    private fun setTapGestureHandler(mapView: MapViewLite) {
        mapView.gestures.setTapListener {
            val geo = mapView.camera.viewToGeoCoordinates(it)
            Log.d("Tap detected at: ", geo.latitude.toString() + ", " + geo.longitude.toString())
            getAddressForCoordinates(geo)
            getTappedAndPin(geo.latitude, geo.longitude)
        }
    }

    private fun getAddressForCoordinates(geo: GeoCoordinates) {
        try {

        } catch (e: InstantiationException) {
            RuntimeException("Initialization of ReverseGeocodingEngine failed: " + e.cause.toString())
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
        mapView.onPause()
    }

    override fun onDestroyMap() {
        mapView.onDestroy()
    }

    override fun onLowMemoryMap() {
        mapView.onLowMemory()
    }

    override fun getTappedAndPin(lat: Double, long: Double) {
        mapView.gestures.setTapListener {
            val geo = mapView.camera.viewToGeoCoordinates(it)
            Log.d("Tap detected at: ", geo.latitude.toString() + ", " + geo.longitude.toString())
            getAddressForCoordinates(geo)
        }

        pinItWithMarker(view.context)
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
        mapView.camera.addObserver(cameraObserver)
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
    }
}