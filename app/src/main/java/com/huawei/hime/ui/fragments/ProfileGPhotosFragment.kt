package com.huawei.hime.profile.profilegallery.profilegalleryphotos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.ui.activities.LiveStreamMainActivity
import com.huawei.hime.helpers.VerticalSpacingItemDecorator
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.util.showLogDebug

class ProfileGPhotosFragment : Fragment(), ProfilePhotosContract {

	private var mView : View? = null
	private var mRecycler : RecyclerView? = null
	private var mLottieLayout : LinearLayout? = null
	private var mLottie : LottieAnimationView? = null
	private var context : FragmentActivity? = null

	private var currentID : String? = ""
	private lateinit var mUserFeedPicturesRef : DatabaseReference
	private lateinit var mUserInfoRef : DatabaseReference

	private lateinit var mFollowedDBRef : DatabaseReference
	private var mFollowerCounter : String = ""

	private var posterName : String = ""
	private var posterImg : String = ""

	private var postUrl : String = ""
	private var postTime = ""

	private lateinit var profAddPhotos : LinearLayout

	private val profileGPhotosPresenter : ProfileGPhotosPresenter by lazy {
		ProfileGPhotosPresenter(this)
	}

	override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {
		mView = inflater.inflate(R.layout.fragment_profile_g_photos, container, false)
		profileGPhotosPresenter.onViewsCreate()

		profAddPhotos.setOnClickListener {
			startActivity(Intent(context?.applicationContext, LiveStreamMainActivity::class.java))
		}
		return mView
	}

	override fun initViews() {
		context = activity!!

		profAddPhotos = mView!!.findViewById(R.id.prof_add_photos_lyt)
		mRecycler = mView!!.findViewById(R.id.profile_photos_recycler)
		mLottieLayout = mView!!.findViewById(R.id.profile_photos_lottie_lyt)
		mLottie = mView!!.findViewById(R.id.profile_photos_lottie)
	}

	override fun initDB() {
		currentID = AppUser.getUserId()
		showLogDebug(mTag, "ID -> " + currentID!!)
		mUserFeedPicturesRef = FirebaseDBHelper.rootRef().child("User Feed/Photos/$currentID")
		mUserInfoRef = FirebaseDBHelper.getUserInfo(currentID!!)

		mUserInfoRef.keepSynced(true)

		mFollowedDBRef = FirebaseDBHelper.getFollowedNumbers(currentID!!)
		mFollowedDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {

            }

            override fun onDataChange(p0 : DataSnapshot) {
                if (p0.hasChildren()) {
                    mFollowerCounter = p0.childrenCount.toString()
                }
            }
        })

		mUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
            }

            override fun onDataChange(p0 : DataSnapshot) {
                posterName = p0.child("nameSurname").value.toString()
                posterImg = p0.child("photoUrl").value.toString()
            }
        })

		mUserFeedPicturesRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {

            }

            override fun onDataChange(snapshot : DataSnapshot) {
                if (snapshot.hasChildren()) {
                    showLogDebug(mTag, "Photos child count: " + snapshot.childrenCount.toString())
                    for (postSnap in snapshot.children) {
                        showLogDebug(mTag, "profile_keys: -> " + postSnap.key!!)

                        mUserFeedPicturesRef.child(postSnap.key!!)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(pS : DatabaseError) {

                                }

                                override fun onDataChange(pS : DataSnapshot) {
                                    postUrl = postSnap.child("photos_photoUrl").value.toString()
                                    postTime = postSnap.child("photos_uploadTime").value.toString()
                                    profileGPhotosPresenter.setupRecycler(
                                        mUserFeedPicturesRef,
                                        mUserInfoRef,
                                        context!!,
                                        currentID!!,
                                        posterImg,
                                        posterName,
                                        mFollowerCounter,
                                        mRecycler!!
                                    )
                                }
                            })
                    }
                } else {
                    mLottieLayout!!.visibility = View.VISIBLE
                }
            }
        })

		mRecycler!!.layoutManager = LinearLayoutManager(context)
		val itemDecorator =
			VerticalSpacingItemDecorator(
                10
            )
		mRecycler!!.addItemDecoration(itemDecorator)
		mRecycler!!.setHasFixedSize(true)
	}

	companion object {
		private const val mTag = "ProfileGPhotosFragment"
	}
}
