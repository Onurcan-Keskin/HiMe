package com.huawei.hime.search.singleuserpage

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.activities.MasterProfileFollowerActivity
import com.huawei.hime.ui.interfaces.ISingleUser
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.views.expandExplanation
import com.huawei.hime.util.views.expandView
import translate.HmsGmsITranslator
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class SingleUserPresenter constructor(
    private val singleUserContract: ISingleUser.ViewSingleUser,
    private val hmsGmsTranslator: HmsGmsITranslator
) {
    fun onDetect(string: String, tvLCode: TextView, tv: TextView) {
        singleUserContract.detect()
        hmsGmsTranslator.detectLanguage(string, tvLCode, tv)
    }

    fun onViewCreate() {
        singleUserContract.initViews()
    }

    fun onMessageClick(userID: String) {
        singleUserContract.startSocializing(userID)
    }

    /* Translation Editor */
    fun translationsEditor(
        detectLanguageTextView: TextView,
        translatedTextView: TextView,
        hmsGmsTranslator: HmsGmsITranslator,
        memoTextView: TextView
    ) {
        detectLanguageTextView.setOnClickListener {
            translatedTextView.visibility = View.VISIBLE
            hmsGmsTranslator.detectLanguage(
                memoTextView.text.toString(),
                detectLanguageTextView,
                translatedTextView
            )
        }
        translatedTextView.setOnClickListener { translatedTextView.expandView() }
        memoTextView.setOnClickListener { memoTextView.expandExplanation() }
    }

    fun setPrefs(
        context: Context,
        sharedPreferencesManager: SharedPreferencesManager,
        followButton: AppCompatButton,
        unFollowButton: AppCompatButton,
        followingSince: TextView,
        messageButton: AppCompatButton
    ) {
        if (sharedPreferencesManager.loadNightModeState()) {
            followButton.background = ContextCompat.getDrawable(context,R.drawable.bg_edit_dark)
            unFollowButton.background = ContextCompat.getDrawable(context,R.drawable.bg_edit_dark)
            followingSince.background = ContextCompat.getDrawable(context,R.drawable.bg_edit_dark)
            messageButton.background = ContextCompat.getDrawable(context,R.drawable.bg_edit_dark)
        } else {
            followButton.background = ContextCompat.getDrawable(context,R.drawable.bg_edit)
            unFollowButton.background = ContextCompat.getDrawable(context,R.drawable.bg_edit)
            followingSince.background = ContextCompat.getDrawable(context,R.drawable.bg_edit)
            messageButton.background =ContextCompat.getDrawable(context,R.drawable.bg_edit)
        }
    }

    fun lovelyTaken(userID: String, currentID: String) {
        val timeDate = DateFormat.getDateTimeInstance().format(Date())
        val lovelyTaken = "LovelyTaken"

        val mLovelyGiven = FirebaseDBHelper.getUserInfo(currentID)
        mLovelyGiven.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val age = p0.child("age").value.toString()
                val image = p0.child("photoUrl").value.toString()
                val name = p0.child("nameSurname").value.toString()

                val lovelyTakenMap: MutableMap<String, String> = HashMap()
                lovelyTakenMap["lovelyGivenDate"] = timeDate
                lovelyTakenMap["age"] = age
                lovelyTakenMap["image"] = image
                lovelyTakenMap["name"] = name

                val mapLovelyTaken: MutableMap<String, Any> = HashMap()
                mapLovelyTaken["$lovelyTaken/$userID/$currentID"] = lovelyTakenMap

                FirebaseDBHelper.rootRef().updateChildren(mapLovelyTaken)
            }
        })
    }

    fun lovelyGiven(userID: String, currentID: String) {
        val timeDate = DateFormat.getDateTimeInstance().format(Date())
        val lovelyGivenRef = "LovelyGiven"

        val mLovelyTaken = FirebaseDBHelper.getUserInfo(userID)
        mLovelyTaken.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val lAge = p0.child("age").value.toString()
                val lImage = p0.child("photoUrl").value.toString()
                val lName = p0.child("nameSurname").value.toString()

                val lovelyGivenMap: MutableMap<String, String> = HashMap()
                lovelyGivenMap["lovelyGivenDate"] = timeDate
                lovelyGivenMap["age"] = lAge
                lovelyGivenMap["image"] = lImage
                lovelyGivenMap["name"] = lName

                val mapLovelyGiven: MutableMap<String, Any> = HashMap()
                mapLovelyGiven["$lovelyGivenRef/$currentID/$userID"] = lovelyGivenMap

                FirebaseDBHelper.rootRef().updateChildren(mapLovelyGiven)
            }
        })
    }

    fun privacyControls(
        context: Context,
        userID: String,
        charArray: CharArray,
        messageButton: AppCompatButton,
        userName: TextView,
        detectLanguage: TextView,
        translatedTextView: TextView,
        memoTextView: TextView,
        gotoFollowers: LinearLayout,
        gotoFollowing: LinearLayout,
        followersTextView: TextView,
        followingTextView: TextView,
        viewPager: ViewPager,
        lottieAnimationView: LottieAnimationView,
        linearLayout: LinearLayout,
        textView: TextView,
        memo: String
    ) {
        /* Receive Message */
        if (charArray[0] == '1') {
            messageButton.isActivated = true
            messageButton.setOnClickListener {
                onMessageClick(userID)
            }
        } else {
            messageButton.isActivated = false
            messageButton.setTextColor(context.getColor(R.color.red))
            messageButton.setOnClickListener {
                TooltipCompat.setTooltipText(
                    messageButton,
                    context.getString(R.string.tooltip_error_send_message) + " " + userName.text.toString()
                )
            }
        }

        /* See Memo */
        if (charArray[1] == '1') {
            translationsEditor(
                detectLanguage,
                translatedTextView,
                hmsGmsTranslator,
                memoTextView
            )
            memoTextView.text = memo
        } else {
            memoTextView.setTextColor(context.getColor(R.color.red))
            memoTextView.text =
                "- " + context.getString(R.string.tooltip_error_see_memo) + " " + userName.text.toString() + " -"
        }

        /* See Followers */
        if (charArray[2] == '1') {
            gotoFollowers.setOnClickListener {
                context.startActivity(
                    Intent(
                        context.applicationContext,
                        MasterProfileFollowerActivity::class.java
                    )
                        .putExtra("followers", followersTextView.text.toString())
                        .putExtra("following", followingTextView.text.toString())
                        .putExtra("type", "followers")
                        .putExtra("userID", userID)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            gotoFollowing.setOnClickListener {
                context.startActivity(
                    Intent(
                        context.applicationContext,
                        MasterProfileFollowerActivity::class.java
                    )
                        .putExtra("followers", followersTextView.text.toString())
                        .putExtra("following", followingTextView.text.toString())
                        .putExtra("type", "following")
                        .putExtra("userID", userID)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        } else {
            followersTextView.text = "?"
            followingTextView.text = "?"
            gotoFollowers.setOnClickListener {
                TooltipCompat.setTooltipText(
                    gotoFollowers,
                    context.getString(R.string.tooltip_error_see_followers) + " " + userName.text.toString()
                )
            }
            gotoFollowing.setOnClickListener {
                TooltipCompat.setTooltipText(
                    gotoFollowing,
                    context.getString(R.string.tooltip_error_see_following_p1) + " " + userName.text.toString() + " " + context.getString(
                        R.string.tooltip_error_see_following_p2
                    )
                )
            }
        }

        /* Observe Posts */
        if (charArray[3] == '1') {
            viewPager.visibility = View.VISIBLE
            linearLayout.visibility = View.GONE
            singleUserContract.setupViewPager()
        } else {
            viewPager.visibility = View.GONE
            linearLayout.visibility = View.VISIBLE
            lottieAnimationView.playAnimation()
            textView.text =
                context.getString(R.string.tooltip_error_posts_p1) + " " + userName.text.toString() + " " + context.getString(
                    R.string.tooltip_error_posts_p2
                )
        }
    }

    fun followMap(currentID: String, lisResID: String) {
        val timeDate = DateFormat.getDateTimeInstance().format(Date())
        val followingRef = "Following"
        val followedRef = "Followed"

        /* FollowedUserData */
        val mFollowerUserInfoRef = FirebaseDBHelper.getUserInfo(currentID)
        mFollowerUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val age = p0.child("age").value.toString()
                val image = p0.child("photoUrl").value.toString()
                val name = p0.child("nameSurname").value.toString()
                val lovely = p0.child("lovely").value.toString()

                /* Followed */
                val followedMap: MutableMap<String, String> = HashMap()
                followedMap["following_since"] = timeDate
                followedMap["age"] = age
                followedMap["image"] = image
                followedMap["name"] = name
                followedMap["lovely"] = lovely

                val mapFollowed: MutableMap<String, Any> = HashMap()
                mapFollowed["$followedRef/$lisResID/$currentID"] = followedMap

                FirebaseDBHelper.rootRef().updateChildren(mapFollowed)
            }
        })

        /* FollowingUserData */
        val mFollowingUserInfoRef = FirebaseDBHelper.getUserInfo(lisResID)
        mFollowingUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val fAge = p0.child("age").value.toString()
                val fImage = p0.child("photoUrl").value.toString()
                val fName = p0.child("nameSurname").value.toString()
                val fLovely = p0.child("lovely").value.toString()

                /* Following */
                val followingMap: MutableMap<String, String> = HashMap()
                followingMap["following_since"] = timeDate
                followingMap["age"] = fAge
                followingMap["image"] = fImage
                followingMap["name"] = fName
                followingMap["lovely"] = fLovely

                val mapFollowing: MutableMap<String, Any> = HashMap()
                mapFollowing["$followingRef/$currentID/$lisResID"] = followingMap

                FirebaseDBHelper.rootRef().updateChildren(mapFollowing)
            }
        })
    }

    fun followerNums(lisResID: String) {
        val mFollowedDBRef = FirebaseDBHelper.getFollowedNumbers(lisResID)
        var mFollowerNumber: String
        mFollowedDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (postSnapshot in p0.children) {
                        mFollowerNumber = p0.childrenCount.toString()
                    }
                } else {
                    mFollowerNumber = "0"
                }
            }
        })
    }
}