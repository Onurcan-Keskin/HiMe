package com.huawei.hime.ui.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.database.*
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.Constants
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.models.ExoModel
import com.huawei.hime.ui.interfaces.IBigPlayerProf
import com.huawei.hime.ui.presenters.BigPlayerProfPresenter
import com.huawei.hime.util.views.expandView
import com.r0adkll.slidr.Slidr
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_big_player_prof.*

class BigPlayerProfActivity : AppCompatActivity(), IBigPlayerProf.ViewBigPlayerProf {

	private lateinit var mFollowersRef : DatabaseReference

	private var postID : String? = ""
	private lateinit var posterImage : String
	private lateinit var posterName : String
	private lateinit var mTotalFollowers : String
	private var mpostUrl : String? = ""

	private lateinit var mVideo : PlayerView

	private lateinit var mCircleImageView : CircleImageView

	private lateinit var context : Context

	private val bigPlayerProfPresenter : BigPlayerProfPresenter by lazy {
		BigPlayerProfPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_big_player)

		Slidr.attach(this)

		bigPlayerProfPresenter.onViewsCreate()

		bp_visibility_controller.setOnClickListener {
			setBannerView()
		}

		ExoModel(this).initializeExo(mpostUrl!!,mVideo)
	}

	override fun initViews() {
		context = applicationContext
		postID = intent.getStringExtra("postID")

		if (intent.getStringExtra("postVideo") != null) {
			mpostUrl = intent.getStringExtra("postVideo")
			Log.d("BigPlayerProfActivity", "Post ID. $postID\nPost URL. $mpostUrl")
		}

		mCircleImageView = findViewById(R.id.bp_poster_img)
		mVideo = findViewById(R.id.bp_bigplayer)

		bp_banner.bringToFront()
		bp_visibility_controller.bringToFront()
	}

	override fun initDB() {
		Constants.fUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {

            }

            override fun onDataChange(p0 : DataSnapshot) {
                posterName = p0.child("nameSurname").value.toString()
                posterImage = p0.child("photoUrl").value.toString()

                Picasso.get().load(posterImage)
                    .placeholder(R.drawable.splachback).into(mCircleImageView)
                bp_poster.text = posterName
            }
        })

		mFollowersRef = FirebaseDBHelper.getFollowedNumbers(AppUser.getUserId())
		mFollowersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {

            }

            override fun onDataChange(p0 : DataSnapshot) {
                if (p0.hasChildren()) {
                    mTotalFollowers = p0.childrenCount.toString()
                    bp_post_followers.text = mTotalFollowers
                }
            }
        })
	}

	override fun setBannerView() {
		bp_banner.expandView()
		if (bp_banner.visibility == View.GONE) {
			bp_visibility_controller.setImageResource(R.drawable.ic_post_show)
		} else {
			bp_visibility_controller.setImageResource(R.drawable.ic_post_hide)
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
		ExoModel(this).releasePlayer()
	}

	override fun onDestroy() {
		super.onDestroy()
        ExoModel(this).releasePlayer()
	}
}
