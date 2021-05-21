package com.huawei.hime.ui.activities

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.database.*
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.Constants
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.models.ExoModel
import com.huawei.hime.ui.interfaces.IPostMessage
import com.huawei.hime.ui.presenters.PostMessagePresenter
import com.huawei.hime.util.LocaleHelper
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.util.showLogError
import com.huawei.hime.util.views.expandView
import com.r0adkll.slidr.Slidr
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_post_message.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class PostMessageActivity : AppCompatActivity(), IPostMessage.ViewPostMessage {

    private lateinit var mUserInfoDB: DatabaseReference

    private lateinit var postMessageDBRef: DatabaseReference
    private lateinit var query: Query
    private lateinit var postUploadsRef: DatabaseReference

    //Vars
    private lateinit var mRecycler: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var context: Context

    private var postID: String?=""
    private var commentAble: String?=""
    private var uploadType: String?=""
    private var uploadImage: String?=""
    private var uploadVideo: String?=""
    private var uploaderID: String?=""

    private lateinit var commenterName: String
    private lateinit var commenterImg: String

    private lateinit var mEditBarCircle: CircleImageView
    private lateinit var mImage: ImageView

    private lateinit var mp: MediaPlayer

    private lateinit var appBar: AppBarLayout

    /* If Video Message  */
    private lateinit var mVideo: PlayerView

    var popularity = 0

    private val postMessagePresenter: PostMessagePresenter by lazy {
        PostMessagePresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPrefs = SharedPreferencesManager(this)
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
        setContentView(R.layout.activity_post_message)

        postMessagePresenter.onViewsCreate()

        Slidr.attach(this)

        val imm: InputMethodManager =
            context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        showLogDebug(mTAg, "Keyboard Service " + imm.isActive.toString())

        uploaderID = intent.getStringExtra("uploaderID")
        mUserInfoDB = FirebaseDBHelper.getUserInfo(uploaderID!!)
        mUserInfoDB.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showLogError(this.javaClass.simpleName,p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                val name = p0.child("nameSurname").value.toString()
                postOwner.text = getString(R.string.postedBy,name)
            }
        })

        when (commentAble) {
            "1" -> {
                message_ui.visibility = View.VISIBLE
                message_closed_post.visibility = View.GONE
            }
            "0" -> {
                message_ui.visibility = View.GONE
                message_closed_post.visibility = View.VISIBLE
            }
        }

        when (uploadType) {
            "image" -> {
                post_video.visibility = View.GONE
                post_image.visibility = View.VISIBLE
                Picasso.get().load(uploadImage).fit().centerCrop().into(mImage)
                /* Keyboard State */
                KeyboardVisibilityEvent.setEventListener(this
                ) { isOpen ->
                    if (isOpen) {
                        mImage.visibility = View.GONE
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_downward,
                            0
                        )
                    } else {
                        mImage.visibility = View.VISIBLE
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_upward,
                            0
                        )
                    }
                }
                /* Selection */
                message_r1.setOnClickListener {
                    mImage.expandView()
                    if (mImage.visibility == View.GONE) {
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_downward,
                            0
                        )
                    } else {
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_upward,
                            0
                        )
                    }
                }
            }
            "map" -> {
                post_video.visibility = View.GONE
                post_image.visibility = View.VISIBLE
                Picasso.get().load(uploadImage).fit().centerCrop().into(mImage)
                /* Keyboard State */
                KeyboardVisibilityEvent.setEventListener(this
                ) { isOpen ->
                    if (isOpen) {
                        mImage.visibility = View.GONE
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_downward,
                            0
                        )
                    } else {
                        mImage.visibility = View.VISIBLE
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_upward,
                            0
                        )
                    }
                }
                /* Selection */
                message_r1.setOnClickListener {
                    mImage.expandView()
                    if (mImage.visibility == View.GONE) {
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_downward,
                            0
                        )
                    } else {
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_upward,
                            0
                        )
                    }
                }
            }
            "event"->{
                post_video.visibility = View.GONE
                post_image.visibility = View.VISIBLE
                Picasso.get().load(uploadImage).fit().centerCrop().into(mImage)
                /* Keyboard State */
                KeyboardVisibilityEvent.setEventListener(this
                ) { isOpen ->
                    if (isOpen) {
                        mImage.visibility = View.GONE
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_downward,
                            0
                        )
                    } else {
                        mImage.visibility = View.VISIBLE
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_upward,
                            0
                        )
                    }
                }
                /* Selection */
                message_r1.setOnClickListener {
                    mImage.expandView()
                    if (mImage.visibility == View.GONE) {
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_downward,
                            0
                        )
                    } else {
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_upward,
                            0
                        )
                    }
                }
            }
            "video" -> {
                post_video.visibility = View.VISIBLE
                post_image.visibility = View.GONE
                ExoModel(this).initializeExo(uploadVideo!!,mVideo)
                /* Keyboard State */
                KeyboardVisibilityEvent.setEventListener(this
                ) { isOpen ->
                    if (isOpen) {
                        ExoModel(this).releasePlayer()
                        mVideo.visibility = View.GONE
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_downward,
                            0
                        )
                    } else {
                        mVideo.visibility = View.VISIBLE
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_upward,
                            0
                        )
                    }
                }
                /* Selection */
                message_r1.setOnClickListener {
                    mVideo.expandView()
                    if (mVideo.visibility == View.GONE) {
                        ExoModel(this).releasePlayer()
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_downward,
                            0
                        )
                    } else {
                        postOwner.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_upward,
                            0
                        )
                        ExoModel(this).initializeExo(uploadVideo!!,mVideo)
                    }
                }
            }
        }

        initDB()

        message_send_button.setOnClickListener {
            sendComments()
            message_edit_txt.setText("")
        }

        findViewById<EditText>(R.id.message_edit_txt).setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    sendComments()
                    message_edit_txt.setText("")
                    true
                }
                else -> false
            }
        }
    }

    override fun initViews() {
        context = applicationContext

        postID = intent.getStringExtra("postID")
        commentAble = intent.getStringExtra("comment")
        uploadType = intent.getStringExtra("uploadType")
        if (intent.getStringExtra("upload_imageUrl") != null) {
            uploadImage = intent.getStringExtra("upload_imageUrl")
            showLogDebug(mTAg, uploadImage!!)
        }
        if (intent.getStringExtra("upload_videoUrl") != null) {
            uploadVideo = intent.getStringExtra("upload_videoUrl")
            showLogDebug(mTAg, uploadVideo!!)
        } else {
            uploadVideo =
                "https://firebasestorage.googleapis.com/v0/b/hime-3518e.appspot.com/o/User%2FVideo%2FyFAJzmMMaReIaN9DYGc7hbecqR32%2F1594884053749.mp4?alt=media&token=61616e20-3a59-435f-bde1-cc44f31aa18a"
        }

        mp = MediaPlayer.create(context.applicationContext, R.raw.message_send)
        mRecycler = findViewById(R.id.messages_recycler)
        mEditBarCircle = findViewById(R.id.message_sender_circle)
        mImage = findViewById(R.id.post_image)
        mVideo = findViewById(R.id.post_video)
        appBar = findViewById(R.id.message_r3)

        mLayoutManager = LinearLayoutManager(context)
        mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = true
        mRecycler.layoutManager = mLayoutManager
        mRecycler.setHasFixedSize(true)
    }

    override fun initDB() {
        postUploadsRef = FirebaseDBHelper.rootRef().child("uploads").child("Shareable").child(postID!!)
        postUploadsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showLogError(this.javaClass.simpleName,p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                val pop = p0.child("popularity").value.toString()
                val poster = p0.child("upload_posterName").value.toString()
                val pId = p0.child("uploaderID").value.toString()

                if (pId == AppUser.getUserId()) {
                    postOwner.text = getString(R.string.postedBy_me,poster)
                } else {
                    postOwner.text = getString(R.string.postedBy,poster)
                }

                popularity = pop.toInt() + 1
                Log.d("Pop ", "$popularity")
                postUploadsRef.child("popularity").setValue((popularity).toString())
            }
        })

        Constants.fUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showLogError(this.javaClass.simpleName,p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                commenterName = p0.child("nameSurname").value.toString()
                commenterImg = p0.child("photoUrl").value.toString()

                Glide.with(context.applicationContext).load(commenterImg).into(mEditBarCircle)
            }
        })

        postMessageDBRef = FirebaseDBHelper.rootRef().child("PostMessage").child(postID!!)
        query = postMessageDBRef.orderByChild("comment_lovely")
        postMessagePresenter.setupRecycler(
            context,
            AppUser.getUserId(),
            query,
            mRecycler,
            postID!!,
            commentAble!!
        )
    }

    override fun sendComments() {
        val comments = message_edit_txt.text.toString()
        if (comments != "") {
            mp.start()
            postMessagePresenter.sendCommentsSetup(postID!!,comments,commenterName,commenterImg)
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
        ExoModel(this).releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        ExoModel(this).releasePlayer()
    }

    companion object {
        private const val mTAg = "PostMessageActivity"
    }
}
