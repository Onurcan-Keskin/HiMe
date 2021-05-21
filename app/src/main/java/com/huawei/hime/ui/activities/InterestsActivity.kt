package com.huawei.hime.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huawei.hime.R
import com.huawei.hime.helpers.Constants
import com.huawei.hime.interests.InterestsGridCustomAdapter
import com.huawei.hime.models.InterestsType
import com.huawei.hime.ui.interfaces.IInterest
import com.huawei.hime.ui.presenters.InterestsPresenter
import com.huawei.hime.util.LocaleHelper
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.showLogError
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_interests.*
import main.MainActivity

class InterestsActivity : AppCompatActivity(), IInterest.ViewInterest {

	private val imgRequestCode = 2020
	private val logTag = "InterestsActivity"

	private lateinit var mStorageRef : StorageReference
	private lateinit var context : Context

	private lateinit var nameSurname : String
	private lateinit var ageInterval : String
	private var interestsCheck : String = ""

	private var interestsList : ArrayList<InterestsType> = ArrayList<InterestsType>()
	private var listGrid : GridView? = null
	private var adapter : InterestsGridCustomAdapter? = null

	private var interests = intArrayOf(
        R.drawable.music, R.drawable.theatre, R.drawable.cinema, R.drawable.game,
        R.drawable.travel, R.drawable.sports, R.drawable.entertainment, R.drawable.education
    )

	private lateinit var circle : CircleImageView

	private val interestsPresenter : InterestsPresenter by lazy {
		InterestsPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {

		context = applicationContext!!
		val sharedPrefs = SharedPreferencesManager(this)
		/* Mode State */
		if (sharedPrefs.loadNightModeState())
			setTheme(R.style.DarkMode)
		else
			setTheme(R.style.LightMode)
		/* Language State */
		if (sharedPrefs.loadLanguage() == "tr")
			LocaleHelper.setLocale(context, "tr")
		else
			LocaleHelper.setLocale(context, "en")

		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_interests)
		supportActionBar?.hide()
		interestsPresenter.onViewCreate()

		context = applicationContext

		interest_next_card_btn.setOnClickListener {
			interestsPresenter.onNextBtnClick(adapter!!.imgList)
		}

		interest_circle_img.setOnClickListener {
			CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
				.setCropShape(CropImageView.CropShape.OVAL)
				.start(this@InterestsActivity)
		}

		if (sharedPrefs.loadNightModeState()) {
			interConstrain.background =
				ContextCompat.getDrawable(this, R.drawable.ic_back_bubble_dark)
		} else {
			interConstrain.background = ContextCompat.getDrawable(this, R.drawable.background_login)
		}

	}

	override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == imgRequestCode && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
			val imageUri = data.data
			CropImage.activity(imageUri).setAspectRatio(1, 1)
				.setCropShape(CropImageView.CropShape.OVAL).start(this@InterestsActivity)
		}
		interestsPresenter.resultActivity(
            requestCode,
            resultCode,
            data,
            this,
            mStorageRef,
            Constants.fUserInfoRef
        )
	}

	override fun initViews() {
		mStorageRef = FirebaseStorage.getInstance().reference

		circle = findViewById(R.id.interest_circle_img)

		/* Grid View Population */
		listGrid = findViewById<GridView>(R.id.interests_grid) as GridView
		populateGrid()
		adapter = InterestsGridCustomAdapter(interestsList, this)
		listGrid?.adapter = adapter

		ageInterval = interest_txt_age.toString()
		ArrayAdapter.createFromResource(
            this,
            R.array.ageSpinner,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
			interests_age_spinner.adapter = adapter

			interests_age_spinner.onItemSelectedListener = object : OnItemSelectedListener {
				override fun onItemSelected(
                    parent : AdapterView<*>,
                    view : View,
                    position : Int,
                    id : Long
                ) {
					ageInterval = parent.getItemAtPosition(position).toString()
				}

				override fun onNothingSelected(parent : AdapterView<*>?) {}
			}
		}

		/* Actual Population */
		Constants.fUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
	            showLogError(this.javaClass.simpleName,p0.message)
            }

            override fun onDataChange(p0 : DataSnapshot) {
                val image = p0.child("photoUrl").value.toString()

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
		startActivity(
            Intent(
                this,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
	}

	override fun populateDB(interestByte : String) {
		nameSurname = interests_name_surname.text.toString()

		val updateMap : MutableMap<String, Any> =
			HashMap()
		updateMap["nameSurname"] = nameSurname
		updateMap["age"] = ageInterval
		updateMap["interests"] = interestByte
		interestsCheck = interestByte
		Constants.fUserInfoRef.updateChildren(updateMap).addOnCompleteListener { taskCheck ->
			if (taskCheck.isSuccessful) {
				showUpdateLog("Update Name - Age: Success")
			} else {
				showUpdateLog("Update Name - Age: Failed")
			}
		}
	}

	override fun checkEmptyFields() {
		when {
			interests_name_surname.text.toString().isEmpty() -> {
				interest_name_surname_inputLayout.error = getString(R.string.error_empty_name)
				interest_name_surname_inputLayout.requestFocus()
			}
			interestsCheck == "00000000" -> {
				Snackbar.make(interConstrain, getText(R.string.error_pickInt), Snackbar.LENGTH_SHORT)
					.show()
				listGrid!!.requestFocus()
			}
			else -> directToMain()
		}
	}

	override fun showUpdateLog(message : String) {
		Log.d(logTag, message)
	}

}