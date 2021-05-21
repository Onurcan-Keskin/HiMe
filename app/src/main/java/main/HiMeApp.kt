package main

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.database.*
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.FirebaseDBHelper
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType

class HiMeApp : Application() {

    private var mUserDatabaseRef: DatabaseReference? = null

    override fun onCreate() {
        Log.i(TAG, "onCreate.")
        super.onCreate()
        setApp(this)

        initImageLoader(applicationContext)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        if (AppUser.getUserId().isNotEmpty() && AppUser.getUserId().isNotBlank() && AppUser.getUserId() != "") {
            mUserDatabaseRef = FirebaseDBHelper.getUserInfo(AppUser.getUserId())

            mUserDatabaseRef!!.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    mUserDatabaseRef!!.child("onlineStatus").onDisconnect()
                        .setValue(ServerValue.TIMESTAMP)
                }
            })
        }
        if (AppUser.getUserId() == "dummy"){
            mUserDatabaseRef = FirebaseDBHelper.getUserInfo(AppUser.getUserId())
            mUserDatabaseRef!!.removeValue()
        }
    }

    fun initImageLoader(context: Context?) {
        val config = ImageLoaderConfiguration.Builder(
            context
        )
        config.threadPriority(Thread.NORM_PRIORITY - 2)
        config.denyCacheImageMultipleSizesInMemory()
        config.diskCacheFileNameGenerator(Md5FileNameGenerator())
        config.diskCacheSize(100 * 1024 * 1024) // 100 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO)
        config.writeDebugLogs() // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build())
    }

    companion object {
        private const val TAG = "HiMeApplication"
        var instance: HiMeApp? = null
            private set

        val context: Context
            get() = instance!!.applicationContext

        @Synchronized
        private fun setApp(app: HiMeApp) {
            instance = app
        }
    }
}