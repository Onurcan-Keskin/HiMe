package com.huawei.hime.ui.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.Constants
import com.huawei.hime.search.singleplacespage.placesfresh.PlacesFreshFragment
import com.huawei.hime.search.singleplacespage.placestrending.PlacesTrendingFragment
import com.huawei.hime.util.FollowTypes
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.search.singleplacespage.SinglePlacesPagerAdapter
import com.huawei.hime.ui.interfaces.ISinglePlaces
import com.huawei.hime.ui.presenters.SinglePlacesPresenter
import com.huawei.hime.util.showLogError
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_single_places.*
import kotlinx.android.synthetic.main.single_country_banner.*
import kotlinx.android.synthetic.main.single_country_banner.ll_following_since

class SinglePlacesActivity : AppCompatActivity(), ISinglePlaces.ViewSinglePlaces {

	private val currentID = AppUser.getUserId()

	private lateinit var mFollowingSinceDBRef : DatabaseReference
	private lateinit var mCountryRef : DatabaseReference

	private var country : String? = ""
	private var pTotal : String? = ""
	private lateinit var context : Context

	private val singlePlacesPresenter : SinglePlacesPresenter by lazy {
		SinglePlacesPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		val sharedPrefs = SharedPreferencesManager(this)
		if (sharedPrefs.loadNightModeState()) {
			setTheme(R.style.DarkMode)
		} else {
			setTheme(R.style.LightMode)
		}
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_single_places)
		supportActionBar?.hide()
		Slidr.attach(this)
		singlePlacesPresenter.onViewsCreate()

		places_follower_relative.setOnClickListener {
			startActivity(
                Intent(
                    this,
                    MasterTPFollowerActivity::class.java
                )
	                .putExtra("followers", single_country_total_follower.text.toString())
	                .putExtra("tpName", single_country_name.text.toString())
                    .putExtra("from","places")
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
		}
	}

	override fun initViews() {
		context = applicationContext
		if (intent.getStringExtra("country") != null)
			country = intent.getStringExtra("country")

		pTotal = if (intent.getStringExtra("total") != null)
			intent.getStringExtra("total")
		else
			"0"

		single_country_total_post.text = NumberConvertor.prettyCount(pTotal!!.toLong())
		single_country_name.text = country

		mFollowingSinceDBRef = FirebaseDBHelper.getPlacesFollowingNumbers(country!!)
		mCountryRef = FirebaseDBHelper.getPlacesFollowingFeed(country!!, currentID)

		val mpFollow = MediaPlayer.create(context, R.raw.follow)
		val mpUnfollow = MediaPlayer.create(context, R.raw.unfollowed)

		mFollowingSinceDBRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0 : DataSnapshot) {
                if (p0.hasChildren()) {
                    single_places_follow_btn.visibility = View.GONE
                    ll_following_since.visibility = View.VISIBLE
                    val pFollow = NumberConvertor.prettyCount(p0.childrenCount)
                    single_country_total_follower.text = pFollow
                    single_places_unfollow_btn.setOnClickListener {
                        val dialog = Dialog(this@SinglePlacesActivity, R.style.BlurTheme)
                        val width = ViewGroup.LayoutParams.MATCH_PARENT
                        val height = ViewGroup.LayoutParams.MATCH_PARENT
                        dialog.window!!.setLayout(width, height)
                        dialog.setContentView(R.layout.dialog_delete)
                        dialog.setCanceledOnTouchOutside(true)
                        val tvMain = dialog.findViewById<TextView>(R.id.dialog_tv_main)
                        val tvSub = dialog.findViewById<TextView>(R.id.dialog_tv_sub)
                        val tvKeep = dialog.findViewById<TextView>(R.id.dialog_keep_post)
                        val tvDelete = dialog.findViewById<TextView>(R.id.dialog_delete_post)
                        tvMain.text = getString(R.string.action_unfollow_main) + " #" + country
                        tvKeep.text = getString(R.string.action_no_keep_as_follower)
                        tvDelete.text = getString(R.string.action_yes_remove_following)
                        tvSub.visibility = View.GONE
                        tvKeep.setOnClickListener {
                            dialog.dismiss()
                        }
                        tvDelete.setOnClickListener {
                            mpUnfollow.start()
                            FollowTypes.unFollowTags(country!!, currentID)
                            recreate()
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                } else {
                    single_places_follow_btn.visibility = View.VISIBLE
                    ll_following_since.visibility = View.GONE
                    single_country_total_follower.text = "0"
                    single_places_follow_btn.setOnClickListener {
                        single_places_follow_btn.visibility = View.GONE
                        mpFollow.start()
                        FollowTypes.followPlaces(currentID, country!!)
                    }
                }
            }

            override fun onCancelled(p0 : DatabaseError) {
	            showLogError(this.javaClass.simpleName,p0.message)
            }
        })

		mCountryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0 : DataSnapshot) {
                single_places_following_since_tv.text =
                    p0.child("following_since").value.toString()
            }

            override fun onCancelled(p0 : DatabaseError) {
	            showLogError(this.javaClass.simpleName,p0.message)
            }
        })
	}

	fun getCountry() : String {
		return country!!
	}

	override fun setupPager() {
		val adapterL = SinglePlacesPagerAdapter(supportFragmentManager)
		adapterL.addFragment(PlacesTrendingFragment(), getString(R.string.frag_trend))
		adapterL.addFragment(PlacesFreshFragment(), getString(R.string.frag_fresh))
		single_country_pager.adapter = adapterL
		single_country_top_fresh_tabs.setupWithViewPager(single_country_pager)
	}

	override fun onStart() {
		super.onStart()
		FirebaseDBHelper.onConnect(Constants.fOnlineDBRef)
	}

	override fun onResume() {
		super.onResume()
		FirebaseDBHelper.onConnect(Constants.fOnlineDBRef)
	}

	override fun onPause() {
		super.onPause()
		FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)

	}

	override fun onDestroy() {
		super.onDestroy()
		FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
	}
}
