package takePhoto

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.livestreamStreamaxia.takephoto.TakePhotoFragment
import com.huawei.hime.ui.interfaces.ITakePhoto
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.util.showLogError
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class HmsGmsITakePhoto constructor(context: TakePhotoFragment) : ITakePhoto {

    private var mContext: FragmentActivity = context.activity!!

    private var mView: View = LayoutInflater.from(mContext).inflate(R.layout.fragment_take_photo,null)
    private lateinit var inc: View

    private lateinit var previewView: PreviewView
    private lateinit var torchView: ImageView

    private lateinit var cameraProviderFeature: ListenableFuture<ProcessCameraProvider>
    private lateinit var imagePreviewView: Preview
    private lateinit var cameraSelector: CameraSelector
    private lateinit var cameraControl: CameraControl
    private lateinit var cameraInfo: CameraInfo
    private lateinit var imageCapture: ImageCapture
    private lateinit var outputDirectory: File

    private lateinit var imageAnalysis: ImageAnalysis
    private val executor = Executors.newSingleThreadExecutor()

    private lateinit var mUserFeedRef: DatabaseReference
    private lateinit var mUserInfoRef: DatabaseReference
    private lateinit var mStorageRef: StorageReference
    private lateinit var currentID: String
    private lateinit var posterName: String
    private lateinit var posterImage: String
    private lateinit var posterInterest: String
    private val timeStamp = System.currentTimeMillis().toString()
    private var imageUrL: String? = null

    override fun initViewsCreate() {
//        inc = mView.findViewById(R.id.take_photo_prv_layout)
//        previewView = inc.findViewById(R.id.take_camera_preview)
//        torchView = inc.findViewById(R.id.take_camera_torch)
    }

    override fun onViewResume() {

    }

    override fun onViewPause() {
    }

    override fun onViewDestroy() {
    }

    override fun initProvider(context: Context) {
        cameraProviderFeature = ProcessCameraProvider.getInstance(context)
    }

    override fun initDB() {
        currentID = AppUser.getUserId()
        mUserInfoRef = FirebaseDBHelper.getUserInfo(currentID)
        mUserFeedRef = FirebaseDBHelper.getUserFeedRootRef().child("Photos/$currentID")
        mStorageRef = FirebaseStorage.getInstance().reference.child("uploads/$currentID")
            .child("Pictures")
            .child(timeStamp)

        mUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val name = p0.child("nameSurname").value.toString()
                posterName = if (name.isNullOrEmpty())
                    "HimeUser"
                else
                    name
                posterInterest = p0.child("interests").value.toString()
                posterImage = p0.child("photoUrl").value.toString()
            }
        })
    }


    override fun takePicture() {
        val file = createFile(
            outputDirectory,
            FILENAME,
            PHOTO_EXTENSION
        )
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            outputFileOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${file.absolutePath}"
                    previewView.post {
                        Toast.makeText(
                            mContext.applicationContext,
                            msg,
                            Toast.LENGTH_SHORT
                        ).show()
                        getImageTask(file)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    val msg = "Photo capture failed: ${exception.message}"
                    showLogError(mTAG, msg)
                }
            })
    }

    override fun setTorchStateObserver() {
        cameraInfo.torchState.observe(mContext, androidx.lifecycle.Observer { state ->
            if (state == TorchState.ON) {
                torchView.setImageResource(R.drawable.ic_flash_on)
            } else {
                torchView.setImageResource(R.drawable.ic_flash_off)
            }
        })
    }

    override fun getImageTask(file: File) {

    }

    override fun toggleTorch() {
        when (cameraInfo.torchState.value) {
            TorchState.ON -> {
                cameraControl.enableTorch(false)
            }
            else -> {
                cameraControl.enableTorch(true)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun startCameraBack() {
        CameraX.unbindAll()
        torchView.visibility = View.VISIBLE
        imagePreviewView = Preview.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
            setTargetRotation(previewView.display.rotation)
            setDefaultResolution(Size(1920, 1080))
            setMaxResolution(Size(3024, 4032))
        }.build()

        imageAnalysis = ImageAnalysis.Builder().apply {
            setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        }.build()
        imageAnalysis.setAnalyzer(executor, LuminosityAnalyzer())

        imageCapture = ImageCapture.Builder().apply {
            setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
        }.build()

        cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        cameraProviderFeature.addListener(Runnable {
            val cameraProvider = cameraProviderFeature.get()
            val camera = cameraProvider.bindToLifecycle(
                mContext,
                cameraSelector,
                imagePreviewView,
                imageAnalysis,
                imageCapture
            )

            cameraControl = camera.cameraControl
            cameraInfo = camera.cameraInfo

            previewView.preferredImplementationMode =
                PreviewView.ImplementationMode.TEXTURE_VIEW
            imagePreviewView.setSurfaceProvider(previewView.createSurfaceProvider(camera.cameraInfo))
        }, ContextCompat.getMainExecutor(mContext.applicationContext))
    }

    @SuppressLint("RestrictedApi")
    override fun startCameraFront() {
        CameraX.unbindAll()
        torchView.visibility = View.GONE
        imagePreviewView = Preview.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_4_3)
            //setTargetRotation(previewView.display.rotation)
            setDefaultResolution(Size(1920, 1080))
            setMaxResolution(Size(3024, 4032))
        }.build()

        imageAnalysis = ImageAnalysis.Builder().apply {
            setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        }.build()
        imageAnalysis.setAnalyzer(executor, LuminosityAnalyzer())

        imageCapture = ImageCapture.Builder().apply {
            setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
        }.build()

        cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

        cameraProviderFeature.addListener(Runnable {
            val cameraProvider = cameraProviderFeature.get()
            val camera = cameraProvider.bindToLifecycle(
                mContext,
                cameraSelector,
                imagePreviewView,
                imageAnalysis,
                imageCapture
            )
            previewView.preferredImplementationMode =
                PreviewView.ImplementationMode.TEXTURE_VIEW
            imagePreviewView.setSurfaceProvider(previewView.createSurfaceProvider(camera.cameraInfo))
        }, ContextCompat.getMainExecutor(mContext.applicationContext))
    }

    private class LuminosityAnalyzer : ImageAnalysis.Analyzer {
        private var lastAnalyzedTimestamp = 0L
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {
            val currentTimestamp = System.currentTimeMillis()
            if (currentTimestamp - lastAnalyzedTimestamp >=
                TimeUnit.SECONDS.toMillis(1)
            ) {
                val buffer = image.planes[0].buffer
                val data = buffer.toByteArray()
                val pixels = data.map { it.toInt() and 0xFF }
                val luma = pixels.average()
                showLogDebug(mTAG, "Average luminosity: $luma")
                lastAnalyzedTimestamp = currentTimestamp
            }
            image.close()
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val mTAG = "HmsGmsTakePhoto"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS" //"yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private var recPath = Environment.getExternalStorageDirectory().path + "/Pictures/HiMe"


        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(
                    recPath
                ).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists()) mediaDir else appContext.filesDir
        }

        fun createFile(baseFolder: File, format: String, extension: String) =
            File(
                baseFolder, SimpleDateFormat(format, Locale.ROOT)
                    .format(System.currentTimeMillis()) + extension
            )
    }
}