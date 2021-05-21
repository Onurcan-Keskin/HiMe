package com.huawei.hime.ui.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.Constants
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.interfaces.IMap
import com.huawei.hime.ui.interfaces.IMapActivity
import com.huawei.hime.ui.presenters.MapPresenter
import com.huawei.hime.util.*
import com.huawei.hime.util.views.expandView
import kotlinx.android.synthetic.main.activity_map.*
import map.HmsGmsIMap

class MapActivity : AppCompatActivity(), IMapActivity.ViewMapActivity {

    private lateinit var mapView: View
    private lateinit var context: Context

    private var latitude: Double = 41.028710
    private var longitude: Double = 29.117660
    private var lt: Float = 0.0F

    private lateinit var locationManager: LocationManager

    private lateinit var countryNameView: TextView

    private lateinit var addressNameView: TextView

    private lateinit var travelFooterEdit: TextInputEditText

    private lateinit var tagEdit1: TextInputEditText

    private lateinit var tagEdit2: TextInputEditText

    private lateinit var tagEdit3: TextInputEditText

    private lateinit var tagEdit4: TextInputEditText

    private lateinit var tagEdit5: TextInputEditText

    private var mPosterName: String? = null
    private var mPosterImge: String? = null

    private val REQUEST_CODE_ASK_PERMISSIONS = 1

    private val RUNTIME_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

    private lateinit var sharedPrefs: SharedPreferencesManager

    private val hmsGmsIMap: IMap by lazy { HmsGmsIMap(this) }

    private val mapPresenter: MapPresenter by lazy { MapPresenter(this, hmsGmsIMap) }


    override fun onCreate(savedInstanceState: Bundle?) {

        sharedPrefs = SharedPreferencesManager(this)
        /* Mode State */
        if (sharedPrefs.loadNightModeState())
            setTheme(R.style.DarkMode)
        else
            setTheme(R.style.LightMode)
        /* Language State */
        if (sharedPrefs.loadLanguage()=="tr")
            LocaleHelper.setLocale(this, "tr")
        else
            LocaleHelper.setLocale(this, "en")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapPresenter.onViewsCreate()
        mapPresenter.setPreferences(
            this,
            sharedPrefs,
            profile_travel_footer_lyt,
            showTag,
            profile_travel_tag1_lyt,
            profile_travel_tag2_lyt,
            profile_travel_tag3_lyt,
            profile_travel_tag4_lyt,
            profile_travel_tag5_lyt
        )

        showTag.setOnClickListener {
            tagContainerLayout.expandView()
            if (tagContainerLayout.visibility == View.GONE) {
                showTag.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_arrow_downward,
                    0
                )
            } else {
                showTag.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_arrow_upward,
                    0
                )
            }
        }

        mapView = findViewById(R.id.map_map)

        hmsGmsIMap.onSavedInstanceBundle(savedInstanceState)
        hmsGmsIMap.listenCamera()
        hmsGmsIMap.onInitMap()

        requestPermissions()

        mapView.setOnClickListener {
            hmsGmsIMap.onMapReady()
        }

        profile_travel_save_btn.setOnClickListener {
            mapPresenter.uploadToDB(
                profile_travel_cnstrnt,
                this,
                AppUser.getUserId(),
                countryNameView,
                travelFooterEdit,
                addressNameView,
                latitude,
                longitude,
                hmsGmsIMap,
                mPosterName!!,
                mPosterImge!!,
                tagEdit1, tagEdit2, tagEdit3, tagEdit4, tagEdit5
            )
        }
    }

    private fun requestPermissions() {
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                RUNTIME_PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS
            )
        } else {
            showLogDebug("Permissions Granted: ", "All")
            hmsGmsIMap.onInitMap()
        }
    }


    private fun String.permissionGranted(ctx: Context) =
        ContextCompat.checkSelfPermission(ctx, this) == PackageManager.PERMISSION_GRANTED

    private fun hasPermissions(): Boolean {
        return RUNTIME_PERMISSIONS.count { !it.permissionGranted(this) } == 0
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        hmsGmsIMap.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                for (index in RUNTIME_PERMISSIONS.indices) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        var notGrantedMessage =
                            "Required permission ${permissions[index]} not granted."

                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                permissions[index]
                            )
                        ) {
                            notGrantedMessage += "Please go to settings and turn on for sample app."
                        }

                        Toast.makeText(this, notGrantedMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun initViews() {
        context = applicationContext
        Constants.fUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showLogError(this.javaClass.simpleName,p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                mPosterName = p0.child("nameSurname").value.toString()
                mPosterImge = p0.child("photoUrl").value.toString()
            }
        })

        countryNameView = findViewById(R.id.profile_invisible_country_db_txt)
        addressNameView = findViewById(R.id.profile_travel_location_tag_txt)
        travelFooterEdit = findViewById(R.id.profile_travel_footer)

        tagEdit1 = findViewById(R.id.profile_travel_tag1)
        tagEdit2 = findViewById(R.id.profile_travel_tag2)
        tagEdit3 = findViewById(R.id.profile_travel_tag3)
        tagEdit4 = findViewById(R.id.profile_travel_tag4)
        tagEdit5 = findViewById(R.id.profile_travel_tag5)
    }


    override fun getLocationUpdate() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
//        locationManager.requestLocationUpdates(
//            LocationManager.GPS_PROVIDER,
//            0,
//            lt,
//            object : LocationListener {
//                override fun onLocationChanged(location: Location?) {
//                    if (location != null) {
//                        latitude = location.latitude
//                        longitude = location.longitude
//                        current_location_ic.bringToFront()
//                        current_location_ic.setOnClickListener {
//                            mapPresenter.onCurrentLocation()
//                            hmsGmsIMap.gotoCurrentLocation(latitude, longitude)
//                        }
//                        showLogDebug("location", "lat: $latitude, long: $longitude")
//                    }
//                }
//
//                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
//                    showLogInfo(this.javaClass.simpleName,"New Stat $status, $provider")
//                }
//
//                override fun onProviderEnabled(provider: String?) {
//                    showLogInfo(this.javaClass.simpleName,"Provider: $provider")
//                }
//
//                override fun onProviderDisabled(provider: String?) {
//                    showLogInfo(this.javaClass.simpleName,"Disabled: $provider")
//                }
//            })
    }

    override fun takeMapSS() {
        showLogInfo(this.javaClass.simpleName,"Take SS")
    }

    override fun onMapClick() {
        mapPresenter.onMapInteract()
    }

    override fun showLogMessage(message: String) {
        showLogDebug("MapActivity ", message)
    }

    override fun onStart() {
        super.onStart()
        hmsGmsIMap.onStartMap()
        FirebaseDBHelper.onConnect(Constants.fOnlineDBRef)
    }

    override fun onResume() {
        super.onResume()
        hmsGmsIMap.onResumeMap()
        FirebaseDBHelper.onConnect(Constants.fOnlineDBRef)
    }

    override fun onPause() {
        super.onPause()
        hmsGmsIMap.onPauseMap()
        FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
    }

    override fun onDestroy() {
        super.onDestroy()
        hmsGmsIMap.onDestroyMap()
        FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        hmsGmsIMap.onLowMemoryMap()
    }

}
