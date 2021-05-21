package com.huawei.hime.ui.presenters

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.huawei.hime.R
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.interfaces.ILiveSave
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.util.showToast
import java.io.File
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class LiveSavePresenter constructor(private val liveSaveContract: ILiveSave.ViewLiveSave) {

    fun onViewsCreate() {
        liveSaveContract.initViews()
        liveSaveContract.initDB()
    }

    fun uploadImageTask(
        currentID: String,
        tag1: String,
        tag2: String,
        tag3: String,
        tag4: String,
        tag5: String,
        saveFooter: String,
        mUserFeedRef: DatabaseReference,
        image: String,
        posterName: String,
        posterImage: String,
        posterInterest: String
    ) {
        val picFeedRef = "User Feed/Photos/$currentID"
        val picUploadsRef = "uploads"
        val timeDate = DateFormat.getDateTimeInstance().format(Date())
        val feedPush = mUserFeedRef.push()
        val pKey = feedPush.key.toString()

        //Footer
        val feedMap: MutableMap<String, String> = HashMap()

        // -picFeedRef
        feedMap["footer"] = saveFooter
        feedMap["photos_lovely"] = "0"
        feedMap["shareStat"] = "0"
        feedMap["photos_photoUrl"] = image
        feedMap["photos_uploadTime"] = timeDate.toString()
        feedMap["photos_uploadMillis"] = (System.currentTimeMillis()).toString()
        feedMap["commentsAllowed"] = "1"
        if (tag1.length <= 1) {
            feedMap["tag1"] = ""

        } else {
            feedMap["tag1"] = tag1
        }
        if (tag2.length <= 1) {
            feedMap["tag2"] = ""
        } else {
            feedMap["tag2"] = tag2
        }
        if (tag3.length <= 1) {
            feedMap["tag3"] = ""
        } else {
            feedMap["tag3"] = tag3
        }
        if (tag4.length <= 1) {
            feedMap["tag4"] = ""
        } else {
            feedMap["tag4"] = tag4
        }
        if (tag5.length <= 1) {
            feedMap["tag5"] = ""
        } else {
            feedMap["tag5"] = tag5
        }
        val mapFeed: MutableMap<String, Any> = HashMap()
        mapFeed["$picFeedRef/$pKey"] = feedMap
        FirebaseDBHelper.rootRef().updateChildren(mapFeed)

        // -picUploadRef
        val uploadMap: MutableMap<String, String> = HashMap()
        uploadMap["upload_imageUrl"] = image
        uploadMap["upload_posterName"] = posterName
        uploadMap["upload_posterImage"] = posterImage
        uploadMap["uploaderID"] = currentID
        uploadMap["uploader_interests"] = posterInterest
        uploadMap["popularity"] = "0" //Click Amount - Defines Trending
        uploadMap["uploadType"] = "image"
        uploadMap["upload_footer"] = saveFooter
        uploadMap["upload_lovely"] = "0"
        uploadMap["upload_date"] = timeDate
        uploadMap["upload_millis"] = (System.currentTimeMillis()).toString()
        uploadMap["commentsAllowed"] = "1"
        uploadMap["typeGroup"] = "1" // Type Group 1: Video, Image 0: Map
        if (tag1.length <= 1) {
            uploadMap["tag1"] = ""
        } else {
            uploadMap["tag1"] = tag1
            val tagMap: MutableMap<String, String> = HashMap()
            tagMap["uploaderID"] = currentID
            val mapTag: MutableMap<String, Any> = HashMap()
            mapTag["Tag/$tag1/$pKey/Uploader"] = tagMap
            FirebaseDBHelper.rootRef().updateChildren(mapTag)
        }
        if (tag2.length <= 1) {
            uploadMap["tag2"] = ""
        } else {
            uploadMap["tag2"] = tag2
            val tagMap: MutableMap<String, String> = HashMap()
            tagMap["uploaderID"] = currentID
            val mapTag: MutableMap<String, Any> = HashMap()
            mapTag["Tag/$tag2/$pKey/Uploader"] = tagMap
            FirebaseDBHelper.rootRef().updateChildren(mapTag)
        }
        if (tag3.length <= 1) {
            uploadMap["tag3"] = ""
        } else {
            uploadMap["tag3"] = tag3
            val tagMap: MutableMap<String, String> = HashMap()
            tagMap["uploaderID"] = currentID
            val mapTag: MutableMap<String, Any> = HashMap()
            mapTag["Tag/$tag3/$pKey/Uploader"] = tagMap
            FirebaseDBHelper.rootRef().updateChildren(mapTag)
        }
        if (tag4.length <= 1) {
            uploadMap["tag4"] = ""
        } else {
            uploadMap["tag4"] = tag4
            val tagMap: MutableMap<String, String> = HashMap()
            tagMap["uploaderID"] = currentID
            val mapTag: MutableMap<String, Any> = HashMap()
            mapTag["Tag/$tag4/$pKey/Uploader"] = tagMap
            FirebaseDBHelper.rootRef().updateChildren(mapTag)
        }
        if (tag5.length <= 1) {
            uploadMap["tag5"] = ""
        } else {
            uploadMap["tag5"] = tag5
            val tagMap: MutableMap<String, String> = HashMap()
            tagMap["uploaderID"] = currentID
            val mapTag: MutableMap<String, Any> = HashMap()
            mapTag["Tag/$tag5/$pKey/Uploader"] = tagMap
            FirebaseDBHelper.rootRef().updateChildren(mapTag)
        }
        val mapUpload: MutableMap<String, Any> = HashMap()
        mapUpload["$picUploadsRef/NShareable/$pKey"] = uploadMap
        FirebaseDBHelper.rootRef().updateChildren(mapUpload)
    }

    fun uploadVideoTask(
        context: Context,
        mProgress: ProgressDialog,
        currentID: String,
        mUserFeedRef: DatabaseReference,
        tag1 : String,
        tag2 : String,
        tag3: String,
        tag4: String,
        tag5: String,
        saveFooter: String,
        video: String,
        posterName: String,
        posterImage: String,
        posterInterest: String,
        elpsTime: String

    ) {
        val mTAG = "LiveSaveVideoActivity"
        var prog = mProgress
        prog = ProgressDialog(context)
        prog.setTitle(context.getString(R.string.prompt_uploadingVideo))
        prog.setMessage(context.getString(R.string.prompt_upladingVideo_message))
        prog.setIcon(R.drawable.logo_curved)
        prog.setCanceledOnTouchOutside(false)
        prog.show()
        val picFeedRef = "User Feed/Videos/$currentID"
        val picUploadsRef = "uploads"
        val timeDate = DateFormat.getDateTimeInstance().format(Date())
        val feedPush = mUserFeedRef.push()
        val pKey = feedPush.key.toString()
        //TAGs
        val mStorageRef = FirebaseStorage.getInstance().reference
        val videoFilePathRef = mStorageRef.child("User").child("Video").child(currentID)
            .child(System.currentTimeMillis().toString() + ".mp4")
        val file = File(video)
        showLogDebug(mTAG, "File -> $file")
        val uri = Uri.fromFile(file)
        showLogDebug(mTAG, "Uri -> $uri")
        try {
            val uploadTask = videoFilePathRef.putFile(uri)
            uploadTask.continueWith {
                if (!it.isSuccessful) {
                    it.exception?.let { t -> throw  t }
                }
                videoFilePathRef.downloadUrl
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result!!.addOnSuccessListener { task ->
                        //UserFeed
                        val feedMap: MutableMap<String, String> = java.util.HashMap()
                        feedMap["footer"] = saveFooter
                        feedMap["video_lovely"] = "0"
                        feedMap["video_videoUrl"] = task.toString()
                        feedMap["shareStat"] = "0"
                        feedMap["commentsAllowed"] = "1"
                        feedMap["video_uploadTime"] = timeDate.toString()
                        feedMap["video_uploadMillis"] = (System.currentTimeMillis()).toString()
                        if (tag1.length <= 1) {
                            feedMap["tag1"] = ""
                        } else {
                            feedMap["tag1"] = tag1
                        }
                        if (tag2.length <= 1) {
                            feedMap["tag2"] = ""
                        } else {
                            feedMap["tag2"] = tag2
                        }
                        if (tag3.length <= 1) {
                            feedMap["tag3"] = ""
                        } else {
                            feedMap["tag3"] = tag3
                        }
                        if (tag4.length <= 1) {
                            feedMap["tag4"] = ""
                        } else {
                            feedMap["tag4"] = tag4
                        }
                        if (tag5.length <= 1) {
                            feedMap["tag5"] = ""
                        } else {
                            feedMap["tag5"] = tag5
                        }
                        val mapFeed: MutableMap<String, Any> = java.util.HashMap()
                        mapFeed["$picFeedRef/$pKey"] = feedMap
                        FirebaseDBHelper.rootRef().updateChildren(mapFeed)
                        //uploads

                        // videoUploadRef
                        val uploadMap: MutableMap<String, String> = java.util.HashMap()
                        uploadMap["upload_videoUrl"] = task.toString()
                        uploadMap["upload_posterName"] = posterName
                        uploadMap["upload_posterImage"] = posterImage
                        uploadMap["uploaderID"] = currentID
                        uploadMap["uploader_interests"] = posterInterest
                        uploadMap["popularity"] = "0" //Click Amount - Defines Trending
                        uploadMap["uploadType"] = "video"
                        uploadMap["upload_footer"] = saveFooter
                        uploadMap["upload_lovely"] = "0"
                        uploadMap["upload_date"] = timeDate
                        uploadMap["upload_videoTime"] = elpsTime
                        uploadMap["upload_millis"] = (System.currentTimeMillis()).toString()
                        uploadMap["typeGroup"] = "1" // Type Group 1: Video, Image 0: Map
                        uploadMap["commentsAllowed"] = "1"
                        if (tag1.length <= 1) {
                            uploadMap["tag1"] = ""
                        } else {
                            uploadMap["tag1"] = "#$tag1"
                            val tagMap: MutableMap<String, String> = java.util.HashMap()
                            tagMap["uploaderID"] = currentID
                            val mapTag: MutableMap<String, Any> = java.util.HashMap()
                            mapTag["Tag/$tag1/$pKey/Uploader"] = tagMap
                            FirebaseDBHelper.rootRef().updateChildren(mapTag)
                        }
                        if (tag2.length <= 1) {
                            uploadMap["tag2"] = ""
                        } else {
                            uploadMap["tag2"] = "#$tag2"
                            val tagMap: MutableMap<String, String> = java.util.HashMap()
                            tagMap["uploaderID"] = currentID
                            val mapTag: MutableMap<String, Any> = java.util.HashMap()
                            mapTag["Tag/$tag2/$pKey/Uploader"] = tagMap
                            FirebaseDBHelper.rootRef().updateChildren(mapTag)
                        }
                        if (tag3.length <= 1) {
                            uploadMap["tag3"] = ""
                        } else {
                            uploadMap["tag3"] = "#$tag3"
                            val tagMap: MutableMap<String, String> = java.util.HashMap()
                            tagMap["uploaderID"] = currentID
                            val mapTag: MutableMap<String, Any> = java.util.HashMap()
                            mapTag["Tag/$tag3/$pKey/Uploader"] = tagMap
                            FirebaseDBHelper.rootRef().updateChildren(mapTag)
                        }
                        if (tag4.length <= 1) {
                            uploadMap["tag4"] = ""
                        } else {
                            uploadMap["tag4"] = "#$tag4"
                            val tagMap: MutableMap<String, String> = java.util.HashMap()
                            tagMap["uploaderID"] = currentID
                            val mapTag: MutableMap<String, Any> = java.util.HashMap()
                            mapTag["Tag/$tag4/$pKey/Uploader"] = tagMap
                            FirebaseDBHelper.rootRef().updateChildren(mapTag)
                        }
                        if (tag5.length <= 1) {
                            uploadMap["tag5"] = ""
                        } else {
                            uploadMap["tag5"] = "#$tag5"
                            val tagMap: MutableMap<String, String> = java.util.HashMap()
                            tagMap["uploaderID"] = currentID
                            val mapTag: MutableMap<String, Any> = java.util.HashMap()
                            mapTag["Tag/$tag5/$pKey/Uploader"] = tagMap
                            FirebaseDBHelper.rootRef().updateChildren(mapTag)
                        }
                        val videoUpload: MutableMap<String, Any> = java.util.HashMap()
                        videoUpload["$picUploadsRef/NShareable/$pKey"] = uploadMap
                        FirebaseDBHelper.rootRef().updateChildren(videoUpload).addOnCompleteListener { it1 ->
                            if (it1.isSuccessful) {
                                prog!!.dismiss()
                                //context.finish()
                            } else {
                                showToast(context,context.getString(R.string.error_video_upload))
                                prog!!.dismiss()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            prog.dismiss()
            showToast(context,context.getString(R.string.error_video_upload))
        }
    }
}