package com.huawei.hime.ui.presenters

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.huawei.hime.R
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.activities.MapActivity
import com.huawei.hime.ui.interfaces.IMap
import com.huawei.hime.ui.interfaces.IMapActivity
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.showLogDebug
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class MapPresenter constructor(
    private val mapContract : IMapActivity.ViewMapActivity,
    private val IMap : IMap
) {

	fun onViewsCreate() {
		mapContract.initViews()
		mapContract.getLocationUpdate()
	}

	fun onMapInteract() {
		IMap.onMapReady()
	}

	fun onCurrentLocation() {
		mapContract.getLocationUpdate()
	}

	fun setPreferences(
        context : Context,
        sharedPreferencesManager : SharedPreferencesManager,
        travel_footer : TextInputLayout,
        showTag : TextView,
        tag1 : TextInputLayout,
        tag2 : TextInputLayout,
        tag3 : TextInputLayout,
        tag4 : TextInputLayout,
        tag5 : TextInputLayout
    ) {
		if (sharedPreferencesManager.loadNightModeState()) {
			travel_footer.background = ContextCompat.getDrawable(context, R.drawable.bg_edit_dark)
			showTag.background = ContextCompat.getDrawable(context, R.drawable.bg_edit_dark)
			tag1.background = ContextCompat.getDrawable(context, R.drawable.bg_edit_dark)
			tag2.background = ContextCompat.getDrawable(context, R.drawable.bg_edit_dark)
			tag3.background = ContextCompat.getDrawable(context, R.drawable.bg_edit_dark)
			tag4.background = ContextCompat.getDrawable(context, R.drawable.bg_edit_dark)
			tag5.background = ContextCompat.getDrawable(context, R.drawable.bg_edit_dark)
		} else {
			travel_footer.background = ContextCompat.getDrawable(context, R.drawable.bg_edit)
			showTag.background = ContextCompat.getDrawable(context, R.drawable.bg_edit)
			tag1.background = ContextCompat.getDrawable(context, R.drawable.bg_edit)
			tag2.background = ContextCompat.getDrawable(context, R.drawable.bg_edit)
			tag3.background = ContextCompat.getDrawable(context, R.drawable.bg_edit)
			tag4.background = ContextCompat.getDrawable(context, R.drawable.bg_edit)
			tag5.background = ContextCompat.getDrawable(context, R.drawable.bg_edit)
		}
	}

	fun uploadToDB(
        constraintLayout : ConstraintLayout,
        context : MapActivity,
        currentID : String,
        countryNameView : TextView,
        travelFooterEdit : TextInputEditText,
        addressNameView : TextView,
        latitude : Double,
        longitude : Double,
        hmsGmsIMap : IMap,
        mPosterName : String,
        mPosterImge : String,
        tagEdit1 : TextInputEditText,
        tagEdit2 : TextInputEditText,
        tagEdit3 : TextInputEditText,
        tagEdit4 : TextInputEditText,
        tagEdit5 : TextInputEditText
    ) {

		val mUserFeedRef = FirebaseDBHelper.getUserFeedRootRef()
		val timeDate = DateFormat.getDateTimeInstance().format(Date())

		if (countryNameView.text.toString().isNotEmpty() || countryNameView.text.toString()
				.isNotBlank()
		) {
			val countryName = countryNameView.text.toString()
			val travelFooter = travelFooterEdit.text.toString()
			val addressName = addressNameView.text.toString()
			val tag1 = tagEdit1.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
			val tag2 = tagEdit2.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
			val tag3 = tagEdit3.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
			val tag4 = tagEdit4.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
			val tag5 = tagEdit5.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
			val travelPush = mUserFeedRef.child("Travel/$currentID/$countryName").push()
			val pushKey = travelPush.key.toString()

			hmsGmsIMap.getTappedLocation(latitude, longitude)
			showLogDebug("Tapped Location", "$latitude, $longitude")

			if (addressName != context.getText(R.string.click_map)) {
				val travelMap : MutableMap<String, String> = HashMap()
				travelMap["travel_latitude"] = latitude.toString()
				travelMap["travel_longitude"] = longitude.toString()
				travelMap["travel_address"] = addressName
				travelMap["footer"] = travelFooter
				travelMap["travel_date"] = timeDate
				travelMap["travel_map_holder"] = ""
				travelMap["commentsAllowed"] = "0"
				travelMap["shareStat"] = "0"
				travelMap["travel_map_lovely"] = "0"
				travelMap["travel_uploadMillis"] = (System.currentTimeMillis()).toString()

				val uploadMap : MutableMap<String, String> = HashMap()
				uploadMap["uploadType"] = "map"
				uploadMap["popularity"] = "0"
				uploadMap["upload_date"] = timeDate
				uploadMap["upload_footer"] = travelFooter
				uploadMap["upload_posterName"] = mPosterName
				uploadMap["upload_posterImage"] = mPosterImge
				uploadMap["uploaderID"] = currentID
				uploadMap["upload_travel_address"] = addressName
				uploadMap["upload_travel_latitude"] = latitude.toString()
				uploadMap["upload_travel_longitude"] = longitude.toString()
				uploadMap["upload_travel_country"] = countryName
				uploadMap["upload_imageUrl"] = ""
				uploadMap["upload_lovely"] = "0"
				uploadMap["typeGroup"] = "0" // Type Group 1: Video, Image 0: Map
				uploadMap["commentsAllowed"] = "0"
				uploadMap["upload_millis"] = (System.currentTimeMillis()).toString()

				if (tagEdit1.length() <= 1) {
					travelMap["tag1"] = ""
				} else {
					travelMap["tag1"] = tag1
					uploadMap["tag1"] = tag1
					val tagMap : MutableMap<String, String> = HashMap()
					tagMap["uploaderID"] = currentID
					val mapTag : MutableMap<String, Any> = HashMap()
					mapTag["Tag/$tag1/$pushKey/Uploader"] = tagMap
					FirebaseDBHelper.rootRef().updateChildren(mapTag)
				}
				if (tagEdit2.length() <= 1) {
					travelMap["tag2"] = ""
				} else {
					travelMap["tag2"] = tag2
					uploadMap["tag2"] = tag2
					val tagMap : MutableMap<String, String> = HashMap()
					tagMap["uploaderID"] = currentID
					val mapTag : MutableMap<String, Any> = HashMap()
					mapTag["Tag/$tag2/$pushKey/Uploader"] = tagMap
					FirebaseDBHelper.rootRef().updateChildren(mapTag)
				}
				if (tagEdit3.length() <= 1) {
					travelMap["tag3"] = ""
				} else {
					travelMap["tag3"] = tag3
					uploadMap["tag3"] = tag3
					val tagMap : MutableMap<String, String> = HashMap()
					tagMap["uploaderID"] = currentID
					val mapTag : MutableMap<String, Any> = HashMap()
					mapTag["Tag/$tag3/$pushKey/Uploader"] = tagMap
					FirebaseDBHelper.rootRef().updateChildren(mapTag)
				}
				if (tagEdit4.length() <= 1) {
					travelMap["tag4"] = ""
				} else {
					travelMap["tag4"] = tag4
					uploadMap["tag4"] = tag4
					val tagMap : MutableMap<String, String> = HashMap()
					tagMap["uploaderID"] = currentID
					val mapTag : MutableMap<String, Any> = HashMap()
					mapTag["Tag/$tag4/$pushKey/Uploader"] = tagMap
					FirebaseDBHelper.rootRef().updateChildren(mapTag)
				}
				if (tagEdit5.length() <= 1) {
					travelMap["tag5"] = ""
				} else {
					travelMap["tag5"] = tag5
					uploadMap["tag5"] = tag5
					val tagMap : MutableMap<String, String> = HashMap()
					tagMap["uploaderID"] = currentID
					val mapTag : MutableMap<String, Any> = HashMap()
					mapTag["Tag/$tag5/$pushKey/Uploader"] = tagMap
					FirebaseDBHelper.rootRef().updateChildren(mapTag)
				}

				val mapUpload : MutableMap<String, Any> = HashMap()
				mapUpload["uploads/NShareable/$pushKey"] = uploadMap
				FirebaseDBHelper.rootRef().updateChildren(mapUpload)

				val mapTravel : MutableMap<String, Any> = HashMap()
				mapTravel["User Feed/Travel/$currentID/$countryName/$pushKey"] = travelMap
				FirebaseDBHelper.rootRef().updateChildren(mapTravel).addOnCompleteListener {
					if (it.isSuccessful) {
						hmsGmsIMap.ssCallback(currentID, pushKey)
						context.finish()
						Toast.makeText(
                            context.applicationContext,
                            context.getText(R.string.prompt_save_success),
                            Toast.LENGTH_SHORT
                        ).show()
					} else {
						Snackbar.make(
                            constraintLayout,
                            context.getText(R.string.error_travel_upload),
                            Snackbar.LENGTH_SHORT
                        ).show()
					}
				}
			} else {
				Snackbar.make(
                    constraintLayout,
                    context.getText(R.string.error_location_pick),
                    Snackbar.LENGTH_SHORT
                ).show()
			}
		} else {
			Snackbar.make(
                constraintLayout,
                context.getText(R.string.error_location_pick),
                Snackbar.LENGTH_SHORT
            ).show()
		}
	}

}