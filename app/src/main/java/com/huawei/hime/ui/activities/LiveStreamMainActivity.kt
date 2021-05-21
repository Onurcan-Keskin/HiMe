package com.huawei.hime.ui.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.hime.R
import com.huawei.hime.helpers.Constants
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.fragments.CaptureVideoFragment
import com.huawei.hime.ui.adapters.LiveStreamPagerAdapter
import com.huawei.hime.livestreamStreamaxia.pickphoto.PickPhotoFragment
import com.huawei.hime.livestreamStreamaxia.takephoto.TakePhotoFragment
import com.huawei.hime.util.LocaleHelper
import com.huawei.hime.util.SharedPreferencesManager
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_live_stream_main.*

class LiveStreamMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        /* Mode State */
        val sharedPrefs = SharedPreferencesManager(this)
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
        setContentView(R.layout.activity_live_stream_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        supportActionBar?.hide()
        requestPermission()
        Slidr.attach(this)

        setupPager()
    }

    private fun setupPager(){
        val adapterL = LiveStreamPagerAdapter(supportFragmentManager)
        adapterL.addFragment(TakePhotoFragment(),getString(R.string.take_photo))
        adapterL.addFragment(CaptureVideoFragment(),getString(R.string.live_capture_video))
        adapterL.addFragment(PickPhotoFragment(),getString(R.string.pick_photo))
        streamerPager.adapter = adapterL
        live_bottom_app_bar.setupWithViewPager(streamerPager)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            2020
        )
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        Log.d("Back", "Pressed")
    }

    override fun onStart() {
        super.onStart()
        FirebaseDBHelper.onConnect(Constants.fOnlineDBRef)
    }

    override fun onPause() {
        super.onPause()
        FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
    }

    override fun onStop() {
        super.onStop()
        FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
    }
}
