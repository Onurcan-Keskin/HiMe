package com.huawei.hime.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.Constants
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.masterfollowers.masterU.MasterPFollowerPagerAdapter
import com.huawei.hime.ui.presenters.MasterProfileFollowerPresenter
import com.huawei.hime.masterfollowers.masterU.followers.FollowersFragment
import com.huawei.hime.masterfollowers.masterU.following.FollowingFragment
import com.huawei.hime.ui.interfaces.IMasterProfileFollower
import com.huawei.hime.util.LocaleHelper
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.SharedPreferencesManager
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_master_profile_follower.*

class MasterProfileFollowerActivity : AppCompatActivity(), IMasterProfileFollower.ViewMasterProfileFollower {

    private var userID: String? = null
    private lateinit var mDatabaseRef: DatabaseReference

    private val lFollower = FollowersFragment()
    private val lFollowing = FollowingFragment()

    private var followers: String? = null
    private var following: String? = null
    private var type: String? = null


    private lateinit var sharedPrefs: SharedPreferencesManager

    private val masterProfileFollowerPresenter: MasterProfileFollowerPresenter by lazy {
        MasterProfileFollowerPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

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
        setContentView(R.layout.activity_master_profile_follower)
        Slidr.attach(this)
        masterProfileFollowerPresenter.onViewsCreate()
    }

    override fun initDB() {
        userID = intent.getStringExtra("userID")
        mDatabaseRef = FirebaseDBHelper.getUserInfo(userID!!)
        mDatabaseRef.keepSynced(true)

        mDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val name = p0.child("nameSurname").value.toString()
                if (AppUser.getUserId() == userID)
                    single_user_name.text = getString(R.string.me,name)
                else
                    single_user_name.text = name
            }
        })
    }

    override fun setupViewPager() {
        type = if (intent.getStringExtra("type") != null)
            intent.getStringExtra("type")
        else
            "followers"

        followers = if (intent.getStringExtra("followers") != null)
            intent.getStringExtra("followers")
        else
            ""

        following = if (intent.getStringExtra("following") != null)
            intent.getStringExtra("following")
        else
            ""

        val pFollowers = NumberConvertor.prettyCount(followers!!.toLong())
        val pFollowing = NumberConvertor.prettyCount(following!!.toLong())
        val adapterL = MasterPFollowerPagerAdapter(supportFragmentManager)
        adapterL.addFragment(lFollower, "$pFollowers " + getString(R.string.followers))
        adapterL.addFragment(lFollowing, "$pFollowing " + getString(R.string.following))
        viewFollowerContainer.adapter = adapterL
        follower_bottom_app_bar.setupWithViewPager(viewFollowerContainer)

        when (type) {
            "following" -> viewFollowerContainer.currentItem = 1
            "followers" -> viewFollowerContainer.currentItem = 0
        }
    }

    fun getUID(): String {
        return userID!!
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
}
