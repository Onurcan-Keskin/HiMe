package com.huawei.hime.profile.profiletravel

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.ui.activities.EditPostActivity
import com.huawei.hime.ui.activities.SingleTagActivity
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.helpers.FirebaseDBCopyHelper
import com.huawei.hime.util.showLogError
import com.huawei.hime.ui.presenters.ViewHolderContract
import com.huawei.hime.ui.presenters.ViewHolderPresenter
import com.huawei.hime.util.views.expandExplanation
import com.huawei.hime.util.views.expandView
import com.ramotion.circlemenu.CircleMenuView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import translate.HmsGmsITranslator

class ProfileTravelViewHolder(itemView : View) :
	RecyclerView.ViewHolder(itemView), ViewHolderContract {

	var parent : View = itemView

	/* UI */
	private var posterName : TextView? = null
	private var posterImage : CircleImageView? = null
	private var postLovely : TextView? = null
	private var postShare : TextView? = null
	private var postMessage : LinearLayout? = null
	private var postFooter : TextView? = null
	private var postAddress : TextView? = null
	private var postedMapHolder : ImageView? = null

	private var circleMenu : CircleMenuView

	private var mHybridLanguage : LinearLayout? = null
	private var mHybridDetect : TextView? = null
	private var mHybridTranslatedText : TextView? = null

	private var mLottieDelete : LottieAnimationView? = null

	private var mTagText1 : TextView? = null
	private var mTagText2 : TextView? = null
	private var mTagText3 : TextView? = null
	private var mTagText4 : TextView? = null
	private var mTagText5 : TextView? = null

	private val hmsGmsTranslator : HmsGmsITranslator by lazy {
		HmsGmsITranslator(parent.context)
	}

	private val viewHolderPresenter : ViewHolderPresenter by lazy {
		ViewHolderPresenter(
            this,
            hmsGmsTranslator
        )
	}

	init {
		hmsGmsTranslator.initOnCreate()
		posterName = parent.findViewById(R.id.hybrid_card_poster)
		posterImage = parent.findViewById(R.id.hybrid_card_poster_img)
		postLovely = parent.findViewById(R.id.hybrid_card_lovely)
		postShare = parent.findViewById(R.id.hybrid_card_share)
		postMessage = parent.findViewById(R.id.hybrid_card_message)
		postFooter = parent.findViewById(R.id.hybrid_card_footer)
		postAddress = parent.findViewById(R.id.hybrid_card_address)
		postedMapHolder = parent.findViewById(R.id.posted_map_holder)

		circleMenu = parent.findViewById(R.id.circle_menu_map)
		mLottieDelete = parent.findViewById(R.id.posted_delete_lottie)

		mHybridLanguage = parent.findViewById(R.id.hybrid_post_change_language)
		mHybridDetect = parent.findViewById(R.id.hybrid_detected_language)
		mHybridTranslatedText = parent.findViewById(R.id.hybrid_translated_text)

		mTagText1 = parent.findViewById(R.id.hybrid_card_tag1)
		mTagText2 = parent.findViewById(R.id.hybrid_card_tag2)
		mTagText3 = parent.findViewById(R.id.hybrid_card_tag3)
		mTagText4 = parent.findViewById(R.id.hybrid_card_tag4)
		mTagText5 = parent.findViewById(R.id.hybrid_card_tag5)

		postShare!!.visibility = View.INVISIBLE
		postMessage!!.visibility = View.INVISIBLE
		circleMenu.visibility = View.GONE
		mHybridTranslatedText!!.visibility = View.GONE
	}

	fun setPosterImage(img : String) {
		if (img != "default" || img.isNotEmpty() || img != "") {
			Picasso.get().load(img).placeholder(R.drawable.splachback).into(posterImage!!)
		} else {
			Picasso.get().load(R.drawable.splachback).into(posterImage!!)
		}
	}

	fun setPosterName(name : String) {
		if (posterName.toString().isEmpty() || posterName.toString() == "" || posterName.toString()
				.isBlank()
		) {
			posterName!!.setText(R.string.himeUser)
		} else {
			posterName!!.text = name.split(' ').first()
		}
	}

	fun bindView(
        mPostFooter : String,
        mLat : String,
        mLong : String,
        mPostAddress : String,
        mMapHolder : String,
        mPostTag1 : String,
        mPostTag2 : String,
        mPostTag3 : String,
        mPostTag4 : String,
        mPostTag5 : String
    ) {
		postFooter!!.text = mPostFooter
		postFooter!!.setOnClickListener {
			postFooter!!.expandExplanation()
		}

		mHybridLanguage!!.setOnClickListener {
			mHybridTranslatedText!!.visibility = View.VISIBLE
			hmsGmsTranslator.detectLanguage(mPostFooter, mHybridDetect!!, mHybridTranslatedText!!)
		}

		mHybridTranslatedText!!.setOnClickListener {
			mHybridTranslatedText!!.expandView()
		}

		postAddress!!.text = mPostAddress

		if (mMapHolder.isNullOrEmpty()) {
			Picasso.get().load(R.drawable.default_map).fit().centerCrop()
				.into(postedMapHolder!!)
		} else
			Picasso.get().load(mMapHolder)
				.placeholder(R.drawable.default_map).fit().centerCrop()
				.into(postedMapHolder!!)

		//Tag
		if (mTagText1!!.text.toString().isNotEmpty() && mPostTag1.isNotEmpty()) {
			mTagText1!!.text = "#" + mPostTag1
			mTagText1!!.setOnClickListener {
				parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", mPostTag1)
                )
			}
		} else
			mTagText1!!.visibility = View.GONE

		if (mTagText2!!.text.toString().isNotEmpty() && mPostTag2.isNotEmpty()) {
			mTagText2!!.text = "#" + mPostTag2
			mTagText2!!.setOnClickListener {
				parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", mPostTag2)
                )
			}
		} else
			mTagText2!!.visibility = View.GONE

		if (mTagText3!!.text.toString().isNotEmpty() && mPostTag3.isNotEmpty()) {
			mTagText3!!.text = "#" + mPostTag3
			mTagText3!!.setOnClickListener {
				parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", mPostTag3)
                )
			}
		} else
			mTagText3!!.visibility = View.GONE

		if (mTagText4!!.text.toString().isNotEmpty() && mPostTag4.isNotEmpty()) {
			mTagText4!!.text = "#" + mPostTag4
			mTagText4!!.setOnClickListener {
				parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", mPostTag4)
                )
			}
		} else
			mTagText4!!.visibility = View.GONE

		if (mTagText5!!.text.toString().isNotEmpty() && mPostTag5.isNotEmpty()) {
			mTagText5!!.text = "#" + mPostTag5
			mTagText5!!.setOnClickListener {
				parent.context.startActivity(
                    Intent(
                        parent.context.applicationContext,
                        SingleTagActivity::class.java
                    ).putExtra("tag", mPostTag5)
                )
			}
		} else
			mTagText5!!.visibility = View.GONE
	}

	fun setCircleMenu(
        context : FragmentActivity,
        uploadDBRef : DatabaseReference,
        feedDBRef : DatabaseReference,
        shareStat : String,
        commentsAllowed : String,
        tableID : String
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
		val feedRef : MutableList<String> = ArrayList()
		feedDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {

            }

            @SuppressLint("RestrictedApi")
            override fun onDataChange(p0 : DataSnapshot) {
                feedRef.add(p0.ref.path.toString())
            }
        })
		postedMapHolder!!.setOnLongClickListener {
			circleMenu.visibility = View.VISIBLE
			mpMenuAppear.start()
			val dbNShareable = FirebaseDBHelper.rootRef().child("uploads/NShareable/$tableID")
			val dbShareable = FirebaseDBHelper.rootRef().child("uploads/Shareable/$tableID")
			circleMenu.eventListener = object : CircleMenuView.EventListener() {
				override fun onMenuOpenAnimationStart(view : CircleMenuView) {
					super.onMenuOpenAnimationStart(view)
					mpMenuOpen.start()
				}

				override fun onMenuOpenAnimationEnd(view : CircleMenuView) {
					super.onMenuOpenAnimationEnd(view)
				}

				override fun onMenuCloseAnimationStart(view : CircleMenuView) {
					super.onMenuCloseAnimationStart(view)
					mpMenuClose.start()
				}

				override fun onMenuCloseAnimationEnd(view : CircleMenuView) {
					super.onMenuCloseAnimationEnd(view)
					circleMenu.visibility = View.GONE
				}

				override fun onButtonClickAnimationStart(view : CircleMenuView, buttonIndex : Int) {
					super.onButtonClickAnimationStart(view, buttonIndex)
					when (buttonIndex) {
                        0 -> {
                            mpMenuItemSelect.start()
                            // Post to
                            //showSnackbar(parent.context.getString(R.string.postac))
                        }
                        1 -> {
                            mpMenuItemSelect.start()
                            // Show on Map
                        }
                        2 -> {
                            mpMenuItemSelect.start()
                            // Edit Post
                            uploadDBRef.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0 : DatabaseError) {
                                }

                                @SuppressLint("RestrictedApi")
                                override fun onDataChange(p0 : DataSnapshot) {
                                    val imageUrl = p0.child("upload_imageUrl").value.toString()
                                    val uploadType = p0.child("uploadType").value.toString()
                                    val uploadFooter = p0.child("upload_footer").value.toString()
                                    val tag1 = p0.child("tag1").value.toString()
                                    val tag2 = p0.child("tag2").value.toString()
                                    val tag3 = p0.child("tag3").value.toString()
                                    val tag4 = p0.child("tag4").value.toString()
                                    val tag5 = p0.child("tag5").value.toString()
                                    val address = p0.child("upload_travel_address").value.toString()
                                    parent.context.startActivity(
                                        Intent(
                                            parent.context,
                                            EditPostActivity::class.java
                                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            .putExtra("photoUrl", imageUrl)
                                            .putExtra("tableID", tableID)
                                            .putExtra("uploadType", uploadType)
                                            .putExtra("uploadFooter", uploadFooter)
                                            .putExtra("uploadDB", p0.ref.path.toString())
                                            .putExtra("address", address)
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
                        3 -> {
                            mpMenuItemSelect.start()
                            // Post Delete
                            mpMenuItemSelect.start()
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
                        4 -> {
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
                        5 -> {
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

				override fun onButtonClickAnimationEnd(view : CircleMenuView, buttonIndex : Int) {
					super.onButtonClickAnimationEnd(view, buttonIndex)
					circleMenu.visibility = View.GONE
				}

				override fun onButtonLongClick(view : CircleMenuView, buttonIndex : Int) : Boolean {
					when (buttonIndex) {
                        0 -> {
                            // Post to
                            showSnackbar(parent.context.getString(R.string.post_settings_post_to))
                        }
                        1 -> {
                            // Show on Map
                            showSnackbar(parent.context.getString(R.string.show_on_map))
                        }
                        2 -> {
                            // Edit Post
                            showSnackbar(parent.context.getString(R.string.post_settings_edit))
                        }
                        3 -> {
                            // Post Delete
                            showSnackbar(parent.context.getString(R.string.post_settings_delete))
                        }
                        4 -> {
                            // Commentability
                            when (commentsAllowed) {
                                "1" -> showSnackbar(parent.context.getString(R.string.post_settings_no_commentable))
                                "0" -> showSnackbar(parent.context.getString(R.string.post_settings_commentable))
                            }
                        }
                        5 -> {
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
                    view : CircleMenuView,
                    buttonIndex : Int
                ) {
					super.onButtonLongClickAnimationStart(view, buttonIndex)
				}

				override fun onButtonLongClickAnimationEnd(
                    view : CircleMenuView,
                    buttonIndex : Int
                ) {
					super.onButtonLongClickAnimationEnd(view, buttonIndex)
				}
			}
			return@setOnLongClickListener true
		}
	}

	private fun showSnackbar(string : String) {
		Snackbar.make(parent, string, Snackbar.LENGTH_SHORT).show()
	}

	override fun detect() {

	}
}