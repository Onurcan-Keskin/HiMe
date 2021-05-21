package com.huawei.hime.ui.activities

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.Constants
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.interfaces.IPostMessageUnder
import com.huawei.hime.ui.presenters.PostMessageUnderPresenter
import com.huawei.hime.util.LocaleHelper
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.util.views.expandExplanation
import com.r0adkll.slidr.Slidr
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_post_message_under.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class PostMessageUnderActivity : AppCompatActivity(), IPostMessageUnder.ViewPostMessageUnder {

	//DB
	private lateinit var postUnderDBRef : DatabaseReference

	//vars From Past Intent
	private lateinit var context : Context
	private var postId : String? = ""
	private var groupID : String? = ""
	private var commenterName : String? = ""
	private var commenterImage : String? = ""
	private var commentTime : String? = ""
	private var comment : String? = ""
	private var commentLove : String? = ""
	private var commentAble : String? = ""
	private var commenterID : String? = ""
	private var type : String? = ""

	private lateinit var mCircle : CircleImageView
	private lateinit var mSenderCircle : CircleImageView
	private lateinit var mRecycler : RecyclerView

	private lateinit var mp : MediaPlayer

	private val postMessageUnderPresenter : PostMessageUnderPresenter by lazy {
		PostMessageUnderPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {

		val sharedPrefs = SharedPreferencesManager(this)
		/* Mode State */
		if (sharedPrefs.loadNightModeState())
			setTheme(R.style.DarkMode)
		else
			setTheme(R.style.LightMode)
		/* Language State */
		if (sharedPrefs.loadLanguage() == "tr")
			LocaleHelper.setLocale(this, "tr")
		else
			LocaleHelper.setLocale(this, "en")

		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_post_message_under)

		postMessageUnderPresenter.onViewsCreate()

		val primary = getColor(R.color.colorPrimaryDark)
		val secondary = getColor(R.color.colorPrimary)

		Slidr.attach(this, primary, secondary)

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

		mp = MediaPlayer.create(context.applicationContext, R.raw.message_send)

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

	override fun initDB() {

	}

	override fun initViews() {
		context = applicationContext

		postId = intent.getStringExtra("postID")
		groupID = intent.getStringExtra("groupID")
		commenterName = intent.getStringExtra("commenterName")
		commenterImage = intent.getStringExtra("commenterImage")
		commentTime = intent.getStringExtra("commentTime")
		comment = intent.getStringExtra("comment")
		commentLove = intent.getStringExtra("commentLove")
		commentAble = intent.getStringExtra("commentAble")
		commenterID = intent.getStringExtra("commenter_id")
		type = intent.getStringExtra("type")

		showLogDebug(
            mTAg, "PostMessageUnder $postId $groupID"
                    + "\n commenterName, $commenterName"
                    + "\n commenterImage $commenterImage"
                    + "\n comment $comment"
                    + "\n commentAble $commentAble"
                    + "\n type $type"
        )

		mCircle = findViewById(R.id.single_bottom_text_sender_circle)
		mSenderCircle = findViewById(R.id.message_sender_circle)
		mRecycler = findViewById(R.id.post_under_recycler)

		if (commenterID == AppUser.getUserId()) {
			single_bottom_text_sender_name.text =
				getString(R.string.me, commenterName)
		} else {
			single_bottom_text_sender_name.text = commenterName
		}
		Glide.with(context.applicationContext).load(commenterImage).into(mCircle)
		Glide.with(context.applicationContext).load(commenterImage).into(mSenderCircle)
		single_bottom_text_get_time_ago.text = commentTime
		val pLove = NumberConvertor.prettyCount(commentLove!!.toLong())
		single_bottom_lovely.text = pLove
		single_bottom_text.text = comment
		single_bottom_text.setOnClickListener {
			single_bottom_text.expandExplanation()
		}

		mRecycler.layoutManager = LinearLayoutManager(context)
		mRecycler.setHasFixedSize(true)

		postUnderDBRef =
			FirebaseDBHelper.rootRef().child("PostMessageUnder").child(postId!!).child(groupID!!)
				.child("Message")
		postUnderDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {

            }

            override fun onDataChange(p0 : DataSnapshot) {
                if (p0.hasChildren()) {
                    val p = NumberConvertor.prettyCount(p0.childrenCount)
                    single_bottom_total_child.text =
                        getString(R.string.num_of_replies, p)
                } else {
                    single_bottom_total_child.text = getString(R.string.num_of_item_message, "0")
                }
            }
        })

		when (type) {
            "view" -> UIUtil.hideKeyboard(this)
            "reply" -> {
                if (commentAble == "0")
                    UIUtil.hideKeyboard(this)
                else
                    UIUtil.showKeyboard(this, message_edit_txt)
            }
		}

		postMessageUnderPresenter.setupRecyclerView(
            context,
            postUnderDBRef,
            AppUser.getUserId(),
            mRecycler,
            postId!!,
            groupID!!
        )
	}

	private fun sendComments() {
		val comments = message_edit_txt.text.toString()
		if (comments != "") {
			mp.start()
			postMessageUnderPresenter.sendCommentsSetup(
                postId!!,
                groupID!!,
                comments,
                commenterName!!,
                commenterImage!!
            )
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
		private const val mTAg = "PostMessageUnderActivity"
	}
}
