package com.huawei.hime.profile.profilemain

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.ui.activities.MasterProfileFollowerActivity
import com.huawei.hime.ui.presenters.ProfileContract
import com.huawei.hime.ui.presenters.ProfilePresenter
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.OnActivity
import com.huawei.hime.helpers.FirebaseDBHelper
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile_prof.*
import kotlinx.android.synthetic.main.fragment_profile_prof.view.*
import java.util.*
import kotlin.collections.HashMap

class ProfileFragment : Fragment(), ProfileContract {

	private var name : String? = ""
	private var memo : String? = ""
	private var stat : String? = ""
	private var pfLottie : LottieAnimationView? = null
	private var photoUrl : String? = null
	private var thumbUrl : String? = null

	private lateinit var profileCircleImg : CircleImageView
	private lateinit var dialogCircleImageView : CircleImageView
	private val timeDate : String = java.text.DateFormat.getDateTimeInstance().format(Date())

	private var currentID : String? = ""
	private lateinit var mDatabaseReference : DatabaseReference
	private lateinit var mUserFeedReference : DatabaseReference
	private lateinit var mStorageRef : StorageReference
	private lateinit var intent : Intent
	private var mUserFeed = "User Feed/Profile Pictures/"

	private lateinit var contxt : Context
	private var ctx : Activity? = null
	private lateinit var profImage : String

	private lateinit var mLovelyText : TextView

	private lateinit var mFollowerCounterDB : DatabaseReference
	private lateinit var mFollowingCounterDB : DatabaseReference
	private lateinit var mFollowerDB1 : DatabaseReference
	private lateinit var mFollowerDB2 : DatabaseReference
	private var mFirstTwoFollowerArray : MutableList<String> = ArrayList()
	private lateinit var mCircleFollower1 : CircleImageView
	private lateinit var mCircleFollower2 : CircleImageView

	private lateinit var mOpenFollowerLayout : RelativeLayout

	private lateinit var following : String

	private lateinit var followerText : TextView

	private lateinit var mView : View

	private val profilePresenter : ProfilePresenter by lazy {
		ProfilePresenter(this)
	}

	override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {

		mView = inflater.inflate(R.layout.fragment_profile_prof, container, false)
		profilePresenter.onViewsCreate()
		contxt = this.context!!
		ctx = activity!!

		mOpenFollowerLayout.setOnClickListener {
			startFollowerActivity()
		}

		mView.profile_imageButton.setOnClickListener {
			setNameMemoDialog(R.style.DialogSlide)
		}

		profileCircleImg.setOnClickListener {
			attachPhotoView(R.style.DialogSlide)
		}

		return mView
	}

	override fun attachPhotoView(type : Int) {
		val dialog = Dialog(ctx!!, R.style.BlurTheme)
		val width = ViewGroup.LayoutParams.MATCH_PARENT
		val height = ViewGroup.LayoutParams.MATCH_PARENT
		dialog.window!!.setLayout(width, height)
		dialog.window!!.attributes.windowAnimations = type
		dialog.setContentView(R.layout.zoom_image)
		dialog.setCanceledOnTouchOutside(true)
		val imageView : ImageView = dialog.findViewById(R.id.zoomed_image)
		val cancel : ImageButton = dialog.findViewById(R.id.cancel_button)
		Glide.with(contxt.applicationContext).load(profImage).into(imageView)
		val photoViewAttacher = PhotoViewAttacher(imageView)
		photoViewAttacher.maximumScale = 20F
		cancel.bringToFront()
		cancel.setOnClickListener {
			dialog.dismiss()
		}
		dialog.show()
	}

