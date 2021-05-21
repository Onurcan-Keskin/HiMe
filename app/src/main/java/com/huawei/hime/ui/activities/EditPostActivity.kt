package com.huawei.hime.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.ui.presenters.EditPostContract
import com.huawei.hime.ui.presenters.EditPostPresenter
import com.huawei.hime.helpers.Constants
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.util.LocaleHelper
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.util.views.expandView
import com.r0adkll.slidr.Slidr
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_post.*

class EditPostActivity : AppCompatActivity(), EditPostContract {

	private var photoUrl : String? = ""
	private var tableID : String? = ""
	private var uploadType : String? = ""
	private var uploadFooter : String? = ""
	private var uploadDBString : String? = ""
	private lateinit var uploadDB : DatabaseReference
	private var feedRefString : String? = ""
	private lateinit var feedRef : DatabaseReference
	private var tag1 : String? = ""
	private var tag2 : String? = ""
	private var tag3 : String? = ""
	private var tag4 : String? = ""
	private var tag5 : String? = ""
	private var address : String? = ""
	private lateinit var context : Context

	private lateinit var mImageView : ImageView

	private val editPostPresenter : EditPostPresenter by lazy {
		EditPostPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		val sharedPrefs = SharedPreferencesManager(this)
		/* Mode State */
		if (sharedPrefs.loadNightModeState())
			setTheme(R.style.DarkMode)
		else
			setTheme(R.style.LightMode)
		/* Language State */
		if (sharedPrefs.loadLanguage() == "tr")
			LocaleHelper.setLocale(this, "tr")
		else
			LocaleHelper.setLocale(this, "en")
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_edit_post)
		Slidr.attach(this)

		editPostPresenter.onViewsCreate()

		edit_post_save_btn.setOnClickListener {
			editPostPresenter.uploadSavesToDB(
                uploadType!!,
                edit_post_tag1,
                edit_post_tag2,
                edit_post_tag3,
                edit_post_tag4,
                edit_post_tag5,
                edit_post_footer,
                tableID!!,
                uploadDB,
                feedRef,
                AppUser.getUserId()
            )
			finish()
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
		}
		editPostPresenter.setPrefs(
            this, sharedPrefs,
            edit_post_footer_lyt,
            showTag,
            edit_post_tag1_lyt,
            edit_post_tag2_lyt,
            edit_post_tag3_lyt,
            edit_post_tag4_lyt,
            edit_post_tag5_lyt
        )
		showTag.setOnClickListener {
			tagLy.expandView()
			if (tagLy.visibility == View.GONE) {
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
		photoUrl = if (intent.getStringExtra("photoUrl") != null) {
			intent.getStringExtra("photoUrl")
		} else {
			ContextCompat.getDrawable(this, R.drawable.splachback).toString()
		}

		tableID = if (intent.getStringExtra("tableID") != null) {
			intent.getStringExtra("tableID")
		} else {
			""
		}

		if (intent.getStringExtra("address") != null) {
			address = intent.getStringExtra("address")
			edit_post_address.text = address
		} else {
			edit_post_address.visibility = View.GONE
		}

		if (intent.getStringExtra("uploadFooter") != null) {
			uploadFooter = intent.getStringExtra("uploadFooter")
			edit_post_footer.setText(uploadFooter)
		} else {
			uploadFooter = ""
		}

		if (intent.getStringExtra("uploadDB") != null) {
			uploadDBString = intent.getStringExtra("uploadDB")
			showLogDebug(mTAG, uploadDBString!!)
			uploadDB = FirebaseDBHelper.rootRef().child(uploadDBString!!)
		} else {
			uploadDB = FirebaseDBHelper.getShareableUploads()
		}

		if (intent.getStringExtra("feedRef") != null) {
			feedRefString = intent.getStringExtra("feedRef")
			showLogDebug(mTAG, feedRefString!!)
			feedRef = FirebaseDBHelper.rootRef().child(feedRefString!!)
		} else {
			feedRef = FirebaseDBHelper.rootRef()
		}

		if (intent.getStringExtra("uploadType") != null) {
			uploadType = intent.getStringExtra("uploadType")
		}

		if (!(intent.getStringExtra("tag1") == null && intent.getStringExtra("tag1") == "")) {
			tag1 = intent.getStringExtra("tag1")
			edit_post_tag1.setText(tag1)
		} else {
			tag1 = ""
			edit_post_tag1.setText("")
		}

		if (!(intent.getStringExtra("tag2") == null && intent.getStringExtra("tag2") == "")) {
			tag2 = intent.getStringExtra("tag2")
			edit_post_tag2.setText(tag2)
		} else {
			tag2 = ""
			edit_post_tag2.setText("")
		}

		if (!(intent.getStringExtra("tag3") == null && intent.getStringExtra("tag3") == "")) {
			tag3 = intent.getStringExtra("tag3")
			edit_post_tag3.setText(tag3)
		} else {
			tag3 = ""
			edit_post_tag3.setText("")
		}

		if (!(intent.getStringExtra("tag4") == null && intent.getStringExtra("tag4") == "")) {
			tag4 = intent.getStringExtra("tag4")
			edit_post_tag4.setText(tag4)
		} else {
			tag4 = ""
			edit_post_tag4.setText("")
		}

		if (!(intent.getStringExtra("tag5") == null && intent.getStringExtra("tag5") == "")) {
			tag5 = intent.getStringExtra("tag5")
			edit_post_tag5.setText(tag5)
		} else {
			tag5 = ""
			edit_post_tag5.setText("")
		}
		mImageView = findViewById(R.id.post_image)
		when (uploadType) {
            "map" -> {
                post_image.visibility = View.VISIBLE
                post_video.visibility = View.GONE
                Picasso.get().load(photoUrl).fit().centerCrop().into(mImageView)
                edit_post_address.text = address
                edit_post_banner.setOnClickListener {
                    post_image.expandView()
                    if (post_image.visibility == View.GONE) {
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_downward,
                            0
                        )
                    } else {
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_upward,
                            0
                        )
                    }
                }
            }
            "image" -> {
                post_image.visibility = View.VISIBLE
                post_video.visibility = View.GONE
                Picasso.get().load(photoUrl).fit().centerCrop().into(mImageView)
                edit_post_banner.setOnClickListener {
                    post_image.expandView()
                    if (post_image.visibility == View.GONE) {
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_downward,
                            0
                        )
                    } else {
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_upward,
                            0
                        )
                    }
                }
            }
            "video" -> {
                post_image.visibility = View.GONE
                post_video.visibility = View.VISIBLE
                edit_post_banner.setOnClickListener {
                    post_video.expandView()
                    if (post_video.visibility == View.GONE) {
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_downward,
                            0
                        )
                    } else {
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_upward,
                            0
                        )
                    }
                }
            }
		}

		showLogDebug(
            mTAG, photoUrl
                    + "\n" + tableID
                    + "\n" + uploadType
        )
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

	companion object {
		private const val mTAG = "EditPostActivity"
	}
}
