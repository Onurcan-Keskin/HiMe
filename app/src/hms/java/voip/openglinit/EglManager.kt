package liveChat.openglinit

import android.opengl.EGLContext
import android.opengl.GLES20
import android.util.Log
import android.view.Surface


class EglManager(
    sharedContext: EGLContext?,
    surface: Surface?
) {
    private val mEglCore: EglCore?
    private var mEglProgram2D: EglProgram? = null

    fun release() {
        mEglCore?.release()
        mEglProgram2D?.release()
    }

    fun draw(
        mvpMatrix: FloatArray?,
        texMatrix: FloatArray?, textureId: Int
    ) {
        if (mEglProgram2D != null) {
            mEglProgram2D!!.draw(mvpMatrix, texMatrix!!, textureId)
        }
    }

    fun swapBuffers() {
        if (mEglCore != null) {
            val isSuccess: Boolean = mEglCore.swapBuffers()
            if (!isSuccess) {
                Log.e(TAG, "swapBuffers() Error.")
            }
        }
    }

    companion object {
        private const val TAG = "EglManager"
    }

    init {
        mEglCore = EglCore(sharedContext)
        if (surface != null) {
            mEglCore.createWindowSurface(surface)
            mEglCore.makeCurrent()
            mEglProgram2D = EglProgram(GLES20.GL_TEXTURE_2D)
        }
    }
}