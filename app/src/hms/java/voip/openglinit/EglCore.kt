package liveChat.openglinit

import android.graphics.SurfaceTexture
import android.opengl.*
import android.opengl.EGLExt.EGL_RECORDABLE_ANDROID
import android.util.Log
import android.view.Surface
import liveChat.openglinit.EglUtil.checkEglError


class EglCore(sharedContext: EGLContext?) {
    private var mEGLDisplay: EGLDisplay = EGL14.EGL_NO_DISPLAY
    private var mEGLContext: EGLContext = EGL14.EGL_NO_CONTEXT
    private var mEGLConfig: EGLConfig? = null
    private var mEglSurface: EGLSurface? = null

    fun release() {
        EGL14.eglDestroySurface(mEGLDisplay, mEglSurface)
        EGL14.eglMakeCurrent(
            mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
            EGL14.EGL_NO_CONTEXT
        )
        EGL14.eglDestroyContext(mEGLDisplay, mEGLContext)
        EGL14.eglReleaseThread()
        EGL14.eglTerminate(mEGLDisplay)
        mEGLDisplay = EGL14.EGL_NO_DISPLAY
        mEGLContext = EGL14.EGL_NO_CONTEXT
        mEglSurface = EGL14.EGL_NO_SURFACE
        mEGLConfig = null
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        try {
            if (mEGLDisplay !== EGL14.EGL_NO_DISPLAY) {
                Log.e(TAG, "EglCore was not explicitly released.")
                release()
            }
        } finally {
            finalize()
        }
    }

    fun createWindowSurface(surface: Any) {
        if (surface !is Surface && surface !is SurfaceTexture) {
            throw RuntimeException("invalid surface: $surface")
        }
        val surfaceAttribs = intArrayOf(
            EGL14.EGL_NONE
        )
        val eglSurface: EGLSurface = EGL14.eglCreateWindowSurface(
            mEGLDisplay, mEGLConfig, surface,
            surfaceAttribs, 0
        )
        checkEglError("eglCreateWindowSurface")
        mEglSurface = eglSurface
    }

    fun makeCurrent() {
        if (mEglSurface == null) {
            Log.e(TAG, "makeCurrent eglSurface is null.")
            return
        }
        if (!EGL14.eglMakeCurrent(mEGLDisplay, mEglSurface, mEglSurface, mEGLContext)) {
            throw RuntimeException("eglMakeCurrent failed")
        }
    }

    fun swapBuffers(): Boolean {
        return EGL14.eglSwapBuffers(mEGLDisplay, mEglSurface)
    }

    companion object {
        private const val TAG = "EglCore"
        private const val VERSION_SIZE = 2
        private const val EGL_SIZE = 8
    }

    init {
        var sharedContext: EGLContext? = sharedContext
        if (mEGLDisplay !== EGL14.EGL_NO_DISPLAY) {
            throw RuntimeException("EGL already set up.")
        }
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        if (mEGLDisplay === EGL14.EGL_NO_DISPLAY) {
            throw RuntimeException("unable to get EGL14 display.")
        }
        val version = IntArray(VERSION_SIZE)
        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            mEGLDisplay = EGL14.EGL_NO_DISPLAY
            throw RuntimeException("eglInitialize failed.")
        }
        if (sharedContext == null) {
            sharedContext = EGL14.EGL_NO_CONTEXT
        }
        if (mEGLContext === EGL14.EGL_NO_CONTEXT) {
            val attributes = intArrayOf(
                EGL14.EGL_RED_SIZE, EGL_SIZE,
                EGL14.EGL_GREEN_SIZE, EGL_SIZE,
                EGL14.EGL_BLUE_SIZE, EGL_SIZE,
                EGL14.EGL_ALPHA_SIZE, EGL_SIZE,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL_RECORDABLE_ANDROID, 1,
                EGL14.EGL_NONE
            )
            val configs: Array<EGLConfig?> = arrayOfNulls(1)
            val numConfigs = IntArray(1)
            if (!EGL14.eglChooseConfig(
                    mEGLDisplay, attributes, 0, configs, 0, configs.size,
                    numConfigs, 0
                )
            ) {
                throw RuntimeException("eglChooseConfig fail.")
            }
            val attribList = intArrayOf(
                EGL14.EGL_CONTEXT_CLIENT_VERSION, VERSION_SIZE,
                EGL14.EGL_NONE
            )
            val context: EGLContext = EGL14.eglCreateContext(
                mEGLDisplay, configs[0], sharedContext,
                attribList, 0
            )
            checkEglError("eglCreateContext")
            mEGLConfig = configs[0]
            mEGLContext = context
        }
    }
}