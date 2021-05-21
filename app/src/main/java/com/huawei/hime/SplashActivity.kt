package com.huawei.hime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.huawei.hime.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val splashLength: Long = 60
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()
        /* Length in s */
        Thread(Runnable {
            doWork()
            startApp()
            finish()
        }).start()
    }

    private fun doWork() {
        for (progrs in 0 until splashLength) {
            try {
                Thread.sleep(splashLength)
                Log.d("Splash Length", progrs.toString())
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.e("Splash Error", e.printStackTrace().toString())
            }
        }
    }

    private fun startApp() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
