package liveChat.caaskitinit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.huawei.dmsdpsdk.localapp.HwDmsdpService
import main.HiMeApp

class DeviceDiscoverReceiver : BroadcastReceiver() {

    private var mContext: Context? = null

    companion object{
        private const val mTag = "DmsdpStartDiscoverReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(mTag, mTag)
        mContext = HiMeApp.context
        HwDmsdpService.init(context,VirtualCameraListener())
    }
}