package com.huawei.hime.util.viewHolders

import android.content.Intent
import android.view.View
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.database.DatabaseReference
import com.huawei.hime.R
import com.huawei.hime.ui.activities.SingleTagActivity
import com.huawei.hime.models.SingleUserInterestType
import com.huawei.hime.ui.presenters.ViewHolderContract
import com.huawei.hime.ui.presenters.ViewHolderPresenter
import com.huawei.hime.util.InterestAdapter
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.views.expandExplanation
import com.huawei.hime.util.views.expandView
import com.squareup.picasso.Picasso
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import de.hdodenhof.circleimageview.CircleImageView
import translate.HmsGmsITranslator

class EventViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), ViewHolderContract {

	val parent : View = itemView

	private var mSingleLayout : LinearLayout
	private var mPosterCircle : CircleImageView
	private var mPosterName : TextView
	private var mPostFooter : TextView

	private var mEventTime : TextView

	private var mTag1 : TextView
	private var mTag2 : TextView
	private var mTag3 : TextView
	private var mTag4 : TextView
	private var mTag5 : TextView

	private var mTotalFollowers : TextView

	private var mViewPager : ViewPager
	private var dotsIndicator : DotsIndicator

	private var mHybridLanguage : LinearLayout
	private var mHybridDetect : TextView
	private var mHybridTranslatedText : TextView

	private var mPopularity : TextView

	private var lovelyLyt : LinearLayout
	private var lovely : TextView

	private var mLottieExp : LottieAnimationView

	var mMessage : LinearLayout
	private var mMessageTextView : TextView
	var mShare : TextView

	private var masterInterList : ArrayList<SingleUserInterestType> = ArrayList()
	private var intGrid : GridView? = null
	private var intAdapter : InterestAdapter? = null

	private var masterInterImgColor = intArrayOf(
		R.drawable.ic_inter_music,
		R.drawable.ic_inter_theater,
		R.drawable.ic_inter_cinema,
		R.drawable.ic_inter_game,
		R.drawable.ic_inter_travel,
		R.drawable.ic_inter_sport,
		R.drawable.ic_inter_entertainment,
		R.drawable.ic_inter_education
	)

	private var masterInterImgFade = intArrayOf(
		R.drawable.ic_inter_music_fade,
		R.drawable.ic_inter_theater_fade,
		R.drawable.ic_inter_cinema_fade,
		R.drawable.ic_inter_game_fade,
		R.drawable.ic_inter_travel_fade,
		R.drawable.ic_inter_sport_fade,
		R.drawable.ic_inter_entertainment_fade,
		R.drawable.ic_inter_education_fade
	)

	private val hmsGmsTranslator : HmsGmsITranslator by lazy {
		HmsGmsITranslator(parent.context)
	}

	private val viewHolderPresenter : ViewHolderPresenter by lazy {
		ViewHolderPresenter(
			this,
			hmsGmsTranslator
		)
	}

	fun playHearts(integer : Int, dbRef : DatabaseReference) {
		var lovely = integer
		lovelyLyt.setOnClickListener {
			mLottieExp!!.visibility = View.VISIBLE
			lovely++
			dbRef.child("upload_lovely").setValue(lovely.toString())
		}
	}

	init {
		hmsGmsTranslator.initOnCreate()
		mSingleLayout = parent.findViewById(R.id.single_post_card_event_layout)
		mPosterCircle = parent.findViewById(R.id.hybrid_card_poster_img)
		mPosterName = parent.findViewById(R.id.hybrid_card_poster)
		mPostFooter = parent.findViewById(R.id.hybrid_card_footer)

		mEventTime = parent.findViewById(R.id.event_time)

		mLottieExp = parent.findViewById(R.id.posted_exp_lovely_lottie)
		dotsIndicator = parent.findViewById(R.id.events_dots)

		mTag1 = parent.findViewById(R.id.hybrid_card_tag1)
		mTag2 = parent.findViewById(R.id.hybrid_card_tag2)
		mTag3 = parent.findViewById(R.id.hybrid_card_tag3)
		mTag4 = parent.findViewById(R.id.hybrid_card_tag4)
		mTag5 = parent.findViewById(R.id.hybrid_card_tag5)

		mTotalFollowers = parent.findViewById(R.id.hybrid_post_followers)

		mViewPager = parent.findViewById(R.id.hybrid_events_pager)

		mHybridLanguage = parent.findViewById(R.id.hybrid_post_change_language)
		mHybridDetect = parent.findViewById(R.id.hybrid_detected_language)
		mHybridTranslatedText = parent.findViewById(R.id.hybrid_translated_text)

		mPopularity = parent.findViewById(R.id.hybrid_card_popularity)

		lovelyLyt = parent.findViewById(R.id.hybrid_card_lovely_lyt)
		lovely = parent.findViewById(R.id.hybrid_card_lovely)

		mMessage = parent.findViewById(R.id.hybrid_card_message)
		mMessageTextView = parent.findViewById(R.id.hybrid_card_total_message)
		mShare = parent.findViewById(R.id.hybrid_card_share)

		intGrid = parent.findViewById(R.id.events_grid)
	}

