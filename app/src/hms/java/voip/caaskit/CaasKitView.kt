package liveChat.caaskit

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.opengl.EGL14
import android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import liveChat.caaskitinit.CaasKitHelper
import liveChat.openglinit.EglProgram
import liveChat.openglinit.EglUtil
import liveChat.openglinit.EglUtil.genOesTextureId
import java.io.File
import java.io.IOException
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CaasKitView(context: Context?) : GLSurfaceView(context),
    GLSurfaceView.Renderer {
    private var mMediaPlayer: MediaPlayer? = null
    private var mPlayingPosition = 0
    private var mVideoFilePath: File? = null
    private var mEglProgramOes: EglProgram? = null
    private var mEglProgram2D: EglProgram? = null
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mOesTextureId = 0
    private val mModelMatrix = FloatArray(EglUtil.matrixSize)
    private var mOffscreenTextureId = 0
    private var mFrameBuffer = 0
    private var mOesSurface: Surface? = null
    override fun onSurfaceCreated(
        gl: GL10,
        config: EGLConfig
    ) {
        Log.d(TAG, "onSurfaceCreated.")
        mOesTextureId = genOesTextureId()
        mSurfaceTexture = SurfaceTexture(mOesTextureId)
        mOesSurface = Surface(mSurfaceTexture)
        mSurfaceTexture!!.setOnFrameAvailableListener { requestRender() }
        mEglProgramOes = EglProgram(GL_TEXTURE_EXTERNAL_OES)
        mEglProgram2D = EglProgram(GLES20.GL_TEXTURE_2D)
        Matrix.setIdentityM(mModelMatrix, 0)
        CaasKitHelper.instance!!.setSharedContext(EGL14.eglGetCurrentContext())
        if (mPlayingPosition > 0) {
            startPlaying(mPlayingPosition)
            mPlayingPosition = 0
        }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceChanged.")
        destroyFrameBuffer()
        prepareFramebuffer(width, height)
        GLES20.glViewport(0, 0, width, height)
        Log.d(TAG, "width: " + width + "height: " + height)
    }

    override fun onDrawFrame(gl: GL10) {
        if (mSurfaceTexture != null) {
            mSurfaceTexture!!.updateTexImage()
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer)
        mEglProgramOes!!.draw(mModelMatrix, EglUtil.identityMatrix, mOesTextureId)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        if (CaasKitHelper.instance!!.isRendering) {
            CaasKitHelper.instance!!.frameAvailable(mOffscreenTextureId)
        }
        mEglProgram2D!!.draw(EglUtil.identityMatrix, EglUtil.identityMatrix, mOffscreenTextureId)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        super.surfaceDestroyed(holder)
        Log.e(TAG, "surfaceDestroyed.")
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mPlayingPosition = mMediaPlayer!!.currentPosition
            Log.e(
                TAG,
                "mPlayingPosition: $mPlayingPosition"
            )
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause.")
        super.onPause()
        if (mSurfaceTexture != null) {
            Log.d(TAG, "renderer pausing.")
            if (CaasKitHelper.instance!!.isRendering) {
                CaasKitHelper.instance!!.stopRendering()
            }
            destroyFrameBuffer()
            mSurfaceTexture!!.release()
            mEglProgramOes!!.release()
            mEglProgram2D!!.release()
            mSurfaceTexture = null
            mEglProgramOes = null
            mEglProgram2D = null
        }
    }

    fun startPlaying(file: File?): Boolean {
        if (file == null || !file.exists()
            || !file.canRead() || mOesSurface == null
        ) {
            return false
        }
        mVideoFilePath = file
        startPlaying(0)
        return true
    }

    fun onDestroy() {
        Log.d(TAG, "onDestroy.")
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    private fun startPlaying(position: Int) {
        if (mVideoFilePath == null) {
            return
        }
        if (mMediaPlayer != null) {
            Log.e(TAG, "MediaPlayer release.")
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
        mMediaPlayer = MediaPlayer()
        try {
            mMediaPlayer!!.setDataSource(mVideoFilePath.toString())
            mMediaPlayer!!.prepare()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "IllegalStateException.")
        } catch (e: IOException) {
            Log.e(TAG, "IOException.")
        }
        mMediaPlayer!!.setScreenOnWhilePlaying(true)
        mMediaPlayer!!.setOnCompletionListener {
            Log.d(TAG, "mMediaPlayer onCompletion.")
            mMediaPlayer!!.seekTo(0)
            mMediaPlayer!!.start()
        }
        mMediaPlayer!!.setSurface(mOesSurface)
        mMediaPlayer!!.setOnPreparedListener(PrepareListener(position))
    }

    private fun adjustAspectRatio(videoWidth: Int, videoHeight: Int) {
        val viewWidth = width
        val viewHeight = height
        val aspectRatio = videoHeight.toDouble() / videoWidth
        val newWidth: Int
        val newHeight: Int
        if (viewHeight > (viewWidth * aspectRatio).toInt()) {
            newWidth = viewWidth
            newHeight = (viewWidth * aspectRatio).toInt()
        } else {
            newWidth = (viewHeight / aspectRatio).toInt()
            newHeight = viewHeight
        }
        post {
            val layoutParams = layoutParams
            layoutParams.height = newHeight
            layoutParams.width = newWidth
            setLayoutParams(layoutParams)
        }
    }

    private fun prepareFramebuffer(width: Int, height: Int) {
        val values = IntArray(1)
        GLES20.glGenTextures(1, values, 0)
        mOffscreenTextureId = values[0]
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mOffscreenTextureId)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
            GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_NEAREST.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glGenFramebuffers(1, values, 0)
        mFrameBuffer = values[0]
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer)
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D,
            mOffscreenTextureId,
            0
        )
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }

    private fun destroyFrameBuffer() {
        GLES20.glDeleteTextures(1, intArrayOf(mOffscreenTextureId), 0)
        GLES20.glDeleteFramebuffers(1, intArrayOf(mFrameBuffer), 0)
    }

    private inner class PrepareListener(private val mPosition: Int) : OnPreparedListener {
        override fun onPrepared(mediaPlayer: MediaPlayer) {
            adjustAspectRatio(mMediaPlayer!!.videoWidth, mMediaPlayer!!.videoHeight)
            if (mPosition > 0) {
                mMediaPlayer!!.seekTo(mPosition)
            }
            Log.d(TAG, "mediaPlayer start.")
            mMediaPlayer!!.start()
            CaasKitHelper.instance!!.sendShow()
        }

    }

    companion object {
        private const val TAG = "CaasKitDemoView"
        private const val EGL_VERSION = 2
    }

    init {
        Log.d(TAG, "CaasKitDemoView.")
        setEGLContextClientVersion(EGL_VERSION)
        setRenderer(this)
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}