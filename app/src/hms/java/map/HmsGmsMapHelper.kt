package map

import android.content.Context
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.util.dbutils.DbUtils
import com.huawei.hime.map.MapActivity
import com.huawei.hime.map.MapHelper
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.CameraPosition
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.Marker
import com.huawei.hms.maps.model.MarkerOptions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class HmsGmsMapHelper constructor(context: MapActivity) : MapHelper,
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

    private val textTag: TextView = context.findViewById(R.id.profile_travel_location_tag_txt)
    private val invisibleDBText: TextView =
        context.findViewById(R.id.profile_invisible_country_db_txt)

    private val img: ImageView = context.findViewById(R.id.testImage)

    private var markerList: MutableList<Marker> = ArrayList()

    companion object {
        private const val mapViewBundleKey = "MapViewBundleKey"
        private const val mTag = "HmsGmsMapHelper"
    }

    /*On Create*/
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

    override fun gotoCurrentLocation(lat: Double, long: Double) {
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

    override fun getTappedLocation(lat: Double, long: Double) {

    }

    override fun listenCamera() {

    }

    override fun pinItWithMarker(context: Context) {

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
        hMap!!.setOnMapLongClickListener {

            val lt = it.latitude
            val ln = it.longitude
            val geocoder = Geocoder(cntx, Locale.getDefault())
            try {
                val address: MutableList<Address> = geocoder.getFromLocation(lt, ln, 1)
                val cityName = address[0].getAddressLine(0)
                val tst1 = address[0].subLocality
                val tst2 = address[0].locality
                val tst3 = address[0].countryCode
                val tst4 = address[0].countryName
                val tst5 = address[0].postalCode
                val tst6 = address[0].featureName
                val tst7 = address[0].premises
                val tst8 = address[0].subThoroughfare
                val tst9 = address[0].thoroughfare
                val tst10 = address[0].subAdminArea
                pinLocation = "$tst1, $tst10 - $tst3"
                fullName = cityName
                countryName = tst4
                Log.d(mTag, "ci-> $cityName") //Full Address
                Log.d(mTag, "t1-> $tst1") // subLocality
                Log.d(mTag, "t2-> $tst2") // Locality
                Log.d(mTag, "t3-> $tst3") // Country Code
                Log.d(mTag, "t4-> $tst4") // Country
                Log.d(mTag, "t5-> $tst5") // Postal Code
                Log.d(mTag, "t6-> $tst6") // Returns null
                Log.d(mTag, "t7-> $tst7") // Returns null
                Log.d(mTag, "t8-> $tst8") // Returns Number
                Log.d(mTag, "t9-> $tst9") // Street Name
                Log.d(mTag, "t10-> $tst10")
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val marker = hMap!!.addMarker(
                MarkerOptions().position(it).title("Title").snippet(pinLocation).clusterable(true)
                    .draggable(true)
            )
            textTag.text = fullName
            invisibleDBText.text = countryName
            markerList.add(marker)
        }
        addMarkerListener()
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

    override fun ssCallback(id: String, push: String) {
        var mUri: String
        Log.d("MapTask", id)
        val callback = HuaweiMap.SnapshotReadyCallback { bitmap ->
            val bit = bitmap
            val recPath = Environment.getExternalStorageDirectory().path + "/Pictures/HiMe"
            val title = countryName + "_" + System.currentTimeMillis().toString()
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
                        //Glide.with(cntx.applicationContext).load(mUri).into(img)
                        Log.d("MapTask", "takeMapSS: Success: $mUri")

                        val dbTravel = DbUtils.getUserFeedRootRef().child("Travel")
                            .child("$id/$countryName/$push")
                        val dbUpload = DbUtils.rootRef().child("uploads/NShareable/$push")

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
        hMap!!.snapshot(callback)
    }
}