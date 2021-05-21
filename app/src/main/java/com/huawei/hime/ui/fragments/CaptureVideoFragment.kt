package com.huawei.hime.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Camera
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.view.View
import androidx.camera.core.*
import androidx.camera.core.impl.VideoCaptureConfig
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
import com.huawei.hime.databinding.FragmentCaptureVideoBinding
import com.huawei.hime.helpers.Constants
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.interfaces.ICaptureVideo
import com.huawei.hime.ui.mvp.BaseFragment
import com.huawei.hime.ui.presenters.CaptureVideoPresenter
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.showToast
import java.io.File
import java.util.concurrent.Executors

class CaptureVideoFragment : BaseFragment(R.layout.fragment_capture_video), ICaptureVideo.ViewCaptureVideo {

	private lateinit var context : FragmentActivity
	private lateinit var binding : FragmentCaptureVideoBinding

	private lateinit var outputDirectory : File

	/* CameraView */
	private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
	private lateinit var cameraSelector: CameraSelector
	private lateinit var videoPreviewView: Preview
	private lateinit var cameraControl: CameraControl
	private lateinit var cameraInfo: CameraInfo

	private lateinit var videoCapture: VideoCapture

	private var isRecording = false

	private var isFrontFacing = true

	private var camera: Camera? = null

	private val executor = Executors.newSingleThreadExecutor()

	private lateinit var mUserFeedRef : DatabaseReference
	private lateinit var mStorageRef : StorageReference
	private lateinit var currentID : String
	private lateinit var posterName : String
	private lateinit var posterImage : String
	private lateinit var posterInterest : String
	private val timeStamp = System.currentTimeMillis().toString()

	private lateinit var sharedPrefs : SharedPreferencesManager

	private var imageUrL : String? = null

	private val captureVideoPresenter : CaptureVideoPresenter by lazy {
		CaptureVideoPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		context = activity!!
		sharedPrefs = SharedPreferencesManager(context)
		if (sharedPrefs.loadNightModeState()) {
			context.setTheme(R.style.DarkMode)
		} else {
			context.setTheme(R.style.LightMode)
		}
		super.onCreate(savedInstanceState)
	}

	override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentCaptureVideoBinding.bind(view)
		captureVideoPresenter.onCreateViews()
		outputDirectory = getOutputDirectory(context.applicationContext)