	fun bindInterests(interests : String) {

		populateInterestsGrid(interests)
		setTooltip()
	}

	private fun setTooltip() {
		intGrid!!.setOnItemClickListener { _, view, position, _ ->
			when (position) {
				0 -> TooltipCompat.setTooltipText(view, "Music")
				1 -> TooltipCompat.setTooltipText(view, "Theatre")
				2 -> TooltipCompat.setTooltipText(view, "Cinema")
				3 -> TooltipCompat.setTooltipText(view, "Game")
				4 -> TooltipCompat.setTooltipText(view, "Travel")
				5 -> TooltipCompat.setTooltipText(view, "Sports")
				6 -> TooltipCompat.setTooltipText(view, "Entertainment")
				7 -> TooltipCompat.setTooltipText(view, "Education")
			}
		}
	}

	private fun populateInterestsGrid(interestString : String) {
		val charArray : CharArray = interestString.toCharArray()

		if (charArray[0] == '1') {
			val inter0 = SingleUserInterestType()
			inter0.imgIcon = masterInterImgColor[0]
			inter0.imgName = "Music"
			masterInterList.add(inter0)
		} else {
			val inter0 = SingleUserInterestType()
			inter0.imgIcon = masterInterImgFade[0]
			inter0.imgName = "Music"
			masterInterList.add(inter0)
		}

		if (charArray[1] == '1') {
			val inter1 = SingleUserInterestType()
			inter1.imgIcon = masterInterImgColor[1]
			inter1.imgName = "Theatre"
			masterInterList.add(inter1)
		} else {
			val inter1 = SingleUserInterestType()
			inter1.imgIcon = masterInterImgFade[1]
			inter1.imgName = "Theatre"
			masterInterList.add(inter1)
		}

		if (charArray[2] == '1') {
			val inter2 = SingleUserInterestType()
			inter2.imgIcon = masterInterImgColor[2]
			inter2.imgName = "Cinema"
			masterInterList.add(inter2)
		} else {
			val inter2 = SingleUserInterestType()
			inter2.imgIcon = masterInterImgFade[2]
			inter2.imgName = "Cinema"
			masterInterList.add(inter2)
		}

		if (charArray[3] == '1') {
			val inter3 = SingleUserInterestType()
			inter3.imgIcon = masterInterImgColor[3]
			inter3.imgName = "Game"
			masterInterList.add(inter3)
		} else {
			val inter3 = SingleUserInterestType()
			inter3.imgIcon = masterInterImgFade[3]
			inter3.imgName = "Game"
			masterInterList.add(inter3)
		}

		if (charArray[4] == '1') {
			val inter4 = SingleUserInterestType()
			inter4.imgIcon = masterInterImgColor[4]
			inter4.imgName = "Travel"
			masterInterList.add(inter4)
		} else {
			val inter4 = SingleUserInterestType()
			inter4.imgIcon = masterInterImgFade[4]
			inter4.imgName = "Travel"
			masterInterList.add(inter4)
		}

		if (charArray[5] == '1') {
			val inter5 = SingleUserInterestType()
			inter5.imgIcon = masterInterImgColor[5]
			inter5.imgName = "Sports"
			masterInterList.add(inter5)
		} else {
			val inter5 = SingleUserInterestType()
			inter5.imgIcon = masterInterImgFade[5]
			inter5.imgName = "Sports"
			masterInterList.add(inter5)
		}

		if (charArray[6] == '1') {
			val inter6 = SingleUserInterestType()
			inter6.imgIcon = masterInterImgColor[6]
			inter6.imgName = "Entertainment"
			masterInterList.add(inter6)
		} else {
			val inter6 = SingleUserInterestType()
			inter6.imgIcon = masterInterImgFade[6]
			inter6.imgName = "Entertainment"
			masterInterList.add(inter6)
		}

		if (charArray[7] == '1') {
			val inter7 = SingleUserInterestType()
			inter7.imgIcon = masterInterImgColor[7]
			inter7.imgName = "Education"
			masterInterList.add(inter7)
		} else {
			val inter7 = SingleUserInterestType()
			inter7.imgIcon = masterInterImgFade[7]
			inter7.imgName = "Education"
			masterInterList.add(inter7)
		}

		//masterInterList.add(inter)
		intAdapter = InterestAdapter(masterInterList, parent.context.applicationContext)
		intGrid?.adapter = intAdapter

	}

