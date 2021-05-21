package com.huawei.hime.ui.activities

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.databinding.ActivityEventAdderBinding
import com.huawei.hime.helpers.Constants
import com.huawei.hime.ui.fragments.CalendarFragment
import com.huawei.hime.ui.fragments.CaptionFragment
import com.huawei.hime.ui.fragments.EventPickCoverFragment
import com.huawei.hime.ui.fragments.EventPickPhotoFragment
import com.huawei.hime.ui.fragments.PlacesFragment
import com.huawei.hime.profile.profileevent.takePhoto.EventTakePhotoFragment
import com.huawei.hime.util.*
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.profile.profileevent.EventAdderContract
import com.huawei.hime.profile.profileevent.EventAdderPresenter
import com.ramotion.circlemenu.CircleMenuView
import kotlinx.android.synthetic.main.activity_event_adder.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class EventAdderActivity : AppCompatActivity(), EventAdderContract,
	CalendarFragment.CalendarOnDataPass,
	CaptionFragment.CaptionOnDataPass,
	EventTakePhotoFragment.TakePhotoOnDataPass,
	EventPickCoverFragment.CoverOnDataClass,
	EventPickPhotoFragment.PickPhotoOnDataPass {

	internal lateinit var binding : ActivityEventAdderBinding

	private lateinit var mpMenuAppear : MediaPlayer
	private lateinit var mpMenuClose : MediaPlayer
	private lateinit var mpMenuOpen : MediaPlayer
	private lateinit var mpMenuItemSelect : MediaPlayer

	private lateinit var feedDBRef : DatabaseReference
	private var pKey : String = ""
	private var inter : String = "00000000"

	private val feedMap : MutableMap<String, String> = HashMap()
	private val uploadMap : MutableMap<String, String> = HashMap()
	private val tagMap : MutableMap<String, String> = HashMap()
	private val mapTag : MutableMap<String, Any> = HashMap()

	private val eventAdderPresenter : EventAdderPresenter by lazy {
		EventAdderPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		val sharedPrefs = SharedPreferencesManager(this)
		if (sharedPrefs.loadNightModeState()) {
			setTheme(R.style.DarkMode)
		} else {
			setTheme(R.style.LightMode)
		}
		super.onCreate(savedInstanceState)
		binding = ActivityEventAdderBinding.inflate(layoutInflater)
		setContentView(R.layout.activity_event_adder)
		setSupportActionBar(binding.homeToolbar)
		//Slidr.attach(this)
		eventAdderPresenter.onViewsCreate()

//		if (calendarOKLottie.visibility == View.INVISIBLE
//			|| captionOKLottie.visibility == View.INVISIBLE
//			|| coverOKLottie.visibility == View.INVISIBLE
//			|| takeOKLottie.visibility == View.INVISIBLE) {
//			save_event.visibility = View.GONE
//		} else {
		save_event.setOnClickListener {
			saveEvent()
			finish()
			showToast(this, getString(R.string.prompt_save_success))
		}

		val fragment = supportFragmentManager
		val fCalendar = CalendarFragment()
		val fCaption = CaptionFragment()
		val fPlaces = PlacesFragment()
		val fTakePhoto = EventTakePhotoFragment()
		val fPickPhoto = EventPickPhotoFragment()
		val fCover = EventPickCoverFragment()

		calendarViewCard.setOnClickListener {
			mpMenuAppear.start()
			fragment.beginTransaction().run {
				return@run setCustomAnimations(
					R.anim.slide_in_up,
					R.anim.fade_out,
					R.anim.fade_in,
					R.anim.slide_out_down
				)
			}
				.add(R.id.eventFrame, fCalendar, fragment.javaClass.simpleName)
				.addToBackStack(fragment.javaClass.name)
				.commit()
		}

		captionViewCard.setOnClickListener {
			mpMenuAppear.start()
			fragment.beginTransaction().run {
				return@run setCustomAnimations(
					R.anim.slide_in_up,
					R.anim.fade_out,
					R.anim.fade_in,
					R.anim.slide_out_down
				)
			}
				.add(R.id.eventFrame, fCaption, fragment.javaClass.simpleName)
				.addToBackStack(fragment.javaClass.name)
				.commit()
		}

		galleryViewCard.setOnClickListener {
			showLogDebug("Clicked: Gallery", "success")
			galleryMenu.visibility = View.VISIBLE
			galleryMenu.bringToFront()
			mpMenuAppear.start()
			galleryMenu.eventListener = object : CircleMenuView.EventListener() {
				override fun onMenuOpenAnimationStart(view : CircleMenuView) {
					super.onMenuOpenAnimationStart(view)
					mpMenuOpen.start()
				}

				override fun onMenuCloseAnimationStart(view : CircleMenuView) {
					super.onMenuCloseAnimationStart(view)
					mpMenuClose.start()
				}

				override fun onMenuCloseAnimationEnd(view : CircleMenuView) {
					super.onMenuCloseAnimationEnd(view)
					galleryMenu.visibility = View.GONE
				}

				override fun onButtonClickAnimationStart(view : CircleMenuView, buttonIndex : Int) {
					super.onButtonClickAnimationStart(view, buttonIndex)
					when (buttonIndex) {
						0 -> {
							mpMenuItemSelect.start()
							/* Take Photo */
							fragment.beginTransaction().run {
								return@run setCustomAnimations(
									R.anim.slide_in_up,
									R.anim.fade_out,
									R.anim.fade_in,
									R.anim.slide_out_down
								)
							}
								.add(R.id.eventFrame, fTakePhoto, fragment.javaClass.simpleName)
								.addToBackStack(fragment.javaClass.name)
								.commit()
						}
						1 -> {
							mpMenuItemSelect.start()
							fragment.beginTransaction().run {
								return@run setCustomAnimations(
									R.anim.slide_in_up,
									R.anim.fade_out,
									R.anim.fade_in,
									R.anim.slide_out_down
								)
							}
								.add(
									R.id.eventFrame,
									fPickPhoto,
									fragment.javaClass.simpleName
								)
								.addToBackStack(fragment.javaClass.name)
								.commit()
						}
						2 -> {
							mpMenuItemSelect.start()
							/* Add Cover */
							fragment.beginTransaction().run {
								return@run setCustomAnimations(
									R.anim.slide_in_up,
									R.anim.fade_out,
									R.anim.fade_in,
									R.anim.slide_out_down
								)
							}
								.add(R.id.eventFrame, fCover, fragment.javaClass.simpleName)
								.addToBackStack(fragment.javaClass.name)
								.commit()
						}
						else -> showLogError("CircleMenuView", "when index exception.")
					}
				}

				override fun onButtonClickAnimationEnd(view : CircleMenuView, buttonIndex : Int) {
					super.onButtonClickAnimationEnd(view, buttonIndex)
					galleryMenu.visibility = View.GONE
				}

				override fun onButtonLongClick(view : CircleMenuView, buttonIndex : Int) : Boolean {
					when (buttonIndex) {
						0 -> {
							/* Take Photo */
							showSnackbar(eventAdderConstraint, getString(R.string.take_photo))
						}
						1 -> {
							/* Pick Photo */
							showSnackbar(
								eventAdderConstraint,
								getString(R.string.pick_photos_from_gallery)
							)
						}
						2 -> {
							showSnackbar(eventAdderConstraint, getString(R.string.add_cover))
						}
					}
					return super.onButtonLongClick(view, buttonIndex)
				}

			}
		}

		placesViewCard.setOnClickListener {
			mpMenuAppear.start()
			fragment.beginTransaction().run {
				return@run setCustomAnimations(
					R.anim.slide_in_up,
					R.anim.fade_out,
					R.anim.fade_in,
					R.anim.slide_out_down
				)
			}
				.add(R.id.eventFrame, fPlaces, fragment.javaClass.simpleName)
				.addToBackStack(fragment.javaClass.name)
				.commit()
		}
	}

	override fun initViews() {
		mpMenuAppear = MediaPlayer.create(this, R.raw.menu_appear)
		mpMenuClose = MediaPlayer.create(this, R.raw.menu_close)
		mpMenuOpen = MediaPlayer.create(this, R.raw.menu_open)
		mpMenuItemSelect = MediaPlayer.create(this, R.raw.menu_item_click)


		feedDBRef = FirebaseDBHelper.getEventFeedRef(AppUser.getUserId())
		pKey = feedDBRef.push().key.toString()
		val dbUser = FirebaseDBHelper.getUserInfo(AppUser.getUserId())
		dbUser.addValueEventListener(object : ValueEventListener {
			override fun onDataChange(p0 : DataSnapshot) {
				inter = p0.child("interests").value.toString()
				uploadMap["upload_posterImage"] = p0.child("photoUrl").value.toString()
				uploadMap["upload_posterName"] = p0.child("nameSurname").value.toString()
				uploadMap["uploader_interests"] = inter
			}

			override fun onCancelled(p0 : DatabaseError) {}
		})
	}

	override fun onCalendarDataPass(calendarTime : String, startTime : String, endTime : String) {
		playLottie(calendarOKLottie)
		calendarTV.text = calendarTime
		feedMap["event_time"] = calendarTime
		feedMap["event_start_time"] = startTime
		feedMap["event_end_time"] = endTime

		uploadMap["upload_event_time"] = calendarTime
		uploadMap["upload_event_start_time"] = startTime
		uploadMap["upload_event_end_time"] = endTime
	}

	override fun onPickPhotoDataPass(list : ArrayList<String>) {
		pickOKLottie.visibility=View.VISIBLE
		pickOKLottie.playAnimation()
		pk.visibility = View.VISIBLE
		var i = 0
		while (i < list.size) {
			feedMap["event_picked$i"] = list[i]
			uploadMap["upload_event_picked$i"] = list[i]
			i++
		}
	}

	override fun onCaptionDataPass(
		caption : String,
		tag1 : String,
		tag2 : String,
		tag3 : String,
		tag4 : String,
		tag5 : String,
		interByte : String
	) {
		playLottie(captionOKLottie)
		showLogInfo(
			mTag, "$caption\n" +
					"$tag1\n" +
					"$tag2\n" +
					"$tag3\n" +
					"$tag4\n" +
					"$tag5\n" +
					interByte
		)
		val t1 = tag1.replace(" ", "_").toLowerCase(Locale.ROOT)
		val t2 = tag2.replace(" ", "_").toLowerCase(Locale.ROOT)
		val t3 = tag3.replace(" ", "_").toLowerCase(Locale.ROOT)
		val t4 = tag4.replace(" ", "_").toLowerCase(Locale.ROOT)
		val t5 = tag5.replace(" ", "_").toLowerCase(Locale.ROOT)

		tagMap["uploaderID"] = AppUser.getUserId()
		if (t1.length > 1)
			mapTag["Tag/$t1/$pKey/Uploader"] = tagMap
		if (t2.length > 1)
			mapTag["Tag/$t2/$pKey/Uploader"] = tagMap
		if (t3.length > 1)
			mapTag["Tag/$t3/$pKey/Uploader"] = tagMap
		if (t4.length > 1)
			mapTag["Tag/$t4/$pKey/Uploader"] = tagMap
		if (t5.length > 1)
			mapTag["Tag/$t5/$pKey/Uploader"] = tagMap

		feedMap["footer"] = caption
		feedMap["tag1"] = t1
		feedMap["tag2"] = t2
		feedMap["tag3"] = t3
		feedMap["tag4"] = t4
		feedMap["tag5"] = t5

		if (interByte.isEmpty()) {
			feedMap["event_interest"] = inter
			uploadMap["upload_event_interest"] = inter
		} else {
			feedMap["event_interest"] = interByte
			uploadMap["upload_event_interest"] = interByte
		}

		uploadMap["upload_footer"] = caption
		uploadMap["tag1"] = t1
		uploadMap["tag2"] = t2
		uploadMap["tag3"] = t3
		uploadMap["tag4"] = t4
		uploadMap["tag5"] = t5
		uploadMap["upload_event_interest"] = interByte

	}

	override fun onTakePhotoDataPass(imageUrl : String) {
		takeOKLottie.visibility = View.VISIBLE
		takeOKLottie.playAnimation()
		cm.visibility = View.VISIBLE
		feedMap["event_photoUrl"] = imageUrl
		uploadMap["upload_event_photoUrl"] = imageUrl
		showLogInfo(mTag, imageUrl)
	}

	override fun onCoverDataClass(coverUrl : String) {
		coverOKLottie.visibility = View.VISIBLE
		coverOKLottie.playAnimation()
		pt.visibility = View.VISIBLE
		feedMap["event_coverUrl"] = coverUrl
		uploadMap["upload_event_coverUrl"] = coverUrl
		showLogInfo(mTag, coverUrl)
	}


	private fun playLottie(lottieAnimationView : LottieAnimationView) {
		lottieAnimationView.visibility = View.VISIBLE
		lottieAnimationView.playAnimation()
		save_event.visibility = View.VISIBLE
	}

	override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		for (fragment in supportFragmentManager.fragments) {
			fragment.onActivityResult(requestCode, resultCode, data)
		}
	}

	private fun saveEvent() {
		val eventFeedRef = "User Feed/Events/${AppUser.getUserId()}"
		val eventUploadRef = "uploads/Shareable"
		val mapFeed : MutableMap<String, Any> = HashMap()
		val mapUpload : MutableMap<String, Any> = HashMap()

		val timeDate = DateFormat.getDateTimeInstance().format(Date())
		val timeMillis = System.currentTimeMillis().toString()

		/* Feed */
		feedMap["upload_millis"] = timeMillis
		feedMap["upload_date"] = timeDate
		feedMap["commentsAllowed"] = "1"
		feedMap["shareStat"] = "1"
		feedMap["event_lovely"] = "0"
		mapFeed["$eventFeedRef/$pKey"] = feedMap
		FirebaseDBHelper.rootRef().updateChildren(mapFeed)

		/* Upload */
		uploadMap["upload_millis"] = timeMillis
		uploadMap["upload_date"] = timeDate
		uploadMap["commentsAllowed"] = "1"
		uploadMap["shareStat"] = "1"
		uploadMap["typeGroup"] = "2"
		uploadMap["uploaderID"] = AppUser.getUserId()
		uploadMap["uploadType"] = "event"
		uploadMap["upload_lovely"] = "0"
		uploadMap["popularity"] = "0"
		mapUpload["$eventUploadRef/$pKey"] = uploadMap
		FirebaseDBHelper.rootRef().updateChildren(mapUpload)

		FirebaseDBHelper.rootRef().updateChildren(mapTag)
	}

	override fun onStart() {
		super.onStart()
		FirebaseDBHelper.onConnect(Constants.fOnlineDBRef)
	}

	override fun onPause() {
		super.onPause()
		FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
	}

	override fun onStop() {
		super.onStop()
		FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
	}

	companion object {
		private const val mTag = "EventAdderActivity"
	}
}

