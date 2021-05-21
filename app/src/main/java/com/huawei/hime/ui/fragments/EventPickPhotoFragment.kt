package com.huawei.hime.ui.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.databinding.FragmentEventPickPhotoBinding
import com.huawei.hime.livestreamStreamaxia.pickphoto.GridViewAdapter
import com.huawei.hime.ui.mvp.BaseFragmentExtend
import com.huawei.hime.ui.mvp.HasBackButton
import com.huawei.hime.ui.mvp.HasToolbar
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.util.showLogError
import com.huawei.hime.util.showLogInfo
import com.huawei.hime.util.views.getColorCompat
import com.huawei.hime.util.views.getDrawableCompat
import main.HiMeApp

class EventPickPhotoFragment : BaseFragmentExtend(R.layout.fragment_event_pick_photo), HasToolbar,
	HasBackButton {

	override val toolbar : Toolbar?
		get() = binding.pickPhotoToolbar

	override val titleRes : Int? = null

	private lateinit var context : FragmentActivity
	private lateinit var binding : FragmentEventPickPhotoBinding
	private lateinit var appCTX : HiMeApp

	private lateinit var pickPhotoOnDataPass : PickPhotoOnDataPass

	private var list = ArrayList<String>()
	private var listFile = ArrayList<String>()

	private val intent : Intent? = null

	override fun onCreate(savedInstanceState : Bundle?) {
		context = activity!!
		super.onCreate(savedInstanceState)
	}

	override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setHasOptionsMenu(true)
		binding = FragmentEventPickPhotoBinding.bind(view)

		appCTX = activity!!.application as HiMeApp
		appCTX.initImageLoader(context.applicationContext)

		binding.openCustomGallery.setOnClickListener {
			ImagePicker.create(this)
				.folderMode(true)
				.toolbarFolderTitle("Folder")
				.toolbarImageTitle("Tap to select")
				.toolbarArrowColor(Color.BLACK)
				.includeVideo(false)
				.showCamera(true)
				.multi()
				.limit(5)
				.imageDirectory("HiMe/Camera")
				.theme(R.style.ImagePickerTheme)
				.enableLog(true)
				.start()
		}

		if (list.isNotEmpty()) {
			//loadGridView(list.toString(), list)
			binding.openCustomGallery.text = context.getString(R.string.save)
			binding.openCustomGallery.setOnClickListener {

				fragmentManager?.popBackStack()
			}
		}
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

	override fun onActivityResult(
		requestCode : Int,
		resultCode : Int,
		imageReturnIntent : Intent?
	) {
		//super.onActivityResult(requestCode, resultCode, imageReturnIntent)
		if (ImagePicker.shouldHandle(requestCode, resultCode, imageReturnIntent)) {
			val selectionResult : MutableList<Image> = ImagePicker.getImages(imageReturnIntent)
			//showToast(context, "$selectionResult")
			showLogDebug("EventPick", "$selectionResult")
			selectionResult.forEachIndexed { index, image ->
				showLogDebug("EventPick", "${image.path} ${image.name} ${image.id}")
				list.add(image.path)
				listFile.add("file://${image.path}")
				loadGridView(image.path, list)
				/**/
				var i = 0
				val uriList = ArrayList<String>()
				while (i < listFile.size) {
					val uri = Uri.parse(listFile[i])
					val mStorageReference = FirebaseStorage.getInstance().reference
					val filepathRef : StorageReference =
						mStorageReference.child("User").child("Images").child(AppUser.getUserId())
							.child(System.currentTimeMillis().toString() + ".jpeg")
					filepathRef.putFile(uri).addOnCompleteListener {
						if (it.isSuccessful) {
							filepathRef.downloadUrl.addOnSuccessListener { uri : Uri? ->
								val photoUrl = uri.toString()
								uriList.add(photoUrl)
								pickPhotoOnDataPass.onPickPhotoDataPass(uriList)
								showLogInfo("EventPick", "Uris $uriList")
							}
						}
					}
					i++
				}
			}
		} else {
			showLogError("EventPick", "Selection Error...")
			//showToast(context, "Selection Error...")
		}
	}

	private fun loadGridView(imagesArray : String, list : ArrayList<String>) {
		val adapter = GridViewAdapter(context.applicationContext, imagesArray, list, false)
		binding.selectedImageGrid.adapter = adapter
	}


	override fun onStop() {
		super.onStop()
		requireActivity().window.apply {
			// Reset statusbar color.
			statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
			decorView.systemUiVisibility = 0
		}
	}

	interface PickPhotoOnDataPass {
		fun onPickPhotoDataPass(
			list : ArrayList<String>
		)
	}

	override fun onAttach(context : Context) {
		super.onAttach(context)
		pickPhotoOnDataPass = context as PickPhotoOnDataPass
	}

	companion object {
		private const val CustomGallerySelectId = 1
		const val CustomGalleryIntentKey = "ImageArray"
		const val OPEN_MEDIA_PICKER = 1
	}
}