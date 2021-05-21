package com.huawei.hime.ui.presenters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.models.InterestsType
import com.huawei.hime.ui.interfaces.IInterest
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.util.showLogError
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import java.io.ByteArrayOutputStream
import java.io.File

class InterestsPresenter constructor(
    private val interestsContract : IInterest.ViewInterest
) {

	fun onViewCreate() {
		interestsContract.initViews()
	}

	fun onNextBtnClick(itemList : List<InterestsType>) {
		var interestByte = ""
		itemList.forEachIndexed { _, interestsType ->
			interestByte = if (interestsType.isSelectedChck) {
				"${interestByte}1"
			} else {
				"${interestByte}0"
			}
		}
		Log.d("Ayberk", interestByte)

		interestsContract.checkEmptyFields()
		interestsContract.populateDB(interestByte)
	}


	fun resultActivity(
        requestCode : Int,
        resultCode : Int,
        data : Intent?,
        context : Context,
        mStorageRef : StorageReference,
        mDatabaseRef : DatabaseReference
    ) {
		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			val result : CropImage.ActivityResult = CropImage.getActivityResult(data)

			if (resultCode == Activity.RESULT_OK) {
				val resultUri = result.uri

				val thumbFilePath = File(resultUri.path)

				val thumbBitmap : Bitmap = Compressor(context)
					.setMaxWidth(200)
					.setMaxHeight(200)
					.setQuality(75)
					.compressToBitmap(thumbFilePath)

				val baos = ByteArrayOutputStream()
				thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
				val thumbByte = baos.toByteArray()

				val filepathRef : StorageReference =
					mStorageRef.child("User").child("Images").child(AppUser.getUserId())
						.child(System.currentTimeMillis().toString() + ".jpeg")
				val thumbFilepathRef : StorageReference =
					mStorageRef.child("User").child("Thumbs").child(AppUser.getUserId())
						.child(
                            System.currentTimeMillis().toString() + ".jpeg"
                        )

				filepathRef.putFile(resultUri)
					.addOnCompleteListener { uploadTask : Task<UploadTask.TaskSnapshot> ->
						if (uploadTask.isSuccessful) {
							filepathRef.downloadUrl.addOnSuccessListener { uri : Uri? ->
								val downloadURL = uri.toString()
								val uploadTaskRef =
									thumbFilepathRef.putBytes(thumbByte)
								uploadTaskRef.addOnCompleteListener { thumbTask ->
									if (thumbTask.isSuccessful) {
										filepathRef.downloadUrl.addOnSuccessListener { uri2 : Uri? ->
											val thumbDownloadUrl = uri2.toString()
											mDatabaseRef.child("thumbUrl")
												.setValue(thumbDownloadUrl)
												.addOnCompleteListener { taskFinal ->
													if (taskFinal.isSuccessful) {
														val updateMap : MutableMap<String, Any> =
															HashMap()
														updateMap["photoUrl"] = downloadURL
														updateMap["thumbUrl"] = thumbDownloadUrl
														mDatabaseRef.updateChildren(updateMap)
															.addOnCompleteListener { tsk ->
																if (tsk.isSuccessful) {
																	showLogDebug(
                                                                        this.javaClass.simpleName,
                                                                        "Update Photo - Thumb: Success"
                                                                    )
																} else {
																	showLogError(
                                                                        this.javaClass.simpleName,
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
		} else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
			Toast.makeText(
                context,
                context.getString(R.string.error_image_upload),
                Toast.LENGTH_SHORT
            )
				.show()
		}
	}
}