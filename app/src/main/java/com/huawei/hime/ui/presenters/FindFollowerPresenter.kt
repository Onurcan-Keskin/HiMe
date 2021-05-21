package com.huawei.hime.findfollower

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.activities.SingleUserActivity
import com.huawei.hime.util.FollowTypes
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.UserInfoDataClass
import com.huawei.hime.util.showLogDebug

class FindFollowerPresenter constructor(private val findFollowerContractor: FindFollowerContractor) {
    fun onViewCreate() {
        findFollowerContractor.initViews()
    }

    fun setupRecycler(
        context: Context,
        currentID: String,
        query: Query,
        mRecycler: RecyclerView
    ) {
        val options =
            FirebaseRecyclerOptions.Builder<UserInfoDataClass>()
                .setQuery(query, UserInfoDataClass::class.java).build()

        val adapterFire = object :
            FirebaseRecyclerAdapter<UserInfoDataClass, FollowerUserInfoViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): FollowerUserInfoViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.single_find_user_card, parent, false)
                return FollowerUserInfoViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: FollowerUserInfoViewHolder,
                position: Int,
                model: UserInfoDataClass
            ) {
                val lisResUid = getRef(position).key

                if (model.nameSurname == "" || model.nameSurname.isBlank() || model.nameSurname.isEmpty()) {
                    holder.setName(context.getString(R.string.himeUser))
                } else {
                    if (currentID == lisResUid) {
                        holder.setName(model.nameSurname + "(" + context.getString(R.string.me) + ")")
                        holder.mFollowBtn!!.visibility = View.GONE
                        holder.mFollowingSince!!.visibility = View.GONE
                    } else {
                        holder.setName(model.nameSurname)
                    }
                }

                if (model.thumbUrl == "default") {
                    model.thumbUrl = model.photoUrl
                    holder.setImage(model.thumbUrl)
                } else {
                    holder.setImage(model.thumbUrl)
                }
                holder.setAge(model.age)
                holder.setLovely(model.lovely)

                val mFollowerTableRef = FirebaseDBHelper.getFollowing(currentID, lisResUid!!)
                mFollowerTableRef.keepSynced(true)

                mFollowerTableRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.hasChildren()) {
                            showLogDebug("Child", p0.childrenCount.toString())
                            holder.mFollowBtn!!.visibility = View.GONE
                            holder.mFollowingSince!!.visibility = View.VISIBLE
                            holder.mLottieFollow!!.visibility = View.VISIBLE
                            val mFollowingSince = p0.child("following_since").value.toString()
                            holder.setFollowingSince(mFollowingSince)
                            showLogDebug("TAG", mFollowingSince)
                        }
                    }
                })

                holder.mFollowBtn!!.setOnClickListener {
                    val mpFollow = MediaPlayer.create(context.applicationContext, R.raw.follow)
                    mpFollow.start()
                    holder.mFollowBtn!!.visibility = View.GONE
                    holder.mLottieFollow!!.visibility = View.VISIBLE
                    FollowTypes.followUsers(currentID, lisResUid)
                    notifyDataSetChanged()
                }

                FollowTypes.followerNumbs(currentID)

                val followerDB = FirebaseDBHelper.getFollowedNumbers(currentID)
                followerDB.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.hasChildren()) {
                            val mTotalFollowers =
                                NumberConvertor.prettyCount(p0.childrenCount)
                            holder.setFollower(mTotalFollowers)
                        } else
                            holder.setFollower("0")
                    }
                })

                holder.parent.setOnClickListener {
                    context.startActivity(
                        Intent(
                            context,
                            SingleUserActivity::class.java
                        ).putExtra("userID", lisResUid)
                    )
                }
            }

            override fun onDataChanged() {
                super.onDataChanged()
                notifyDataSetChanged()
            }
        }
        adapterFire.startListening()
        mRecycler.adapter = adapterFire
    }


}

interface FindFollowerContractor {
    fun initViews()
}

interface SplashHelper{

}