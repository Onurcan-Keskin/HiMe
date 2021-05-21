package com.huawei.hime.ui.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.Constants
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.models.SingleUserInterestType
import com.huawei.hime.search.singleuserpage.SingleUserInterestsAdapter
import com.huawei.hime.search.singleuserpage.SingleUserPagerAdapter
import com.huawei.hime.search.singleuserpage.SingleUserPresenter
import com.huawei.hime.search.singleuserpage.event.EventFragment
import com.huawei.hime.search.singleuserpage.gallery.GalleryFragment
import com.huawei.hime.search.singleuserpage.travel.TravelFragment
import com.huawei.hime.ui.interfaces.ISingleUser
import com.huawei.hime.util.FollowTypes
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.ui.presenters.ViewHolderContract
import com.huawei.hime.util.showLogError
import com.r0adkll.slidr.Slidr
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_single_user.*
import kotlinx.android.synthetic.main.single_user_banner.*
import translate.HmsGmsITranslator

class SingleUserActivity : AppCompatActivity(), ISingleUser.ViewSingleUser, ViewHolderContract {

    private lateinit var context: Context

    private val currentID = AppUser.getUserId()

    private lateinit var mDatabaseRefUser: DatabaseReference
    private lateinit var mInterestDBRef: DatabaseReference
    private lateinit var mPrivacyDBRef: DatabaseReference
    private lateinit var mFollowerCounterDB: DatabaseReference
    private lateinit var mFollowingCounterDB: DatabaseReference
    private lateinit var mLovelyCountDBRef: DatabaseReference

    private lateinit var mFollowingSinceDBRef: DatabaseReference

    private lateinit var mUserCircle: CircleImageView
    private lateinit var mMyCircle: CircleImageView

    private lateinit var memo: String

    private var masterInterList: ArrayList<SingleUserInterestType> = ArrayList()
    private var intGrid: GridView? = null
    private var intAdapter: SingleUserInterestsAdapter? = null
    private var interestString: String? = null

    private var privacyString: String? = null

    private lateinit var mUserImage: String
    private lateinit var lovely: String
    private lateinit var mCurrentImage: String

    private var mUserID: String?=""

    private var i = 0

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

    private val hmsGmsTranslator: HmsGmsITranslator by lazy {
        HmsGmsITranslator(this)
    }

    private val singleUserPresenter: SingleUserPresenter by lazy {
        SingleUserPresenter(this, hmsGmsTranslator)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPrefs = SharedPreferencesManager(this)
        if (sharedPrefs.loadNightModeState()) {
            setTheme(R.style.DarkMode)
        } else {
            setTheme(R.style.LightMode)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_user)
        supportActionBar?.hide()
        singleUserPresenter.onViewCreate()
        Slidr.attach(this)

        singleUserPresenter.setPrefs(
            context,
            sharedPrefs,
            single_user_follow_btn,
            single_user_unfollow_btn,
            single_user_following_since_tv,
            single_user_message_btn
        )

        val mp = MediaPlayer.create(this, R.raw.heart_fall1)

        single_user_send_lovely.setOnClickListener {
            if (currentID != mUserID)
                mp.start()
            hitLovely()
        }

        mUserCircle.setOnClickListener {
            attachPhotoView(R.style.DialogSlide)
        }
        single_user_if_i_send_lovely.setOnClickListener {
            TooltipCompat.setTooltipText(
                single_user_if_i_send_lovely,
                getString(R.string.tooltip_info_like_given) + " " + single_user_name.text.toString()
            )
        }
    }

