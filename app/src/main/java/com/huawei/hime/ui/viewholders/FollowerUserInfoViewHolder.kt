package com.huawei.hime.findfollower

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.huawei.hime.R
import com.huawei.hime.util.NumberConvertor
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class FollowerUserInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val parent: View = itemView
    var mFollowBtn: Button? = null
    var mFollowerCounter: TextView? = null
    var mFollowingSince: TextView? = null
    var mLottieFollow: LottieAnimationView? = null

    fun setName(name: String) {
        val mName = parent.findViewById<TextView>(R.id.single_user_name)
        if (mName.toString().isNullOrEmpty() || mName.toString() == "" || mName.toString()
                .isNullOrBlank()
        ) {
            mName.setText(R.string.himeUser)
        } else {
            mName.text = name
        }
    }

    fun setAge(age: String) {
        val mAge = parent.findViewById<TextView>(R.id.single_user_age)
        if (mAge.toString().isEmpty()) {
            mAge.setText(R.string.zero)
        } else {
            mAge.text = age
        }
    }

    fun setFollower(num: String) {
        if (num != "") {
            val pFollow = NumberConvertor.prettyCount(num.toLong())
            mFollowerCounter!!.text = pFollow
        } else mFollowerCounter!!.text = "0"

    }

    fun setFollowingSince(date: String) {
        mFollowingSince!!.text = date
    }

    fun setLovely(lovely: String) {
        val mLovely = parent.findViewById<TextView>(R.id.single_user_lovely)
        val pLovely = NumberConvertor.prettyCount(lovely.toLong())
        mLovely.text = pLovely
    }

    fun setImage(img: String) {
        val mImage = parent.findViewById<CircleImageView>(R.id.single_user_img)
        if (img != "default" || img.isNotEmpty() || img != "") {
            Picasso.get().load(img).placeholder(R.drawable.splachback).into(mImage)
        } else {
            Picasso.get().load(R.drawable.splachback).placeholder(R.drawable.splachback)
                .into(mImage)
        }
    }

    fun makeGone() {
        parent.visibility = View.GONE
    }

    init {
        mFollowBtn = parent.findViewById(R.id.single_user_follow_lyt)
        mFollowerCounter = parent.findViewById(R.id.single_user_follower)
        mFollowingSince = parent.findViewById(R.id.single_following_since)
        mLottieFollow = parent.findViewById(R.id.single_user_follow_lottie)

        mLottieFollow!!.visibility = View.GONE
    }

}