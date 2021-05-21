package com.huawei.hime.util.viewHolders

import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.database.DatabaseReference
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

class TravelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ViewHolderContract {

    val parent: View = itemView

    /* UI */
    private var posterName: TextView? = null
    private var posterImage: CircleImageView? = null
    private var postSettings: ImageButton? = null
    private var postLovely: TextView? = null
    private var postShare: TextView? = null
    var postMessage: LinearLayout? = null
    private var mMessageTextView: TextView? = null
    private var postFooter: TextView? = null
    private var postAddress: TextView? = null
    private var postedMapHolder: ImageView? = null

    private var mLottieExp: LottieAnimationView?=null
    private var mLovelyLayout:LinearLayout?=null

    private var mTagText1: TextView? = null
    private var mTagText2: TextView? = null
    private var mTagText3: TextView? = null
    private var mTagText4: TextView? = null
    private var mTagText5: TextView? = null

    private var mHybridLanguage: LinearLayout
    private var mHybridDetect: TextView
    private var mHybridTranslatedText: TextView

    private var mPopularity: TextView? = null

    private var mTotalFollower: TextView? = null

    private val hmsGmsTranslator: HmsGmsITranslator by lazy {
        HmsGmsITranslator(parent.context)
    }

    private val viewHolderPresenter: ViewHolderPresenter by lazy {
        ViewHolderPresenter(
            this,
            hmsGmsTranslator
        )
    }

    init {
        hmsGmsTranslator.initOnCreate()
        posterName = parent.findViewById(R.id.hybrid_card_poster)
        posterImage = parent.findViewById(R.id.hybrid_card_poster_img)
        postSettings = parent.findViewById(R.id.hybrid_card_settings)
        postLovely = parent.findViewById(R.id.hybrid_card_lovely)
        postShare = parent.findViewById(R.id.hybrid_card_share)
        postMessage = parent.findViewById(R.id.hybrid_card_message)
        mMessageTextView = parent.findViewById(R.id.hybrid_card_total_message)
        postFooter = parent.findViewById(R.id.hybrid_card_footer)
        postAddress = parent.findViewById(R.id.hybrid_card_address)
        postedMapHolder = parent.findViewById(R.id.posted_map_holder)

        mLottieExp = parent.findViewById(R.id.posted_exp_lovely_lottie)
        mLovelyLayout = parent.findViewById(R.id.hybrid_card_lovely_lyt)

        mTotalFollower = parent.findViewById(R.id.hybrid_post_followers)
        mPopularity = parent.findViewById(R.id.hybrid_card_popularity)

        mHybridLanguage = parent.findViewById(R.id.hybrid_post_change_language)
        mHybridDetect = parent.findViewById(R.id.hybrid_detected_language)
        mHybridTranslatedText = parent.findViewById(R.id.hybrid_translated_text)

        mTagText1 = parent.findViewById(R.id.hybrid_card_tag1)
        mTagText2 = parent.findViewById(R.id.hybrid_card_tag2)
        mTagText3 = parent.findViewById(R.id.hybrid_card_tag3)
        mTagText4 = parent.findViewById(R.id.hybrid_card_tag4)
        mTagText5 = parent.findViewById(R.id.hybrid_card_tag5)

        postShare!!.visibility = View.VISIBLE
        postMessage!!.visibility = View.VISIBLE
        postSettings!!.visibility = View.INVISIBLE
        mHybridTranslatedText.visibility = View.GONE
        mLottieExp!!.visibility = View.GONE
    }

    fun setFollowers(string: String) {
        val pFollow = NumberConvertor.prettyCount(string.toLong())
        mTotalFollower!!.text = pFollow
    }

    fun shareMapContent(latitude: String, longitude: String){
        postShare!!.setOnClickListener {
            val uri = "https://share.here.com/l/$latitude,$longitude?z=13&t=satellite&p=no"
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Share HiMe - Travel")
            sharingIntent.putExtra(Intent.EXTRA_TEXT,uri)
            parent.context.startActivity(Intent.createChooser(sharingIntent,"Share through HiMe"))
        }
    }

