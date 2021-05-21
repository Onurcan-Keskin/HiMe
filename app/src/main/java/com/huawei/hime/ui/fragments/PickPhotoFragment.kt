package com.huawei.hime.livestreamStreamaxia.pickphoto

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.esafirm.imagepicker.features.ImagePicker
import com.huawei.hime.R
import com.huawei.hime.util.showLogDebug
import com.nostra13.universalimageloader.core.ImageLoader
import main.HiMeApp


class PickPhotoFragment : Fragment(), PickPhotoContract {

	private lateinit var mView : View

	private var selectedImagesList : List<String>? = null
	private var selectImagesArray : ArrayList<String>? = null

	private var selectedImageGridView : GridView? = null

	private var sourcePath : String? = null

	private var openCustomGallery : TextView? = null

	private val intent : Intent? = null

	private lateinit var appCTX : HiMeApp

	private val pickPhotoPresenter : PickPhotoPresenter by lazy {
		PickPhotoPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(
		inflater : LayoutInflater, container : ViewGroup?,
		savedInstanceState : Bundle?
	) : View? {
		mView = inflater.inflate(R.layout.fragment_pick_photo, container, false)

		pickPhotoPresenter.onViewsCreate()

		openCustomGallery!!.setOnClickListener {
			ImagePicker.create(this)
				.folderMode(true)
				.toolbarFolderTitle("Folder")
				.toolbarImageTitle("Tap to select")
				.toolbarArrowColor(Color.BLACK)
				.includeVideo(false)
				.showCamera(false)
				.single()
				.multi()
				.limit(5)
				.imageDirectory("HiMe/Camera")
				.theme(R.style.ImagePickerTheme)
				.enableLog(true)
				.start()
		}

		appCTX = activity!!.application as HiMeApp

		appCTX.initImageLoader(context!!.applicationContext)

		//getSharedImages()

		return mView
	}

	override fun onActivityResult(
		requestCode : Int,
		resultCode : Int,
		imagereturnintent : Intent?
	) {
		//super.onActivityResult(requestCode, resultCode, imagereturnintent)
		if (ImagePicker.shouldHandle(requestCode, resultCode, imagereturnintent)) {
			val selectionResult = ImagePicker.getImages(imagereturnintent)
			showLogDebug(mTAG, "$selectionResult")
			//loadGridView(selectionResult)
		}
	}


	//Load GridView
	private fun loadGridView(list : String, imagesArray : ArrayList<String>) {
		val adapter =
			GridViewAdapter(context!!.applicationContext, list, imagesArray, false)
		selectedImageGridView!!.adapter = adapter

		showLogDebug(
			mTAG,
			ImageLoader.getInstance().diskCache.directory.toPath().toUri().toString()
		)
		showLogDebug(mTAG, "$sourcePath # $selectImagesArray # $imagesArray # $selectedImagesList")
	}

	companion object {
		private const val CustomGallerySelectId = 1
		const val CustomGalleryIntentKey = "ImageArray"
		private const val PROPERTY_IMAGE_REQUEST = 10
		private const val mTAG = "PickPhotoFragment"
	}

	override fun initViews() {
		selectedImageGridView = mView.findViewById(R.id.selectedImageGrid)
		openCustomGallery = mView.findViewById(R.id.openCustomGallery)
	}

	override fun initDB() {
	}

}
