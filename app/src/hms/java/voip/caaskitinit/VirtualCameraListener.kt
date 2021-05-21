package liveChat.caaskitinit

import android.util.Log
import android.view.Surface
import com.huawei.dmsdpsdk.localapp.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class VirtualCameraListener : CameraListener() {

    val deviceID = UUID.randomUUID().toString()
    val virCameraID = "himeVirtualCamera$deviceID"

    companion object {
        private const val videoBufferModeYUV = 1
        private const val deviceCamera = 5

        //change it
        private const val deviceName = "CaasKitCamera"
        private const val mTag = "VirtualCameraListener"
    }

    override fun getDeviceInfo(): MutableList<DeviceInfo> {
        Log.d(mTag, "getDeviceInfo")
        val listDeviceInfo: MutableList<DeviceInfo> = ArrayList()
        val deviceInfo = DeviceInfo()
        deviceInfo.deviceID = deviceID
        deviceInfo.deviceName = deviceName
        deviceInfo.deviceType = deviceCamera
        deviceInfo.properties[CommonUtils.DEVICE_SUPPORTCAMERA_BOOLEAN] = "true"
        listDeviceInfo.add(deviceInfo)

        return listDeviceInfo
    }

    override fun getCameraInfo(p0: String?): HashMap<String, CameraInfo> {
        Log.d(mTag, "getCameraInfo")

        val mapCameraInfo: HashMap<String, CameraInfo> = HashMap()
        val cameraInfo = CameraInfo()
        cameraInfo.cameraId = virCameraID
        cameraInfo.videoType = videoBufferModeYUV
        cameraInfo.supportedFpsRange = "25000,25000"
        cameraInfo.supportedResolutionRange = "1080,1920"
        mapCameraInfo[virCameraID] = cameraInfo

        return mapCameraInfo
    }

    override fun getCameraParameters(cameraId: String?): CameraParameters {
        Log.d(mTag, "getCameraParameters")
        val cameraParams = CameraParameters()
        cameraParams.cameraId = virCameraID
        cameraParams.properties[CommonUtils.CURRENT_RESOLUTION] = "1080,1920"
        cameraParams.properties[CommonUtils.CURRENT_FRAMERATES] = "25000,30000"
        cameraParams.properties[CommonUtils.CURRENT_IMAGEFORMAT] = CommonUtils.IMAGE_FORMAT_NV21
        cameraParams.properties[CommonUtils.CURRENT_DECODEFORMAT] = CommonUtils.DECODE_FORMAT_RGBA

        return cameraParams
    }

    override fun startCaptureVideo(cameraId: String?, surface: Surface?): Int {
        Log.d(mTag,"startCaptureVideo: $cameraId")
        if (ExtSurfaceRender.instance!!.isEGLContextReady){
            Log.d(mTag,"startCaptureVideo:")
            ExtSurfaceRender.instance!!.startRendering(surface)
        }
        return 0
    }

    override fun stopCaptureVideo(cameraID: String?): Int {
        Log.d(mTag,"stopCaptureVideo.")
        ExtSurfaceRender.instance!!.stopRendering()
        return 0
    }

    override fun notifyUnbindMSDPService(): Int {
        Log.d(mTag,"notifyUnbindMSDPService.")
        HwDmsdpService.release()
        return 0
    }

    override fun checkPermissionOnCallback(result: Int) {
        Log.d(mTag, "checkPermissionOnCallback.result: $result")
    }
}