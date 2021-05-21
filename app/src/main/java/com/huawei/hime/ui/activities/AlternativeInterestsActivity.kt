package com.huawei.hime.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.interests.InterestsGridCustomAdapter
import com.huawei.hime.ui.presenters.InterestsPresenter
import com.huawei.hime.models.InterestsType
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.interfaces.IInterest
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_interests.*
import main.MainActivity
import java.io.ByteArrayOutputStream
import java.io.File

class AlternativeInterestsActivity : AppCompatActivity() , IInterest.ViewInterest {

    private val imgRequestCode = 2020
    private val logTag = "AlternativeInterestsActivity"

    private lateinit var mStorageRef: StorageReference
    private lateinit var currentUID: String
    private lateinit var context: Context
    private lateinit var mDatabaseRef: DatabaseReference

    private lateinit var nameSurname: String
    private lateinit var ageInterval: String

    private var interestsList: ArrayList<InterestsType> = ArrayList()
    private var listGrid: GridView? = null
    private var adapter: InterestsGridCustomAdapter? = null

    private var interests = intArrayOf(
        R.drawable.music, R.drawable.theatre, R.drawable.cinema, R.drawable.game,
        R.drawable.travel, R.drawable.sports, R.drawable.entertainment, R.drawable.education
    )

    private lateinit var circle: CircleImageView

    private val interestsPresenter: InterestsPresenter by lazy {
        InterestsPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternative_interests)
        supportActionBar?.hide()
        interestsPresenter.onViewCreate()

        interest_next_card_btn.setOnClickListener {
            interestsPresenter.onNextBtnClick(adapter!!.imgList)
        }

        interest_circle_img.setOnClickListener {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                .start(this@AlternativeInterestsActivity)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imgRequestCode && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            CropImage.activity(imageUri).setAspectRatio(1, 1).start(this@AlternativeInterestsActivity);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri

                val thumbFilePath = File(resultUri.path)

                val thumbBitmap: Bitmap = Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(thumbFilePath)

                val baos = ByteArrayOutputStream()
                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val thumbByte = baos.toByteArray()

                val filepathRef: StorageReference =
                    mStorageRef.child("User").child("Images").child(currentUID)
                        .child(System.currentTimeMillis().toString() + ".jpeg")
                val thumbFilepathRef: StorageReference =
                    mStorageRef.child("User").child("Thumbs").child(currentUID)
                        .child(
                            System.currentTimeMillis().toString() + ".jpeg"
                        )

                filepathRef.putFile(resultUri)
                    .addOnCompleteListener { uploadTask: Task<UploadTask.TaskSnapshot> ->
                        if (uploadTask.isSuccessful) {
                            filepathRef.downloadUrl.addOnSuccessListener { uri: Uri? ->
                                val downloadURL = uri.toString()
                                val uploadTaskRef =
                                    thumbFilepathRef.putBytes(thumbByte)
                                uploadTaskRef.addOnCompleteListener { thumbTask ->
                                    if (thumbTask.isSuccessful) {
                                        filepathRef.downloadUrl.addOnSuccessListener { uri2: Uri? ->
                                            val thumbDownloadUrl = uri2.toString()
                                            mDatabaseRef.child("thumbUrl")
                                                .setValue(thumbDownloadUrl)
                                                .addOnCompleteListener { taskFinal ->
                                                    if (taskFinal.isSuccessful) {
                                                        val updateMap: MutableMap<String, Any> =
                                                            HashMap()
                                                        updateMap["photoUrl"] = downloadURL
                                                        updateMap["thumbUrl"] = thumbDownloadUrl
                                                        mDatabaseRef.updateChildren(updateMap)
                                                            .addOnCompleteListener { tsk ->
                                                                if (tsk.isSuccessful) {
                                                                    showUpdateLog("Update Photo - Thumb: Success")
                                                                } else {
                                                                    showUpdateLog("Update Photo - Thumb: Failed")
                                                                }
                                                            }

                                                    }
                                                }
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            getString(R.string.error_image_upload),
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
            Toast.makeText(context, getString(R.string.error_image_upload), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun initViews() {
        mStorageRef = FirebaseStorage.getInstance().reference
        currentUID = AppUser.getUserId()
        mDatabaseRef = FirebaseDBHelper.getUserInfo(currentUID)
        mDatabaseRef.keepSynced(true)

        circle = findViewById(R.id.interest_circle_img)

        ageInterval = interest_txt_age.toString()
        ArrayAdapter.createFromResource(
            this,
            R.array.ageSpinner,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            interests_age_spinner.adapter = adapter

            interests_age_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    ageInterval = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        /* Actual Population */
        mDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val image = p0.child("photoUrl").value.toString()
                val name = p0.child("nameSurname").value.toString()


                if (image != "default") {
                    Picasso.get().load(image)
                        .into(circle)
                }
            }
        })
    }

    override fun populateGrid() {
        val inter1 = InterestsType()
        inter1.imgName = "Music"
        inter1.imgIcon = interests[0]
        interestsList.add(inter1)

        val inter2 = InterestsType()
        inter2.imgName = "Theatre"
        inter2.imgIcon = interests[1]
        interestsList.add(inter2)

        val inter3 = InterestsType()
        inter3.imgName = "Cinema"
        inter3.imgIcon = interests[2]
        interestsList.add(inter3)

        val inter4 = InterestsType()
        inter4.imgName = "Game"
        inter4.imgIcon = interests[3]
        interestsList.add(inter4)

        val inter5 = InterestsType()
        inter5.imgName = "Travel"
        inter5.imgIcon = interests[4]
        interestsList.add(inter5)

        val inter6 = InterestsType()
        inter6.imgName = "Sports"
        inter6.imgIcon = interests[5]
        interestsList.add(inter6)

        val inter7 = InterestsType()
        inter7.imgName = "Entertainment"
        inter7.imgIcon = interests[6]
        interestsList.add(inter7)

        val inter8 = InterestsType()
        inter8.imgName = "Education"
        inter8.imgIcon = interests[7]
        interestsList.add(inter8)
    }

    override fun directToMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun populateDB(interestByte: String) {
        nameSurname = interests_name_surname.text.toString()

        val updateMap: MutableMap<String, Any> =
            HashMap()
        updateMap["nameSurname"] = nameSurname
        updateMap["age"] = ageInterval
        updateMap["interests"] = interestByte
        mDatabaseRef.updateChildren(updateMap).addOnCompleteListener { taskCheck ->
            if (taskCheck.isSuccessful) {
                showUpdateLog("Update Name - Age: Success")
            } else {
                showUpdateLog("Update Name - Age: Failed")
            }
        }

    }

    override fun checkEmptyFields() {
        if (interests_name_surname.text.toString().isEmpty()) {
            interest_name_surname_inputLayout.error = getString(R.string.error_empty_name)
            interest_name_surname_inputLayout.requestFocus()
            return
        }

        if (!interest_circle_img.isPressed) {
            interest_circle_img.requestFocus()
            return
        }
    }

    @SuppressLint("LongLogTag")
    override fun showUpdateLog(message: String) {
        Log.d(logTag, message)
    }
}
