package com.huawei.hime.util.viewHolders

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DatabaseReference
import com.huawei.hime.R
import com.huawei.hime.ui.activities.SingleTagActivity
import com.huawei.hime.ui.presenters.ViewHolderContract
import com.huawei.hime.ui.presenters.ViewHolderPresenter
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.views.expandExplanation
import com.huawei.hime.util.views.expandView
import com.klinker.android.simple_videoview.SimpleVideoView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import translate.HmsGmsITranslator

class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ViewHolderContract {

    val parent: View = itemView

    private var videoContainerLayout: FrameLayout? = null
    var videoView: SimpleVideoView? = null

    private var mPosterImg: CircleImageView? = null
    private var mPoster: TextView? = null
    private var mLovelyText: TextView? = null
    var mLovelyLyt: LinearLayout? = null
    private var mPostFooter: TextView? = null
    var mMessage: LinearLayout? = null
    private var mMessageTextView:TextView?=null
    var mShareTxt: TextView? = null
    private var mMaterialCard: MaterialCardView? = null
    var mPostSettings: ImageButton? = null

    var lottiHearth: LottieAnimationView? = null
    var mFullscreen: ImageView? = null

    private var mHybridLanguage: LinearLayout
    private var mHybridDetect: TextView
    private var mHybridTranslatedText: TextView

    private var mPopularity: TextView? = null

    private var mTotalFollower: TextView? = null

    private var mTagText1: TextView? = null
    private var mTagText2: TextView? = null
    private var mTagText3: TextView? = null
    private var mTagText4: TextView? = null
    private var mTagText5: TextView? = null

    private val hmsGmsTranslator: HmsGmsITranslator by lazy {
        HmsGmsITranslator(parent.context)
    }

    private val viewHolderPresenter: ViewHolderPresenter by lazy {
        ViewHolderPresenter(
            this,
            hmsGmsTranslator
        )
    }

    fun setFollowers(string: String) {
        val pFollow= NumberConvertor.prettyCount(string.toLong())
        mTotalFollower!!.text = pFollow
    }

    fun setPosterImage(img: String) {
        if (img != "default" || img.isNotEmpty() || img != "") {
            Picasso.get().load(img).placeholder(R.drawable.splachback).into(mPosterImg!!)
        } else {
            Picasso.get().load(R.drawable.splachback).placeholder(R.drawable.splachback)
                .into(mPosterImg!!)
        }
    }

    fun bindTotalMessage(string: String){
        val pTotal = NumberConvertor.prettyCount(string.toLong())
        mMessageTextView!!.text = pTotal
    }

    fun setPosterName(name: String) {
        if (mPoster.toString().isEmpty() || mPoster.toString() == "" || mPoster.toString()
                .isBlank()
        ) {
            mPoster!!.setText(R.string.himeUser)
        } else {
            mPoster!!.text = name.split(' ').first()
        }
    }

    fun setPopularity(string: String) {
        val pPop= NumberConvertor.prettyCount(string.toLong())
        mPopularity!!.text = pPop
    }

