package com.huawei.hime.livestreamStreamaxia.takephoto

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Size
import android.view.MotionEvent
import android.view.View
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
import com.huawei.hime.databinding.FragmentTakePhotoBinding
import com.huawei.hime.ui.mvp.BaseFragment
import com.huawei.hime.util.*
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.presenters.TakePhotoContract
import com.huawei.hime.ui.presenters.TakePhotoPresenter
import com.huawei.hime.util.views.expandView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_take_photo.*
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TakePhotoFragment : BaseFragment(R.layout.fragment_take_photo), TakePhotoContract {

	private lateinit var context : FragmentActivity
	private lateinit var binding : FragmentTakePhotoBinding

	private lateinit var outputDirectory : File
	private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

	/* CameraX Features */
	private lateinit var cameraProviderFeature : ListenableFuture<ProcessCameraProvider>
	private lateinit var cameraSelector : CameraSelector
	private lateinit var imagePreviewView : Preview
	private lateinit var cameraControl : CameraControl
	private lateinit var cameraInfo : CameraInfo
	private lateinit var imageCapture : ImageCapture

	private var clickCount = 0
	private var startTime : Long? = null
	private var duration : Long? = null
	private val maxDuration = 500

	private lateinit var imageAnalysis : ImageAnalysis
	private val executor = Executors.newSingleThreadExecutor()

	private lateinit var mUserFeedRef : DatabaseReference
	private lateinit var mUserInfoRef : DatabaseReference
	private lateinit var mStorageRef : StorageReference
	private val currentID = AppUser.getUserId()
	private lateinit var posterName : String
	private lateinit var posterImage : String
	private lateinit var posterInterest : String
	private val timeStamp = System.currentTimeMillis().toString()

	private lateinit var sharedPrefs : SharedPreferencesManager

	private var imageUrL : String? = null

	private val takePhotoPresenter : TakePhotoPresenter by lazy {
		TakePhotoPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		context = activity!!
		sharedPrefs = SharedPreferencesManager(context)
		/* Mode State */
		val sharedPrefs = SharedPreferencesManager(context)
		if (sharedPrefs.loadNightModeState())
			context.setTheme(R.style.DarkMode)
		else
			context.setTheme(R.style.LightMode)
		/* Language State */
		if (sharedPrefs.loadLanguage()=="tr")
			LocaleHelper.setLocale(context, "tr")
		else
			LocaleHelper.setLocale(context, "en")
		super.onCreate(savedInstanceState)
		cameraProviderFeature = ProcessCameraProvider.getInstance(context.applicationContext)
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentTakePhotoBinding.bind(view)
		takePhotoPresenter.onCreateViews()

		if (allPermissionsGranted()) {
			binding.camera.post {
				startCameraFront()
			}
		} else {
			requestPermissions(
				REQUIRED_PERMISSIONS,
				REQUEST_CODE_PERMISSIONS
			)
		}

		val mp = MediaPlayer.create(context.applicationContext, R.raw.shutter_click)

		binding.camera.setOnTouchListener { _, event ->
			onTouchEvent(event!!)
			true
		}

		binding.capturePicture.setOnClickListener {
			mp.start()
			takePicture()
		}

		binding.cameraFlash.setOnClickListener {
			toggleTorch()
			setTorchStateObserver()
		}

		binding.sideView.setOnClickListener {
			side_layout.expandView()
		}

		binding.toggleCamera.setOnTouchListener { _, event ->
			onTouchEvent(event!!)
			true
		}
	}

	private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
		ContextCompat.checkSelfPermission(
			context.applicationContext,
			it
		) == PackageManager.PERMISSION_GRANTED
	}

	fun getImageTask(file : File) {
		binding.lottiePr.visibility = View.VISIBLE
		binding.lottiePr.bringToFront()
		val uri = Uri.fromFile(file)
		try {
			val uploadTask = mStorageRef.putFile(uri)
			uploadTask.continueWith {
				if (!it.isSuccessful) {
					it.exception?.let { t -> throw t }
				}
				mStorageRef.downloadUrl
			}.addOnCompleteListener {
				if (it.isSuccessful) {
					it.result!!.addOnSuccessListener { task ->
						imageUrL = task.toString()
						binding.lottiePr.visibility = View.GONE
						if (imageUrL != null) {
							binding.watermarkHolder.visibility=View.VISIBLE
							Picasso.get().load(imageUrL).centerCrop().fit().into(binding.watermark)
						} else {
							binding.watermarkHolder.visibility=View.GONE
						}
					}
				}
			}
		} catch (e : Exception) {
			Toast.makeText(
				context.applicationContext,
				R.string.error_video_upload,
				Toast.LENGTH_SHORT
			).show()
		}
	}

	fun onTouchEvent(event : MotionEvent) : Boolean {
		when (event.action and MotionEvent.ACTION_MASK) {
			MotionEvent.ACTION_DOWN -> {
				startTime = System.currentTimeMillis()
				clickCount++
				binding.camera.post { startCameraFront() }
			}
			MotionEvent.ACTION_UP -> {
				if (clickCount == 1) {
					startTime = System.currentTimeMillis()
				} else if (clickCount == 2) {
					duration = System.currentTimeMillis() - startTime!!
					if (duration!! <= maxDuration) {
						showLogInfo(
							mTAG,
							"onTouchEvent: Double Tap " + binding.camera.cameraDistance
						)
						binding.camera.post { startCameraBack() }
						clickCount = 0
						duration = 0
						//state = 1
					} else {
						clickCount = 1
						startTime = System.currentTimeMillis()
					}
				}
			}
		}
		return true
	}

	@SuppressLint("RestrictedApi")
	fun startCameraFront() {
		showLogDebug(mTAG, "startCameraFront")

		CameraX.unbindAll()

		binding.cameraFlash.visibility = View.INVISIBLE

		imagePreviewView = Preview.Builder().apply {
			setTargetAspectRatio(AspectRatio.RATIO_4_3)
			setTargetRotation(binding.camera.display.rotation)
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
				this,
				cameraSelector,
				imagePreviewView,
				imageAnalysis,
				imageCapture
			)
			cameraControl = camera.cameraControl
			cameraInfo = camera.cameraInfo
			binding.camera.preferredImplementationMode =
				PreviewView.ImplementationMode.TEXTURE_VIEW
			imagePreviewView.setSurfaceProvider(binding.camera.createSurfaceProvider(cameraInfo))
		}, ContextCompat.getMainExecutor(context.applicationContext))
	}

	@SuppressLint("RestrictedApi")
	fun startCameraBack() {
		showLogDebug(mTAG, "startCameraBack")

		CameraX.unbindAll()

		binding.cameraFlash.visibility = View.VISIBLE

		imagePreviewView = Preview.Builder().apply {
			setTargetAspectRatio(AspectRatio.RATIO_16_9)
			setTargetRotation(binding.camera.display.rotation)
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
				this,
				cameraSelector,
				imagePreviewView,
				imageAnalysis,
				imageCapture
			)
			cameraControl = camera.cameraControl
			cameraInfo = camera.cameraInfo
			binding.camera.preferredImplementationMode =
				PreviewView.ImplementationMode.TEXTURE_VIEW
			imagePreviewView.setSurfaceProvider(binding.camera.createSurfaceProvider(cameraInfo))
		}, ContextCompat.getMainExecutor(context.applicationContext))
	}

	private class LuminosityAnalyzer : ImageAnalysis.Analyzer {
		private var lastAnalyzedTimestamp = 0L

		private fun ByteBuffer.toByteArray() : ByteArray {
			rewind()    // Rewind the buffer to zero
			val data = ByteArray(remaining())
			get(data)   // Copy the buffer into a byte array
			return data // Return the byte array
		}

		override fun analyze(image : ImageProxy) {
			val currentTimestamp = System.currentTimeMillis()
			// Calculate the average luma no more often than every second
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

	fun toggleTorch() {
		when (cameraInfo.torchState.value) {
			TorchState.ON -> {
				cameraControl.enableTorch(false)
			}
			else -> {
				cameraControl.enableTorch(true)
			}
		}
	}

	private fun setTorchStateObserver() {
		cameraInfo.torchState.observe(this, androidx.lifecycle.Observer { state ->
			if (state == TorchState.ON)
				binding.cameraFlash.setImageResource(R.drawable.ic_flash_on)
			else
				binding.cameraFlash.setImageResource(R.drawable.ic_flash_off)
		})
	}

	fun takePicture() {
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
				override fun onImageSaved(outputFileResults : ImageCapture.OutputFileResults) {
					val msg = "Photo capture succeeded: ${file.absolutePath}"
					binding.camera.post {
						Toast.makeText(
							context.applicationContext,
							msg,
							Toast.LENGTH_SHORT
						).show()
						getImageTask(file)
					}
				}

				override fun onError(exception : ImageCaptureException) {
					val msg = "Photo capture failed: ${exception.message}"
					showSnackbar(binding.camera, msg)
				}
			}
		)
	}

	override fun initViews() {
		context = activity!!
		outputDirectory = getOutputDirectory(context.applicationContext)
	}

	override fun initDB() {
		mUserInfoRef = FirebaseDBHelper.getUserInfo(currentID)
		mUserFeedRef = FirebaseDBHelper.getUserFeedRootRef().child("Photos/$currentID")
		mStorageRef = FirebaseStorage.getInstance().reference.child("uploads/$currentID").child("photo")
			.child(System.currentTimeMillis().toString() + ".jpg")

		mUserInfoRef.addValueEventListener(object : ValueEventListener {
			override fun onCancelled(p0 : DatabaseError) {
			}

			override fun onDataChange(p0 : DataSnapshot) {
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

	companion object {
		private const val REQUEST_CODE_PERMISSIONS = 10
		private const val mTAG = "TakePhotoFragment"
		private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS" //"yyyy-MM-dd-HH-mm-ss-SSS"
		private const val PHOTO_EXTENSION = ".jpg"
		private const val VIDEO_EXTENSION = ".mp4"
		private const val USE_FRAME_PROCESSOR = true
		private const val DECODE_BITMAP = false
		private var recPath = Environment.getExternalStorageDirectory().path + "/Pictures/HiMe"


		fun getOutputDirectory(context : Context) : File {
			val appContext = context.applicationContext
			val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
				File(
					recPath
				).apply { mkdirs() }
			}
			return if (mediaDir != null && mediaDir.exists()) mediaDir else appContext.filesDir
		}

		fun createFile(baseFolder : File, format : String, extension : String) =
			File(
				baseFolder, SimpleDateFormat(format, Locale.ROOT)
					.format(System.currentTimeMillis()) + extension
			)
	}

}