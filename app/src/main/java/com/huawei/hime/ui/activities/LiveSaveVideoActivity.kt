package com.huawei.hime.ui.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.Constants
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.interfaces.ILiveSave
import com.huawei.hime.ui.presenters.LiveSavePresenter
import com.huawei.hime.util.showLogError
import com.huawei.hime.util.views.expandView
import com.otaliastudios.cameraview.VideoResult
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_live_save_image.save_live_btn
import kotlinx.android.synthetic.main.activity_live_save_video.*
import kotlinx.android.synthetic.main.activity_live_save_video.l1
import kotlinx.android.synthetic.main.activity_live_save_video.live_save_tag1_lyt
import kotlinx.android.synthetic.main.activity_live_save_video.live_save_tag2_lyt
import kotlinx.android.synthetic.main.activity_live_save_video.live_save_tag3_lyt
import kotlinx.android.synthetic.main.activity_live_save_video.live_save_tag4_lyt
import kotlinx.android.synthetic.main.activity_live_save_video.live_save_tag5_lyt
import kotlinx.android.synthetic.main.activity_live_save_video.showTag
import java.util.*

class LiveSaveVideoActivity : AppCompatActivity(), ILiveSave.ViewLiveSave {

	companion object {
		var videoResult : VideoResult? = null
	}

	private var video : String? = ""
	private var elpsTime : String? = ""

	private lateinit var image : String

	private lateinit var mUserFeedRef : DatabaseReference
	private lateinit var mStorageRef : StorageReference
	private lateinit var posterName : String
	private lateinit var posterImage : String
	private lateinit var posterInterest : String
	private var mProgress : ProgressDialog? = null

	private val timeStamp = System.currentTimeMillis().toString()

	private lateinit var tagEdit1 : TextInputEditText
	private lateinit var tagEdit2 : TextInputEditText
	private lateinit var tagEdit3 : TextInputEditText
	private lateinit var tagEdit4 : TextInputEditText
	private lateinit var tagEdit5 : TextInputEditText
	private lateinit var saveFooter : TextInputEditText

	private lateinit var videoLayout : VideoView

	private lateinit var context : Context

	private val liveSavePresenter : LiveSavePresenter by lazy {
		LiveSavePresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		val sharedPrefs = SharedPreferencesManager(this)
		if (sharedPrefs.loadNightModeState()) {
			setTheme(R.style.DarkMode)
		} else {
			setTheme(R.style.LightMode)
		}
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_live_save_video)
		liveSavePresenter.onViewsCreate()

		val result = videoResult ?: kotlin.run {
			finish()
			return
		}

		Slidr.attach(this)

		setPrefs(
			sharedPrefs,
			save_footer_layout,
			showTag,
			live_save_tag1_lyt,
			live_save_tag2_lyt,
			live_save_tag3_lyt,
			live_save_tag4_lyt,
			live_save_tag5_lyt
		)

		val tag1 = tagEdit1.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
		val tag2 = tagEdit2.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
		val tag3 = tagEdit3.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
		val tag4 = tagEdit4.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
		val tag5 = tagEdit5.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
		val footer = saveFooter.text.toString()

		save_live_btn.setOnClickListener {
			liveSavePresenter.uploadVideoTask(
				context,
				mProgress!!,
				AppUser.getUserId(),
				mUserFeedRef,
				tag1,
				tag2,
				tag3,
				tag4,
				tag5,
				footer,
				video!!,
				posterName,
				posterImage,
				posterInterest,
				elpsTime!!
			)
		}

		save_live_btn.setOnClickListener {
			liveSavePresenter.uploadImageTask(
				AppUser.getUserId(),
				tag1,
				tag2,
				tag3,
				tag4,
				tag5,
				footer,
				mUserFeedRef,
				image,
				posterName,
				posterImage,
				posterInterest
			)
			finish()
		}
		showTag.setOnClickListener {
			l1.expandView()
			if (l1.visibility == View.GONE) {
				showTag.setCompoundDrawablesRelativeWithIntrinsicBounds(
					0,
					0,
					R.drawable.ic_arrow_downward,
					0
				)
			} else {
				showTag.setCompoundDrawablesRelativeWithIntrinsicBounds(
					0,
					0,
					R.drawable.ic_arrow_upward,
					0
				)
			}
		}
	}

	override fun initViews() {
		context = applicationContext
		videoLayout = findViewById(R.id.live_video_frame)

		tagEdit1 = findViewById(R.id.live_save_tag1)
		tagEdit2 = findViewById(R.id.live_save_tag2)
		tagEdit3 = findViewById(R.id.live_save_tag3)
		tagEdit4 = findViewById(R.id.live_save_tag4)
		tagEdit5 = findViewById(R.id.live_save_tag5)
		saveFooter = findViewById(R.id.save_footer)

		video = intent.getStringExtra("videoUri")
		elpsTime = intent.getStringExtra("elapseTime")
		val m = intent.getStringExtra("location")

		saveFooter.setText(m)

		val mediaC = MediaController(this)
		mediaC.setAnchorView(videoCnstrnt)
		videoLayout.setMediaController(mediaC)
		videoLayout.setVideoPath(video)
		videoLayout.start()
		finish()
	}

	override fun initDB() {
		mUserFeedRef = FirebaseDBHelper.getUserFeedRootRef().child("Videos/$AppUser.getUserId()")
		mStorageRef = FirebaseStorage.getInstance().reference.child("uploads/$AppUser.getUserId()")
			.child("Videos")
			.child(timeStamp)

		Constants.fUserInfoRef.addValueEventListener(object : ValueEventListener {
			override fun onCancelled(p0 : DatabaseError) {
				showLogError(this.javaClass.simpleName,p0.message)
			}

			override fun onDataChange(p0 : DataSnapshot) {
				val name = p0.child("nameSurname").value.toString()
				posterName = if (name.isEmpty())
					"HimeUser"
				else
					name
				posterInterest = p0.child("interests").value.toString()
				posterImage = p0.child("photoUrl").value.toString()
			}
		})
	}

	fun setPrefs(
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
			travel_footer.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_dark)
			showTag.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_dark)
			tag1.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_dark)
			tag2.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_dark)
			tag3.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_dark)
			tag4.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_dark)
			tag5.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_dark)
		} else {
			travel_footer.background = ContextCompat.getDrawable(this, R.drawable.bg_edit)
			showTag.background = ContextCompat.getDrawable(this, R.drawable.bg_edit)
			tag1.background = ContextCompat.getDrawable(this, R.drawable.bg_edit)
			tag2.background = ContextCompat.getDrawable(this, R.drawable.bg_edit)
			tag3.background = ContextCompat.getDrawable(this, R.drawable.bg_edit)
			tag4.background = ContextCompat.getDrawable(this, R.drawable.bg_edit)
			tag5.background = ContextCompat.getDrawable(this, R.drawable.bg_edit)
		}
	}

	override fun onStart() {
		super.onStart()
		FirebaseDBHelper.onConnect(Constants.fOnlineDBRef)
	}

	override fun onPause() {
		super.onPause()
		FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
	}

	override fun onStop() {
		super.onStop()
		FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
	}
}
