package liveChat.caaskitinit

import android.content.Context
import android.content.IntentFilter
import android.opengl.EGLContext
import android.util.Log
import com.huawei.caas.caasservice.HwCaasHandler
import com.huawei.caas.caasservice.HwCaasServiceCallBack
import com.huawei.caas.caasservice.HwCaasServiceManager
import com.huawei.caas.caasservice.HwCaasUtils
import main.HiMeApp

class CaasKitHelper private constructor() {
    private var mDiscoverReceiver: DeviceDiscoverReceiver? = null
    private var mHwCaasServiceManager: HwCaasServiceManager? = null
    private var mHwCaasHandler: HwCaasHandler? = null
    private val mContext: Context
    private var mIsSendShowFail = false
    private var mIsCaasKitInit = false
    private val mCallBack: HwCaasServiceCallBack = object : HwCaasServiceCallBack {
        override fun initSuccess(handler: HwCaasHandler) {
            // Callback after successful initialization of HwCaasHandler.
            mHwCaasHandler = handler
            if (mHwCaasHandler != null) {
                var isSetSuccess = false
                isSetSuccess = mHwCaasHandler!!.setContactViewSize(
                    VIEWWIDTH,
                    VIEWHEIGHT
                )
                Log.i(TAG, " isSetSuccess: $isSetSuccess")
                isSetSuccess = mHwCaasHandler!!.setAppMode(HwCaasUtils.LANDSCAPE)
                Log.i(TAG, " isSetSuccess: $isSetSuccess")
                isSetSuccess =
                    mHwCaasHandler!!.setStartViewBackgroundColor(BACKGROUND_COLOR)
                Log.i(TAG, " isSetSuccess: $isSetSuccess")
                isSetSuccess = mHwCaasHandler!!.setFloatViewLocation(
                    HwCaasUtils.STARTVIEW,
                    HwCaasUtils.POINT_RIGHTANDDOWN,
                    LOCATION_X,
                    LOCATION_STARTY
                )
                Log.i(
                    TAG,
                    "viewType: " + HwCaasUtils.STARTVIEW + " isSetSuccess: " + isSetSuccess
                )
                isSetSuccess = mHwCaasHandler!!.setFloatViewLocation(
                    HwCaasUtils.CONTACTVIEW,
                    HwCaasUtils.POINT_RIGHTANDUP,
                    LOCATION_X,
                    LOCATION_Y
                )
                Log.i(
                    TAG,
                    "viewType: " + HwCaasUtils.CONTACTVIEW + " isSetSuccess: " + isSetSuccess
                )
                isSetSuccess = mHwCaasHandler!!.setFloatViewLocation(
                    HwCaasUtils.CALLVIEW, HwCaasUtils.POINT_RIGHTANDUP,
                    LOCATION_X, LOCATION_Y
                )
                Log.i(
                    TAG,
                    "viewType: " + HwCaasUtils.CALLVIEW + " isSetSuccess: " + isSetSuccess
                )
                isSetSuccess = mHwCaasHandler!!.setFloatViewLocation(
                    HwCaasUtils.VIDEOVIEW, HwCaasUtils.POINT_RIGHTANDUP,
                    LOCATION_X, LOCATION_Y
                )
                Log.i(
                    TAG,
                    "viewType: " + HwCaasUtils.VIDEOVIEW + " isSetSuccess: " + isSetSuccess
                )
                if (mIsSendShowFail) {
                    sendShow()
                    mIsSendShowFail = false
                }
            }
        }

        override fun initFail(retCode: Int) {
            // Callback if init Handler fail.
            Log.i(TAG, "retCode: $retCode")
            if (retCode == HwCaasUtils.SERVICE_EXCEPTION) {
                stopRendering()
            }
        }

        override fun releaseSuccess() {
            // Callback after successful release of mHwCaasServiceManager.
            mHwCaasHandler = null
            mIsSendShowFail = false
        }
    }

    private fun registerDiscoverReceiver() {
        Log.d(TAG, "registerDiscoverReceiver.")
        val intentFilter = IntentFilter()
        intentFilter.addAction(DMSDP_STARTDISCOVERY)
        if (mDiscoverReceiver == null) {
            Log.d(TAG, "mDiscoverReceiver.")
            mDiscoverReceiver = DeviceDiscoverReceiver()
            mContext.registerReceiver(mDiscoverReceiver, intentFilter)
        }
    }