    fun bindTotalMessage(string: String) {
        val pTotal = NumberConvertor.prettyCount(string.toLong())
        mMessageTextView!!.text = pTotal
    }

    fun playHearts(integer:Int, dbRef: DatabaseReference) {
        var lovely = integer
       mLovelyLayout!!.setOnClickListener {
            mLottieExp!!.visibility = View.VISIBLE
            lovely++
            dbRef.child("upload_lovely").setValue(lovely.toString())
        }
    }

    fun bindView(
        mPostFooter: String,
        mLat: String,
        mLong: String,
        mPostAddress: String,
        popularity: String,
        mPosterName: String,
        mPosterImage: String,
        mMapHolder: String,
        mPostTag1: String,
        mPostTag2: String,
        mPostTag3: String,
        mPostTag4: String,
        mPostTag5: String,
        mPostLovely: String
    ) {
        postFooter!!.text = mPostFooter
        postFooter!!.setOnClickListener {
            postFooter!!.expandExplanation()
        }

        mHybridLanguage.setOnClickListener {
            mHybridTranslatedText.visibility = View.VISIBLE
            hmsGmsTranslator.detectLanguage(mPostFooter, mHybridDetect, mHybridTranslatedText)
        }

        mHybridTranslatedText.setOnClickListener {
            mHybridTranslatedText.expandView()
        }

        val pPop = NumberConvertor.prettyCount(popularity.toLong())
        mPopularity!!.text = pPop

        val pLovely = NumberConvertor.prettyCount(mPostLovely.toLong())
        postLovely!!.text = pLovely

        posterName!!.text = mPosterName
        Picasso.get().load(mPosterImage)
            .placeholder(R.drawable.splachback).into(posterImage!!)

        postAddress!!.text = mPostAddress

        if (mMapHolder.isNullOrEmpty()) {
            Picasso.get().load(R.drawable.default_map).fit().centerCrop()
                .into(postedMapHolder!!)
        } else {
            Picasso.get().load(mMapHolder)
                .placeholder(R.drawable.splachback).fit().centerCrop()
                .into(postedMapHolder!!)
        }

        //Tag
        if (mTagText1!!.text.toString().isNotEmpty() && mPostTag1.isNotEmpty()) {
            mTagText1!!.text = "#" + mPostTag1
            mTagText1!!.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", mPostTag1)
                        .putExtra("fid", "t")
                )
            }
        } else
            mTagText1!!.visibility = View.GONE

        if (mTagText2!!.text.toString().isNotEmpty() && mPostTag2.isNotEmpty()) {
            mTagText2!!.text = "#" + mPostTag2
            mTagText2!!.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", mPostTag2)
                        .putExtra("fid", "t")
                )
            }
        } else
            mTagText2!!.visibility = View.GONE

        if (mTagText3!!.text.toString().isNotEmpty() && mPostTag3.isNotEmpty()) {
            mTagText3!!.text = "#" + mPostTag3
            mTagText3!!.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", mPostTag3)
                        .putExtra("fid", "t")
                )
            }
        } else
            mTagText3!!.visibility = View.GONE

        if (mTagText4!!.text.toString().isNotEmpty() && mPostTag4.isNotEmpty()) {
            mTagText4!!.text = "#" + mPostTag4
            mTagText4!!.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", mPostTag4)
                        .putExtra("fid", "t")
                )
            }
        } else
            mTagText4!!.visibility = View.GONE

        if (mTagText5!!.text.toString().isNotEmpty() && mPostTag5.isNotEmpty()) {
            mTagText5!!.text = "#" + mPostTag5
            mTagText5!!.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", mPostTag5)
                        .putExtra("fid", "t")
                )
            }
        } else
            mTagText5!!.visibility = View.GONE
    }

    override fun detect() {

    }
}