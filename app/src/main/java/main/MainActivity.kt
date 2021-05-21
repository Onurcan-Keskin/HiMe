package main

import ads.HmsGmsAdsHelper
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.dev.adnetworkm.CheckNetworkStatus
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.discover.DiscoverFragment
import com.huawei.hime.findfollower.FindFollowerFragment
import com.huawei.hime.fresh.FreshFragment
import com.huawei.hime.ui.activities.LiveStreamMainActivity
import com.huawei.hime.ui.activities.MapActivity
import com.huawei.hime.ui.activities.ProfileActivity
import com.huawei.hime.ui.activities.SearchActivity
import com.huawei.hime.trending.TrendingFragment
import com.huawei.hime.util.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity(), MainContract {

    private lateinit var mStorageRef: StorageReference
    private lateinit var currentUID: String
    private lateinit var mDatabaseRef: DatabaseReference
    private lateinit var mCircle: CircleImageView
    private lateinit var mTabs: TabLayout
    private lateinit var mAnimatedBottomBar: AnimatedBottomBar
    private val currentID = AppUser.getUserId()
    private var mOnlineDBRef = FirebaseDBHelper.getUser(currentID)
    private var context: Context? = null


    private lateinit var mAdsView: View

    private lateinit var sharedPrefs: SharedPreferencesManager

    private val hmsGmsAdsHelper: HmsGmsAdsHelper by lazy {
        HmsGmsAdsHelper(this)
    }

    private val mainPresenter: MainPresenter by lazy {
        MainPresenter(this, hmsGmsAdsHelper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        context = applicationContext!!
        sharedPrefs = SharedPreferencesManager(this)
        /* Mode State */
        if (sharedPrefs.loadNightModeState())
            setTheme(R.style.DarkMode)
        else
            setTheme(R.style.LightMode)
        /* Language State */
        if (sharedPrefs.loadLanguage()=="tr")
            LocaleHelper.setLocale(this, "tr")
        else
            LocaleHelper.setLocale(this, "en")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        mainPresenter.onViewCreate()

        context = applicationContext

        tbar.setOnClickListener {
            mainPresenter.onMainBannerClick()
        }

        mainPresenter.showAds()

        mAdsView.setOnClickListener {
            hmsGmsAdsHelper.adsClicked()
        }

        CheckNetworkStatus.getNetworkLiveData(applicationContext).observe(this, Observer { t ->
            when (t) {
                true -> {
                    Log.d("Internet Observer", "true")
                    slideUpGone(internet_status_text)
                }
                false -> {
                    Log.d("Internet Observer", "false")
                    slideDownVisible(internet_status_text)
                }
                null -> {
                    Log.d("Internet Observer", "null")
                    slideUpVisible(internet_status_text)
                }
            }
        })

        if (sharedPrefs.loadNightModeState()) {
            ts_input_layout.background = getDrawable(R.drawable.ic_fragment_dark)
            tabs_rltv.setBackgroundResource(R.drawable.ic_banner_dark)
            main_bottom_app_bar.setBackgroundResource(R.drawable.dark_mask)
        } else {
            ts_input_layout.background = getDrawable(R.drawable.ic_fragment_background)
        }

        main_bottom_app_bar.onTabSelected = {
            Log.d("bottom_bar", "Selected tab: " + it.title)
        }
        main_bottom_app_bar.onTabReselected = {
            Log.d("bottom_bar", "Reselected tab: " + it.title)
        }

        main_bottom_app_bar.iconSize = 75

        main_bottom_app_bar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                val fragment = supportFragmentManager

                Log.d("bottom_bar", "Selected index: $newIndex, title: ${newTab.title}")
                when (newIndex) {
                    0 -> {
                        //Home Activity
                        if (sharedPrefs.loadNightModeState()) {
                            tabs_rltv.setBackgroundResource(R.drawable.ic_banner_dark)
                            main_bottom_app_bar.setBackgroundResource(R.drawable.dark_mask)
                        } else {
                            tabs_rltv.setBackgroundResource(R.drawable.ic_banner_red)
                            main_bottom_app_bar.setBackgroundResource(R.drawable.red_mask)
                        }
                    }
                    1 -> {
                        // Travel Activity
                        if (sharedPrefs.loadNightModeState()) {
                            tabs_rltv.setBackgroundResource(R.drawable.ic_banner_dark)
                            main_bottom_app_bar.setBackgroundResource(R.drawable.dark_mask)
                        } else {
                            tabs_rltv.setBackgroundResource(R.drawable.ic_banner_yellow)
                            main_bottom_app_bar.setBackgroundResource(R.drawable.yellow_mask)
                        }
                        //mainPresenter.onMapItemClick()
                    }
                    2 -> {
                        //Live Chat
                        if (sharedPrefs.loadNightModeState()) {
                            tabs_rltv.setBackgroundResource(R.drawable.ic_banner_dark)
                            main_bottom_app_bar.setBackgroundResource(R.drawable.dark_mask)
                        } else {
                            tabs_rltv.setBackgroundResource(R.drawable.ic_banner_purple)
                            main_bottom_app_bar.setBackgroundResource(R.drawable.purple_mask)
                        }
                        mainPresenter.onShutterItemClick()
                    }
                    3 -> {
                        //Event Activity
                        if (sharedPrefs.loadNightModeState()) {
                            tabs_rltv.setBackgroundResource(R.drawable.ic_banner_dark)
                            main_bottom_app_bar.setBackgroundResource(R.drawable.dark_mask)
                        } else {
                            tabs_rltv.setBackgroundResource(R.drawable.ic_banner_orange)
                            main_bottom_app_bar.setBackgroundResource(R.drawable.orange_mask)
                        }
                    }
                    4 -> {
                        //Video Activity
                        if (sharedPrefs.loadNightModeState()) {
                            tabs_rltv.setBackgroundResource(R.drawable.ic_banner_dark)
                            main_bottom_app_bar.setBackgroundResource(R.drawable.dark_mask)
                        } else {
                            tabs_rltv.setBackgroundResource(R.drawable.ic_banner_blue)
                            main_bottom_app_bar.setBackgroundResource(R.drawable.blue_mask)
                        }
                        //startActivity(Intent(this@MainActivity, VideoActivity::class.java))
                    }
                    5 -> {
                        //Search Activity
                        if (sharedPrefs.loadNightModeState()) {
                            tabs_rltv.setBackgroundResource(R.drawable.ic_banner_dark)
                            main_bottom_app_bar.setBackgroundResource(R.drawable.dark_mask)
                        } else {
                            tabs_rltv.setBackgroundResource(R.drawable.ic_banner_search)
                            main_bottom_app_bar.setBackgroundResource(R.drawable.search_mask)
                        }
                        startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                    }
                }
            }

            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {
                Log.d("bottom_bar", "Reselected index: $index, title: ${tab.title}")
            }
        })

        mainPresenter.displayTuto(this, mCircle, mTabs, mAnimatedBottomBar)
    }

    fun onlineHash() {
        try {

        } catch (e: Exception) {
            showLogError("onlineHash", e.message!!)
        }
    }

    override fun setUpViewPager() {
        val adapterL = MainFragmentPagerAdapter(supportFragmentManager)
        adapterL.addFragment(DiscoverFragment(), getString(R.string.frag_discover))
        adapterL.addFragment(FreshFragment(), getString(R.string.frag_fresh))
        adapterL.addFragment(TrendingFragment(), getString(R.string.frag_trend))
        adapterL.addFragment(FindFollowerFragment(), getString(R.string.frag_find_hime_rs))
        main_pager.adapter = adapterL
        ts_input_layout.setupWithViewPager(main_pager)
    }

    override fun initViews() {
        mTabs = findViewById(R.id.ts_input_layout)
        mAnimatedBottomBar = findViewById(R.id.main_bottom_app_bar)
        mStorageRef = FirebaseStorage.getInstance().reference
        currentUID = AppUser.getUserId()
        mDatabaseRef = FirebaseDBHelper.getUserInfo(currentUID)
        mDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var stat: String = p0.child("stat").value.toString()

                if (stat.isEmpty() || stat.isBlank()) {
                    stat = getString(R.string.hi_status)
                    main_txt_status.text = stat
                } else {
                    main_txt_status.text = stat
                }
            }
        })
        mDatabaseRef.keepSynced(true)

        mCircle = findViewById(R.id.main_circle_img)
        mAdsView = findViewById(R.id.bannerView)
    }

    override fun populateViewWithDB() {
        mDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val name = p0.child("nameSurname").value.toString()
                val image = p0.child("photoUrl").value.toString()

                if (name == "") {
                    main_txt_name.text = getString(R.string.himeUser)
                }
                main_txt_name.text =
                    getString(R.string.main_hello) + " " + name.split(' ').first()

                if (image != "default") {
                    Picasso.get().load(image)
                        .placeholder(R.drawable.profilecircleicon)
                        .into(mCircle)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        FirebaseDBHelper.onConnect(mOnlineDBRef)
    }

    override fun onPause() {
        super.onPause()
        FirebaseDBHelper.onDisconnect(mOnlineDBRef)
    }

    override fun onStop() {
        super.onStop()
        FirebaseDBHelper.onDisconnect(mOnlineDBRef)
    }

    override fun gotoProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    override fun mapItemClick() {
        startActivity(Intent(this, MapActivity::class.java))
    }

    override fun shutterItemClick() {
        startActivity(Intent(this, LiveStreamMainActivity::class.java))
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

}
