package com.huawei.hime.profile.profilegallery.profilegalleryphotos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.huawei.hime.R
import com.huawei.hime.util.ProfilePhotosDataClass
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.util.showLogDebug

class ProfileGPhotosPresenter constructor(private val profileGPhotosContract : ProfilePhotosContract) {

	fun onViewsCreate() {
		profileGPhotosContract.initViews()
		profileGPhotosContract.initDB()
	}

	fun setupRecycler(
        mUserFeedPicturesRef : Query,
        mUserInfoRef : DatabaseReference,
        context : FragmentActivity,
        currentID : String,
        posterImg : String,
        posterName : String,
        mFollowerCounter : String,
        mRecycler : RecyclerView
    ) {
		var shareStat = ""
		var feedPic = ""
		var commentsAllowed = ""
		val options = FirebaseRecyclerOptions.Builder<ProfilePhotosDataClass>()
			.setQuery(mUserFeedPicturesRef, ProfilePhotosDataClass::class.java).build()

		val adapterFire =
			object :
				FirebaseRecyclerAdapter<ProfilePhotosDataClass, ProfileGPhotosViewHolder>(options) {
				override fun onCreateViewHolder(
                    parent : ViewGroup,
                    viewType : Int
                ) : ProfileGPhotosViewHolder {
					val view = LayoutInflater.from(context)
						.inflate(R.layout.single_posted_card, parent, false)
					return ProfileGPhotosViewHolder(view)
				}

				override fun onBindViewHolder(
                    holder : ProfileGPhotosViewHolder,
                    position : Int,
                    model : ProfilePhotosDataClass
                ) {
					holder.setPosterImage(posterImg)
					holder.setPosterName(posterName)
					holder.mMaterialCard!!.performClick()

					val lisResUid = getRef(position).key

					holder.setContentImage(model.photos_photoUrl)
					holder.setFooter(model.footer)
					holder.setLovely(model.photos_lovely)
					holder.setFollower(mFollowerCounter)


					//Tag Preparation
					holder.setTag1(model.tag1)
					holder.setTag2(model.tag2)
					holder.setTag3(model.tag3)
					holder.setTag4(model.tag4)
					holder.setTag5(model.tag5)

					holder.mLovelyLyt!!.setOnClickListener {
						holder.playHearts()
					}

					holder.parent.setOnClickListener {
						showLogDebug(mTag, "PostID $lisResUid")

						val feedDb = FirebaseDBHelper.getPhotoFeed(currentID).child(lisResUid!!)
						feedDb.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0 : DatabaseError) {

                            }

                            override fun onDataChange(p0 : DataSnapshot) {
                                shareStat = p0.child("shareStat").value.toString()
                                commentsAllowed = p0.child("commentsAllowed").value.toString()
                                feedPic = p0.child("photos_photoUrl").value.toString()
                            }
                        })

						if (shareStat == "1") {
							val uploadDB = FirebaseDBHelper.rootRef().child("uploads").child("Shareable")
								.child(lisResUid)
							holder.setCircleMenu(
                                context,
                                uploadDB,
                                feedDb,
                                mUserInfoRef,
                                shareStat,
                                commentsAllowed,
                                feedPic,
                                lisResUid
                            )
						} else if (shareStat == "0") {
							val uploadDB =
								FirebaseDBHelper.rootRef().child("uploads").child("NShareable")
									.child(lisResUid)
							holder.setCircleMenu(
                                context!!,
                                uploadDB,
                                feedDb,
                                mUserInfoRef,
                                shareStat,
                                commentsAllowed,
                                feedPic,
                                lisResUid
                            )
						}
						showLogDebug(
                            mTag,
                            "ShareStat $shareStat\nComments $commentsAllowed"
                        )
					}
				}
			}
		adapterFire.startListening()
		mRecycler.adapter = adapterFire
	}

	companion object {
		private const val mTag = "ProfileGPhotosPresenter"
	}
}

interface ProfilePhotosContract {
	fun initViews()
	fun initDB()
}