    private fun unregisterDiscoverReceiver() {
        Log.d(TAG, "unregisterDiscoverReceiver.")
        if (mDiscoverReceiver != null) {
            mContext.unregisterReceiver(mDiscoverReceiver)
            mDiscoverReceiver = null
        }
    }

    /**
     * identifies whether the rendering thread is working.
     *
     * @return Returns true if recording has been started.
     */
    val isRendering: Boolean
        get() = ExtSurfaceRender.instance!!.isRendering

    /**
     * tells the surface render to stop rendering.
     */
    fun stopRendering() {
        Log.d(TAG, "stopRendering.")
        if (isRendering) {
            ExtSurfaceRender.instance!!.stopRendering()
        }
    }

    /**
     * Handles an available frame.
     *
     * @param textureId textureId available for rendering.
     */
    fun frameAvailable(textureId: Int) {
        ExtSurfaceRender.instance!!.frameAvailable(textureId)
    }

    /**
     * set the shared EGLContext.
     *
     * @param sharedContext The context to share.
     */
    fun setSharedContext(sharedContext: EGLContext?) {
        ExtSurfaceRender.instance!!.setSharedContext(sharedContext!!)
    }

    /**
     * Initialization before using CaaSKitLite.
     */
    fun caasKitInit() {
        Log.d(TAG, "caasKitInit.$mIsCaasKitInit")
        if (!mIsCaasKitInit) {
            registerDiscoverReceiver()
            // Initialize mHwCaasServiceManager instance.
            mHwCaasServiceManager = HwCaasServiceManager.init()
            // Initialize HwCaasHandler instance through handlerType.
            mHwCaasServiceManager!!.initHandler(mContext, HwCaasUtils.VIRTUAL_CAMERA_TYPE, mCallBack)
            mIsCaasKitInit = true
        }
    }

    /**
     * Virtualize Camera and show float ball.
     */
    fun sendShow() {
        Log.d(TAG, "sendShow.")
        if (mHwCaasHandler != null) {
            // show float ball.
            val ret = mHwCaasHandler!!.sendEventToCaasService(HwCaasUtils.SHOW)
            Log.d(TAG, "ret: $ret")
        } else {
            // Prevent first call, mHwCaasHandler hasn't returned yet.
            mIsSendShowFail = true
            Log.e(TAG, "sendShow fail.")
        }
    }

    /**
     * Called when the application is in the foreground and some scenes do not want to display the float ball.
     *
     * @return returns true if sent successfully.
     */
    fun sendHide(): Boolean {
        Log.d(TAG, "sendHide.")
        if (mHwCaasHandler != null) {
            // Send HIDE event to hide float ball.
            val isSendoK = mHwCaasHandler!!.sendEventToCaasService(HwCaasUtils.HIDE)
            Log.d(TAG, "isSendoK: $isSendoK")
            return isSendoK
        }
        Log.e(TAG, "sendHide fail.")
        return false
    }

    /**
     * called when the app exits.
     */
    fun caasKitRelease() {
        Log.d(TAG, "caasKitRelease.$mIsCaasKitInit")
        if (mIsCaasKitInit) {
            if (mHwCaasServiceManager != null) {
                // Source release.
                mHwCaasServiceManager!!.release()
                mHwCaasServiceManager = null
            }
            unregisterDiscoverReceiver()
            mIsCaasKitInit = false
        }
    }

    companion object {
        private const val TAG = "CaasKitHelper"
        private const val BACKGROUND_COLOR = "#FF4081"
        private const val DMSDP_STARTDISCOVERY = "com.huawei.dmsdp.DMSDP_STARTDISCOVERY"
        private const val VIEWHEIGHT = 248
        private const val VIEWWIDTH = 256
        private const val LOCATION_X = 102
        private const val LOCATION_Y = 40
        private const val LOCATION_STARTY = 24
        private var sCaasKitHelper: CaasKitHelper? = null
        val instance: CaasKitHelper?
            get() {
                if (sCaasKitHelper == null) {
                    synchronized(CaasKitHelper::class.java) {
                        if (sCaasKitHelper == null) {
                            sCaasKitHelper = CaasKitHelper()
                        }
                    }
                }
                return sCaasKitHelper
            }
    }

    init {
        mContext = HiMeApp.context
    }
}