package com.huawei.hime.discover

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huawei.hime.R
import com.huawei.hime.ui.activities.SearchActivity
import com.huawei.hime.util.views.expandExplanation
import de.hdodenhof.circleimageview.CircleImageView

class DiscoverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val parent: View = itemView

    private var mSingleLayout: LinearLayout
    private var mPosterCircle: CircleImageView
    private var mPosterName: TextView
    private var mPost: ImageView
    private var mPostFooter: TextView
    private var mTag1: TextView
    private var mTag2: TextView
    private var mTag3: TextView
    private var mTag4: TextView
    private var mTag5: TextView
    private var lovely: TextView

    var mMessage: TextView
    var mShare: TextView

    fun bindImage(
        posterCircle: String,
        posterName: String,
        post: String,
        postFooter: String,
        postLovely: String
    ) {
        Glide.with(parent.context.applicationContext).load(posterCircle).into(mPosterCircle)
        mPosterName.text = posterName
        Glide.with(parent.context.applicationContext).load(post).into(mPost)
        mPostFooter.text = postFooter
        mPostFooter.setOnClickListener {
            mPostFooter.expandExplanation()
        }
        lovely.text = postLovely
    }

    fun emptyHolder() {
        mSingleLayout.visibility = View.GONE
    }

    fun setTag1(string: String) {
        if (mTag1.text.toString().isNotEmpty()) {
            mTag1.text = string
            mTag1.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SearchActivity::class.java
                    ).putExtra("tag", string)
                )
            }
        } else
            mTag1.visibility = View.GONE
    }

    fun setTag2(string: String) {
        if (mTag2.text.toString().isNotEmpty()) {
            mTag2.text = string
            mTag2.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SearchActivity::class.java
                    ).putExtra("tag", string)
                )
            }
        } else
            mTag2.visibility = View.GONE
    }

    fun setTag3(string: String) {
        if (mTag3.text.toString().isNotEmpty()) {
            mTag3.text = string
            mTag3.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SearchActivity::class.java
                    ).putExtra("tag", string)
                )
            }
        } else
            mTag3.visibility = View.GONE
    }

    fun setTag4(string: String) {
        if (mTag4.text.toString().isNotEmpty()) {
            mTag4.text = string
            mTag4.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SearchActivity::class.java
                    ).putExtra("tag", string)
                )
            }
        } else
            mTag4.visibility = View.GONE
    }

    fun setTag5(string: String) {
        if (mTag5.text.toString().isNotEmpty()) {
            mTag5.text = string
            mTag5.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SearchActivity::class.java
                    ).putExtra("tag", string)
                )
            }
        } else
            mTag5.visibility = View.GONE
    }

    init {
        mSingleLayout = parent.findViewById(R.id.single_post_card_layout)
        mPosterCircle = parent.findViewById(R.id.hybrid_card_poster_img)
        mPosterName = parent.findViewById(R.id.hybrid_card_poster)
        mPost = parent.findViewById(R.id.posted_content_image)
        mPostFooter = parent.findViewById(R.id.hybrid_card_footer)
        lovely = parent.findViewById(R.id.hybrid_card_lovely)
        mTag1 = parent.findViewById(R.id.hybrid_card_tag1)
        mTag2 = parent.findViewById(R.id.hybrid_card_tag2)
        mTag3 = parent.findViewById(R.id.hybrid_card_tag3)
        mTag4 = parent.findViewById(R.id.hybrid_card_tag4)
        mTag5 = parent.findViewById(R.id.hybrid_card_tag5)

        mMessage = parent.findViewById(R.id.hybrid_card_message)
        mShare = parent.findViewById(R.id.hybrid_card_share)
    }
}