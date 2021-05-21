package com.huawei.hime.postmessageunder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.huawei.hime.R
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.ui.presenters.ViewHolderContract
import com.huawei.hime.ui.presenters.ViewHolderPresenter
import com.huawei.hime.util.views.expandView
import de.hdodenhof.circleimageview.CircleImageView
import translate.HmsGmsITranslator

class PostMessageUnderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
	ViewHolderContract {

    val parent: View = itemView

    val mCommenterLayout: LinearLayout

    private var mCommenterCircle: CircleImageView
    private var mCommenterName: TextView
    private var mCommentTime: TextView
    private var mComment: TextView

    private var mHybridLanguage: LinearLayout
    private var mHybridDetect: TextView
    private var mHybridTranslatedText: TextView

    var mLottieAnimationView: LottieAnimationView

    val mLovely: TextView

    private val hmsGmsTranslator: HmsGmsITranslator by lazy {
        HmsGmsITranslator(parent.context)
    }

    private val viewHolderPresenter: ViewHolderPresenter by lazy {
        ViewHolderPresenter(
            this,
            hmsGmsTranslator
        )
    }

    fun bindComments(
        circleImage: String,
        name: String,
        time: String,
        item: String,
        lovely: String
    ) {
        Glide.with(parent.context.applicationContext).load(circleImage).into(mCommenterCircle)
        mCommenterName.text = name
        mCommentTime.text = time
        mComment.text = item
        val pLovely = NumberConvertor.prettyCount(lovely.toLong())
        mLovely.text = pLovely

        mHybridLanguage.setOnClickListener {
            mHybridTranslatedText.visibility = View.VISIBLE
            hmsGmsTranslator.detectLanguage(item, mHybridDetect, mHybridTranslatedText)
        }
        mHybridTranslatedText.setOnClickListener {
            mHybridTranslatedText.expandView()
        }
    }

    init {
        hmsGmsTranslator.initOnCreate()
        mCommenterLayout = parent.findViewById(R.id.single_bottom_text_sender_layout)

        mCommenterCircle = parent.findViewById(R.id.single_bottom_text_sender_circle)
        mCommenterName = parent.findViewById(R.id.single_bottom_text_sender_name)
        mCommentTime = parent.findViewById(R.id.single_bottom_text_get_time_ago)
        mComment = parent.findViewById(R.id.single_bottom_text)

        mHybridLanguage = parent.findViewById(R.id.hybrid_post_change_language)
        mHybridDetect = parent.findViewById(R.id.hybrid_detected_language)
        mHybridTranslatedText = parent.findViewById(R.id.post_translated_txt)

        mLottieAnimationView = parent.findViewById(R.id.lottie_post_lovely)

        mLovely = parent.findViewById(R.id.single_bottom_lovely)
    }

    override fun detect() {

    }
}