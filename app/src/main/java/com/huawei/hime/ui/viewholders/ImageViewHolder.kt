package com.huawei.hime.util.viewHolders

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hime.R
import com.huawei.hime.ui.activities.SingleTagActivity
import com.huawei.hime.ui.presenters.ViewHolderContract
import com.huawei.hime.ui.presenters.ViewHolderPresenter
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.views.expandExplanation
import com.huawei.hime.util.views.expandView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import translate.HmsGmsITranslator

class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ViewHolderContract {

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
    private var mTotalFollowers: TextView

    private var mHybridLanguage: LinearLayout
    private var mHybridDetect: TextView
    private var mHybridTranslatedText: TextView

    private var mPopularity: TextView

    var mMessage: LinearLayout
    private var mMessageTextView: TextView
    var mShare: TextView

    private val hmsGmsTranslator: HmsGmsITranslator by lazy {
        HmsGmsITranslator(parent.context)
    }

    private val viewHolderPresenter: ViewHolderPresenter by lazy {
        ViewHolderPresenter(
            this,
            hmsGmsTranslator
        )
    }

    fun bindImage(
        posterCircle: String,
        posterName: String,
        post: String,
        postFooter: String,
        postLovely: String,
        popularity: String
    ) {
        Picasso.get().load(posterCircle)
            .placeholder(R.drawable.splachback).into(mPosterCircle)
        mPosterName.text = posterName
        Picasso.get().load(post).into(mPost)
        mPostFooter.text = postFooter
        val pPop = NumberConvertor.prettyCount(popularity.toLong())
        mPopularity.text = pPop
        mPostFooter.setOnClickListener {
            mPostFooter.expandExplanation()
        }
        mHybridLanguage.setOnClickListener {
            mHybridTranslatedText.visibility = View.VISIBLE
            hmsGmsTranslator.detectLanguage(postFooter, mHybridDetect, mHybridTranslatedText)
        }
        mHybridTranslatedText.setOnClickListener {
            mHybridTranslatedText.expandView()
        }
        val pLovely = NumberConvertor.prettyCount(postLovely.toLong())
        lovely.text = pLovely
    }

    fun bindTotalMessage(string: String) {
        val pTotal = NumberConvertor.prettyCount(string.toLong())
        mMessageTextView.text = pTotal
    }

    fun setTag1(string: String) {
        if (mTag1.text.toString().isNotEmpty() && string.isNotEmpty()) {
            mTag1.text = "#$string"
            mTag1.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", string)
                        .putExtra("fid", "t")
                )
            }
        } else
            mTag1.visibility = View.GONE
    }

    fun setTag2(string: String) {
        if (mTag2.text.toString().isNotEmpty() && string.isNotEmpty()) {
            mTag2.text = "#$string"
            mTag2.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", string)
                        .putExtra("fid", "t")
                )
            }
        } else
            mTag2.visibility = View.GONE
    }

    fun setTag3(string: String) {
        if (mTag3.text.toString().isNotEmpty() && string.isNotEmpty()) {
            mTag3.text = "#$string"
            mTag3.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", string)
                        .putExtra("fid", "t")
                )
            }
        } else
            mTag3.visibility = View.GONE
    }

    fun setTag4(string: String) {
        if (mTag4.text.toString().isNotEmpty() && string.isNotEmpty()) {
            mTag4.text = "#$string"
            mTag4.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", string)
                        .putExtra("fid", "t")
                )
            }
        } else
            mTag4.visibility = View.GONE
    }

    fun setTag5(string: String) {
        if (mTag5.text.toString().isNotEmpty() && string.isNotEmpty()) {
            mTag5.text = "#$string"
            mTag5.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", string)
                        .putExtra("fid", "t")
                )
            }
        } else
            mTag5.visibility = View.GONE
    }

    fun setFollowers(string: String) {
        val pFollow = NumberConvertor.prettyCount(string.toLong())
        mTotalFollowers.text = pFollow
    }

    init {
        hmsGmsTranslator.initOnCreate()
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

        mPopularity = parent.findViewById(R.id.hybrid_card_popularity)
        mTotalFollowers = parent.findViewById(R.id.hybrid_post_followers)

        mHybridLanguage = parent.findViewById(R.id.hybrid_post_change_language)
        mHybridDetect = parent.findViewById(R.id.hybrid_detected_language)
        mHybridTranslatedText = parent.findViewById(R.id.hybrid_translated_text)

        mMessage = parent.findViewById(R.id.hybrid_card_message)
        mMessageTextView = parent.findViewById(R.id.hybrid_card_total_message)
        mShare = parent.findViewById(R.id.hybrid_card_share)

        mHybridTranslatedText.visibility = View.GONE
    }

    override fun detect() {

    }
}