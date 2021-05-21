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
import com.huawei.hime.search.singletagpage.tagfresh.TagFreshFragment
import com.huawei.hime.search.singletagpage.tagtrending.TagTrendingFragment
import com.huawei.hime.util.FollowTypes
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.search.singletagpage.SingleTagFragmentPagerAdapter
import com.huawei.hime.ui.interfaces.ISingleTag
import com.huawei.hime.ui.presenters.SingleTagPresenter
import com.huawei.hime.util.showLogDebug
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_single_tag.*
import kotlinx.android.synthetic.main.single_tag_banner.*
import kotlinx.android.synthetic.main.single_tag_banner.ll_following_since

class SingleTagActivity : AppCompatActivity(), ISingleTag.ViewSingleTag {

	private val currentID = AppUser.getUserId()

	private var mOnlineDBRef = FirebaseDBHelper.getUser(currentID)

	private lateinit var mFollowingSinceDBRef : DatabaseReference
	private lateinit var mTagChildrenRef : DatabaseReference
	private lateinit var mTagRef : DatabaseReference

	private lateinit var hashTag : String
	private var tagText : String?=""
	private lateinit var context : Context

	private val singleTagPresenter : SingleTagPresenter by lazy {
		SingleTagPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		val sharedPrefs = SharedPreferencesManager(this)
		if (sharedPrefs.loadNightModeState()) {
			setTheme(R.style.DarkMode)
		} else {
			setTheme(R.style.LightMode)
		}
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_single_tag)
		supportActionBar?.hide()
		Slidr.attach(this)
		singleTagPresenter.onViewsCreate()

