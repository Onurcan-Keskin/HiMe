package com.huawei.hime.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.database.*
import com.huawei.hime.R
import com.huawei.hime.helpers.Constants
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.interfaces.IMap
import com.huawei.hime.models.ExoModel
import com.huawei.hime.ui.interfaces.IBigPlayer
import com.huawei.hime.ui.presenters.BigPlayerPresenter
import com.huawei.hime.util.*
import com.huawei.hime.util.viewHolders.EventViewPagerAdapter
import com.huawei.hime.util.views.expandView
import com.r0adkll.slidr.Slidr
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_big_player.*
import map.HmsGmsBPIMap

class BigPlayerActivity : AppCompatActivity(), IBigPlayer.ViewBigPlayer {

	private lateinit var mUserInfoDBRef : DatabaseReference
	private lateinit var mUploadRef : DatabaseReference
	private lateinit var mFollowerRef : DatabaseReference

	private var postID : String? = ""
	private var posterImage : String? = ""
	private var posterName : String? = ""
	private var postType : String? = ""
	private var mTotalFollowers : String? = ""
	private var mTotalPopularity : String? = ""
	private var mUserID : String? = ""
	private var mpostUrl : String? = ""
	private var mLat : String? = ""
	private var mLong : String? = ""

	private lateinit var mMapview : View

	private lateinit var mVideo : PlayerView
	var popularity = 0

	private lateinit var mCircleImageView : CircleImageView
	private lateinit var mImageView : ImageView

	private lateinit var context : Context

	private val hmsGmsIMap : IMap by lazy { HmsGmsBPIMap(this) }

	private val bigPlayerPresenter : BigPlayerPresenter by lazy {
		BigPlayerPresenter(this, hmsGmsIMap)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		/* Mode State */
		val sharedPrefs = SharedPreferencesManager(this)
		if (sharedPrefs.loadNightModeState())
			setTheme(R.style.DarkMode)
		else
			setTheme(R.style.LightMode)
		/* Language State */
		if (sharedPrefs.loadLanguage()=="tr")
			LocaleHelper.setLocale(this, "tr")
		else
			LocaleHelper.setLocale(this, "en")
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_big_player)

		Slidr.attach(this)

		bigPlayerPresenter.onViewsCreate()

		bp_visibility_controller.setOnClickListener {
			setBannerView()
		}

