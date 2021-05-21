package com.huawei.hime.search.usersearch

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.activities.SingleUserActivity
import com.huawei.hime.util.FollowTypes
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.UserInfoDataClass
import com.huawei.hime.util.showLogDebug

class UserSearchPresenter constructor(private val userSearchContract: UserSearchContract) {

    companion object {
        private const val mTAG = "UserSearchPresenter"
    }

    private var mFollowingSince: String = "0"
    private var mFollowerNumber: String = "0"

    fun onViewsCreate() {
        userSearchContract.initViews()
    }

    fun setPrefs(
        context: Context,
        sharedPreferencesManager: SharedPreferencesManager,
        text: TextInputEditText
    ) {
        if (sharedPreferencesManager.loadNightModeState())
            text.background = context.getDrawable(R.drawable.dark_mask_bottom)
        else
            text.background = context.getDrawable(R.drawable.search_mask_bottom_light)
    }

    fun setupUserRecycler(
        context: Context,
        query: DatabaseReference,
        currentID: String,
        mRecycler: RecyclerView,
        text: String
    ) {
        val mp = MediaPlayer.create(context.applicationContext, R.raw.follow)
        val options =
            FirebaseRecyclerOptions.Builder<UserInfoDataClass>()
                .setQuery(query, UserInfoDataClass::class.java).build()
        val adapterFire = object :
            FirebaseRecyclerAdapter<UserInfoDataClass, UserSearchViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): UserSearchViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.single_find_user_card, parent, false)
                return UserSearchViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: UserSearchViewHolder,
                position: Int,
                model: UserInfoDataClass
            ) {
                val lisResUid = getRef(position).key

                showLogDebug(mTAG, "BEFORE Text: $text, Model: ${model.nameSurname}")
                if (model.nameSurname.contains(text)) {
                    showLogDebug(mTAG, "AFTER Text: $text, Model: ${model.nameSurname}, DB $query")
                    if (model.nameSurname == "" || model.nameSurname.isBlank() || model.nameSurname.isEmpty()) {
                        holder.setName(context.getString(R.string.himeUser))

                    } else {
                        if (currentID == lisResUid) {
                            holder.setName(model.nameSurname + " (" + context.getString(R.string.me) + ")")
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

                    val mFollowerTableRef: DatabaseReference =
                        FirebaseDBHelper.getFollowing(currentID, lisResUid!!)
                    mFollowerTableRef.keepSynced(true)

                    mFollowerTableRef.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.hasChildren()) {
                                showLogDebug("Child", p0.childrenCount.toString())
                                holder.mFollowBtn!!.visibility = View.GONE
                                holder.mFollowingSince!!.visibility = View.VISIBLE
                                holder.mLottie.visibility = View.VISIBLE
                                mFollowingSince = p0.child("following_since").value.toString()
                                holder.setFollowingSince(mFollowingSince)
                                showLogDebug("TAG", mFollowingSince)
                            }
                        }
                    })

                    holder.mFollowBtn!!.setOnClickListener {
                        mp.start()
                        holder.mFollowBtn!!.visibility = View.GONE
                        holder.mLottie.visibility = View.VISIBLE
                        FollowTypes.followUsers(currentID, lisResUid)
                        notifyDataSetChanged()
                    }

                    FollowTypes.followerNumbs(currentID)
                    holder.setFollower(mFollowerNumber)

                    holder.parent.setOnClickListener {
                        showLogDebug(mTAG, lisResUid)
                        context.startActivity(
                            Intent(
                                context,
                                SingleUserActivity::class.java
                            ).putExtra("userID", lisResUid)
                        )
                    }
                } else {
                    holder.itemView.visibility = View.GONE
                    holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
                }
            }
        }
        adapterFire.startListening()
        mRecycler.adapter = adapterFire
    }
}

interface UserSearchContract {
    fun initViews()
    fun initDB(string: String)
}