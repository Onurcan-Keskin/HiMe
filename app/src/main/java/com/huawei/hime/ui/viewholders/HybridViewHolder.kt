package com.huawei.hime.util.viewHolders

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.*
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
import com.klinker.android.simple_videoview.SimpleVideoView
import com.squareup.picasso.Picasso
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import de.hdodenhof.circleimageview.CircleImageView
import translate.HmsGmsITranslator

class HybridViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView),
	ViewHolderContract {

	val parent : View = itemView

	private var mPosterImage : CircleImageView
	private var mPosterName : TextView
	private var mTotalFollowers : TextView
	private var mTotalPopularity : TextView
	private var mLovely : TextView
	private var mFooter : TextView
	private var mLottieLovely : LottieAnimationView

	/* Hybrid Holder -> Map + Photo */
	private var mHybridImageHolder : ImageView
	private var mHybridAddressTag : TextView

	private var mHybridLanguage : LinearLayout
	private var mHybridDetect : TextView
	private var mHybridTranslatedText : TextView

	/* Hybrid Holder -> Video */
	private var mFrameLayout : FrameLayout
	private var mHybridVideoView : SimpleVideoView
	var mFullscreen : ImageView

	/* Hybrid Holder -> UI Controller */
	private var mLovelyLayout : LinearLayout
	private var mShare : TextView
	var mMessage : LinearLayout
	private var mMessageTextView : TextView

	/* Hybrid Holder -> Event */
	private var masterInterList : ArrayList<SingleUserInterestType> = ArrayList()
	private var intGrid : GridView? = null
	private var intAdapter : InterestAdapter? = null
	var mViewPager : ViewPager
	private var dotsIndicator : DotsIndicator
	private var mEventTime : TextView

	/* Hybrid Holder -> Tag*/
	private var mTag1 : TextView
	private var mTag2 : TextView
	private var mTag3 : TextView
	private var mTag4 : TextView
	private var mTag5 : TextView

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

	fun giveLovely(integer : Int, dbRef : DatabaseReference) {
		var lovely = integer
		mLovelyLayout.setOnClickListener {
			mLottieLovely.visibility = View.VISIBLE
			lovely++
			dbRef.child("upload_lovely").setValue(lovely.toString())
		}
	}

	fun shareImageContent(string : String) {
		mShare.setOnClickListener {
			val uri = Uri.parse(string)
			Picasso.get().load(uri).into(object : com.squareup.picasso.Target {
				override fun onBitmapLoaded(bitmap : Bitmap?, from : Picasso.LoadedFrom?) {
					val sharingIntent = Intent(Intent.ACTION_SEND)
					sharingIntent.type = "image/*"
					sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
					parent.context.startActivity(
						Intent.createChooser(
							sharingIntent,
							"Share HiMe - Image"
						)
					)
				}

				override fun onBitmapFailed(e : Exception?, errorDrawable : Drawable?) {}

				override fun onPrepareLoad(placeHolderDrawable : Drawable?) {}
			})
		}
	}

	fun shareMapContent(latitude : String, longitude : String) {
		mShare.setOnClickListener {
			val uri = "https://share.here.com/l/$latitude,$longitude?z=13&t=satellite&p=no"
			val sharingIntent = Intent(Intent.ACTION_SEND)
			sharingIntent.type = "text/plain"
			sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Share HiMe - Travel")
			sharingIntent.putExtra(Intent.EXTRA_TEXT, uri)
			parent.context.startActivity(Intent.createChooser(sharingIntent, "Share through HiMe"))
		}
	}

	init {
		hmsGmsTranslator.initOnCreate()
		mPosterImage = parent.findViewById(R.id.hybrid_card_poster_img)
		mPosterName = parent.findViewById(R.id.hybrid_card_poster)
		mTotalFollowers = parent.findViewById(R.id.hybrid_post_followers)
		mTotalPopularity = parent.findViewById(R.id.hybrid_card_popularity)
		mLovely = parent.findViewById(R.id.hybrid_card_lovely)
		mFooter = parent.findViewById(R.id.hybrid_card_footer)
		mLottieLovely = parent.findViewById(R.id.hybrid_lottie_exp)

		mHybridImageHolder = parent.findViewById(R.id.hybrid_image_holder)
		mHybridAddressTag = parent.findViewById(R.id.hybrid_card_address)

		mEventTime = parent.findViewById(R.id.event_time)

		mHybridLanguage = parent.findViewById(R.id.hybrid_post_change_language)
		mHybridTranslatedText = parent.findViewById(R.id.hybrid_translated_text)
		mHybridDetect = parent.findViewById(R.id.hybrid_detected_language)

		mFrameLayout = parent.findViewById(R.id.hybrid_videoContainerLayout)
		mHybridVideoView = parent.findViewById(R.id.hybrid_video_view)
		mFullscreen = parent.findViewById(R.id.hybrid_card_video_fullscreen)

		mLovelyLayout = parent.findViewById(R.id.hybrid_card_lovely_lyt)
		mShare = parent.findViewById(R.id.hybrid_card_share)
		mMessage = parent.findViewById(R.id.hybrid_card_message)
		mMessageTextView = parent.findViewById(R.id.hybrid_card_total_message)

		mTag1 = parent.findViewById(R.id.hybrid_card_tag1)
		mTag2 = parent.findViewById(R.id.hybrid_card_tag2)
		mTag3 = parent.findViewById(R.id.hybrid_card_tag3)
		mTag4 = parent.findViewById(R.id.hybrid_card_tag4)
		mTag5 = parent.findViewById(R.id.hybrid_card_tag5)

		mViewPager = parent.findViewById(R.id.hybrid_events_pager)
		dotsIndicator = parent.findViewById(R.id.events_dots)
		intGrid = parent.findViewById(R.id.events_grid)

		mHybridTranslatedText.visibility = View.GONE
		mLottieLovely.visibility = View.GONE
		mViewPager.visibility = View.GONE
		dotsIndicator.visibility = View.GONE
	}

	fun bindGlobalValues(
		posterName : String,
		posterImage : String,
		popularity : String,
		lovely : String,
		footer : String,
		tag1 : String,
		tag2 : String,
		tag3 : String,
		tag4 : String,
		tag5 : String
	) {
		val pPop = NumberConvertor.prettyCount(popularity.toLong())
		mTotalPopularity.text = pPop
		val pLovely = NumberConvertor.prettyCount(lovely.toLong())
		mLovely.text = pLovely
		mFooter.text = footer

		mFooter.setOnClickListener {
			mFooter.expandExplanation()
		}

		mHybridLanguage.setOnClickListener {
			mHybridTranslatedText.visibility = View.VISIBLE
			hmsGmsTranslator.detectLanguage(footer, mHybridDetect, mHybridTranslatedText)
		}

		mHybridTranslatedText.setOnClickListener {
			mHybridTranslatedText.expandView()
		}

		if (mPosterName.text != null || mPosterName.text != "")
			mPosterName.text = posterName
		else
			mPosterName.text = parent.context.getString(R.string.himeUser)

		Picasso.get().load(posterImage)
			.placeholder(R.drawable.splachback).into(mPosterImage)

		/* Tag */
		if (mTag1.text.toString().isNotEmpty() && tag1.isNotEmpty()) {
			mTag1.text = "#" + tag1
			mTag1.setOnClickListener {
				parent.context.startActivity(
					Intent(
						parent.context.applicationContext,
						SingleTagActivity::class.java
					).putExtra("tag", tag1)
						.putExtra("fid", "t")
				)
			}
		} else
			mTag1.visibility = View.GONE
		if (mTag2.text.toString().isNotEmpty() && tag2.isNotEmpty()) {
			mTag2.text = "#" + tag2
			mTag2.setOnClickListener {
				parent.context.startActivity(
					Intent(
						parent.context.applicationContext,
						SingleTagActivity::class.java
					).putExtra("tag", tag2)
						.putExtra("fid", "t")
				)
			}
		} else
			mTag2.visibility = View.GONE
		if (mTag3.text.toString().isNotEmpty() && tag3.isNotEmpty()) {
			mTag3.text = "#" + tag3
			mTag3.setOnClickListener {
				parent.context.startActivity(
					Intent(
						parent.context.applicationContext,
						SingleTagActivity::class.java
					).putExtra("tag", tag3)
						.putExtra("fid", "t")
				)
			}
		} else
			mTag3.visibility = View.GONE
		if (mTag4.text.toString().isNotEmpty() && tag4.isNotEmpty()) {
			mTag4.text = "#" + tag4
			mTag4.setOnClickListener {
				parent.context.startActivity(
					Intent(
						parent.context.applicationContext,
						SingleTagActivity::class.java
					).putExtra("tag", tag4)
						.putExtra("fid", "t")
				)
			}
		} else
			mTag4.visibility = View.GONE
		if (mTag5.text.toString().isNotEmpty() && tag5.isNotEmpty()) {
			mTag5.text = "#" + tag5
			mTag5.setOnClickListener {
				parent.context.startActivity(
					Intent(
						parent.context.applicationContext,
						SingleTagActivity::class.java
					).putExtra("tag", tag5)
						.putExtra("fid", "t")
				)
			}
		} else
			mTag5.visibility = View.GONE
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

	fun bindTotalMessage(string : String) {
		val pTotal = NumberConvertor.prettyCount(string.toLong())
		mMessageTextView.text = pTotal
	}

	fun bindFollowers(followers : String) {
		val pFollow = NumberConvertor.prettyCount(followers.toLong())
		mTotalFollowers.text = pFollow
	}

	fun bindImage(holderImage : String) {
		mHybridImageHolder.visibility = View.VISIBLE
		mHybridAddressTag.visibility = View.GONE
		mFrameLayout.visibility = View.GONE
		mHybridVideoView.visibility = View.GONE
		mFullscreen.visibility = View.INVISIBLE
		intGrid!!.visibility = View.GONE
		mViewPager.visibility = View.GONE
		dotsIndicator.visibility = View.GONE

		Picasso.get().load(holderImage).fit().centerCrop()
			.into(mHybridImageHolder)
	}

	fun bindEvents(
		coverImage : String,
		photoImage : String,
		e0:String,
		e1:String,
		e2:String,
		e3:String,
		e4:String,
		eventTime : String
	) {
		mHybridImageHolder.visibility = View.GONE
		mHybridAddressTag.visibility = View.GONE
		mFrameLayout.visibility = View.GONE
		mHybridVideoView.visibility = View.GONE
		mFullscreen.visibility = View.INVISIBLE
		intGrid!!.visibility = View.VISIBLE
		mViewPager.visibility = View.VISIBLE
		dotsIndicator.visibility = View.VISIBLE

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
	}

	fun bindMap(
		latitude : String,
		longitude : String,
		address : String,
		holderImage : String
	) {
		mHybridImageHolder.visibility = View.VISIBLE
		mHybridAddressTag.visibility = View.VISIBLE
		mFrameLayout.visibility = View.GONE
		mHybridVideoView.visibility = View.GONE
		mFullscreen.visibility = View.INVISIBLE
		intGrid!!.visibility = View.GONE
		mViewPager.visibility = View.GONE
		dotsIndicator.visibility = View.GONE

		Picasso.get().load(holderImage).fit().centerCrop()
			.into(mHybridImageHolder)
		mHybridAddressTag.text = address
	}

	fun bindVideo(videoUrl : String) {
		mHybridImageHolder.visibility = View.GONE
		mHybridAddressTag.visibility = View.GONE
		mFrameLayout.visibility = View.VISIBLE
		mHybridVideoView.visibility = View.VISIBLE
		mFullscreen.visibility = View.VISIBLE
		intGrid!!.visibility = View.GONE
		mViewPager.visibility = View.GONE
		dotsIndicator.visibility = View.GONE

		mHybridVideoView.start(Uri.parse(videoUrl))
		mHybridVideoView.setOnClickListener {
			if (mHybridVideoView.isPlaying) {
				mHybridVideoView.pause()
			} else {
				mHybridVideoView.play()
			}
		}
	}

	override fun detect() {

	}
}