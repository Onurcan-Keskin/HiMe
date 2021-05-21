package com.huawei.hime.profile.profileevent.takePhoto

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Size
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.databinding.FragmentEventTakePhotoBinding
import com.huawei.hime.ui.mvp.BaseFragmentExtend
import com.huawei.hime.ui.mvp.HasBackButton
import com.huawei.hime.ui.mvp.HasToolbar
import com.huawei.hime.util.*
import com.huawei.hime.util.views.getColorCompat
import com.huawei.hime.util.views.getDrawableCompat
import com.squareup.picasso.Picasso
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class EventTakePhotoFragment : BaseFragmentExtend(R.layout.fragment_event_take_photo), HasToolbar,
	HasBackButton, EventTakePhotoContract {

	override val toolbar : Toolbar?
		get() = binding.takePhotoToolbar

	override val titleRes : Int?
		get() = null

	private lateinit var takePhotoDataPass : TakePhotoOnDataPass

	private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

	/* CameraX Features */
	private lateinit var cameraProviderFeature : ListenableFuture<ProcessCameraProvider>
	private lateinit var cameraSelector : CameraSelector
	private lateinit var imagePreviewView : Preview
	private lateinit var cameraControl : CameraControl
	private lateinit var cameraInfo : CameraInfo
	private lateinit var imageCapture : ImageCapture

	private var imageUrL : String? = null

	private var clickCount = 0
	private var startTime : Long? = null
	private var duration : Long? = null
	private val maxDuration = 500

	private lateinit var imageAnalysis : ImageAnalysis
	private val executor = Executors.newSingleThreadExecutor()

	private lateinit var outputDirectory : File

	private val currentID = AppUser.getUserId()

	private lateinit var context : FragmentActivity
	private lateinit var binding : FragmentEventTakePhotoBinding
	private lateinit var mStorageRef : StorageReference

	private val eventTakePhotoPresenter : EventTakePhotoPresenter by lazy {
		EventTakePhotoPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		context = activity!!
		super.onCreate(savedInstanceState)
		cameraProviderFeature = ProcessCameraProvider.getInstance(context.applicationContext)
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentEventTakePhotoBinding.bind(view)
		eventTakePhotoPresenter.onViewsCreate()

		if (allPermissionsGranted()) {
			binding.previewView.post {
				startCameraFront()
			}
		} else {
			requestPermissions(
				REQUIRED_PERMISSIONS,
				REQUEST_CODE_PERMISSIONS
			)
		}

		binding.previewView.setOnTouchListener { _, event ->
			onTouchEvent(event!!)
			true
		}

		binding.flipCircleView.setOnTouchListener { _, event ->
			onTouchEvent(event!!)
			true
		}


		val mp = MediaPlayer.create(context.applicationContext, R.raw.shutter_click)

		binding.takePhotoBtn.setOnClickListener {
			mp.start()
			takePicture()
		}

		binding.torchView.setOnClickListener {
			toggleTorch()
			setTorchStateObserver()
		}
	}

	private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
		ContextCompat.checkSelfPermission(
			context.applicationContext,
			it
		) == PackageManager.PERMISSION_GRANTED
	}

	override fun onStart() {
		super.onStart()
		val closeIndicator = requireContext().getDrawableCompat(R.drawable.ic_cancel)?.apply {
			setColorFilter(
				requireContext().getColorCompat(R.color.red),
				PorterDuff.Mode.SRC_ATOP
			)
		}
		(activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(closeIndicator)
		requireActivity().window.apply {
			// Update statusbar color to match toolbar color.
			statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
			// decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
		}
	}

	override fun onStop() {
		super.onStop()
		requireActivity().window.apply {
			// Reset statusbar color.
			statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
			decorView.systemUiVisibility = 0
		}
	}

	override fun initViews() {
		outputDirectory = getOutputDirectory(context.applicationContext)
		mStorageRef = FirebaseStorage.getInstance().reference.child("uploads/$currentID").child("photo")
			.child(System.currentTimeMillis().toString() + ".jpg")
	}

	override fun getImageTask(file : File) {
		binding.progressLottie.visibility = View.VISIBLE
		binding.progressLottie.bringToFront()
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
						binding.progressLottie.visibility = View.GONE
						if (imageUrL != null) {
							slideUpVisible(binding.savePick)
							binding.savePick.setOnClickListener {
								takePhotoDataPass.onTakePhotoDataPass(imageUrL!!)
								fragmentManager?.popBackStack()
							}
						} else {
							slideDownGone(binding.savePick)
						}
						Glide.with(context.applicationContext).load(imageUrL)
							.into(binding.videoCircleView)
						binding.videoCircleView.setOnClickListener {
							startZoomDialog(R.style.DialogSlide)
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

	override fun onTouchEvent(event : MotionEvent) : Boolean {
		when (event.action and MotionEvent.ACTION_MASK) {
			MotionEvent.ACTION_DOWN -> {
				startTime = System.currentTimeMillis()
				clickCount++
				binding.previewView.post { startCameraFront() }
			}
			MotionEvent.ACTION_UP -> {
				if (clickCount == 1) {
					startTime = System.currentTimeMillis()
				} else if (clickCount == 2) {
					duration = System.currentTimeMillis() - startTime!!
					if (duration!! <= maxDuration) {
						showLogInfo(
							mTAG,
							"onTouchEvent: Double Tap " + binding.previewView.cameraDistance
						)
						binding.previewView.post { startCameraBack() }
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
	override fun startCameraFront() {
		showLogDebug(mTAG, "startCameraFront")

		CameraX.unbindAll()

		binding.torchView.visibility = View.INVISIBLE

		imagePreviewView = Preview.Builder().apply {
			setTargetAspectRatio(AspectRatio.RATIO_4_3)
			setTargetRotation(binding.previewView.display.rotation)
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
			binding.previewView.preferredImplementationMode =
				PreviewView.ImplementationMode.TEXTURE_VIEW
			imagePreviewView.setSurfaceProvider(binding.previewView.createSurfaceProvider(cameraInfo))
		}, ContextCompat.getMainExecutor(context.applicationContext))
	}

	@SuppressLint("RestrictedApi")
	override fun startCameraBack() {
		showLogDebug(mTAG, "startCameraBack")

		CameraX.unbindAll()

		binding.torchView.visibility = View.VISIBLE

		imagePreviewView = Preview.Builder().apply {
			setTargetAspectRatio(AspectRatio.RATIO_16_9)
			setTargetRotation(binding.previewView.display.rotation)
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
			binding.previewView.preferredImplementationMode =
				PreviewView.ImplementationMode.TEXTURE_VIEW
			imagePreviewView.setSurfaceProvider(binding.previewView.createSurfaceProvider(cameraInfo))
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

	private fun setTorchStateObserver() {
		cameraInfo.torchState.observe(this, androidx.lifecycle.Observer { state ->
			if (state == TorchState.ON)
				binding.torchView.setImageResource(R.drawable.ic_flash_on)
			else
				binding.torchView.setImageResource(R.drawable.ic_flash_off)
		})
	}

	override fun startZoomDialog(type : Int) {
		val dialog = Dialog(context, R.style.BlurTheme)
		val width = ViewGroup.LayoutParams.MATCH_PARENT
		val height = ViewGroup.LayoutParams.MATCH_PARENT
		dialog.window!!.setLayout(width, height)
		dialog.window!!.attributes.windowAnimations = type
		dialog.setContentView(R.layout.zoom_image)
		dialog.setCanceledOnTouchOutside(true)
		val imageView : ImageView = dialog.findViewById(R.id.zoomed_image)
		val cancel : ImageButton = dialog.findViewById(R.id.cancel_button)
		Picasso.get().load(imageUrL)
			.placeholder(R.drawable.splachback)
			.into(imageView)
		val photoViewAttacher = PhotoViewAttacher(imageView)
		photoViewAttacher.maximumScale = 20F
		cancel.bringToFront()
		cancel.setOnClickListener {
			dialog.dismiss()
		}
		dialog.show()
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
				override fun onImageSaved(outputFileResults : ImageCapture.OutputFileResults) {
					val msg = "Photo capture succeeded: ${file.absolutePath}"
					binding.previewView.post {
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
					showSnackbar(binding.previewView, msg)
				}
			}
		)
	}

	interface TakePhotoOnDataPass {
		fun onTakePhotoDataPass(
			imageUrl : String
		)
	}

	override fun onAttach(context : Context) {
		super.onAttach(context)
		takePhotoDataPass = context as TakePhotoOnDataPass
	}

	companion object {
		private const val REQUEST_CODE_PERMISSIONS = 10
		private const val mTAG = "EventTakePhotoFragment"
		private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS" //"yyyy-MM-dd-HH-mm-ss-SSS"
		private const val PHOTO_EXTENSION = ".jpg"
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