	override fun setNameMemoDialog(type : Int) {
		val dialog = Dialog(ctx!!, R.style.BlurTheme)
		val width = ViewGroup.LayoutParams.MATCH_PARENT
		val height = ViewGroup.LayoutParams.MATCH_PARENT
		dialog.window!!.setLayout(width, height)
		dialog.window!!.attributes.windowAnimations = type
		dialog.setContentView(R.layout.dialog_profile_card)
		dialog.setCanceledOnTouchOutside(true)
		val edtName = dialog.findViewById<TextInputEditText>(R.id.profile_change_name)
		val edtNameLyt = dialog.findViewById<TextInputLayout>(R.id.profile_change_name_lyt)
		val edtMemo = dialog.findViewById<TextInputEditText>(R.id.profile_change_memory)
		val edtMemoLyt = dialog.findViewById<TextInputLayout>(R.id.profile_change_memory_lyt)
		val edtStat = dialog.findViewById<TextInputEditText>(R.id.profile_change_status)
		val edtStatLyt = dialog.findViewById<TextInputLayout>(R.id.profile_change_status_lyt)
		val btnSave = dialog.findViewById<Button>(R.id.profile_save)
		val pfCard = dialog.findViewById<ConstraintLayout>(R.id.pf_card_lyt)
		val circleProf = dialog.findViewById<CircleImageView>(R.id.profile_change_circle_img)

		val curMemo = edtMemo.text.toString().length
		val maxMemo = edtMemoLyt.counterMaxLength
		val curStat = edtStat.text.toString().length
		val maxStat = edtStatLyt.counterMaxLength

		mDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {

            }

            override fun onDataChange(p0 : DataSnapshot) {
                val image = p0.child("photoUrl").value.toString()
                Picasso.get().load(image).fit().centerCrop()
                    .placeholder(R.drawable.splachback)
                    .into(dialogCircleImageView)
            }
        })
		dialogCircleImageView = dialog.findViewById(R.id.profile_change_circle_img)
		pfLottie = dialog.findViewById(R.id.pfCardLottie)
		edtName.setText(profile_name.text)
		edtMemo.setText(profile_memo.text)
		edtStat.setText(stat)
		circleProf.setOnClickListener {
			CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
				.setCropShape(CropImageView.CropShape.OVAL).start(ctx!!)
		}
		btnSave.setOnClickListener {
			//to db
			if (edtMemo.text.toString().isEmpty() || edtName.toString()
					.isEmpty() || edtStat.toString().isEmpty()
			) {
				Snackbar.make(
                    pfCard,
                    R.string.error_empty_field, Snackbar.LENGTH_SHORT
                ).show()
			} else if (curMemo > maxMemo || curStat > maxStat) {
				Snackbar.make(
                    pfCard,
                    R.string.error_max_fields, Snackbar.LENGTH_SHORT
                ).show()
			} else {
				btnSave.isClickable = true

				name = edtName.text.toString()
				memo = edtMemo.text.toString()
				stat = edtStat.text.toString()

				val updateMap : MutableMap<String, Any> = HashMap()
				updateMap["nameSurname"] = name.toString()
				updateMap["memory"] = memo.toString()
				updateMap["stat"] = stat.toString()

				mDatabaseReference.updateChildren(updateMap).addOnCompleteListener {
					if (it.isSuccessful) {
						Snackbar.make(
                            pfCard,
                            R.string.prompt_save_success,
                            Snackbar.LENGTH_SHORT
                        )
							.show()
					} else {
						showLogs("Upload Task: Failed")
					}
				}
				dialog.dismiss()
			}
		}
		dialog.show()
	}

	override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {

		OnActivity.forPictureResult(
            imgRequestCode, requestCode,
            resultCode, data, ctx!!, mStorageRef, currentID, mUserFeedReference, mUserFeed,
            name.toString()
        )
	}

	override fun initDB() {
		name = mView.profile_name.text.toString()
		currentID = AppUser.getUserId()
		mStorageRef = FirebaseStorage.getInstance().reference
		mDatabaseReference = FirebaseDBHelper.getUserInfo(currentID!!)
		mFollowerCounterDB = FirebaseDBHelper.getFollowedNumbers(currentID!!)
		mFollowingCounterDB = FirebaseDBHelper.getFollowingNumbers(currentID!!)
		mUserFeedReference = FirebaseDBHelper.getProfileFeed(currentID!!)

		mDatabaseReference.keepSynced(true)

		mDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {

            }

            override fun onDataChange(p0 : DataSnapshot) {
                profImage = p0.child("photoUrl").value.toString()
                name = p0.child("nameSurname").value.toString()
                memo = p0.child("memory").value.toString()
                stat = p0.child("stat").value.toString()
                if (stat!!.isEmpty() || stat == null) {
                    stat = getString(R.string.hi_status)
                }
                val lovely = p0.child("lovely").value.toString()

                mView.profile_memo.text = memo
                mView.profile_name.text = name
                mLovelyText.text = lovely

                Picasso.get().load(profImage).fit().centerCrop()
                    .into(profileCircleImg)
            }
        })

		/* Get Followers */
		mFollowerCounterDB.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {

            }

            override fun onDataChange(p0 : DataSnapshot) {
                if (p0.hasChildren()) {
                    followerText.text = "+${p0.childrenCount}"
                    val queryID = mFollowerCounterDB.orderByKey().limitToFirst(2)
                    queryID.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0 : DatabaseError) {

                        }

                        override fun onDataChange(db : DataSnapshot) {
                            for (postSnapshot in db.children) {
                                val key = postSnapshot.key!!
                                mFollowerDB1 = FirebaseDBHelper.getUserInfo(key)
                                mFirstTwoFollowerArray.add(key)
                            }
                            if (mFirstTwoFollowerArray.size > 1) {
                                showLogs("0 " + mFirstTwoFollowerArray[0])
                                showLogs("1 " + mFirstTwoFollowerArray[1])

                                mFollowerDB1 = FirebaseDBHelper.getUserInfo(mFirstTwoFollowerArray[0])
                                mFollowerDB1.addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0 : DatabaseError) {
                                    }

                                    override fun onDataChange(p0 : DataSnapshot) {
                                        val image = p0.child("photoUrl").value.toString()
                                        Glide.with(ctx!!.applicationContext).load(image)
                                            .into(mCircleFollower1)
                                    }
                                })
                                mFollowerDB2 = FirebaseDBHelper.getUserInfo(mFirstTwoFollowerArray[1])
                                mFollowerDB2.addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0 : DatabaseError) {
                                    }

                                    override fun onDataChange(p0 : DataSnapshot) {
                                        val image = p0.child("photoUrl").value.toString()
                                        Glide.with(ctx!!.applicationContext).load(image)
                                            .into(mCircleFollower2)
                                    }
                                })
                            }
                            if (mFirstTwoFollowerArray.size == 1) {
                                mCircleFollower1.visibility = View.INVISIBLE
                                mFollowerDB2 = FirebaseDBHelper.getUserInfo(mFirstTwoFollowerArray[0])
                                mFollowerDB2.addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0 : DatabaseError) {
                                    }

                                    override fun onDataChange(p0 : DataSnapshot) {
                                        val image = p0.child("photoUrl").value.toString()
                                        Glide.with(ctx!!.applicationContext).load(image)
                                            .into(mCircleFollower2)
                                    }
                                })
                            }
                        }
                    })
                } else {
                    mCircleFollower1.visibility = View.GONE
                    mCircleFollower2.visibility = View.GONE
                    followerText.text = "0"
                }
            }
        })

		/* Get Following */
		mFollowingCounterDB.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
            }

            override fun onDataChange(p0 : DataSnapshot) {
                if (p0.hasChildren()) {
                    val pFollow = NumberConvertor.prettyCount(p0.childrenCount)
                    following = pFollow
                } else {
                    following = "0"
                }
            }
        })
	}

	override fun initViews() {
		profileCircleImg = mView.findViewById(R.id.profile_circle_img)
		mCircleFollower1 = mView.findViewById(R.id.profile_follower_1)
		mCircleFollower2 = mView.findViewById(R.id.profile_follower_2)
		mOpenFollowerLayout = mView.findViewById(R.id.prof_openFollower_layout)
		mLovelyText = mView.findViewById(R.id.prof_lovely_txt)
		followerText = mView.findViewById(R.id.master_num_of_follower_txt)
	}

	override fun startFollowerActivity() {
		intent = Intent(ctx!!.applicationContext, MasterProfileFollowerActivity::class.java)
		intent.putExtra("userID", currentID)
			.putExtra("followers", followerText.text.toString())
			.putExtra("following", following)
			.putExtra("type", "followers")
		startActivity(intent)
	}

	private fun showLogs(msg : String) {
		Log.d(mTAG, msg)
	}

	companion object {
		private const val mTAG = "ProfileprofFragment"
		private const val imgRequestCode = 2021
	}
}
