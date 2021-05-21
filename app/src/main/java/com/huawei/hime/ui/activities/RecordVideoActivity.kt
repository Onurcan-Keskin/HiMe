package com.huawei.hime.videorecord

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.common.io.Files.getFileExtension
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import kotlinx.android.synthetic.main.activity_record_video.*

class RecordVideoActivity : AppCompatActivity(), RecordVideoContractor {

    private val VIDEO_CAPTURE = 101
    private val PICK_VIDEO_REQUEST = 1

    private var mStorageVidRef: StorageReference? = null
    private var mStorageGalRef: StorageReference? = null
    private var mDatabaseGalRef: DatabaseReference? = null
    private var mDatabaseVidRef: DatabaseReference? = null
    private val currentID = AppUser.getUserId()

    var videoUri: Uri? = null
    private var mUploadTask: StorageTask<*>? = null

    private val recordVideoPresenter: RecordVideoPresenter by lazy {
        RecordVideoPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_video)
        supportActionBar?.hide()
        recordVideoPresenter.onViewsCreate()
        showDialog(this, "a", "b")
    }

    override fun initDB() {
        mStorageGalRef = FirebaseStorage.getInstance().getReference("Uploads/$currentID/Gallery")
        mStorageVidRef = FirebaseStorage.getInstance().getReference("Uploads/$currentID/Videos")
        mDatabaseVidRef = FirebaseDatabase.getInstance().getReference("Uploads/Videos")
        mDatabaseGalRef = FirebaseDatabase.getInstance().getReference("Uploads/Gallery")
    }

    private fun showDialog(activity: Activity?, title: String?, message: CharSequence?) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
            .setPositiveButton(getText(R.string.action_record_video)) { dialog, which ->
                recordVideo()
            }.setNegativeButton(getText(R.string.action_choose_gallery)) { dialog, which ->
                chooseVideoGallery()
            }
        val alert = builder.create()
        alert.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null
        ) {
            videoUri = data.data
            post_card_videoView.visibility = View.VISIBLE
            post_card_videoView.setVideoURI(videoUri)
            val vidControl = MediaController(this)
            vidControl.setAnchorView(post_card_videoView)
            post_card_videoView.setMediaController(vidControl)
            post_card_videoView.requestFocus()
            post_card_videoView.start()
        }
        if (requestCode === VIDEO_CAPTURE) {
            if (resultCode === Activity.RESULT_OK) {
                post_card_videoView.visibility = View.VISIBLE
                post_card_videoView.setVideoURI(videoUri)
                val vidControl = MediaController(this)
                vidControl.setAnchorView(post_card_videoView)
                post_card_videoView.setMediaController(vidControl)
                post_card_videoView.requestFocus()
                post_card_videoView.start()
            } else if (resultCode === Activity.RESULT_CANCELED) {
                Toast.makeText(
                    this, "Video recording cancelled.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this, "Failed to record video",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun uploadFile() {
        if (videoUri != null) {
            val fileReference = mStorageVidRef!!.child(
                System.currentTimeMillis()
                    .toString() + "." + getFileExtension(videoUri.toString())
            )
            mUploadTask = fileReference.putFile(videoUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    val handler = Handler()
                    handler.postDelayed({ pbar.progress = 0 }, 500)
                    Toast.makeText(this, "Upload successful", Toast.LENGTH_LONG)
                        .show()
                    val upload = Upload(
                        hybrid_card_poster!!.text.toString().trim { it <= ' ' },
                        taskSnapshot.uploadSessionUri.toString()
                    )
                    val uploadId = mDatabaseVidRef!!.push().key
                    mDatabaseVidRef!!.child(uploadId!!).setValue(upload)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress =
                        100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    pbar.progress = progress.toInt()
                }
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }

    fun upload(view: View) {
        uploadFile()
    }

    fun recordVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_CAPTURE)
    }

    fun chooseVideoGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_VIDEO_REQUEST)
    }

    fun recordVideoOnclick(view: View) {
        recordVideo()
    }

    fun chooseVideoGalleryOnclick(view: View) {
        chooseVideoGallery()
    }
}
