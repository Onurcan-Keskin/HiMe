package com.huawei.hime.util

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.huawei.hime.R
import com.huawei.hime.helpers.FirebaseDBHelper
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.collections.HashMap

object OnActivity {

	private const val mTag = "OnActivity"

	fun forPictureResult(
		imageRequest : Int,
		requestCode : Int,
		resultCode : Int,
		data : Intent?,
		context : Activity,
		mStorageReference : StorageReference,
		currentID : String?,
		mUserInfoRef : DatabaseReference,
		mUserFeed : String,
		posterName : String
	) {
		if (requestCode == imageRequest && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
			val imageUri = data.data
			CropImage.activity(imageUri).setAspectRatio(1, 1)
				.setCropShape(CropImageView.CropShape.OVAL).start(context)
		} else {
			showLogError(mTag, "Failed: Crop")
		}
		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			showLogInfo(mTag, "Worked:::::")
			val result = CropImage.getActivityResult(data)

			if (resultCode == Activity.RESULT_OK) {
				val timeDate : String = java.text.DateFormat.getDateTimeInstance().format(
					Date()
				)
				val resultUri = result.uri

				val thumbFilepath = File(resultUri.path)

				val thumbBitmap = Compressor(context)
					.setMaxWidth(200)
					.setMaxHeight(200)
					.setQuality(75)
					.compressToBitmap(thumbFilepath)

				val baos = ByteArrayOutputStream()
				thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
				val thumbByte = baos.toByteArray()

				val filepathRef : StorageReference =
					mStorageReference.child("User").child("Images").child(currentID!!)
						.child(System.currentTimeMillis().toString() + ".jpeg")
				val thumbFilepathRef : StorageReference =
					mStorageReference.child("User").child("Thumbs").child(currentID!!)
						.child(
							System.currentTimeMillis().toString() + ".jpeg"
						)

				val pfPush = FirebaseDBHelper.rootRef()
					.child("User Feed")
					.child("Profile Pictures").push()

				val pushPfKey = pfPush.key.toString()

				filepathRef.putFile(resultUri)
					.addOnCompleteListener { uploadTask : Task<UploadTask.TaskSnapshot> ->
						if (uploadTask.isSuccessful) {
							filepathRef.downloadUrl.addOnSuccessListener { uri : Uri? ->
								val photoUrl = uri.toString()
								val uploadTaskRef =
									thumbFilepathRef.putBytes(thumbByte)
								uploadTaskRef.addOnCompleteListener { thumbTask ->
									if (thumbTask.isSuccessful) {
										filepathRef.downloadUrl.addOnSuccessListener { uri2 : Uri? ->
											val thumbUrl = uri2.toString()
											mUserInfoRef.child("thumbUrl")
												.setValue(thumbUrl)
												.addOnCompleteListener { taskFinal ->
													if (taskFinal.isSuccessful) {
														val updateMap : MutableMap<String, Any> =
															HashMap()
														updateMap["photoUrl"] = photoUrl.toString()
														updateMap["thumbUrl"] = thumbUrl.toString()

														val profileMap : MutableMap<String, String> =
															HashMap()
														profileMap["profile_uploadTime"] = timeDate
														profileMap["profile_photoUrl"] =
															photoUrl.toString()
														profileMap["profile_uploadMillis"] =
															(System.currentTimeMillis()).toString()
														profileMap["profile_thumbUrl"] =
															thumbUrl.toString()
														profileMap["shareStat"] = "0"
														profileMap["commentsAllowed"] = "1"
														profileMap["footer"] =
															"No explanation..."
														profileMap["profile_lovely"] = "0"
														profileMap["tag1"] = ""
														profileMap["tag2"] = ""
														profileMap["tag3"] = ""
														profileMap["tag4"] = ""
														profileMap["tag5"] = ""

														val uploadMap : MutableMap<String, String> =
															HashMap()
														uploadMap["upload_date"] = timeDate
														uploadMap["upload_imageUrl"] =
															photoUrl.toString()
														uploadMap["upload_thumbUrl"] =
															thumbUrl.toString()
														uploadMap["upload_millis"] =
															(System.currentTimeMillis()).toString()
														uploadMap["shareStat"] = "0"
														uploadMap["commentsAllowed"] = "1"
														uploadMap["typeGroup"] =
															"1" // Type Group 1: Video, Image 0: Map
														uploadMap["upload_footer"] =
															"No explanation..."
														uploadMap["uploadType"] = "image"
														uploadMap["upload_posterName"] =
															posterName
														uploadMap["upload_posterImage"] =
															photoUrl.toString()
														uploadMap["uploaderID"] =
															currentID.toString()
														uploadMap["popularity"] = "0"
														uploadMap["upload_lovely"] = "0"
														uploadMap["tag1"] = ""
														uploadMap["tag2"] = ""
														uploadMap["tag3"] = ""
														uploadMap["tag4"] = ""
														uploadMap["tag5"] = ""

														val mapUpload : MutableMap<String, Any> =
															HashMap()
														mapUpload["uploads/NShareable/$pushPfKey"] =
															uploadMap
														FirebaseDBHelper.rootRef().updateChildren(mapUpload)

														val mapProfile : MutableMap<String, Any> =
															HashMap()
														mapProfile["$mUserFeed$currentID/$pushPfKey"] =
															profileMap
														showLogDebug(
															mTag,
															"path: $mUserFeed/$currentID/$pushPfKey"
														)
														FirebaseDBHelper.rootRef().updateChildren(mapProfile)
															.addOnCompleteListener {
																if (it.isSuccessful) {
																	showLogInfo(
																		mTag,
																		"Update Photo - User Feed: Success"
																	)
																} else {
																	showLogError(
																		mTag,
																		"Update Photo - User Feed: Failed"
																	)
																}
															}

														mUserInfoRef.updateChildren(updateMap)
															.addOnCompleteListener { tsk ->
																if (tsk.isSuccessful) {
																	showLogInfo(
																		mTag,
																		"Update Photo - Thumb: Success"
																	)
																} else {
																	showLogError(
																		mTag,
																		"Update Photo - Thumb: Failed"
																	)
																}
															}
													}
												}
										}
									} else {
										Toast.makeText(
											context,
											context.getString(R.string.error_image_upload),
											Toast.LENGTH_SHORT
										)
											.show()
									}
								}
							}
						}
					}
			}
		}
	}

}

