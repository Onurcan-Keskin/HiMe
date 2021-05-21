package com.huawei.hime.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.huawei.agconnect.remoteconfig.AGConnectConfig
import com.huawei.agconnect.remoteconfig.ConfigValues
import com.huawei.hime.R
import com.huawei.hime.login.LoginActivity
import com.huawei.hms.aaid.HmsInstanceId
import kotlinx.android.synthetic.main.activity_splash.*

class MainActivity : AppCompatActivity() {

    private val splashLength: Long = 20

    private lateinit var config: AGConnectConfig
    private lateinit var abconfig: AGConnectConfig
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)

        config = AGConnectConfig.getInstance()
        config.applyDefault(R.xml.remote_config)

        config.loadLastFetched()
        config.fetch(0)
            .addOnSuccessListener { configValues: ConfigValues? ->
                config.apply(configValues)
                Log.d("SplashConfig", config.getValueAsString("Days"))

                showAllValues()

                Log.d(mTag, "Fetch Success")
            }.addOnFailureListener {
                Log.d(
                    mTag,
                    "Fetch Fail $it"
                )
                Log.e("Fetch Error",
                    "$it " + config.getSource(AGConnectConfig.SOURCE.REMOTE.toString())
                        .toString()
                )
            }
        Log.d("AAID", HmsInstanceId.getInstance(baseContext).id)


        abconfig = AGConnectConfig.getInstance()
        abconfig.applyDefault(R.xml.ab_testing)

        //val last = config.loadLastFetched()
        abconfig.loadLastFetched()
        abconfig.fetch(0)
            .addOnSuccessListener { configValues: ConfigValues? ->
                abconfig.apply(configValues)
                Log.d("MainInterestABtesting", abconfig.getValueAsString("Interests"))

                abShowAllValues()

                Log.d(mTag, "Fetch Success")
            }.addOnFailureListener {
                Log.d(
                    mTag,
                    "Fetch Fail $it"
                )
                Log.e("Fetch Error",
                    "$it " + abconfig.getSource(AGConnectConfig.SOURCE.REMOTE.toString())
                        .toString()
                )
            }
//        abShowAllValues()
        Log.d("AAID", HmsInstanceId.getInstance(baseContext).id)


        /* Length in s */
        Thread {
            doWork()
            startApp()
            finish()
        }.start()
    }

    private fun showAllValues() {
        val map: MutableMap<String, Any>? = config.mergedAll
        val string = StringBuilder()
        for ((key, value) in map!!) {
            string.append(key)
            string.append(" : ")
            string.append(value)
            string.append("\n")
        }
        remote_cfg.text = string
    }

    private fun abShowAllValues() {
        val map: MutableMap<String, Any>? = abconfig.mergedAll
        val string = StringBuilder()
        for ((key, value) in map!!) {
            string.append(key)
            string.append(" : ")
            string.append(value)
            string.append("\n")
        }
        text_abtesting.text = string
    }
    fun initViews() {

    }

    private fun doWork() {
        for (progress in 0 until splashLength) {
            try {
                Thread.sleep(splashLength)
                Log.d("Splash Length", progress.toString())
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.e("Splash Error", e.printStackTrace().toString())
            }
        }
    }

    companion object{
        private const val mTag="Splash"
    }

    private fun startApp() {
        startActivity(
            Intent(
                this@MainActivity,
                LoginActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()
    }
}