		binding.previewView.post { initFrontCamera() }
		binding.toggleCamera.setOnClickListener {
			toggleCamera()
		}
		binding.captureVideo.setOnClickListener { recordVideo() }
	}

	override fun initViews() {
		context = activity!!
		cameraProviderFuture = ProcessCameraProvider.getInstance(context)
	}

	override fun initDB() {
		currentID = AppUser.getUserId()
		mUserFeedRef = FirebaseDBHelper.getUserFeedRootRef().child("Photos/$currentID")
		mStorageRef = FirebaseStorage.getInstance().reference.child("uploads/$currentID")
			.child("Pictures")
			.child(timeStamp)

		Constants.fUserInfoRef.addValueEventListener(object : ValueEventListener {
			override fun onCancelled(p0 : DatabaseError) {
			}

			override fun onDataChange(p0 : DataSnapshot) {
				val name = p0.child("nameSurname").value.toString()
				posterName = if (name.isEmpty())
					"HimeUser"
				else
					name
				posterInterest = p0.child("interests").value.toString()
				posterImage = p0.child("photoUrl").value.toString()
			}
		})
	}

	override fun toggleCamera() {
		isFrontFacing = if (isFrontFacing) {
			binding.previewView.post { initFrontCamera() }
			false
		} else {
			binding.previewView.post { initBackCamera() }
			true
		}
	}

	override fun recordVideo() {
		val camStartFx = MediaPlayer.create(context, R.raw.camera_start_marimba)
		val camStopFx = MediaPlayer.create(context, R.raw.camera_stop_marimba)
		isRecording = if (!isRecording) {
			dAccept.setOnClickListener {
				dialog.dismiss()
				camStartFx.start()
				binding.recordImg.setImageDrawable(
					ContextCompat.getDrawable(
						context,
						R.drawable.ic_start_video
					)
				)
				startRecording()
			}

			true
		} else {
			camStopFx.start()
			binding.recordImg.setImageDrawable(
				ContextCompat.getDrawable(
					context,
					R.drawable.ic_stop_video
				)
			)
			stopRecording()
			false
		}
	}

	@SuppressLint("RestrictedApi")
	override fun startRecording() {
		val file = Constants.createFile(
			outputDirectory,
			Constants.FILENAME,
			Constants.VIDEO_EXTENSION
		)
		videoCapture.startRecording(
			file,
			executor,
			object : VideoCapture.OnVideoSavedCallback {
				override fun onVideoSaved(file: File) {
					Handler(Looper.getMainLooper()).post {
						showToast(context, file.name)
						val uri = Uri.fromFile(file)
						//TODO intent to LiveSave
					}
				}

				override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
					Handler(Looper.getMainLooper()).post {
						showToast(context, file.name + " failed to save. / $message")
					}
				}
			}
		)
	}

	@SuppressLint("RestrictedApi")
	override fun stopRecording() {
		videoCapture.stopRecording()
	}

	@SuppressLint("RestrictedApi")
	override fun initFrontCamera() {
		CameraX.unbindAll()
		outputDirectory = Constants.getOutputDirectory(this)
		videoPreviewView = Preview.Builder().apply {
			setTargetAspectRatio(AspectRatio.RATIO_16_9)
			setTargetRotation(binding.previewView.display.rotation)
		}.build()

		cameraSelector =
			CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

		val videoCaptureCfg = VideoCaptureConfig.Builder().apply {
			setTargetRotation(binding.previewView.display.rotation)
			setCameraSelector(cameraSelector)
			setTargetAspectRatio(AspectRatio.RATIO_16_9)
		}
		videoCapture = VideoCapture(videoCaptureCfg.useCaseConfig)

		cameraProviderFuture.addListener({
			val cameraProvider = cameraProviderFuture.get()
			camera = cameraProvider.bindToLifecycle(
				context,
				cameraSelector,
				videoPreviewView,
				videoCapture
			)
			cameraInfo = camera?.cameraInfo!!
			cameraControl = camera?.cameraControl!!
			binding.previewView.preferredImplementationMode =
				PreviewView.ImplementationMode.TEXTURE_VIEW
			videoPreviewView.setSurfaceProvider(binding.previewView.createSurfaceProvider(cameraInfo))
		}, ContextCompat.getMainExecutor(context.applicationContext))
	}

	@SuppressLint("RestrictedApi")
	override fun initBackCamera() {
		CameraX.unbindAll()
		outputDirectory = Constants.getOutputDirectory(this)
		videoPreviewView = Preview.Builder().apply {
			setTargetAspectRatio(AspectRatio.RATIO_16_9)
			setTargetRotation(binding.previewView.display.rotation)
		}.build()

		cameraSelector =
			CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

		val videoCaptureCfg = VideoCaptureConfig.Builder().apply {
			setTargetRotation(binding.previewView.display.rotation)
			setCameraSelector(cameraSelector)
			setMaxResolution(Size(1080, 2310))
			setDefaultResolution(Size(1080, 2310))
		}

		videoCapture = VideoCapture(videoCaptureCfg.useCaseConfig)

		cameraProviderFuture.addListener({
			val cameraProvider = cameraProviderFuture.get()
			camera = cameraProvider.bindToLifecycle(
				this,
				cameraSelector,
				videoPreviewView,
				videoCapture
			)
			cameraInfo = camera?.cameraInfo!!
			cameraControl = camera?.cameraControl!!
			binding.previewView.preferredImplementationMode =
				PreviewView.ImplementationMode.TEXTURE_VIEW
			videoPreviewView.setSurfaceProvider(binding.previewView.createSurfaceProvider(cameraInfo))
		}, ContextCompat.getMainExecutor(context.applicationContext))
	}
}