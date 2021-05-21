package com.huawei.hime.profile.profilegallery.profilegalleryvideo

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.activities.EditPostActivity
import com.huawei.hime.ui.activities.SingleTagActivity
import com.huawei.hime.helpers.FirebaseDBCopyHelper
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.util.showLogError
import com.huawei.hime.util.views.expandExplanation
import com.klinker.android.simple_videoview.SimpleVideoView
import com.ramotion.circlemenu.CircleMenuView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ProfileGVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val parent: View = itemView

    private var videoContainerLayout: FrameLayout? = null
    var videoView: SimpleVideoView? = null

    private var mPosterImg: CircleImageView? = null
    private var mPoster: TextView? = null
    private var mLovelyText: TextView? = null
    var mLovelyLyt: LinearLayout? = null
    private var mPostFooter: TextView? = null
    private var mMessageTxt: TextView? = null
    private var mMaterialCard: MaterialCardView? = null
    var mPostSettings: ImageButton? = null

    var mFullscreen: ImageView? = null
    var circleMenu: CircleMenuView

    private var mTotalPopularity: TextView? = null
    private var mPopViews: TextView? = null

    private var mTotalFollowers: TextView? = null

    var mLottieHearts: LottieAnimationView? = null
    var mLottieDelete: LottieAnimationView? = null

    private var mTagText1: TextView? = null
    private var mTagText2: TextView? = null
    private var mTagText3: TextView? = null
    private var mTagText4: TextView? = null
    private var mTagText5: TextView? = null

    fun setPosterImage(img: String) {
        if (img != "default" || img.isNotEmpty() || img != "") {
            Picasso.get().load(img).placeholder(R.drawable.splachback).into(mPosterImg!!)
        } else {
            Picasso.get().load(R.drawable.splachback).into(mPosterImg!!)
        }
    }

    fun setPosterName(name: String) {
        if (mPoster.toString().isEmpty() || mPoster.toString() == "" || mPoster.toString()
                .isBlank()
        ) {
            mPoster!!.setText(R.string.himeUser)
        } else {
            mPoster!!.text = name.split(' ').first()
        }
    }

    fun setTag1(string: String) {
        if (mTagText1!!.text.toString().isNotEmpty() && string.isNotEmpty()) {
            mTagText1!!.text = "#" + string
            mTagText1!!.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", string)
                        .putExtra("fid", "t")
                )
            }
        } else
            mTagText1!!.visibility = View.GONE
    }

    fun setTag2(string: String) {
        if (mTagText2!!.text.toString().isNotEmpty() && string.isNotEmpty()) {
            mTagText2!!.text = "#" + string
            mTagText2!!.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", string)
                        .putExtra("fid", "t")
                )
            }
        } else
            mTagText2!!.visibility = View.GONE
    }

    fun setTag3(string: String) {
        if (mTagText3!!.text.toString().isNotEmpty() && string.isNotEmpty()) {
            mTagText3!!.text = "#" + string
            mTagText3!!.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", string)
                        .putExtra("fid", "t")
                )
            }
        } else
            mTagText3!!.visibility = View.GONE
    }

    fun setTag4(string: String) {
        if (mTagText4!!.text.toString().isNotEmpty() && string.isNotEmpty()) {
            mTagText4!!.text = "#" + string
            mTagText4!!.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", string)
                        .putExtra("fid", "t")
                )
            }
        } else
            mTagText4!!.visibility = View.GONE
    }

    fun setTag5(string: String) {
        if (mTagText5!!.text.toString().isNotEmpty() && string.isNotEmpty()) {
            mTagText5!!.text = "#" + string
            mTagText5!!.setOnClickListener {
                parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", string)
                        .putExtra("fid", "t")
                )
            }
        } else
            mTagText5!!.visibility = View.GONE
    }

    fun bindVideo(videoUrl: String) {
        videoView!!.start(Uri.parse(videoUrl))
        videoView!!.setOnClickListener {
            if (videoView!!.isPlaying) {
                videoView!!.pause()
            } else {
                videoView!!.play()
            }
        }
    }

    fun setFollower(string: String) {
        if (string != "") {
            val pFollower = NumberConvertor.prettyCount(string.toLong())
            mTotalFollowers!!.text = string
        } else mTotalFollowers!!.text = "0"
    }

    fun setLovely(string: String) {
        mLovelyText!!.text = string
    }

    fun setFooter(string: String) {
        mPostFooter!!.text = string
        mPostFooter!!.setOnClickListener {
            mPostFooter!!.expandExplanation()
        }
    }

    fun playHearts() {
        val mp = MediaPlayer.create(parent.context.applicationContext, R.raw.like_given)
        mp.start()
        mLottieHearts!!.visibility = View.VISIBLE
        mLottieHearts!!.bringToFront()
        mLottieHearts!!.playAnimation()
    }

    private fun showSnackbar(string: String) {
        Snackbar.make(parent, string, Snackbar.LENGTH_SHORT).show()
    }

    fun setCircleMenu(
        context: FragmentActivity,
        uploadDBRef: DatabaseReference,
        feedDBRef: DatabaseReference,
        shareStat: String,
        commentsAllowed: String,
        tableID: String
    ) {
        val dialog = Dialog(context, R.style.BlurTheme)
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.window!!.setLayout(width, height)
        dialog.setContentView(R.layout.dialog_delete)
        dialog.setCanceledOnTouchOutside(true)
        val tvKeep = dialog.findViewById<TextView>(R.id.dialog_keep_post)
        val tvDelete = dialog.findViewById<TextView>(R.id.dialog_delete_post)
        val mpMenuAppear = MediaPlayer.create(parent.context.applicationContext, R.raw.menu_appear)
        val mpMenuClose = MediaPlayer.create(parent.context.applicationContext, R.raw.menu_close)
        val mpMenuOpen = MediaPlayer.create(parent.context.applicationContext, R.raw.menu_open)
        val mpMenuItemSelect =
            MediaPlayer.create(parent.context.applicationContext, R.raw.menu_item_click)
        val mpNoClick = MediaPlayer.create(parent.context.applicationContext, R.raw.pop_water)
        val mpMenuDelete = MediaPlayer.create(parent.context.applicationContext, R.raw.delete)
        val feedRef: MutableList<String> = ArrayList()
        feedDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            @SuppressLint("RestrictedApi")
            override fun onDataChange(p0: DataSnapshot) {
                feedRef.add(p0.ref.path.toString())
            }
        })
        videoContainerLayout!!.setOnLongClickListener {

            circleMenu.visibility = View.VISIBLE
            mpMenuAppear.start()
            val dbNShareable = FirebaseDBHelper.rootRef().child("uploads/NShareable/$tableID")
            val dbShareable = FirebaseDBHelper.rootRef().child("uploads/Shareable/$tableID")
            circleMenu.eventListener = object : CircleMenuView.EventListener() {
                override fun onMenuOpenAnimationStart(view: CircleMenuView) {
                    super.onMenuOpenAnimationStart(view)
                    mpMenuOpen.start()
                }

                override fun onMenuOpenAnimationEnd(view: CircleMenuView) {
                    super.onMenuOpenAnimationEnd(view)
                }

                override fun onMenuCloseAnimationStart(view: CircleMenuView) {
                    super.onMenuCloseAnimationStart(view)
                    mpMenuClose.start()
                }

                override fun onMenuCloseAnimationEnd(view: CircleMenuView) {
                    super.onMenuCloseAnimationEnd(view)
                    circleMenu.visibility = View.GONE
                }

                override fun onButtonClickAnimationStart(view: CircleMenuView, buttonIndex: Int) {
                    super.onButtonClickAnimationStart(view, buttonIndex)
                    when (buttonIndex) {
                        0 -> {
                            mpMenuItemSelect.start()
                            // Post to
                            //showSnackbar(parent.context.getString(R.string.postac))
                        }
                        1 -> {
                            mpMenuItemSelect.start()
                            // Edit Post
                            uploadDBRef.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }

                                @SuppressLint("RestrictedApi")
                                override fun onDataChange(p0: DataSnapshot) {
                                    val imageUrl = p0.child("upload_imageUrl").value.toString()
                                    val uploadType = p0.child("uploadType").value.toString()
                                    val uploadFooter = p0.child("upload_footer").value.toString()
                                    val tag1 = p0.child("tag1").value.toString()
                                    val tag2 = p0.child("tag2").value.toString()
                                    val tag3 = p0.child("tag3").value.toString()
                                    val tag4 = p0.child("tag4").value.toString()
                                    val tag5 = p0.child("tag5").value.toString()
                                    showLogDebug(
                                        "setCircleMenu",
                                        p0.ref.path!!.toString() + " " + feedRef[0]
                                    )
                                    parent.context.startActivity(
                                        Intent(
                                            parent.context,
                                            EditPostActivity::class.java
                                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            .putExtra("photoUrl", imageUrl)
                                            .putExtra("tableID", tableID)
                                            .putExtra("uploadType", uploadType)
                                            .putExtra("uploadDB", p0.ref.path!!.toString())
                                            .putExtra("uploadFooter", uploadFooter)
                                            .putExtra("tag1", tag1)
                                            .putExtra("tag2", tag2)
                                            .putExtra("tag3", tag3)
                                            .putExtra("tag4", tag4)
                                            .putExtra("tag5", tag5)
                                            .putExtra("feedRef", feedRef[0])
                                    )
                                }
                            })
                            showSnackbar(parent.context.getString(R.string.post_action_editing))
                        }
                        2 -> {
                            mpMenuItemSelect.start()
                            // Post Delete
                            tvKeep.setOnClickListener {
                                mpMenuItemSelect.start()
                                dialog.dismiss()
                            }
                            tvDelete.setOnClickListener {
                                mpMenuDelete.start()
                                mLottieDelete!!.visibility = View.VISIBLE
                                mLottieDelete!!.bringToFront()
                                mLottieDelete!!.playAnimation()
                                val handler = Handler()
                                handler.postDelayed({
                                    feedDBRef.removeValue()
                                    uploadDBRef.removeValue()
                                    dialog.dismiss()
                                }, 2000)
                                showSnackbar(parent.context.getString(R.string.post_action_deleting))
                            }
                            dialog.show()
                        }
                        3 -> {
                            mpMenuItemSelect.start()
                            // Commentability
                            when (commentsAllowed) {
                                "1" -> {
                                    showSnackbar(parent.context.getString(R.string.post_action_closing_to_comments))
                                    feedDBRef.child("commentsAllowed").setValue("0")
                                    uploadDBRef.child("commentsAllowed").setValue("0")
                                }
                                "0" -> {
                                    showSnackbar(parent.context.getString(R.string.post_action_opening_to_comments))
                                    feedDBRef.child("commentsAllowed").setValue("1")
                                    uploadDBRef.child("commentsAllowed").setValue("1")
                                }
                            }
                        }
                        4 -> {
                            mpMenuItemSelect.start()
                            // Post Show - Hide
                            when (shareStat) {
                                "1" -> {
                                    showSnackbar(parent.context.getString(R.string.post_action_hiding))
                                    feedDBRef.child("shareStat").setValue("0")
                                    FirebaseDBCopyHelper.copyFromToPath(uploadDBRef, dbNShareable)
                                    uploadDBRef.removeValue()
                                }
                                "0" -> {
                                    showSnackbar(parent.context.getString(R.string.post_action_showing))
                                    feedDBRef.child("shareStat").setValue("1")
                                    FirebaseDBCopyHelper.copyFromToPath(uploadDBRef, dbShareable)
                                    uploadDBRef.removeValue()
                                }
                            }
                        }
                        else -> showLogError("CircleMenuView", "when index exception.")
                    }
                }

                override fun onButtonClickAnimationEnd(view: CircleMenuView, buttonIndex: Int) {
                    super.onButtonClickAnimationEnd(view, buttonIndex)
                    circleMenu.visibility = View.GONE
                }

                override fun onButtonLongClick(view: CircleMenuView, buttonIndex: Int): Boolean {
                    when (buttonIndex) {
                        0 -> {
                            // Post to
                            showSnackbar(parent.context.getString(R.string.post_settings_post_to))
                        }
                        1 -> {
                            // Edit Post
                            showSnackbar(parent.context.getString(R.string.post_settings_edit))
                        }
                        2 -> {
                            // Post Delete
                            showSnackbar(parent.context.getString(R.string.post_settings_delete))
                        }
                        3 -> {
                            // Commentability
                            when (commentsAllowed) {
                                "1" -> showSnackbar(parent.context.getString(R.string.post_settings_no_commentable))
                                "0" -> showSnackbar(parent.context.getString(R.string.post_settings_commentable))
                            }
                        }
                        4 -> {
                            // Post Show - Hide
                            when (shareStat) {
                                "1" -> showSnackbar(parent.context.getString(R.string.post_settings_hide))
                                "0" -> showSnackbar(parent.context.getString(R.string.post_settings_show))
                            }

                        }
                    }
                    return super.onButtonLongClick(view, buttonIndex)
                }

                override fun onButtonLongClickAnimationStart(
                    view: CircleMenuView,
                    buttonIndex: Int
                ) {
                    super.onButtonLongClickAnimationStart(view, buttonIndex)
                }

                override fun onButtonLongClickAnimationEnd(view: CircleMenuView, buttonIndex: Int) {
                    super.onButtonLongClickAnimationEnd(view, buttonIndex)
                }
            }
            return@setOnLongClickListener true
        }


    }

    init {
        videoContainerLayout = parent.findViewById(R.id.hybrid_videoContainerLayout)
        videoView = parent.findViewById(R.id.hybrid_video_view)

        mMaterialCard = parent.findViewById(R.id.post_card_video_view)
        mPosterImg = parent.findViewById(R.id.hybrid_card_poster_img)
        mPoster = parent.findViewById(R.id.hybrid_card_poster)
        mLovelyText = parent.findViewById(R.id.hybrid_card_lovely)
        mLovelyLyt = parent.findViewById(R.id.hybrid_card_lovely_lyt)
        mPostFooter = parent.findViewById(R.id.hybrid_card_footer)
        mMessageTxt = parent.findViewById(R.id.hybrid_card_message)
        mPostSettings = parent.findViewById(R.id.hybrid_card_settings)

        mTotalFollowers = parent.findViewById(R.id.hybrid_post_followers)
        mFullscreen = parent.findViewById(R.id.hybrid_card_video_fullscreen)
        circleMenu = parent.findViewById(R.id.circle_menu_video)

        mTotalPopularity = parent.findViewById(R.id.hybrid_card_popularity) //Make Them INVISIBLE
        mPopViews = parent.findViewById(R.id.post_card_popviews) //Make Them INVISIBLE

        mLottieHearts = parent.findViewById(R.id.posted_exp_lovely_lottie)
        mLottieDelete = parent.findViewById(R.id.posted_delete_lottie)

        mTagText1 = parent.findViewById(R.id.hybrid_card_tag1)
        mTagText2 = parent.findViewById(R.id.hybrid_card_tag2)
        mTagText3 = parent.findViewById(R.id.hybrid_card_tag3)
        mTagText4 = parent.findViewById(R.id.hybrid_card_tag4)
        mTagText5 = parent.findViewById(R.id.hybrid_card_tag5)

        mLovelyLyt!!.isClickable = false
        mLovelyLyt!!.isFocusable = false
        mPostSettings!!.visibility = View.VISIBLE
        mMessageTxt!!.visibility = View.INVISIBLE
        mPopViews!!.visibility = View.INVISIBLE
        mTotalPopularity!!.visibility = View.INVISIBLE
    }
}