	fun bindProfile(image:String, name:String){
		Picasso.get().load(image).fit().centerCrop().into(mPosterCircle)
		mPosterName.text=name
	}

	fun bindByProfileUserType(coverImage : String,
	                 photoImage : String,
	                 e0 : String,
	                 e1 : String,
	                 e2 : String,
	                 e3 : String,
	                 e4 : String,
	                 eventTime : String,
	                 postFooter : String,
	                 postLovely : String){
		/* Pager Magic */
		mMessage.visibility=View.GONE
		val imageUrls = ArrayList<String>()
		if (coverImage.isNotEmpty()) imageUrls.add(coverImage)
		if (photoImage.isNotEmpty()) imageUrls.add(photoImage)
		if (e0.isNotEmpty()) imageUrls.add(e0)
		if (e1.isNotEmpty()) imageUrls.add(e1)
		if (e2.isNotEmpty()) imageUrls.add(e2)
		if (e3.isNotEmpty()) imageUrls.add(e3)
		if (e4.isNotEmpty()) imageUrls.add(e4)
		val adapter = EventViewPagerAdapter(parent.context, imageUrls)
		mViewPager.adapter = adapter
		dotsIndicator.setViewPager(mViewPager)
		mEventTime.text = eventTime
		mPostFooter.text = postFooter
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

	fun bindEvents(
		coverImage : String,
		photoImage : String,
		e0 : String,
		e1 : String,
		e2 : String,
		e3 : String,
		e4 : String,
		eventTime : String,
		postFooter : String,
		postLovely : String,
		popularity : String,
		posterName : String,
		posterImage : String
	) {
		/* Pager Magic */
		val imageUrls = ArrayList<String>()
		if (coverImage.isNotEmpty()) imageUrls.add(coverImage)
		if (photoImage.isNotEmpty()) imageUrls.add(photoImage)
		if (e0.isNotEmpty()) imageUrls.add(e0)
		if (e1.isNotEmpty()) imageUrls.add(e1)
		if (e2.isNotEmpty()) imageUrls.add(e2)
		if (e3.isNotEmpty()) imageUrls.add(e3)
		if (e4.isNotEmpty()) imageUrls.add(e4)
		val adapter = EventViewPagerAdapter(parent.context, imageUrls)
		mViewPager.adapter = adapter
		dotsIndicator.setViewPager(mViewPager)
		mEventTime.text = eventTime

		mPosterName.text = posterName
		Picasso.get().load(posterImage)
			.placeholder(R.drawable.splachback).into(mPosterCircle)
		val pPop = NumberConvertor.prettyCount(popularity.toLong())
		mPopularity.text = pPop
		mPostFooter.text = postFooter
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

	fun setFollowers(string : String) {
		val pFollow = NumberConvertor.prettyCount(string.toLong())
		mTotalFollowers.text = pFollow
	}

	fun setTag1(string : String) {
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

	fun setTag2(string : String) {
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

	fun setTag3(string : String) {
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

	fun setTag4(string : String) {
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

	fun setTag5(string : String) {
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

	fun bindTotalMessage(string : String) {
		val pTotal = NumberConvertor.prettyCount(string.toLong())
		mMessageTextView.text = pTotal
	}

	override fun detect() {

	}
}