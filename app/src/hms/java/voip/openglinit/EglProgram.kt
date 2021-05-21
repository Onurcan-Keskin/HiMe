package liveChat.openglinit

import android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES
import android.opengl.GLES20
import android.util.Log
import java.nio.FloatBuffer

class EglProgram(textureTarget: Int) {

    private val mTAG = "EglProgram"
    private val seperator = System.lineSeparator()
    private val squareCoords = floatArrayOf(
        -1.0f, 1.0f,
        -1.0f, -1.0f,
        1.0f, 1.0f,
        1.0f, -1.0f
    )
    private val textureCoordsOES = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        1.0f, 1.0f
    )
    private val textureCoords2D = floatArrayOf(
        0.0f, 1 - 0.0f,
        0.0f, 1 - 1.0f,
        1.0f, 1 - 0.0f,
        1.0f, 1 - 1.0f
    )

    // For example: botton left hava 2 coords
    private val coordPerVertex = 2
    private val vertexSize = 2
    private val simpleVertexShader = ("uniform mat4 uMVPMatrix;" + seperator
            + "uniform mat4 uTexMatrix;" + seperator
            + "attribute vec4 aPosition;" + seperator
            + "attribute vec4 aTextureCoord;" + seperator
            + "varying vec2 vTextureCoord;" + seperator
            + "void main() {" + seperator
            + "    gl_Position = uMVPMatrix * aPosition;" + seperator
            + "    vTextureCoord = (uTexMatrix * aTextureCoord).xy;" + seperator
            + "}" + seperator)
    private val simpleFragmentShader = ("precision mediump float;" + seperator
            + "varying vec2 vTextureCoord;" + seperator
            + "uniform sampler2D sTexture;" + seperator
            + "void main() {" + seperator
            + "    gl_FragColor = texture2D(sTexture, vTextureCoord);" + seperator
            + "}" + seperator)
    private val oesFragmentShader =
        ("#extension GL_OES_EGL_image_external : require" + seperator
                + "precision mediump float;" + seperator
                + "varying vec2 vTextureCoord;" + seperator
                + "uniform samplerExternalOES sTexture;" + seperator
                + "void main() {" + seperator
                + "  gl_FragColor = texture2D(sTexture, vTextureCoord);" + seperator
                + "}" + seperator)
    private val mStride: Int = coordPerVertex * EglUtil.sizeofFloat
    private val mVertexCount = squareCoords.size / coordPerVertex
    private val mVertexBuf: FloatBuffer? = null
    private var mTexureBuf: FloatBuffer? = null
    private var mProgramId = 0
    private var muMVPMatrixLocation = 0
    private var muTexMatrixLocation = 0
    private var maPositionLocation = 0
    private var maTextureCoordLocation = 0
    private var mTextureTarget = 0

    init {
        Log.d(mTAG, "EglProgram")
        mTextureTarget = textureTarget
        initVertexAndShaderBuffer()
        mProgramId = when (mTextureTarget) {
            GLES20.GL_TEXTURE_2D -> {
                EglUtil.makeProgram(simpleFragmentShader, simpleFragmentShader)
            }
            GL_TEXTURE_EXTERNAL_OES -> {
                EglUtil.makeProgram(simpleVertexShader,oesFragmentShader)
            }
            else -> {
                throw  RuntimeException("textureTarget is illegal.")
            }
        }
        if (mProgramId == 0) {
            throw RuntimeException("Unable to create program.")
        }
        getLocationAttr()
    }

    fun release() {
        Log.d(mTAG, "deleting program: $mProgramId")
        GLES20.glDeleteProgram(mProgramId)
        mProgramId = -1
    }

    fun draw(
        mvpMatrix: FloatArray?,
        texMatrix: FloatArray,
        textureID: Int
    ) {
        // Clear
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Select Program
        GLES20.glUseProgram(mProgramId)
        EglUtil.checkEglError("glUseProgram")

        // Set the texture.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(mTextureTarget, textureID)
        EglUtil.checkEglError("glBindTexture")

        // Copy the model / view / projection matrix over
        GLES20.glUniformMatrix4fv(muMVPMatrixLocation, 1, false, mvpMatrix, 0)
        EglUtil.checkEglError("muMVPMatrixLocation")

        // Copy the texture transformation matrix over
        GLES20.glUniformMatrix4fv(muTexMatrixLocation, 1, false, texMatrix, 0)
        EglUtil.checkEglError("muTexMatrixLocation")

        // Enable the "aPosition" vertex attribute
        GLES20.glEnableVertexAttribArray(maPositionLocation)
        EglUtil.checkEglError("glEnableVertexAttribArray")

        // Connect mVertexBuf to "aPosition"
        GLES20.glVertexAttribPointer(
            maPositionLocation, coordPerVertex,
            GLES20.GL_FLOAT, false, mStride, mVertexBuf
        )
        EglUtil.checkEglError("glVertexAttribPointer")

        // Enable the "aTextureCoord" vertex attribute
        GLES20.glEnableVertexAttribArray(maTextureCoordLocation)
        EglUtil.checkEglError("glEnableVertexAttribArray")

        // Connect mTexureBuf to "aTextureCoord"
        GLES20.glVertexAttribPointer(
            maTextureCoordLocation, vertexSize,
            GLES20.GL_FLOAT, false, mStride, mTexureBuf
        )
        EglUtil.checkEglError("glVertexAttribPointer")

        // Draw the rect
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mVertexCount)
        EglUtil.checkEglError("glDrawArrays")

        // Done -- disable vertex array, texture, and program
        GLES20.glDisableVertexAttribArray(maPositionLocation)
        GLES20.glDisableVertexAttribArray(maTextureCoordLocation)
        GLES20.glBindTexture(mTextureTarget, 0)
        GLES20.glUseProgram(0)
    }

    private fun initVertexAndShaderBuffer(){
        if (mVertexBuf == null){
            mTexureBuf = EglUtil.createFloatBuffer(textureCoords2D)
        } else {
            mTexureBuf = EglUtil.createFloatBuffer(textureCoordsOES)
        }
    }

    private fun getLocationAttr(){
        maPositionLocation = GLES20.glGetAttribLocation(mProgramId, "aPosition")
        EglUtil.checkLocationError(maPositionLocation, "aPosition")
        muMVPMatrixLocation = GLES20.glGetUniformLocation(mProgramId, "uMVPMatrix")
        EglUtil.checkLocationError(muMVPMatrixLocation, "uMVPMatrix")
        maTextureCoordLocation = GLES20.glGetAttribLocation(mProgramId, "aTextureCoord")
        EglUtil.checkLocationError(maTextureCoordLocation, "aTextureCoord")
        muTexMatrixLocation = GLES20.glGetUniformLocation(mProgramId, "uTexMatrix")
        EglUtil.checkLocationError(muTexMatrixLocation, "uTexMatrix")
    }

}