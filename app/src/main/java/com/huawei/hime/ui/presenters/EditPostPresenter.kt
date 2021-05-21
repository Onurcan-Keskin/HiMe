package com.huawei.hime.ui.presenters

import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.huawei.hime.R
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.util.SharedPreferencesManager
import java.util.*
import kotlin.collections.HashMap

class EditPostPresenter constructor(private val editPostContract: EditPostContract) {

    fun onViewsCreate() {
        editPostContract.initViews()
    }

    fun uploadSavesToDB(
        uploadType: String,
        tagEdit1: TextInputEditText,
        tagEdit2: TextInputEditText,
        tagEdit3: TextInputEditText,
        tagEdit4: TextInputEditText,
        tagEdit5: TextInputEditText,
        saveFooter: TextInputEditText,
        tableKey: String,
        uploadDb: DatabaseReference,
        feedRef: DatabaseReference,
        uploaderID: String
    ) {
        val footer = saveFooter.text.toString()
        val tag1: String?
        if (tagEdit1.length() <= 1) tag1 = null else {
            tag1 = tagEdit1.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
            uploadDb.child("tag1").setValue("$tag1")
            feedRef.child("tag1").setValue("$tag1")
            val tagMap: MutableMap<String, String> = HashMap()
            tagMap["uploaderID"] = uploaderID
            val mapTag: MutableMap<String, Any> = HashMap()
            mapTag["Tag/$tag1/$tableKey/Uploader"] = tagMap
            FirebaseDBHelper.rootRef().updateChildren(mapTag)
        }
        val tag2: String?
        if (tagEdit2.length() <= 1) tag2 = null else {
            tag2 = tagEdit2.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
            uploadDb.child("tag2").setValue("$tag2")
            feedRef.child("tag2").setValue("$tag2")
            val tagMap: MutableMap<String, String> = HashMap()
            tagMap["uploaderID"] = uploaderID
            val mapTag: MutableMap<String, Any> = HashMap()
            mapTag["Tag/$tag2/$tableKey/Uploader"] = tagMap
            FirebaseDBHelper.rootRef().updateChildren(mapTag)
        }
        val tag3: String?
        if (tagEdit3.length() <= 1) tag3 = null else {
            tag3 = tagEdit3.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
            uploadDb.child("tag3").setValue("$tag3")
            feedRef.child("tag3").setValue("$tag3")
            val tagMap: MutableMap<String, String> = HashMap()
            tagMap["uploaderID"] = uploaderID
            val mapTag: MutableMap<String, Any> = HashMap()
            mapTag["Tag/$tag3/$tableKey/Uploader"] = tagMap
            FirebaseDBHelper.rootRef().updateChildren(mapTag)
        }
        val tag4: String?
        if (tagEdit4.length() <= 1) tag4 = null else {
            tag4 = tagEdit4.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
            uploadDb.child("tag4").setValue("$tag4")
            feedRef.child("tag4").setValue("$tag4")
            val tagMap: MutableMap<String, String> = HashMap()
            tagMap["uploaderID"] = uploaderID
            val mapTag: MutableMap<String, Any> = HashMap()
            mapTag["Tag/$tag4/$tableKey/Uploader"] = tagMap
            FirebaseDBHelper.rootRef().updateChildren(mapTag)
        }
        val tag5: String?
        if (tagEdit5.length() <= 1) tag5 = null else {
            tag5 = tagEdit5.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
            uploadDb.child("tag5").setValue("$tag5")
            feedRef.child("tag5").setValue("$tag5")
            val tagMap: MutableMap<String, String> = HashMap()
            tagMap["uploaderID"] = uploaderID
            val mapTag: MutableMap<String, Any> = HashMap()
            mapTag["Tag/$tag5/$tableKey/Uploader"] = tagMap
            FirebaseDBHelper.rootRef().updateChildren(mapTag)
        }
        when (uploadType) {
            "map" -> {
                feedRef.child("footer").setValue(footer)

                uploadDb.child("upload_footer").setValue(footer)
            }
            "video" -> {
                feedRef.child("footer").setValue(footer)

                uploadDb.child("upload_footer").setValue(footer)
            }
            "image" -> {
                feedRef.child("footer").setValue(footer)

                uploadDb.child("upload_footer").setValue(footer)
            }
        }
    }

    fun setPrefs(
        context: Context,
        sharedPreferencesManager: SharedPreferencesManager,
        travel_footer: TextInputLayout,
        showTag: TextView,
        tag1: TextInputLayout,
        tag2: TextInputLayout,
        tag3: TextInputLayout,
        tag4: TextInputLayout,
        tag5: TextInputLayout
    ) {
        if (sharedPreferencesManager.loadNightModeState()) {
            travel_footer.background = ContextCompat.getDrawable(context,R.drawable.bg_edit_dark)
            showTag.background = ContextCompat.getDrawable(context,R.drawable.bg_edit_dark)
            tag1.background = ContextCompat.getDrawable(context,R.drawable.bg_edit_dark)
            tag2.background = ContextCompat.getDrawable(context,R.drawable.bg_edit_dark)
            tag3.background = ContextCompat.getDrawable(context,R.drawable.bg_edit_dark)
            tag4.background = ContextCompat.getDrawable(context,R.drawable.bg_edit_dark)
            tag5.background = ContextCompat.getDrawable(context,R.drawable.bg_edit_dark)
        } else {
            travel_footer.background = ContextCompat.getDrawable(context,R.drawable.bg_edit)
            showTag.background = ContextCompat.getDrawable(context,R.drawable.bg_edit)
            tag1.background = ContextCompat.getDrawable(context,R.drawable.bg_edit)
            tag2.background = ContextCompat.getDrawable(context,R.drawable.bg_edit)
            tag3.background = ContextCompat.getDrawable(context,R.drawable.bg_edit)
            tag4.background = ContextCompat.getDrawable(context,R.drawable.bg_edit)
            tag5.background = ContextCompat.getDrawable(context,R.drawable.bg_edit)
        }
    }
}

interface EditPostContract {
    fun initViews()
}

