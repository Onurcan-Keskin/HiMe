package com.huawei.hime.ui.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.Constants
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.interfaces.ILiveSave
import com.huawei.hime.ui.presenters.LiveSavePresenter
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.util.views.expandView
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_live_save_image.*
import kotlinx.android.synthetic.main.activity_live_save_image.l1
import kotlinx.android.synthetic.main.activity_live_save_image.live_save_tag1_lyt
import kotlinx.android.synthetic.main.activity_live_save_image.live_save_tag2_lyt
import kotlinx.android.synthetic.main.activity_live_save_image.live_save_tag3_lyt
import kotlinx.android.synthetic.main.activity_live_save_image.live_save_tag4_lyt
import kotlinx.android.synthetic.main.activity_live_save_image.live_save_tag5_lyt
import kotlinx.android.synthetic.main.activity_live_save_image.save_footer
import kotlinx.android.synthetic.main.activity_live_save_image.save_live_btn
import kotlinx.android.synthetic.main.activity_live_save_image.showTag
import kotlinx.android.synthetic.main.fragment_caption.*
import java.util.*

class LiveSaveImageActivity : AppCompatActivity(), ILiveSave.ViewLiveSave {

    private lateinit var image: String

    private lateinit var mUserFeedRef: DatabaseReference
    private lateinit var mStorageRef: StorageReference
    private lateinit var posterName: String
    private lateinit var posterImage: String
    private lateinit var posterInterest: String
    private val timeStamp = System.currentTimeMillis().toString()
    private lateinit var imageLayout: ImageView

   private var tag1: String = ""
   private var tag2: String = ""
   private var tag3: String = ""
   private var tag4: String = ""
   private var tag5: String = ""
   private var footer: String = ""

    private val liveSavePresenter: LiveSavePresenter by lazy {
        LiveSavePresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPrefs = SharedPreferencesManager(this)
        if (sharedPrefs.loadNightModeState()) {
            setTheme(R.style.DarkMode)
        } else {
            setTheme(R.style.LightMode)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_save_image)
        liveSavePresenter.onViewsCreate()

        Slidr.attach(this)


        setPrefs(
            sharedPrefs,
            save_footer_im_layout,
            showTag,
            live_save_tag1_lyt,
            live_save_tag2_lyt,
            live_save_tag3_lyt,
            live_save_tag4_lyt,
            live_save_tag5_lyt
        )

        tag1 = tag1EditText.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
        tag2 = tag2EditText.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
        tag3 = tag3EditText.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
        tag4 = tag4EditText.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
        tag5 = tag5EditText.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
        footer = save_footer.text.toString()

        save_live_btn.setOnClickListener {
            //TAGs

            liveSavePresenter.uploadImageTask(
                AppUser.getUserId(),
                tag1,
                tag2,
                tag3,
                tag4,
                tag5,
                footer,
                mUserFeedRef,
                image,
                posterName,
                posterImage,
                posterInterest
            )
            finish()
        }

        showTag.setOnClickListener {
            l1.expandView()
            if (l1.visibility == View.GONE) {
                showTag.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_arrow_downward,
                    0
                )
            } else {
                showTag.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_arrow_upward,
                    0
                )
            }
        }

        showLogDebug(mTag, image)
    }

    override fun initViews() {
        imageLayout = findViewById(R.id.live_image_frame)

        val fileImage = intent.getStringExtra("imageUrl")
        //val loc = intent.getStringExtra("location")

        image = Uri.parse(fileImage).toString()
        //showLogDebug(mTag,"File $fileImage Image $image Location $loc")
        Glide.with(applicationContext).load(image).into(imageLayout)
    }

    override fun initDB() {
        mUserFeedRef = FirebaseDBHelper.getUserFeedRootRef().child("Photos/${AppUser.getUserId()}")
        mStorageRef = FirebaseStorage.getInstance().reference.child("uploads/${AppUser.getUserId()}")
            .child("Pictures")
            .child(timeStamp)

        Constants.fUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val name = p0.child("nameSurname").value.toString()
                posterName = if (name.isEmpty())
                    "HimeUser"
                else
                    name
                posterInterest = p0.child("interests").value.toString()
                posterImage = p0.child("photoUrl").value.toString()
            }
        })
    }

    fun setPrefs(
        sharedPreferencesManager : SharedPreferencesManager,
        travel_footer : TextInputLayout,
        showTag : TextView,
        tag1 : TextInputLayout,
        tag2 : TextInputLayout,
        tag3 : TextInputLayout,
        tag4 : TextInputLayout,
        tag5 : TextInputLayout
    ) {
        if (sharedPreferencesManager.loadNightModeState()) {
            travel_footer.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_dark)
            showTag.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_dark)
            tag1.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_dark)
            tag2.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_dark)
            tag3.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_dark)
            tag4.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_dark)
            tag5.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_dark)
        } else {
            travel_footer.background = ContextCompat.getDrawable(this, R.drawable.bg_edit)
            showTag.background = ContextCompat.getDrawable(this, R.drawable.bg_edit)
            tag1.background = ContextCompat.getDrawable(this, R.drawable.bg_edit)
            tag2.background = ContextCompat.getDrawable(this, R.drawable.bg_edit)
            tag3.background = ContextCompat.getDrawable(this, R.drawable.bg_edit)
            tag4.background = ContextCompat.getDrawable(this, R.drawable.bg_edit)
            tag5.background = ContextCompat.getDrawable(this, R.drawable.bg_edit)
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
        private const val mTag = "LiveSaveActivity"
    }
}