    override fun initViews() {
        context = applicationContext
        mUserID = intent.getStringExtra("userID")

        val mpFollow = MediaPlayer.create(context, R.raw.follow)
        val mpUnfollow = MediaPlayer.create(context, R.raw.unfollowed)

        hmsGmsTranslator.initOnCreate()
        mUserCircle = findViewById(R.id.single_user_image)
        mMyCircle = findViewById(R.id.single_user_if_i_send_lovely)

        /* Control Page if user is Me */
        if (currentID == mUserID) {
            fm_layout.visibility = View.GONE
            single_user_if_i_send_lovely.visibility = View.GONE
        }

        mDatabaseRefUser = FirebaseDBHelper.getUserInfo(mUserID!!)
        mFollowerCounterDB = FirebaseDBHelper.getFollowedNumbers(mUserID!!)
        mFollowingCounterDB = FirebaseDBHelper.getFollowingNumbers(mUserID!!)
        mInterestDBRef = FirebaseDBHelper.getUserInfo(mUserID!!)
        mLovelyCountDBRef = FirebaseDBHelper.getLovelyTaken(mUserID!!)
        mPrivacyDBRef = FirebaseDBHelper.getUserInfo(mUserID!!)
        mFollowingSinceDBRef = FirebaseDBHelper.getFollowing(currentID, mUserID!!)

        /* CurrentUserDB */
        Constants.fUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showLogError(this.javaClass.simpleName,p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                mCurrentImage = p0.child("photoUrl").value.toString()
            }
        })

        /* LovelyCount */
        mLovelyCountDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showLogError(this.javaClass.simpleName,p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    lovely = p0.childrenCount.toString()
                    val pLovely = NumberConvertor.prettyCount(lovely.toLong())
                    single_user_send_lovely.text = pLovely
                    mDatabaseRefUser.child("lovely").setValue(lovely)
                    for (postSnap in p0.children) {
                        val key = postSnap.key!!
                        if (key == currentID) {
                            Picasso.get().load(mCurrentImage)
                                .placeholder(R.drawable.splachback).into(mMyCircle)
                        }
                    }
                }
            }
        })

        /* UserDB */
        mDatabaseRefUser.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val name = p0.child("nameSurname").value.toString()
                memo = p0.child("memory").value.toString()
                mUserImage = p0.child("photoUrl").value.toString()
                if (currentID == mUserID)
                    single_user_name.text = getString(R.string.me,name)
                else
                    single_user_name.text = name
                single_user_memo.text = memo
                if (mUserImage.isNotEmpty() && mUserImage.isNotBlank())
                    Glide.with(context.applicationContext).load(mUserImage).into(mUserCircle)
                else
                    Glide.with(context.applicationContext).load(R.drawable.ic_account_circle)
                        .into(mUserCircle)
            }
        })

        /* FollowingDB */
        mFollowingCounterDB.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    val pFollow = NumberConvertor.prettyCount(p0.childrenCount)
                    single_user_following_num.text = pFollow
                } else {
                    single_user_following_num.text = "0"
                }
            }
        })

        /* FollowersDB */
        mFollowerCounterDB.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    val pFollow = NumberConvertor.prettyCount(p0.childrenCount)
                    single_user_followers_num.text = pFollow
                } else {
                    single_user_followers_num.text = "0"
                }
            }
        })


        /* Total Post by user */
        val mShareableDBRef = FirebaseDBHelper.getShareableUploads()
        val query = mShareableDBRef.orderByChild("uploaderID").equalTo(mUserID)
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    val pTotal = NumberConvertor.prettyCount(p0.childrenCount)
                    single_user_post_num.text = pTotal
                }
            }
        })

        /* Following Controller */
        mFollowingSinceDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    single_user_follow_btn.visibility = View.GONE
                    ll_following_since.visibility = View.VISIBLE
                    single_user_following_since_tv.text =
                        p0.child("following_since").value.toString()
                    single_user_unfollow_btn.setOnClickListener {
                        val dialog = Dialog(this@SingleUserActivity, R.style.BlurTheme)
                        val width = ViewGroup.LayoutParams.MATCH_PARENT
                        val height = ViewGroup.LayoutParams.MATCH_PARENT
                        dialog.window!!.setLayout(width, height)
                        dialog.setContentView(R.layout.dialog_delete)
                        dialog.setCanceledOnTouchOutside(true)
                        val tvMain = dialog.findViewById<TextView>(R.id.dialog_tv_main)
                        val tvSub = dialog.findViewById<TextView>(R.id.dialog_tv_sub)
                        val tvKeep = dialog.findViewById<TextView>(R.id.dialog_keep_post)
                        val tvDelete = dialog.findViewById<TextView>(R.id.dialog_delete_post)
                        tvMain.text =
                            getString(R.string.action_unfollow_main,single_user_name.text.toString())
                        tvKeep.text = getString(R.string.action_no_keep_as_follower)
                        tvDelete.text = getString(R.string.action_yes_remove_following)
                        tvSub.visibility = View.GONE
                        tvKeep.setOnClickListener {
                            dialog.dismiss()
                        }
                        tvDelete.setOnClickListener {
                            mpUnfollow.start()
                            FollowTypes.unFollowUsers(currentID, mUserID!!)
                            recreate()
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                } else {
                    /* Start Following */
                    single_user_follow_btn.visibility = View.VISIBLE
                    ll_following_since.visibility = View.GONE
                    single_user_follow_btn.setOnClickListener {
                        single_user_follow_btn.visibility = View.GONE
                        mpFollow.start()
                        FollowTypes.followUsers(currentID, mUserID!!)
                    }
                }
            }
        })

        intGrid = findViewById<GridView>(R.id.single_user_grid_inter)
        populateInterests()
        setPrivacySelections()
    }

    override fun setupViewPager() {
        val adapterL = SingleUserPagerAdapter(supportFragmentManager)
        adapterL.addFragment(GalleryFragment(), getString(R.string.nav_galley))
        adapterL.addFragment(EventFragment(), getString(R.string.nav_events))
        adapterL.addFragment(TravelFragment(), getString(R.string.nav_travel))
        singleUserPager.adapter = adapterL
        single_user_tabs.setupWithViewPager(singleUserPager)
    }

    override fun showLog(message: String) {
        Log.d("SingleUserActivity", message)
    }

    override fun hitLovely() {
        single_user_falling_lovely.visibility = View.VISIBLE
        single_user_falling_lovely.bringToFront()
        single_user_falling_lovely.playAnimation()
        singleUserPresenter.lovelyGiven(mUserID!!, currentID)
        singleUserPresenter.lovelyTaken(mUserID!!, currentID)
    }

    override fun startSocializing(userID: String) {
        startActivity(Intent(this, ChatActivity::class.java).putExtra("visitorID", userID))
    }

    override fun setTooltip() {
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

    override fun setPrivacySelections() {
        mPrivacyDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                privacyString = p0.child("privacy").value.toString()

                privacyMode()
            }
        })
    }

    override fun privacyMode() {
        val charArray = privacyString!!.toCharArray()

        if (mUserID != currentID) {
            changeView.visibility = View.GONE
            singleUserPresenter.privacyControls(
                context,
                mUserID!!,
                charArray,
                single_user_message_btn,
                single_user_name,
                hybrid_detected_language,
                hybrid_translated_text,
                single_user_memo,
                gotoFollowers,
                gotoFollowing,
                single_user_followers_num,
                single_user_following_num,
                singleUserPager,
                privacyLottie,
                lnPrivacy,
                sorryTv,
                memo
            )
        } else { /* Take a peek*/
            lnPrivacy.visibility = View.GONE
            singleUserPager.visibility = View.VISIBLE
            showLog("Before $i")
            su_toolbar.setOnClickListener {
                TooltipCompat.setTooltipText(
                    changeView,
                    getString(R.string.tooltip_info_take_a_peek)
                )
                if (i % 2 == 0) {
                    singleUserPresenter.privacyControls(
                        context,
                        mUserID!!,
                        charArray,
                        single_user_message_btn,
                        single_user_name,
                        hybrid_detected_language,
                        hybrid_translated_text,
                        single_user_memo,
                        gotoFollowers,
                        gotoFollowing,
                        single_user_followers_num,
                        single_user_following_num,
                        singleUserPager,
                        privacyLottie,
                        lnPrivacy,
                        sorryTv,
                        memo
                    )
                } else if (i % 2 == 1) {
                    /* FollowingDB */
                    lnPrivacy.visibility = View.GONE
                    singleUserPager.visibility = View.VISIBLE
                    setupViewPager()
                    mFollowingCounterDB.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.hasChildren()) {
                                val pFollow = NumberConvertor.prettyCount(p0.childrenCount)
                                single_user_following_num.text = pFollow
                            } else {
                                single_user_following_num.text = "0"
                            }
                        }
                    })
                    mDatabaseRefUser.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            memo = p0.child("memory").value.toString()
                            single_user_memo.text = memo
                        }
                    })
                    /* FollowersDB */
                    mFollowerCounterDB.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.hasChildren()) {
                                val pFollow = NumberConvertor.prettyCount(p0.childrenCount)
                                single_user_followers_num.text = pFollow
                            } else {
                                single_user_followers_num.text = "0"
                            }
                        }
                    })

                    singleUserPresenter.translationsEditor(
                        hybrid_translated_text,
                        hybrid_translated_text,
                        hmsGmsTranslator,
                        single_user_memo
                    )
                    gotoFollowers.setOnClickListener {
                        context.startActivity(
                            Intent(
                                context.applicationContext,
                                MasterProfileFollowerActivity::class.java
                            )
                                .putExtra("followers", single_user_followers_num.text.toString())
                                .putExtra("following", single_user_following_num.text.toString())
                                .putExtra("type", "followers")
                                .putExtra("userID", mUserID)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                    gotoFollowing.setOnClickListener {
                        context.startActivity(
                            Intent(
                                context.applicationContext,
                                MasterProfileFollowerActivity::class.java
                            )
                                .putExtra("followers", single_user_followers_num.text.toString())
                                .putExtra("following", single_user_following_num.text.toString())
                                .putExtra("type", "following")
                                .putExtra("userID", mUserID)
                                .putExtra("from","user")
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                }
                showLog("After $i | " + i % 2)
                i++
            }
        }
    }

    fun mGetUserID(): String {
        return mUserID!!
    }

    override fun populateInterestsGrid() {
        val charArray: CharArray = interestString!!.toCharArray()

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
        intAdapter = SingleUserInterestsAdapter(masterInterList, this)
        intGrid?.adapter = intAdapter

        showLog(interestString!!)
    }

    override fun populateInterests() {
        mInterestDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                interestString = p0.child("interests").value.toString()

                showLog(mUserID!!)

                populateInterestsGrid()
                setTooltip()
            }
        })
    }

    override fun attachPhotoView(type: Int) {
        val dialog = Dialog(this, R.style.BlurTheme)
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.attributes.windowAnimations = type
        dialog.setContentView(R.layout.zoom_image)
        dialog.setCanceledOnTouchOutside(true)
        val imageView: ImageView = dialog.findViewById(R.id.zoomed_image)
        val cancel: ImageButton = dialog.findViewById(R.id.cancel_button)
        Picasso.get().load(mUserImage).placeholder(R.drawable.splachback)
            .into(imageView)
        val photoViewAttacher = PhotoViewAttacher(imageView)
        photoViewAttacher.maximumScale = 20F
        cancel.bringToFront()
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
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

    override fun detect() {

    }
}
