package com.huawei.hime.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.databinding.FragmentEventPickCoverBinding
import com.huawei.hime.profile.profileevent.coverphoto.EventPickCoverContract
import com.huawei.hime.profile.profileevent.coverphoto.EventPickCoverPresenter
import com.huawei.hime.ui.mvp.BaseFragmentExtend
import com.huawei.hime.ui.mvp.HasBackButton
import com.huawei.hime.ui.mvp.HasToolbar
import com.huawei.hime.util.views.getColorCompat
import com.huawei.hime.util.views.getDrawableCompat
import java.io.File


class EventPickCoverFragment : BaseFragmentExtend(R.layout.fragment_event_pick_cover), HasToolbar,
	HasBackButton, EventPickCoverContract {

	override val toolbar : Toolbar?
		get() = binding.coverToolbar

	override val titleRes : Int? = null

	private lateinit var context : FragmentActivity
	private lateinit var binding : FragmentEventPickCoverBinding
	private lateinit var mStorageRef : StorageReference

	private var imageUrL : String? = null

	lateinit var coverDataPass : CoverOnDataClass

	private val currentID = AppUser.getUserId()

	private val eventPickCoverPresenter : EventPickCoverPresenter by lazy {
		EventPickCoverPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		context = activity!!
		super.onCreate(savedInstanceState)
	}

	override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setHasOptionsMenu(true)
		binding = FragmentEventPickCoverBinding.bind(view)

		eventPickCoverPresenter.onViewsCreate()

		binding.pickSaveBtn.setOnClickListener {
			val i = Intent(
				Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI
			)
			i.type = "image/"
			startActivityForResult(i, RESULT_LOAD_IMAGE)
		}
	}

	override fun initViews() {
		mStorageRef = FirebaseStorage.getInstance().reference.child("uploads/$currentID").child("cover")
			.child(System.currentTimeMillis().toString() + ".jpg")
	}

	override fun getImageTask(file : File) {
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
						coverDataPass.onCoverDataClass(imageUrL!!)
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

	override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
		//super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
			val selected = data.data
			val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
			val cursor : Cursor? = context.contentResolver.query(
				selected!!,
				filePathColumn, null, null, null
			)
			cursor!!.moveToFirst()
			val columnIndex : Int = cursor.getColumnIndex(filePathColumn[0])
			val picturePath : String = cursor.getString(columnIndex)
			val file = File(picturePath)
			binding.coverPick.setImageBitmap(BitmapFactory.decodeFile(picturePath))
			val photoViewAttacher = PhotoViewAttacher(binding.coverPick)
			photoViewAttacher.maximumScale = 20F
			binding.pickSaveBtn.text = context.getText(R.string.save)
			binding.pickSaveBtn.setOnClickListener {
				getImageTask(file)

				fragmentManager?.popBackStack()
			}
			cursor.close()
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

	override fun onStop() {
		super.onStop()
		requireActivity().window.apply {
			// Reset statusbar color.
			statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
			decorView.systemUiVisibility = 0
		}
	}

	interface CoverOnDataClass {
		fun onCoverDataClass(coverUrl : String)
	}

	override fun onAttach(context : Context) {
		super.onAttach(context)
		coverDataPass = context as CoverOnDataClass
	}

	companion object {
		private const val RESULT_LOAD_IMAGE = 1
	}
}