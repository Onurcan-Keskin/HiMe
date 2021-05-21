package com.huawei.hime.ui.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.media.MediaPlayer
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.Constants
import com.huawei.hime.profile.profileevent.ProfileEventFragment
import com.huawei.hime.profile.profilegallery.ProfileGalleryFragment
import com.huawei.hime.profile.profileinbox.ProfileInboxFragment
import com.huawei.hime.profile.profilemain.ProfileFragment
import com.huawei.hime.profile.profiletravel.ProfileTravelFragment
import com.huawei.hime.util.LocaleHelper
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.models.ImageData
import com.huawei.hime.profile.ProfileContract
import com.huawei.hime.profile.ProfilePresenter
import com.huawei.hime.profile.WheelImageAdapter
import com.huawei.hime.util.slideDownGone
import com.huawei.hime.util.slideUpVisible
import com.r0adkll.slidr.Slidr
import github.hellocsl.cursorwheel.CursorWheelLayout
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.bottom_sheet_settings.*
import java.util.*

class ProfileActivity : AppCompatActivity(), CursorWheelLayout.OnMenuSelectedListener,
	ProfileContract {

	var wheelImage : CursorWheelLayout? = null
	var lstImage : List<ImageData>? = null

	private lateinit var bottomSheetVideoBehavior : BottomSheetBehavior<*>

	private lateinit var mUserFeedReference : DatabaseReference
	private lateinit var mStorageRef : StorageReference

	private lateinit var context : Context
	private var currentLang = "en"

	private var age : String = ""

	private lateinit var privacy : String

	private lateinit var sharedPrefs : SharedPreferencesManager

	private val profilePresenter : ProfilePresenter by lazy {
		ProfilePresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
        context = applicationContext!!
		sharedPrefs = SharedPreferencesManager(this)
		/* Mode State */
		if (sharedPrefs.loadNightModeState())
			setTheme(R.style.DarkMode)
		else
			setTheme(R.style.LightMode)
		/* Language State */
		if (sharedPrefs.loadLanguage()=="tr")
			LocaleHelper.setLocale(context, "tr")
		else
			LocaleHelper.setLocale(context, "en")

		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_profile)
		supportActionBar?.hide()
		//loadLocale()

		profilePresenter.onViewsCreate()

		context = applicationContext

		Slidr.attach(this)
		wheelImage!!.setOnMenuSelectedListener(this)
		setUpSheets()
		initDB()

		val mpMenuClose = MediaPlayer.create(this, R.raw.menu_close)
		val mpMenuOpen = MediaPlayer.create(this, R.raw.menu_open)

		profile_settings.setOnClickListener {
			menuGetSettingsPopup()
		}

		lang_tr_txt.setOnClickListener {
			//currentLang = "tr"
			//LocaleHelper.setLocale(this, currentLang)
			sharedPrefs.setLanguage("tr")
			LocaleHelper.setLocale(this, "tr")
			restartApp()
		}
		lang_en_txt.setOnClickListener {
			//currentLang = "en"
			//LocaleHelper.setLocale(this, currentLang)
			sharedPrefs.setLanguage("en")
			LocaleHelper.setLocale(this, "en")
			restartApp()
		}


		if (sharedPrefs.loadNightModeState()) {
			dayNight.isChecked = true
			bottom_sheet_relative.background = ContextCompat.getDrawable(this,R.drawable.cut_banner_dark_)
			dayNight.text = getString(R.string.night)
		} else {
			bottom_sheet_relative.background = ContextCompat.getDrawable(this,R.drawable.ic_cut_banner)
		}
		dayNight.setOnCheckedChangeListener { _, isChecked ->
			if (isChecked) {
				sharedPrefs.setNightModeState(true)
				dayNight.text = getString(R.string.day)
				restartApp()
			} else {
				sharedPrefs.setNightModeState(false)
				restartApp()
			}
		}

		wheel_view.setOnClickListener {
			if (circle_menu.visibility == View.VISIBLE) {
				//circle_menu.visibility = View.GONE
				mpMenuClose.start()
				slideDownGone(circle_menu)
				wheel_view.setImageResource(R.drawable.circle_yes)
			} else if (circle_menu.visibility == View.GONE) {
				//circle_menu.visibility = View.VISIBLE
				mpMenuOpen.start()
				slideUpVisible(circle_menu)
				wheel_view.setImageResource(R.drawable.circle_no)
			}
		}

		setupSpinner()
	}

	override fun restartApp() {
		startActivity(Intent(applicationContext, ProfileActivity::class.java))
		finish()
	}

	fun setLocale(localeName : String) {
		val mylocale = Locale(localeName)
		val res : Resources = resources
		val dm : DisplayMetrics = res.displayMetrics
		val config : Configuration = res.configuration
		config.locale = mylocale
		res.updateConfiguration(config, dm)
		val refresh = Intent(this, ProfileActivity::class.java)
		refresh.putExtra(currentLang, localeName)
		startActivity(refresh)
	}

	override fun loadData() {
		lstImage = ArrayList<ImageData>()
		(lstImage as ArrayList<ImageData>).add(
            ImageData(
                R.drawable.profilecircleiconn,
                "Profile"
            )
        )
		(lstImage as ArrayList<ImageData>).add(ImageData(R.drawable.eventcircleicon, "Event"))
		(lstImage as ArrayList<ImageData>).add(
            ImageData(
                R.drawable.messagecircleicon,
                "Message"
            )
        )
		(lstImage as ArrayList<ImageData>).add(ImageData(R.drawable.travelcircleicon, "Travel"))
		(lstImage as ArrayList<ImageData>).add(ImageData(R.drawable.videocircleicon, "Video"))
		(lstImage as ArrayList<ImageData>).add(
            ImageData(
                R.drawable.profilecircleiconn,
                "Profile"
            )
        )
		(lstImage as ArrayList<ImageData>).add(ImageData(R.drawable.eventcircleicon, "Event"))
		(lstImage as ArrayList<ImageData>).add(
            ImageData(
                R.drawable.messagecircleicon,
                "Message"
            )
        )
		(lstImage as ArrayList<ImageData>).add(ImageData(R.drawable.travelcircleicon, "Travel"))
		(lstImage as ArrayList<ImageData>).add(ImageData(R.drawable.videocircleicon, "Video"))
		val adapter = WheelImageAdapter(baseContext, (lstImage as ArrayList<ImageData>))
		wheelImage!!.setAdapter(adapter)
	}

	override fun initViews() {
		wheelImage = findViewById<View>(R.id.circle_menu) as CursorWheelLayout
	}

	override fun menuGetSettingsPopup() {
		bottom_sheet_settings_inc.bringToFront()
		if (sharedPrefs.loadNightModeState())
			getDarkPopup()
		else
			getLightPopup()
	}

	override fun getDarkPopup() {
		val layoutInflater =
			(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)) as LayoutInflater
		val popUpView = layoutInflater.inflate(R.layout.popup_settings_dark, null)

		val settingItem = popUpView.findViewById<LinearLayout>(R.id.menu_settings_item)
		val privacyItem = popUpView.findViewById<LinearLayout>(R.id.menu_privacy_item)

		val popupWindow = PopupWindow(
            popUpView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
		popupWindow.isOutsideTouchable = true
		settingItem.setOnClickListener {
			popupWindow.dismiss()
			bottomSheetVideoBehavior.state = BottomSheetBehavior.STATE_EXPANDED
			tryToGetAppVersion()
		}

		privacyItem.setOnClickListener {

		}

		popupWindow.showAsDropDown(profile_settings, 5, -120)
	}

	override fun getLightPopup() {
		val layoutInflater =
			(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)) as LayoutInflater
		val popUpView = layoutInflater.inflate(R.layout.popup_settings_light, null)

		val settingItem = popUpView.findViewById<LinearLayout>(R.id.menu_settings_item)
		val privacyItem = popUpView.findViewById<LinearLayout>(R.id.menu_privacy_item)

		val popupWindow = PopupWindow(
            popUpView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
		popupWindow.isOutsideTouchable = true
		settingItem.setOnClickListener {
			popupWindow.dismiss()
			bottomSheetVideoBehavior.state = BottomSheetBehavior.STATE_EXPANDED
			tryToGetAppVersion()
		}

		privacyItem.setOnClickListener {

		}

		popupWindow.showAsDropDown(profile_settings, 5, -120)
	}

	override fun setUpSheets() {
		bottomSheetVideoBehavior = BottomSheetBehavior.from(bottom_sheet_settings_inc)
		bottomSheetVideoBehavior.isHideable = true
		bottomSheetVideoBehavior.state = BottomSheetBehavior.STATE_HIDDEN
	}

	override fun setupSpinner() {
		age = prof_txt_age.toString()
		ArrayAdapter.createFromResource(
            this,
            R.array.ageSpinner,
            R.layout.spinner_text
        ).also { adapter ->
			adapter.setDropDownViewResource(R.layout.spinner_drop_down)
			prof_age_spinner.adapter = adapter

			prof_age_spinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
				AdapterView.OnItemSelectedListener {
				override fun onNothingSelected(parent : AdapterView<*>?) {}

				override fun onItemSelected(
                    parent : AdapterView<*>?,
                    view : View?,
                    position : Int,
                    id : Long
                ) {
					age = parent!!.getItemAtPosition(position).toString()
					Constants.fUserInfoRef.child("age").setValue(age)
				}

				override fun onItemClick(
                    parent : AdapterView<*>?,
                    view : View?,
                    position : Int,
                    id : Long
                ) {

				}
			}
		}
	}

	override fun tryToGetAppVersion() {
		try {
			val info = context.packageManager.getPackageInfo(packageName, 0)
			val version = info.versionName
			bottom_sheet_settings_app_version.text =
				getString(R.string.app_version) + " " + version
		} catch (e : PackageManager.NameNotFoundException) {
			e.printStackTrace()
		}
	}

	override fun initDB() {
		mStorageRef = FirebaseStorage.getInstance().reference
		mUserFeedReference = FirebaseDBHelper.getProfileFeed(AppUser.getUserId())

		Constants.fUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {

            }

            override fun onDataChange(p0 : DataSnapshot) {
                age = p0.child("age").value.toString()
                privacy = p0.child("privacy").value.toString()
                showLogs("Privacy: $privacy")
                val photoViewAttacher = PhotoViewAttacher(changing_prof_image)
                profilePresenter.setPrivacyLight(
                    privacy, changing_prof_image
                )
                profilePresenter.checkedPrivacy(
                    hide_message,
                    hide_memory,
                    hide_shares,
                    hide_followers,
                    privacy,
	                Constants.fUserInfoRef,
                    changing_prof_image,
                    context
                )

            }
        })

		Constants.fUserInfoRef.keepSynced(true)
	}

	private fun showLogs(msg : String) {
		Log.d(mTAG, msg)
	}

	override fun onItemSelected(parent : CursorWheelLayout?, view : View?, pos : Int) {
		val fragment : FragmentManager = supportFragmentManager

		val fProfile = ProfileFragment()
		val fVideo = ProfileGalleryFragment()
		val fInbox = ProfileInboxFragment()
		val fTravel = ProfileTravelFragment()
		val fEvent = ProfileEventFragment()

		if (parent!!.id == R.id.circle_menu) {
			//mpClick.start()
			showLogs("selected:" + (lstImage?.get(pos)?.imageDescription))
		}
		if (lstImage?.get(pos)?.imageDescription.equals("Profile")) {
			if (sharedPrefs.loadNightModeState()) {
				profileFrameLayout.setBackgroundResource(R.drawable.ic_back_bubble_dark)
			} else
				profileFrameLayout.setBackgroundResource(R.drawable.ic_back_bubble_purple)
			fragment.beginTransaction().replace(R.id.profileFrameLayout, fProfile)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit()
		}
		if (lstImage?.get(pos)?.imageDescription.equals("Event")) {
			if (sharedPrefs.loadNightModeState()) {
				profileFrameLayout.setBackgroundResource(R.drawable.ic_back_bubble_dark)
			} else
				profileFrameLayout.setBackgroundResource(R.drawable.ic_back_bubble_orange)
			fragment.beginTransaction().replace(R.id.profileFrameLayout, fEvent)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit()
		}
		if (lstImage?.get(pos)?.imageDescription.equals("Message")) {
			if (sharedPrefs.loadNightModeState()) {
				profileFrameLayout.setBackgroundResource(R.drawable.ic_back_bubble_dark)
			} else
				profileFrameLayout.setBackgroundResource(R.drawable.ic_back_bubble_red)
			fragment.beginTransaction().replace(R.id.profileFrameLayout, fInbox)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit()
		}
		if (lstImage?.get(pos)?.imageDescription.equals("Travel")) {
			if (sharedPrefs.loadNightModeState()) {
				profileFrameLayout.setBackgroundResource(R.drawable.ic_back_bubble_dark)
			} else
				profileFrameLayout.setBackgroundResource(R.drawable.ic_back_bubble_yellow)
			fragment.beginTransaction().replace(R.id.profileFrameLayout, fTravel)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit()
		}
		if (lstImage?.get(pos)?.imageDescription.equals("Video")) {
			if (sharedPrefs.loadNightModeState()) {
				profileFrameLayout.setBackgroundResource(R.drawable.ic_back_bubble_dark)
			} else
				profileFrameLayout.setBackgroundResource(R.drawable.ic_back_bubble_blue)
			fragment.beginTransaction().replace(R.id.profileFrameLayout, fVideo)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit()
		}
	}

	override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		for (fragment in supportFragmentManager.fragments) {
			fragment.onActivityResult(requestCode, resultCode, data)
		}
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
		private const val mTAG = "ProfileActivity"
	}
}