    fun setTag1(string: String) {
        if (mTagText1!!.text.toString().isNotEmpty() && string.isNotEmpty()) {
            mTagText1!!.text = "#" + string
            mTagText1!!.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", string)
                        .putExtra("fid","t")
                )
            }
        } else
            mTagText1!!.visibility = View.GONE
    }

    fun setTag2(string: String) {
        if (mTagText2!!.text.toString().isNotEmpty() && string.isNotEmpty()) {
            mTagText2!!.text = "#" + string
            mTagText2!!.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", string)
                        .putExtra("fid","t")
                )
            }
        } else
            mTagText2!!.visibility = View.GONE
    }

    fun setTag3(string: String) {
        if (mTagText3!!.text.toString().isNotEmpty() && string.isNotEmpty()) {
            mTagText3!!.text = "#" + string
            mTagText3!!.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", string)
                        .putExtra("fid","t")
                )
            }
        } else
            mTagText3!!.visibility = View.GONE
    }

    fun setTag4(string: String) {
        if (mTagText4!!.text.toString().isNotEmpty() && string.isNotEmpty()) {
            mTagText4!!.text = "#" + string
            mTagText4!!.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", string)
                        .putExtra("fid","t")
                )
            }
        } else
            mTagText4!!.visibility = View.GONE
    }

    fun setTag5(string: String) {
        if (mTagText5!!.text.toString().isNotEmpty() && string.isNotEmpty()) {
            mTagText5!!.text = "#" + string
            mTagText5!!.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", string)
                        .putExtra("fid","t")
                )
            }
        } else
            mTagText5!!.visibility = View.GONE
    }

    fun bindVideo(videoUrl: String) {
        videoView!!.start(Uri.parse(videoUrl))
        videoView!!.setOnClickListener {
            if (videoView!!.isPlaying) {
                videoView!!.pause()
            } else {
                videoView!!.play()
            }
        }
    }

    fun setLovely(string: String) {
        val pLovely= NumberConvertor.prettyCount(string.toLong())
        mLovelyText!!.text = pLovely
    }

    fun setFooter(string: String) {
        mPostFooter!!.text = string
        mPostFooter!!.setOnClickListener {
            mPostFooter!!.expandExplanation()
        }

        mHybridLanguage.setOnClickListener {
            mHybridTranslatedText.visibility = View.VISIBLE
            hmsGmsTranslator.detectLanguage(string, mHybridDetect, mHybridTranslatedText)
        }

        mHybridTranslatedText.setOnClickListener {
            mHybridTranslatedText.expandView()
        }
    }

    fun playHearts(integer:Int, dbRef: DatabaseReference) {
        var lovely = integer
        mLovelyLyt!!.setOnClickListener {
            lottiHearth!!.visibility = View.VISIBLE
            lovely++
            dbRef.child("upload_lovely").setValue(lovely.toString())
        }
    }

    init {
        hmsGmsTranslator.initOnCreate()
        videoContainerLayout = parent.findViewById(R.id.hybrid_videoContainerLayout)
        videoView = parent.findViewById(R.id.hybrid_video_view)

        mMaterialCard = parent.findViewById(R.id.post_card_video_view)
        mPosterImg = parent.findViewById(R.id.hybrid_card_poster_img)
        mPoster = parent.findViewById(R.id.hybrid_card_poster)
        mLovelyText = parent.findViewById(R.id.hybrid_card_lovely)
        mLovelyLyt = parent.findViewById(R.id.hybrid_card_lovely_lyt)
        mPostFooter = parent.findViewById(R.id.hybrid_card_footer)
        mMessage = parent.findViewById(R.id.hybrid_card_message)
        mShareTxt = parent.findViewById(R.id.hybrid_card_share)
        mPostSettings = parent.findViewById(R.id.hybrid_card_settings)

        lottiHearth = parent.findViewById(R.id.posted_exp_lovely_lottie)
        mTotalFollower = parent.findViewById(R.id.hybrid_post_followers)
        mFullscreen = parent.findViewById(R.id.hybrid_card_video_fullscreen)
        mPopularity = parent.findViewById(R.id.hybrid_card_popularity)

        mHybridLanguage = parent.findViewById(R.id.hybrid_post_change_language)
        mHybridDetect = parent.findViewById(R.id.hybrid_detected_language)
        mHybridTranslatedText = parent.findViewById(R.id.hybrid_translated_text)

        mTagText1 = parent.findViewById(R.id.hybrid_card_tag1)
        mTagText2 = parent.findViewById(R.id.hybrid_card_tag2)
        mTagText3 = parent.findViewById(R.id.hybrid_card_tag3)
        mTagText4 = parent.findViewById(R.id.hybrid_card_tag4)
        mTagText5 = parent.findViewById(R.id.hybrid_card_tag5)

        mLovelyLyt!!.isClickable = true
        mLovelyLyt!!.isFocusable = true
        mPostSettings!!.visibility = View.VISIBLE
        mMessage!!.visibility = View.VISIBLE
        mPostSettings!!.visibility = View.INVISIBLE
        mHybridTranslatedText.visibility = View.GONE
    }

    override fun detect() {

    }
}