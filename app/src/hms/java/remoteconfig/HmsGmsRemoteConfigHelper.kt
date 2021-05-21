package remoteconfig

import android.content.Context
import android.util.Log
import com.huawei.agconnect.remoteconfig.AGConnectConfig
import com.huawei.hime.R
import com.huawei.hime.splash.SplashHelper
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.analytics.HiAnalytics
import com.huawei.hms.analytics.HiAnalyticsInstance

class HmsGmsRemoteConfigHelper constructor(private val context: Context) : SplashHelper {

    private lateinit var hiAnalyticsInstance: HiAnalyticsInstance
    private lateinit var config: AGConnectConfig


    fun initTestProfile(){
        hiAnalyticsInstance = HiAnalytics.getInstance(this.context)
        hiAnalyticsInstance.setUserProfile("Weekend","Weekend")
        hiAnalyticsInstance.setUserProfile("Weekday","Weekday")

        config = AGConnectConfig.getInstance()
        config.applyDefault(R.xml.remote_config)
        config.fetch().addOnSuccessListener { configValues ->
            config.apply(configValues)
            showAllValues()
        }

        Log.d("AAID",HmsInstanceId.getInstance(context).id)
    }

    private fun showAllValues() {
        val map: MutableMap<String, Any> = config.mergedAll
        val string = StringBuilder()
        for ((key, value) in map) {
            string.append(key)
            string.append(" : ")
            string.append(value)
            string.append("\n")
        }
        Log.d("Time", string.toString())
    }
}