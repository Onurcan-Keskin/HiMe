package liveChat.openglinit

import android.opengl.EGL14
import android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES
import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10


object EglUtil {

    const val mTAG = "EglUtil"

    const val sizeofFloat = 4

    val identityMatrix: FloatArray

    const val matrixSize = 16

    fun makeProgram(
        vertexProgramSource: String?,
        fragmentProgramSource: String?
    ): Int {
        val vertexProgramShader =
            loadShader(GLES20.GL_VERTEX_SHADER, vertexProgramSource)
        if (vertexProgramShader == 0) {
            return 0
        }
        val pixelProgramShader =
            loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentProgramSource)
        if (pixelProgramShader == 0) {
            return 0
        }
        var programId = GLES20.glCreateProgram()
        checkEglError("glCreateProgram")
        if (programId == 0) {
            Log.e(mTAG, "Could not make program")
        }
        GLES20.glAttachShader(programId, vertexProgramShader)
        checkEglError("vertexProgramShader")
        GLES20.glAttachShader(programId, pixelProgramShader)
        checkEglError("pixelProgramShader")
        GLES20.glLinkProgram(programId)
        val linkProgramStatus = IntArray(1)
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkProgramStatus, 0)
        if (linkProgramStatus[0] != GLES20.GL_TRUE) {
            Log.e(mTAG, "Could not link program.")
            Log.e(mTAG, GLES20.glGetProgramInfoLog(programId))
            GLES20.glDeleteProgram(programId)
            programId = 0
        }
        return programId
    }

    fun loadShader(shaderType: Int, shaderSource: String?): Int {
        var shaderId = GLES20.glCreateShader(shaderType)
        checkEglError("glCreateShader type: $shaderType")
        GLES20.glShaderSource(shaderId, shaderSource)
        GLES20.glCompileShader(shaderId)
        val compiledShaders = IntArray(1)
        GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, compiledShaders, 0)
        if (compiledShaders[0] == 0) {
            Log.e(mTAG, "Could not compile shaderId: $shaderType : ")
            Log.e(mTAG, " " + GLES20.glGetShaderInfoLog(shaderId))
            GLES20.glDeleteShader(shaderId)
            shaderId = 0
        }
        return shaderId
    }

    fun checkEglError(msg: String) {
        var error: Int
        if (EGL14.eglGetError().also { error = it } != EGL14.EGL_SUCCESS) {
            throw RuntimeException(
                "$msg-EGL error: 0x" + Integer.toHexString(
                    error
                )
            )
        }
    }

    fun checkLocationError(location: Int, label: String) {
        if (location < 0) {
            throw RuntimeException("location error: $label")
        }
    }

    fun createFloatBuffer(coords: FloatArray): FloatBuffer {
        val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(coords.size * sizeofFloat)
        byteBuffer.order(ByteOrder.nativeOrder())
        val floatBuffer: FloatBuffer = byteBuffer.asFloatBuffer()
        floatBuffer.put(coords)
        floatBuffer.position(0)
        return floatBuffer
    }

    fun rotateMatrix(matrix: FloatArray, angle: Float): FloatArray {
        Matrix.rotateM(matrix, 0, angle, 0F, 0F, 1F)
        return matrix
    }


    fun genOesTextureId(): Int {
        val textureObjectId = IntArray(1)
        GLES20.glGenTextures(1, textureObjectId, 0)
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureObjectId[0])
        GLES20.glTexParameterf(
            GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameteri(
            GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE
        )
        return textureObjectId[0]
    }

    init {
        identityMatrix = FloatArray(matrixSize)
        Matrix.setIdentityM(identityMatrix, 0)
    }
}