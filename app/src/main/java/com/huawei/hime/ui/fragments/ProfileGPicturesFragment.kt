package com.huawei.hime.profile.profilegallery.profilegallerypictures

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.VerticalSpacingItemDecorator
import com.huawei.hime.util.OnActivity
import com.huawei.hime.helpers.FirebaseDBHelper
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class ProfileGPicturesFragment : Fragment(), ProfileGPicturesContractor {

	private var mView : View? = null
	private var mRecycler : RecyclerView? = null
	private var mLottieLayout : LinearLayout? = null
	private var mLottie : LottieAnimationView? = null
	private var context : FragmentActivity? = null

	private var currentID : String? = ""
	private lateinit var mStorageReference : StorageReference
	private lateinit var mUserFeedPicturesRef : DatabaseReference
	private lateinit var mUserInfoRef : DatabaseReference
	private var mUserFeed = "User Feed/Profile Pictures/"

	private lateinit var mFollowedDBRef : DatabaseReference
	private var mFollowerCounter : String = ""

	private var posterName : String = ""
	private var posterImg : String = ""

	private lateinit var profAddLyt : LinearLayout

	private var postUrl : String = ""
	private var postThumbUrl : String = ""
	private var postTime = ""

	private val profileGPicturesPresenter : ProfileGPicturesPresenter by lazy {
		ProfileGPicturesPresenter(this)
	}

	override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {
		mView = inflater.inflate(R.layout.fragment_profile_gallery_pictures, container, false)
		profileGPicturesPresenter.onViewsCreate()

		profAddLyt.setOnClickListener {
			CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
				.setCropShape(CropImageView.CropShape.OVAL).start(context!!)
		}
		return mView
	}

	override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
		//super.onActivityResult(requestCode, resultCode, data)
		OnActivity.forPictureResult(
            imgRequestCode,
            requestCode,
            resultCode,
            data,
            context!!,
            mStorageReference,
            currentID,
            mUserInfoRef,
            mUserFeed,
            posterName
        )
	}

	override fun initViews() {
		context = activity!!

		profAddLyt = mView!!.findViewById(R.id.prof_add_prof_lyt)
		mRecycler = mView!!.findViewById(R.id.profile_pictures_recycler)
		mLottieLayout = mView!!.findViewById(R.id.profile_pictures_lottie_lyt)
		mLottie = mView!!.findViewById(R.id.profile_pictures_lottie)
	}

	override fun initDB() {
		currentID = AppUser.getUserId()
		showLogs("ID -> " + currentID!!)
		mUserFeedPicturesRef = FirebaseDBHelper.rootRef().child("User Feed/Profile Pictures/$currentID")
		mUserInfoRef = FirebaseDBHelper.getUserInfo(currentID!!)

		mStorageReference = FirebaseStorage.getInstance().reference

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

            override fun onDataChange(p0 : DataSnapshot) {
                if (p0.hasChildren()) {
                    showLogs("Profile Pictures Size: " + p0.childrenCount.toString())
                    for (postSnap in p0.children) {
                        showLogs("profile_keys: -> " + postSnap.key!!)

                        mUserFeedPicturesRef.child(postSnap.key!!)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(pS : DatabaseError) {

                                }

                                override fun onDataChange(pS : DataSnapshot) {
                                    postUrl = postSnap.child("profile_photoUrl").value.toString()
                                    postThumbUrl =
                                        postSnap.child("profile_thumbUrl").value.toString()
                                    postTime = postSnap.child("profile_uploadTime").value.toString()
                                    profileGPicturesPresenter.setupRecycler(
                                        mUserFeedPicturesRef,
                                        context!!,
                                        posterName,
                                        posterImg,
                                        mFollowerCounter,
                                        currentID!!,
                                        mUserInfoRef,
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

	override fun showLogs(string : String) {
		Log.d("ProfileGalleryPictures: ", string)
	}

	companion object {
		private const val imgRequestCode = 2021
	}
}
