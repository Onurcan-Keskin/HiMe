package profiletravel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import com.huawei.hime.R
import com.huawei.hime.profile.profiletravel.ProfileMapHelper
import com.huawei.hime.profile.profiletravel.ProfileTravelViewHolder
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.Marker
import com.huawei.hms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class HmsGmsProfileTravelHelper constructor(context: ProfileTravelViewHolder) : ProfileMapHelper,
    OnMapReadyCallback {

    private var view: View = context.parent
    private var ui: View = view.findViewById(R.id.map_layout)
    private val cntx = context

    private var hMap: HuaweiMap? = null

    private var mMarker: Marker? = null
    private var pinLocation: String? = null
    private var markerList: MutableList<Marker> = ArrayList()

    private var mapView: MapView? = ui.findViewById(R.id.posted_single_map)

    companion object {
        private const val mapviewBundleKey = "MapViewBundleKey"
        private const val mTag = "HmsGmsProfileTravelHelper"
    }

    override fun onInitMap(lat: Double, long: Double) {
        mapView!!.getMapAsync(this)
    }

    override fun onMapReady() {

    }

    override fun onSavedInstanceBundle(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(mapviewBundleKey)
        }
        mapView!!.onCreate(mapViewBundle)
        mapView!!.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
    }

    override fun onStartMap() {
        mapView!!.onStart()
    }

    override fun onPauseMap() {
        mapView!!.onPause()
    }

    override fun onResumeMap() {
        mapView!!.onResume()
    }

    override fun onDestroyMap() {
        mapView!!.onDestroy()
    }

    override fun onLowMemoryMap() {
    }

    override fun getTappedAndPin(lat: Double, long: Double) {

    }

    override fun listenCamera() {

    }

    override fun pinItWithMarker(context: Context) {

    }

    private fun addMarker() {
        mMarker!!.isDraggable = true
        hMap!!.setOnMarkerClickListener {
            val clusterable = mMarker!!.isClusterable
            true
        }

        hMap!!.setOnMarkerDragListener(object : HuaweiMap.OnMarkerDragListener {
            override fun onMarkerDragEnd(p0: Marker?) {
                Log.d(mTag, "onMarkerDragStart: ")
            }

            override fun onMarkerDragStart(p0: Marker?) {
                Log.d(mTag, "onMarkerDragStart: ")
            }

            override fun onMarkerDrag(p0: Marker?) {
                Log.d(mTag, "onMarkerDrag: ")
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
            val geocoder = Geocoder(view.context, Locale.getDefault())
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
                pinLocation = "$tst6, $tst10 - $tst3"
                Log.d(mTag, "ci-> $cityName")
                Log.d(mTag, "t1-> $tst1")
                Log.d(mTag, "t2-> $tst2")
                Log.d(mTag, "t3-> $tst3")
                Log.d(mTag, "t4-> $tst4")
                Log.d(mTag, "t5-> $tst5")
                Log.d(mTag, "t6-> $tst6")
                Log.d(mTag, "t7-> $tst7")
                Log.d(mTag, "t8-> $tst8")
                Log.d(mTag, "t9-> $tst9")
                Log.d(mTag, "t10-> $tst10")
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val marker = hMap!!.addMarker(
                MarkerOptions().position(it).title("Title").snippet(pinLocation).clusterable(true)
                    .draggable(true)
            )
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
}