		postID = intent.getStringExtra("postID")
		setTypes(savedInstanceState, postID!!)
	}

	override fun initViews() {
		context = applicationContext
		postID = intent.getStringExtra("postID")
		mpostUrl = if (intent.getStringExtra("postVideo") != null)
			intent.getStringExtra("postVideo")
		else
			"https://spark.adobe.com/video/mrY2wlv6KRKCy" // Dummy
		mUserID = intent.getStringExtra("postUploader")
		postType = intent.getStringExtra("postType")
		posterImage = if (intent.getStringExtra("postImage") != null)
			intent.getStringExtra("postImage")
		else
			ContextCompat.getDrawable(this,R.drawable.splachback).toString()
		mLat = if (intent.getStringExtra("upload_lat") != null)
			intent.getStringExtra("upload_lat")
		else
			""
		mLong = if (intent.getStringExtra("upload_long") != null)
			intent.getStringExtra("upload_long")
		else
			""

		showLogDebug(mTAG, "$mLat $mLong")

		showLogDebug(
            mTAG, "Post ID. $postID" +
                    "\nPost URL. $mpostUrl" +
                    "\nPoster ID. $mUserID" +
                    "\nPostType. $postType"
        )
		mCircleImageView = findViewById(R.id.bp_poster_img)
		mVideo = findViewById(R.id.bp_bigplayer)
		mImageView = findViewById(R.id.bp_image)
		mMapview = findViewById(R.id.map_map)

		bp_banner.bringToFront()
		bp_ui_controller.bringToFront()
	}

	override fun setTypes(sis : Bundle?, postID : String) {
		when (postType) {
            "image" -> {
                mVideo.visibility = View.GONE
                mMapview.visibility = View.GONE
                map_layout.visibility = View.GONE
                mImageView.visibility = View.VISIBLE
                bp_eventPager.visibility = View.GONE
                events_dots.visibility = View.GONE

                val photoViewAttacher = PhotoViewAttacher(bp_image)
                photoViewAttacher.maximumScale = 20F
                Picasso.get().load(posterImage).fit().centerCrop()
                    .into(mImageView)
            }
            "video" -> {
                mVideo.visibility = View.VISIBLE
                mMapview.visibility = View.GONE
                map_layout.visibility = View.GONE
                mImageView.visibility = View.GONE
                bp_eventPager.visibility = View.GONE
                events_dots.visibility = View.GONE
	            ExoModel(this).initializeExo(mpostUrl!!,mVideo)
            }
            "event" -> {
                mVideo.visibility = View.GONE
                mMapview.visibility = View.GONE
                map_layout.visibility = View.GONE
                mImageView.visibility = View.VISIBLE

                val imageUrls = ArrayList<String>()
                val dbPost = FirebaseDBHelper.getShareableUploads().child(postID)
                dbPost.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(p0 : DataSnapshot) {
                        val cover = p0.child("upload_event_coverUrl").value.toString()
                        val photo = p0.child("upload_event_photoUrl").value.toString()
                        val e0 = p0.child("upload_event_picked0").value.toString()
                        val e1 = p0.child("upload_event_picked1").value.toString()
                        val e2 = p0.child("upload_event_picked2").value.toString()
                        val e3 = p0.child("upload_event_picked3").value.toString()
                        val e4 = p0.child("upload_event_picked4").value.toString()

                        if (cover.isNotEmpty()) imageUrls.add(cover)
                        if (photo.isNotEmpty()) imageUrls.add(photo)
                        if (e0.isNotEmpty()) imageUrls.add(e0)
                        if (e1.isNotEmpty()) imageUrls.add(e1)
                        if (e2.isNotEmpty()) imageUrls.add(e2)
                        if (e3.isNotEmpty()) imageUrls.add(e3)
                        if (e4.isNotEmpty()) imageUrls.add(e4)
                        val adapter = EventViewPagerAdapter(this@BigPlayerActivity, imageUrls)
                        bp_eventPager.adapter = adapter
                        events_dots.setViewPager(bp_eventPager)
                    }

                    override fun onCancelled(p0 : DatabaseError) {}
                })
            }
            "map" -> {
                mVideo.visibility = View.GONE
                mMapview.visibility = View.VISIBLE
                map_layout.visibility = View.VISIBLE
                mImageView.visibility = View.GONE
                bp_eventPager.visibility = View.GONE
                events_dots.visibility = View.GONE

                hmsGmsIMap.onSavedInstanceBundle(sis)
                hmsGmsIMap.listenCamera()
                hmsGmsIMap.onInitMap()
                hmsGmsIMap.gotoCurrentLocation(mLat!!.toDouble(), mLong!!.toDouble())
            }
		}
	}

	override fun initDB() {
		mUploadRef = FirebaseDBHelper.rootRef().child("uploads/Shareable").child(postID!!)
		mUploadRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
            }

            override fun onDataChange(p0 : DataSnapshot) {
                mTotalPopularity = p0.child("popularity").value.toString()
                bp_popularity.text = NumberConvertor.prettyCount(mTotalPopularity!!.toLong())

                popularity = mTotalPopularity!!.toInt() + 1
            }
        })
		mUserInfoDBRef = FirebaseDBHelper.getUserInfo(mUserID!!)
		mUserInfoDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
	            showLogError(this.javaClass.simpleName,p0.message)
            }

            override fun onDataChange(p0 : DataSnapshot) {
                posterImage = p0.child("photoUrl").value.toString()
                posterName = p0.child("nameSurname").value.toString()

                Picasso.get().load(posterImage)
                    .placeholder(R.drawable.splachback).into(mCircleImageView)
                bp_poster.text = posterName
            }
        })
		mFollowerRef = FirebaseDBHelper.getFollowedNumbers(mUserID!!)
		mFollowerRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
	            showLogError(this.javaClass.simpleName,p0.message)
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

	override fun getLocationUpdate() {
		showLogDebug(mTAG,"Location update")
	}

	//TODO Fix Intent passing
	fun startMessagingActivity(uploadType : String) {
		when (uploadType) {
            "image" -> startActivity(
                Intent(context.applicationContext, PostMessageActivity::class.java)
                    .putExtra("postID", postID)
                    .putExtra("comment", 2) // Fix
                    .putExtra("uploadType", uploadType)
                    .putExtra("upload_imageUrl", 2)// Fix
            )
            "map" -> startActivity(
                Intent(context.applicationContext, PostMessageActivity::class.java)
                    .putExtra("postID", postID)
                    .putExtra("comment", 2) // Fix
                    .putExtra("uploadType", uploadType)
                    .putExtra("upload_imageUrl", 2) // Fix
            )
            "video" -> startActivity(
                Intent(context.applicationContext, PostMessageActivity::class.java)
                    .putExtra("postID", postID)
                    .putExtra("comment", 2) // Fix
                    .putExtra("uploadType", uploadType)
                    .putExtra("upload_videoUrl", 2)
            )
		}

	}

	override fun onStart() {
		super.onStart()
		FirebaseDBHelper.onConnect(Constants.fOnlineDBRef)
		when (postType) {
            "map" -> {
                hmsGmsIMap.onStartMap()
            }
		}
	}

	override fun onPause() {
		super.onPause()
		FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
		when (postType) {
            "map" -> {
                hmsGmsIMap.onPauseMap()
            }
		}
	}

	override fun onStop() {
		super.onStop()
		FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
		when (postType) {
            "video" -> {
                ExoModel(this).releasePlayer()
            }
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
		when (postType) {
            "video" -> {
	            ExoModel(this).releasePlayer()
            }
            "map" -> {
                hmsGmsIMap.onDestroyMap()
            }
		}
	}

	override fun onLowMemory() {
		super.onLowMemory()
		when (postType) {
            "map" -> {
                hmsGmsIMap.onLowMemoryMap()
            }
		}
	}

	companion object {
		private const val mTAG = "BigPlayerActivity"
	}
}
