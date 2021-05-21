package com.huawei.hime.ui.activities

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.chat.ChatAdapter
import com.huawei.hime.getTime.GetOnlineTimeAgo
import com.huawei.hime.helpers.Constants
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.models.ChatType
import com.huawei.hime.ui.interfaces.IChat
import com.huawei.hime.ui.presenters.ChatPresenter
import com.huawei.hime.util.*
import com.r0adkll.slidr.Slidr
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity(), IChat.ViewChat {

	private val chatPresenter : ChatPresenter by lazy {
		ChatPresenter(this)
	}

	private lateinit var mDatabaseRefUser : DatabaseReference
	private lateinit var mDatabaseRefMessage : DatabaseReference
	private lateinit var mCircleBanner : CircleImageView

	private val currentID = AppUser.getUserId()
	private var mOnlineDBRef = FirebaseDBHelper.getUser(currentID)
	private lateinit var mOnlineUserDBRef : DatabaseReference

	private var pushMessageKey : String = ""
	private var userID : String? = ""
	private var mCurrentUserName = ""

	private lateinit var context : Context

	private val messagesList : ArrayList<ChatType> = ArrayList()
	private lateinit var mLinearLayoutManager : LinearLayoutManager
	private var mChatAdapter : ChatAdapter? = null

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
		setContentView(R.layout.activity_chat)
		supportActionBar?.hide()

		chatPresenter.onViewCreate()

		Slidr.attach(this)

		val mp = MediaPlayer.create(context.applicationContext, R.raw.message_send)

		message_send_button.setOnClickListener {
			mp.start()
			chatPresenter.sendChatMessage(message_edit_txt.text.toString(),AppUser.getUserId(),userID!!)
			message_edit_txt.setText("")
			messages_recycler
		}

		if (sharedPrefs.loadNightModeState()) {
			chat_constraint.background = ContextCompat.getDrawable(this, R.drawable.chat_bg_dark_2)
		} else {
			chat_constraint.background = ContextCompat.getDrawable(this, R.drawable.chat_bg_1)
		}
	}

	override fun initViews() {
		context = applicationContext
		//UserID of the seen profile
		userID = intent.getStringExtra("visitorID")
		mDatabaseRefUser = FirebaseDBHelper.getUserInfo(userID!!)
		mOnlineUserDBRef = FirebaseDBHelper.getUser(userID!!)
		mDatabaseRefUser.keepSynced(true)

		showLogs("userID: $userID | currentID:  $currentID")

		mDatabaseRefMessage = FirebaseDBHelper.rootRef()
			.child("Messages").child(currentID).child(pushMessageKey)

		mCircleBanner = findViewById(R.id.chat_rec_img)

		/* Message Load Init*/
		mChatAdapter = ChatAdapter(messagesList)
		mLinearLayoutManager = LinearLayoutManager(this)
		messages_recycler.setHasFixedSize(true)
		messages_recycler.layoutManager = mLinearLayoutManager
		messages_recycler.adapter = mChatAdapter

		loadMessages()

		Log.d("tag", "Seen Profile: $userID , User Profile: $currentID")
	}

	override fun populateViewsWithDB() {
		mOnlineUserDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
	            showLogDebug(this.javaClass.simpleName,p0.message)
            }

            override fun onDataChange(p0 : DataSnapshot) {
	            when (val online = p0.child("onlineStatus").value.toString()) {
                    "true" -> {
                        mCircleBanner.borderColor = getColor(R.color.colorOnline)
                        chat_rec_onlineStat.text = getString(R.string.statOnline)
                        chat_rec_onlineStat_img.setImageResource(R.color.colorOnline)
                        showLogs("OnlineStatus: Online")
                        chat_rec_onlineStat.text = getString(R.string.online)
                    }
                    "offline" -> {
                        Constants.fUserInfoRef.child("onlineStatus").onDisconnect()
                            .setValue(ServerValue.TIMESTAMP)
                        showLogs("OnlineStatus: Offline")
                        chat_rec_onlineStat.text = getString(R.string.offline)
                        mCircleBanner.borderColor = getColor(R.color.colorOffline)
                        chat_rec_onlineStat_img.setImageResource(R.color.colorOffline)
                    }
                    else -> {
                        mCircleBanner.borderColor = getColor(R.color.colorOffline)
                        chat_rec_onlineStat_img.setImageResource(R.color.colorOffline)
                        val getTime = GetOnlineTimeAgo()
                        val lt = online.toLong()
                        val lastOnlineTime = getTime.getTimeAgo(lt, applicationContext)
                        showLogs("OnlineStatus: " + lastOnlineTime!!)

                        chat_rec_onlineStat.text = lastOnlineTime

                    }
                }
            }
        })
		mDatabaseRefUser.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
	            showLogError(this.javaClass.simpleName,p0.message)
            }

            override fun onDataChange(p0 : DataSnapshot) {
                val img = p0.child("photoUrl").value.toString()
                val name = p0.child("nameSurname").value.toString()


                Picasso.get().load(img).placeholder(R.drawable.splachback)
                    .into(mCircleBanner)
                if (name != "")
                    chat_rec_name.text = name
                else
                    chat_rec_name.text = getString(R.string.himeUser)
            }
        })

		Constants.fUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0 : DatabaseError) {
            }

            override fun onDataChange(p0 : DataSnapshot) {
                mCurrentUserName = p0.child("nameSurname").value.toString()
            }
        })
	}

	override fun loadMessages() {
		val messagesRef =
			FirebaseDBHelper.rootRef().child("Messages").child(currentID).child(userID!!)

		messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0 : DatabaseError) {
            }

            override fun onChildMoved(p0 : DataSnapshot, p1 : String?) {
            }

            override fun onChildChanged(p0 : DataSnapshot, p1 : String?) {
            }

            override fun onChildAdded(p0 : DataSnapshot, p1 : String?) {
                val messages = p0.getValue(ChatType::class.java)
                Log.d("tagMessage", messages.toString())
                messagesList.add(messages!!)
                mChatAdapter!!.notifyDataSetChanged()
                mLinearLayoutManager.smoothScrollToPosition(
                    messages_recycler,
                    null,
                    mChatAdapter!!.itemCount
                )
                //mChatAdapter!!.notifyDataSetChanged()
            }

            override fun onChildRemoved(p0 : DataSnapshot) {
                showLogInfo(this.javaClass.simpleName,p0.key.toString()+" removed.")
            }

        })
		mChatAdapter!!.notifyDataSetChanged()
	}

	override fun onStart() {
		super.onStart()
		FirebaseDBHelper.onDisconnect(mOnlineDBRef)
	}

	override fun onPause() {
		super.onPause()
		FirebaseDBHelper.onDisconnect(mOnlineDBRef)
	}

	override fun onStop() {
		super.onStop()
		FirebaseDBHelper.onDisconnect(mOnlineDBRef)
	}

	override fun onDestroy() {
		super.onDestroy()
		FirebaseDBHelper.onDisconnect(mOnlineDBRef)
	}

	private fun showLogs(string : String) {
		Log.d("ChatActivity", string)
	}
}
