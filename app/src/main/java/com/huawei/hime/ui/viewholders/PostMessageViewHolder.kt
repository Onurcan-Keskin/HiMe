package com.huawei.hime.postmessage

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

class PostMessageViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView),
	ViewHolderContract {

	val parent : View = itemView

	val mCommenterLayout : LinearLayout
	val mViewReply : LinearLayout
	private var mCommenterCircle : CircleImageView
	private var mCommenterName : TextView
	private var mCommentTime : TextView
	private var mComment : TextView

	var mLottieAnimationView : LottieAnimationView

	private var mHybridLanguage : LinearLayout
	private var mHybridDetect : TextView
	private var mHybridTranslatedText : TextView

	private var totalUnder : TextView

	val mReply : TextView
	val mLovely : TextView

	private val hmsGmsTranslator : HmsGmsITranslator by lazy {
		HmsGmsITranslator(parent.context)
	}

	private val viewHolderPresenter : ViewHolderPresenter by lazy {
		ViewHolderPresenter(
            this,
            hmsGmsTranslator
        )
	}

	fun bindTotalUnder(string : String) {
		val pTotal = NumberConvertor.prettyCount(string.toLong())
		totalUnder.visibility = View.VISIBLE
		totalUnder.text = pTotal
	}


fun bindComments(
    circleImage : String,
    name : String,
    time : String,
    item : String,
    lovely : String
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
	mViewReply = parent.findViewById(R.id.single_bottom_view_reply)

	mHybridLanguage = parent.findViewById(R.id.hybrid_post_change_language)
	mHybridDetect = parent.findViewById(R.id.hybrid_detected_language)
	mHybridTranslatedText = parent.findViewById(R.id.post_translated_txt)

	totalUnder = parent.findViewById(R.id.totalUnderTV)

	mCommenterCircle = parent.findViewById(R.id.single_bottom_text_sender_circle)
	mCommenterName = parent.findViewById(R.id.single_bottom_text_sender_name)
	mCommentTime = parent.findViewById(R.id.single_bottom_text_get_time_ago)
	mComment = parent.findViewById(R.id.single_bottom_text)

	mLottieAnimationView = parent.findViewById(R.id.lottie_post_lovely)

	mReply = parent.findViewById(R.id.single_bottom_reply)
	mLovely = parent.findViewById(R.id.single_bottom_lovely)

}

override fun detect() {

}
}