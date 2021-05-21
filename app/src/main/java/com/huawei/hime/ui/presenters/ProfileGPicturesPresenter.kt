package com.huawei.hime.profile.profilegallery.profilegallerypictures

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.ui.activities.BigPlayerProfActivity
import com.huawei.hime.util.ProfilePicturesDataClass
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.util.showLogDebug

class ProfileGPicturesPresenter constructor(private val profileGPicturesContractor: ProfileGPicturesContractor) {

    fun onViewsCreate() {
        profileGPicturesContractor.initViews()
        profileGPicturesContractor.initDB()
    }

    fun setupRecycler(
        mUserFeedPicturesRef:DatabaseReference,
        context:FragmentActivity,
        posterName:String,
        posterImg:String,
        mFollowerCounter:String,
        currentID:String,
        mUserInfoRef:DatabaseReference,
        mRecycler:RecyclerView
    ){
        var shareStat = ""
        var feedPic = ""
        var commentsAllowed = ""
        val options = FirebaseRecyclerOptions.Builder<ProfilePicturesDataClass>()
            .setQuery(mUserFeedPicturesRef, ProfilePicturesDataClass::class.java).build()

        val adapterFire = object :
            FirebaseRecyclerAdapter<ProfilePicturesDataClass, ProfileGPicturesViewHolder>(options) {
            override fun onCreateViewHolder(
                parent : ViewGroup,
                viewType : Int
            ) : ProfileGPicturesViewHolder {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.single_posted_card, parent, false)
                return ProfileGPicturesViewHolder(view)
            }

            override fun onBindViewHolder(
                holder : ProfileGPicturesViewHolder,
                position : Int,
                model : ProfilePicturesDataClass
            ) {
                /*Poster*/
                holder.setPosterImage(posterImg)
                holder.setPosterName(posterName)

                val lisResUid = getRef(position).key

                holder.parent.setOnClickListener {
                    showLogDebug(mTag,lisResUid.toString())
                }

                holder.mLovelyLyt!!.setOnClickListener {
                    holder.playHearts()
                }
                /*Post*/
                holder.setContentImage(model.profile_photoUrl)
                holder.setFooter(model.footer)
                holder.setTag1(model.tag1)
                holder.setTag2(model.tag2)
                holder.setTag3(model.tag3)
                holder.setTag4(model.tag4)
                holder.setTag5(model.tag5)
                holder.mMaterialCard!!.performClick()
                holder.setFollower(mFollowerCounter)
                showLogDebug(
                    mTag,
                    "profile_photoUrl: " + model.profile_photoUrl
                            + "\nprofile_thumbUrl: " + model.profile_thumbUrl
                            + "\nprofile_uploadTime: " + model.profile_uploadTime
                )

                holder.parent.setOnClickListener {
                    context.startActivity(
                        Intent(
                            context.applicationContext,
                            BigPlayerProfActivity::class.java
                        ).putExtra("postID", lisResUid)
                    )
                }

                holder.mPostContent!!.setOnClickListener {
                    showLogDebug(mTag,"PostID $lisResUid")

                    val feedDb = FirebaseDBHelper.getProfileFeed(currentID).child(lisResUid!!)
                    feedDb.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0 : DatabaseError) {

                        }

                        override fun onDataChange(p0 : DataSnapshot) {
                            shareStat = p0.child("shareStat").value.toString()
                            commentsAllowed = p0.child("commentsAllowed").value.toString()
                            feedPic = p0.child("profile_photoUrl").value.toString()
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
                        val uploadDB = FirebaseDBHelper.rootRef().child("uploads").child("NShareable")
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
                    }
                    showLogDebug(mTag,"ShareStat $shareStat")
                }
            }
        }
        adapterFire.startListening()
        mRecycler.adapter = adapterFire
    }

    companion object{
        private const val mTag="ProfileGPicturesPresenter"
    }
}

interface ProfileGPicturesContractor {
    fun initViews()
    fun initDB()
    fun showLogs(string: String)
}

