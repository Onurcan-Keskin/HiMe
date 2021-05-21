package com.huawei.hime.masterfollowers.masterU.followers

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huawei.hime.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class FollowerViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    val parent: View = itemView
    var mAge: TextView? = null
    var mImage: CircleImageView? = null
    var mLovely: TextView? = null
    var mFollowing: TextView? = null
    var mFollowerName: TextView? = null
    var mRemoveBtn: Button? = null

    fun setAge(age: String) {
        mAge!!.text = age
    }

    fun setImage(image: String) {
        Picasso.get().load(image)
            .placeholder(R.drawable.ic_account_circle).fit().centerCrop().into(mImage!!)
    }

    fun setLovely(lovely: String) {
        mLovely!!.text = lovely
    }

    fun setFollowing(following: String) {
        mFollowing!!.text = following
    }

    fun setName(name: String) {
        mFollowerName!!.text = name.split(' ').first()
    }

    init {
        mAge = parent.findViewById(R.id.follower_age)
        mImage = parent.findViewById(R.id.follower_img)
        mLovely = parent.findViewById(R.id.follower_lovely)
        mFollowing = parent.findViewById(R.id.follower_following_since)
        mFollowerName = parent.findViewById(R.id.follower_name)
        mRemoveBtn = parent.findViewById(R.id.remove_follower)

        mRemoveBtn!!.visibility = View.GONE
    }
}