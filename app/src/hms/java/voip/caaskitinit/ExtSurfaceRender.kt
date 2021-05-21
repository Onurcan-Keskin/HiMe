package liveChat.caaskitinit

import android.graphics.Point
import android.opengl.EGLContext
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Surface
import liveChat.openglinit.EglManager
import liveChat.openglinit.EglUtil
import java.lang.ref.WeakReference
import java.util.concurrent.locks.ReentrantLock


class ExtSurfaceRender private constructor() : Runnable {
    private val mObject = ReentrantLock()
    private val condition = mObject.newCondition()
    private var mEglManager: EglManager? = null
    private var mExtSurface: Surface? = null

    var isEGLContextReady = false
        private set

    var sharedContext: EGLContext? = null
        private set

    @Volatile
    private var mHandler: RenderHandler? = null
    private var mIsThreadReady = false
    private var mIsRunning = false
    private var videoResolution: Point? = null

    fun frameAvailable(textureId: Int) {
        synchronized(mObject) {
            if (!mIsThreadReady) {
                return
            }
        }
        mHandler!!.sendMessage(
            mHandler!!.obtainMessage(
                MSG_FRAME_AVAILABLE,
                textureId,
                0,
                null
            )
        )
    }

    fun setSharedContext(sharedContext: EGLContext) {
        Log.d(mTAG, "setSharedContext: $sharedContext")
        this.sharedContext = sharedContext
        isEGLContextReady = true
    }

    fun setVideoResolution(width: Int, height: Int) {
        Log.d(mTAG, "width: " + width + "height: " + height)
        videoResolution = Point(width, height)
    }

    fun getVideoResolution(): Point? {
        return if (videoResolution == null) {
            Point(
                VIDEO_WEIGHT,
                VIDEO_HEIGHT
            )
        } else videoResolution
    }

    fun startRendering(surface: Surface?) {
        Log.d(mTAG, "startRendering.")
        synchronized(mObject) {
            if (mIsRunning) {
                Log.w(mTAG, "Render thread already running.")
                return
            }
            if (!isEGLContextReady) {
                Log.w(mTAG, "EGLContext is not ready.")
                return
            }
            mIsRunning = true
            mExtSurface = surface
            Thread(this, "ExtSurfaceRender").start()
            while (!mIsThreadReady) {
                try {
                    condition.await()
                } catch (e: InterruptedException) {
                    Log.e(mTAG, "InterruptedException.")
                }
            }
        }
        mHandler!!.sendMessage(mHandler!!.obtainMessage(MSG_START_RENDERING))
    }

    fun stopRendering() {
        Log.d(mTAG, "stopRendering.")
        if (isRendering) {
            mHandler!!.sendMessage(mHandler!!.obtainMessage(MSG_STOP_RENDERING))
            mHandler!!.sendMessage(mHandler!!.obtainMessage(MSG_QUIT_RENDERING))
        }
    }

    val isRendering: Boolean
        get() {
            synchronized(mObject) { return mIsRunning }
        }

    override fun run() {
        Looper.prepare()
        synchronized(mObject) {
            mHandler = RenderHandler(this)
            mIsThreadReady = true
            condition.signal()
        }
        Looper.loop()
        Log.d(mTAG, "Render thread exiting.")
        synchronized(mObject) {
            mIsThreadReady = false
            mIsRunning = false
            mHandler = null
        }
    }

    private fun handleFrameAvailable(textureId: Int) {
        if (mEglManager != null) {
            mEglManager!!.draw(EglUtil.identityMatrix, EglUtil.identityMatrix, textureId)
            mEglManager!!.swapBuffers()
        }
    }

    private class RenderHandler(render: ExtSurfaceRender) : Handler() {
        private val mWeakRender: WeakReference<ExtSurfaceRender> = WeakReference(render)
        override fun handleMessage(inputMessage: Message) {
            val what = inputMessage.what
            val render = mWeakRender.get()
            if (render == null) {
                Log.e(mTAG, "render is null.")
                return
            }
            when (what) {
                MSG_START_RENDERING -> render.handleStartRendering()
                MSG_STOP_RENDERING -> render.handleStopRendering()
                MSG_FRAME_AVAILABLE -> render.handleFrameAvailable(
                    inputMessage.arg1
                )
                MSG_QUIT_RENDERING -> Looper.myLooper()!!.quit()
                else -> throw RuntimeException("invalid value: $what")
            }
        }
    }

    private fun handleStartRendering() {
        Log.d(mTAG, "StartRendering.")
        mEglManager = EglManager(sharedContext, mExtSurface)
    }

    private fun handleStopRendering() {
        Log.d(mTAG, "handleStopRendering.")
        if (mEglManager != null) {
            mEglManager!!.release()
            mEglManager = null
        }
        mExtSurface = null
    }

    companion object {
        private const val mTAG = "ExtSurfaceRender"
        private const val MSG_START_RENDERING = 0
        private const val MSG_STOP_RENDERING = 1
        private const val MSG_FRAME_AVAILABLE = 2
        private const val MSG_QUIT_RENDERING = 3
        private const val VIDEO_WEIGHT = 1920
        private const val VIDEO_HEIGHT = 1080
        private var sExtSurfaceRender: ExtSurfaceRender? = null
        val instance: ExtSurfaceRender?
            get() {
                if (sExtSurfaceRender == null) {
                    synchronized(ExtSurfaceRender::class.java) {
                        if (sExtSurfaceRender == null) {
                            sExtSurfaceRender = ExtSurfaceRender()
                        }
                    }
                }
                return sExtSurfaceRender
            }
    }
}