		tag_follower_relative.setOnClickListener {
			startActivity(
				Intent(
					this,
					MasterTPFollowerActivity::class.java
				)
					.putExtra("followers", single_tag_total_follower.text.toString())
					.putExtra("from", "tag")
					.putExtra("tpName", tagText)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			)
		}
	}

	override fun initViews() {
		context = applicationContext
		if (intent.getStringExtra("tag") != null) {
			hashTag = "#" + intent.getStringExtra("tag")
			tagText = intent.getStringExtra("tag")
			single_tag_txt.text = hashTag
		} else
			hashTag = ""

		// single_tag_total_post.text = intent.getStringExtra("size")
		showLogDebug("mTagChildrenRef", "$tagText, $currentID")
		mFollowingSinceDBRef = FirebaseDBHelper.getTagFollowingNumbers(tagText!!)
		mTagChildrenRef = FirebaseDBHelper.getTagReference(tagText!!)
		mTagRef = FirebaseDBHelper.getTagFollowingFeed(tagText!!, currentID)

		val mpFollow = MediaPlayer.create(context, R.raw.follow)
		val mpUnfollow = MediaPlayer.create(context, R.raw.unfollowed)

		mTagChildrenRef.addValueEventListener(object : ValueEventListener {
			override fun onDataChange(p0 : DataSnapshot) {
				if (p0.hasChildren()) {
					val pTotal = NumberConvertor.prettyCount(p0.childrenCount)
					single_tag_total_post.text = pTotal
				} else {
					single_tag_total_post.text = "0"
				}
			}

			override fun onCancelled(p0 : DatabaseError) {

			}
		})

		/* Following Controller */
		mFollowingSinceDBRef.addValueEventListener(object : ValueEventListener {
			override fun onDataChange(p0 : DataSnapshot) {
				if (p0.hasChildren()) {
					lottie_follow.visibility = View.VISIBLE
					lottie_follow.bringToFront()
					single_tag_follow_btn.visibility = View.GONE
					ll_following_since.visibility = View.VISIBLE
					val pFollow = NumberConvertor.prettyCount(p0.childrenCount)
					single_tag_total_follower.text = pFollow
					single_tag_unfollow_btn.setOnClickListener {
						val dialog = Dialog(this@SingleTagActivity, R.style.BlurTheme)
						val width = ViewGroup.LayoutParams.MATCH_PARENT
						val height = ViewGroup.LayoutParams.MATCH_PARENT
						dialog.window!!.setLayout(width, height)
						dialog.setContentView(R.layout.dialog_delete)
						dialog.setCanceledOnTouchOutside(true)
						val tvMain = dialog.findViewById<TextView>(R.id.dialog_tv_main)
						val tvSub = dialog.findViewById<TextView>(R.id.dialog_tv_sub)
						val tvKeep = dialog.findViewById<TextView>(R.id.dialog_keep_post)
						val tvDelete = dialog.findViewById<TextView>(R.id.dialog_delete_post)
						tvMain.text = getString(R.string.action_unfollow_main,tagText)
						tvKeep.text = getString(R.string.action_no_keep_as_follower)
						tvDelete.text = getString(R.string.action_yes_remove_following)
						tvSub.visibility = View.GONE
						tvKeep.setOnClickListener {
							dialog.dismiss()
							lottie_follow.visibility = View.VISIBLE
							lottie_follow.bringToFront()
						}
						tvDelete.setOnClickListener {
							mpUnfollow.start()
							lottie_follow.visibility = View.GONE
							FollowTypes.unFollowTags(tagText!!, currentID)
							recreate()
							dialog.dismiss()
						}
						dialog.show()
					}
				} else {
					single_tag_follow_btn.visibility = View.VISIBLE
					ll_following_since.visibility = View.GONE
					lottie_follow.visibility = View.GONE
					single_tag_total_follower.text = "0"
					single_tag_follow_btn.setOnClickListener {
						single_tag_follow_btn.visibility = View.GONE
						mpFollow.start()
						FollowTypes.followTags(currentID, tagText!!)
					}
				}
			}

			override fun onCancelled(p0 : DatabaseError) {
			}
		})

		mTagRef.addValueEventListener(object : ValueEventListener {
			override fun onDataChange(p0 : DataSnapshot) {
				single_tag_following_since_tv.text =
					p0.child("following_since").value.toString()
			}

			override fun onCancelled(p0 : DatabaseError) {
			}
		})
	}

	fun getTag() : String {
		return tagText!!
	}

	override fun initDB() {
		showLogDebug("mTagChildrenRef", "$tagText, $currentID")
		mFollowingSinceDBRef = FirebaseDBHelper.getTagFollowingFeed(tagText!!, currentID)
		mTagChildrenRef = FirebaseDBHelper.getTagReference(tagText!!)

		val mpFollow = MediaPlayer.create(context, R.raw.follow)
		val mpUnfollow = MediaPlayer.create(context, R.raw.unfollowed)

		mTagChildrenRef.addValueEventListener(object : ValueEventListener {
			override fun onDataChange(p0 : DataSnapshot) {
				if (p0.hasChildren()) {
					val pTotal = NumberConvertor.prettyCount(p0.childrenCount)
					single_tag_total_post.text = pTotal
				} else {
					single_tag_total_post.text = "0"
				}
			}

			override fun onCancelled(p0 : DatabaseError) {

			}
		})

		/* Following Controller */
		mFollowingSinceDBRef.addValueEventListener(object : ValueEventListener {
			override fun onDataChange(p0 : DataSnapshot) {
				if (p0.hasChildren()) {
					single_tag_follow_btn.visibility = View.GONE
					ll_following_since.visibility = View.VISIBLE
					val pFollow = NumberConvertor.prettyCount(p0.childrenCount)
					single_tag_total_follower.text = pFollow
					single_tag_following_since_tv.text =
						p0.child("following_since").value.toString()
					single_tag_unfollow_btn.setOnClickListener {
						val dialog = Dialog(this@SingleTagActivity, R.style.BlurTheme)
						val width = ViewGroup.LayoutParams.MATCH_PARENT
						val height = ViewGroup.LayoutParams.MATCH_PARENT
						dialog.window!!.setLayout(width, height)
						dialog.setContentView(R.layout.dialog_delete)
						dialog.setCanceledOnTouchOutside(true)
						val tvMain = dialog.findViewById<TextView>(R.id.dialog_tv_main)
						val tvSub = dialog.findViewById<TextView>(R.id.dialog_tv_sub)
						val tvKeep = dialog.findViewById<TextView>(R.id.dialog_keep_post)
						val tvDelete = dialog.findViewById<TextView>(R.id.dialog_delete_post)
						tvMain.text = getString(R.string.action_unfollow_main,tagText)
						tvKeep.text = getString(R.string.action_no_keep_as_follower)
						tvDelete.text = getString(R.string.action_yes_remove_following)
						tvSub.visibility = View.GONE
						tvKeep.setOnClickListener {
							dialog.dismiss()
						}
						tvDelete.setOnClickListener {
							mpUnfollow.start()
							FollowTypes.unFollowTags(tagText!!, currentID)
							recreate()
							dialog.dismiss()
						}
						dialog.show()
					}
				} else {
					single_tag_follow_btn.visibility = View.VISIBLE
					ll_following_since.visibility = View.GONE
					single_tag_total_follower.text = "0"
					single_tag_follow_btn.setOnClickListener {
						single_tag_follow_btn.visibility = View.GONE
						mpFollow.start()
						FollowTypes.followTags(currentID, tagText!!)
					}
				}
			}

			override fun onCancelled(p0 : DatabaseError) {
			}
		})
	}

	override fun setupViewPager() {
		val adapterL = SingleTagFragmentPagerAdapter(supportFragmentManager)
		adapterL.addFragment(TagTrendingFragment(), getString(R.string.frag_trend))
		adapterL.addFragment(TagFreshFragment(), getString(R.string.frag_fresh))
		single_tag_pager.adapter = adapterL
		single_tag_top_fresh_tabs.setupWithViewPager(single_tag_pager)
	}

	override fun onStart() {
		super.onStart()
		FirebaseDBHelper.onConnect(mOnlineDBRef)
	}

	override fun onResume() {
		super.onResume()
		FirebaseDBHelper.onConnect(mOnlineDBRef)
	}

	override fun onPause() {
		super.onPause()
		FirebaseDBHelper.onDisconnect(mOnlineDBRef)

	}

	override fun onDestroy() {
		super.onDestroy()
		FirebaseDBHelper.onDisconnect(mOnlineDBRef)
	}
}
