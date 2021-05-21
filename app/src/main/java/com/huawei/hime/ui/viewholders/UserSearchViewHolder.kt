package com.huawei.hime.search.usersearch

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.huawei.hime.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val parent: View = itemView
    var mFollowBtn: Button?= null
    var mFollowerCounter: TextView?=null
    var mFollowingSince: TextView?=null
    var mConstraintCard: ConstraintLayout
    var mName:TextView
    var mLottie: LottieAnimationView

    fun setName(name: String) {
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

    fun setFollower(num:String){
        mFollowerCounter!!.text = num
    }

    fun setFollowingSince(date:String){
        mFollowingSince!!.text = date
    }

    fun setLovely(lovely: String) {
        val mLovely = parent.findViewById<TextView>(R.id.single_user_lovely)
        mLovely.text = lovely
    }

    fun setImage(img: String) {
        val mImage = parent.findViewById<CircleImageView>(R.id.single_user_img)
        if (img != "default" || img.isNotEmpty() || img != "") {
            Picasso.get().load(img).placeholder(R.drawable.splachback).into(mImage)
        } else {
            Picasso.get().load(R.drawable.splachback).placeholder(R.drawable.splachback).into(mImage)
        }
    }

    fun makeGone(){
        mConstraintCard.visibility=View.GONE
    }

    init {
        mFollowBtn = parent.findViewById(R.id.single_user_follow_lyt)
        mFollowerCounter = parent.findViewById(R.id.single_user_follower)
        mFollowingSince = parent.findViewById(R.id.single_following_since)
        mConstraintCard = parent.findViewById(R.id.single_find_user_constraint)
        mName= parent.findViewById(R.id.single_user_name)
        mLottie = parent.findViewById(R.id.single_user_follow_lottie)
    }

}