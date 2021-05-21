package map

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.here.sdk.core.Anchor2D
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.LanguageCode
import com.here.sdk.core.Point2D
import com.here.sdk.mapviewlite.*
import com.here.sdk.search.Address
import com.here.sdk.search.ReverseGeocodingEngine
import com.here.sdk.search.ReverseGeocodingOptions
import com.here.sdk.search.SearchError
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.ui.activities.MapActivity
import com.huawei.hime.ui.interfaces.IMap
import com.huawei.hime.helpers.FirebaseDBHelper
import java.io.File
import java.io.FileOutputStream

class HmsGmsIMap constructor(private val context: MapActivity) : IMap {

    private var view: View = context.findViewById(R.id.map_layout)
    private var mapView: MapViewLite = view.findViewById(R.id.map_map) as MapViewLite
    private lateinit var geoCoordinates: GeoCoordinates
    private val mapMarkerList: MutableList<MapMarker> = ArrayList()

    private val id = AppUser.getUserId()
    private var cntry = ""

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


    private fun getAddressForCoordinates(geoCoordinates: GeoCoordinates) {
        try {
            val textLocationTag =
                context.findViewById<TextView>(R.id.bottom_sheet_clicked_location_tag)
            val textTag = context.findViewById<TextView>(R.id.profile_travel_location_tag_txt)
            val invisibleDBText = context.findViewById<TextView>(R.id.profile_invisible_country_db_txt)
            val reverseGeocodingEngine = ReverseGeocodingEngine()
            val reverseGeocodingOptions = ReverseGeocodingOptions(LanguageCode.EN_GB)
            reverseGeocodingEngine.searchAddress(
                geoCoordinates,
                reverseGeocodingOptions,
                object : ReverseGeocodingEngine.Callback {
                    override fun onSearchCompleted(searchError: SearchError?, address: Address?) {
                        if (searchError != null) {
                            Log.e("Reverse geocoding", "Error: $searchError")
                            return
                        } else {
                            //textLocationTag.text = address!!.addressText //Bottom Sheet
                            textTag.text = address!!.addressText // Under Map
                            invisibleDBText.text=address.country // InvisibleText for DB
                            cntry = address.country
                            Log.d("getAddressForCoordinates: Country",invisibleDBText.text.toString())
                        }
                    }
                })
        } catch (e: InstantiationException) {
            RuntimeException("Initialization of ReverseGeocodingEngine failed: " + e.cause.toString())
        }
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
        var mUri :String
        val callback = MapViewLite.CaptureScreenshotCallback { bitmap ->
            val bit = bitmap
            val recPath = Environment.getExternalStorageDirectory().path + "/Pictures/HiMe"
            val title = cntry + "_" + System.currentTimeMillis().toString()
            val suffix = ".png"
            val path = "$recPath/$title$suffix"
            val file = File(path)
            val out = FileOutputStream(file)
            bit.compress(Bitmap.CompressFormat.PNG, 90, out)
            val uri = Uri.fromFile(file)
            Log.d("MapTask", "$uri\n$out\n$bitmap")
            val mStorageRef: StorageReference = FirebaseStorage.getInstance().reference
            val mapFilePathRef: StorageReference =
                mStorageRef.child("User").child("Map").child(id)
                    .child(System.currentTimeMillis().toString() + ".jpg")
            val uploadTask = mapFilePathRef.putFile(uri)
            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mapFilePathRef.downloadUrl.addOnSuccessListener { uri: Uri? ->
                        mUri = uri.toString()
                        Log.d("MapTask", "takeMapSS: Success: $mUri")

                        val dbTravel = FirebaseDBHelper.getUserFeedRootRef().child("Travel")
                            .child("$id/$cntry/$push")
                        val dbUpload = FirebaseDBHelper.rootRef().child("uploads/NShareable/$push")

                        Log.d("MapTask",id+" "+push)
                        dbTravel.child("travel_map_holder").setValue(mUri)
                        dbUpload.child("upload_imageUrl").setValue(mUri).addOnCompleteListener {
                        }
                    }
                } else {
                    Log.e("MapTask", "takeMapSS : Failed")
                }
            }
        }
        mapView.captureScreenshot(callback)
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
            getAddressForCoordinates(geo)
            //pinItWithMarker(context)
            pickMapMarker(it)
        }
    }

    private fun setTapGestureHandler() {
        mapView.gestures.setTapListener {
            pickMapMarker(it)
            clearMap()
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

}