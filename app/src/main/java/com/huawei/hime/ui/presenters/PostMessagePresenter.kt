package com.huawei.hime.ui.presenters

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.ui.activities.PostMessageUnderActivity
import com.huawei.hime.ui.activities.SingleUserActivity
import com.huawei.hime.util.PostMessageDataClass
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.postmessage.PostMessageViewHolder
import com.huawei.hime.ui.interfaces.IPostMessage
import com.huawei.hime.util.showLogDebug
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class PostMessagePresenter constructor(private val postMessageContract : IPostMessage.ViewPostMessage) {

	fun onViewsCreate() {
		postMessageContract.initViews()
	}

	fun setupRecycler(
        context : Context,
        currentID : String,
        query : Query,
        mRecycler : RecyclerView,
        postID : String,
        commentAble : String
    ) {
		val mp = MediaPlayer.create(context, R.raw.heart_fall1)
		val options = FirebaseRecyclerOptions.Builder<PostMessageDataClass>()
			.setQuery(query, PostMessageDataClass::class.java).build()
		val adapterFire =
			object :
				FirebaseRecyclerAdapter<PostMessageDataClass, PostMessageViewHolder>(options) {
				override fun onCreateViewHolder(
                    parent : ViewGroup,
                    viewType : Int
                ) : PostMessageViewHolder {
					val view = LayoutInflater.from(parent.context)
						.inflate(R.layout.single_bottom_message_item, parent, false)
					return PostMessageViewHolder(view)
				}

				override fun onBindViewHolder(
					holder : PostMessageViewHolder,
					position : Int,
					model : PostMessageDataClass
                ) {
					val lisResId = getRef(position).key

					if (model.commenter_ID == currentID) {
						holder.bindComments(
                            model.commenter_image,
                            model.commenter_name + " (" + context.getString(R.string.me) + ")",
                            model.time,
                            model.comment,
                            model.comment_lovely
                        )
					} else
						holder.bindComments(
                            model.commenter_image,
                            model.commenter_name,
                            model.time,
                            model.comment,
                            model.comment_lovely
                        )

                    holder.mCommenterLayout.setOnClickListener {
                        context.startActivity(
                            Intent(
                                context.applicationContext,
                                SingleUserActivity::class.java
                            ).putExtra("userID", model.commenter_ID)
	                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }

					val totalUnderDBRef = FirebaseDBHelper.getPostMessageUnderRef(postID,lisResId!!)
					totalUnderDBRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(p0 : DataSnapshot) {
                            if (p0.hasChildren()) {
                                holder.bindTotalUnder(p0.childrenCount.toString())
                            } else
                                holder.bindTotalUnder("0")
                        }
                        override fun onCancelled(p0 : DatabaseError) {}
                    })

					holder.mViewReply.setOnClickListener {
						context.startActivity(
                            Intent(
                                context.applicationContext,
                                PostMessageUnderActivity::class.java
                            ).putExtra("commenterName", model.commenter_name)
                                .putExtra("commenterImage", model.commenter_image)
                                .putExtra("commentTime", model.time)
                                .putExtra("comment", model.comment)
                                .putExtra("commentLove", model.comment_lovely)
                                .putExtra("commentAble", commentAble)
                                .putExtra("postID", postID)
                                .putExtra("groupID", lisResId)
                                .putExtra("commenter_id", model.commenter_ID)
                                .putExtra("type", "view")
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
					}

					holder.mReply.setOnClickListener {
						context.startActivity(
                            Intent(
                                context.applicationContext,
                                PostMessageUnderActivity::class.java
                            ).putExtra("commenterName", model.commenter_name)
                                .putExtra("commenterImage", model.commenter_image)
                                .putExtra("commentTime", model.time)
                                .putExtra("comment", model.comment)
                                .putExtra("commentLove", model.comment_lovely)
                                .putExtra("commentAble", commentAble)
                                .putExtra("postID", postID)
                                .putExtra("groupID", lisResId)
                                .putExtra("type", "reply")
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
					}

                    showLogDebug(mTag, "Post - $postID List -- $lisResId")

                    var lovely = model.comment_lovely.toInt()
                    val dbRef = FirebaseDBHelper.getPostMessageRef(postID).child(lisResId)
					holder.mLovely.setOnClickListener {
						mp.start()
                        lovely++
                        dbRef.child("comment_lovely").setValue(lovely.toString())
						holder.mLottieAnimationView.visibility = View.VISIBLE
						holder.mLottieAnimationView.bringToFront()
						holder.mLottieAnimationView.playAnimation()
						showLogDebug(mTag, "KEy: $lisResId")
					}
					showLogDebug(
                        mTag, "Holder"
                                + "\nmodel.commenter_image " + model.commenter_image
                                + "\nmodel.commenter_name " + model.commenter_name
                                + "\nmodel.time " + model.time
                    )
				}
			}
		adapterFire.startListening()
		mRecycler.adapter = adapterFire
	}

	fun sendCommentsSetup(postID:String,comments:String,commenterName:String,commenterImg:String) {

		val commentsPush = FirebaseDBHelper.rootRef()
			.child("PostMessage")
			.child(postID).push()
		val commentID = commentsPush.key.toString()

		val commentsRef = "PostMessage/$postID/$commentID"
		val commentUnderRef = "PostMessageUnder/$postID/$commentID"

		val commentMap: MutableMap<String, String> = HashMap()
		commentMap["comment"] = comments
		commentMap["time"] = DateFormat.getDateTimeInstance().format(Date())
		commentMap["commenter_name"] = commenterName
		commentMap["commenter_image"] = commenterImg
		commentMap["commenter_ID"] = AppUser.getUserId()
		commentMap["type"] = "text"
		commentMap["comment_lovely"] = "0"


		val mapGroup: MutableMap<String, Any> = HashMap()
		mapGroup[commentUnderRef] = commentMap
		FirebaseDBHelper.rootRef().updateChildren(mapGroup)

		val mapComment: MutableMap<String, Any> = HashMap()
		mapComment[commentsRef] = commentMap
		FirebaseDBHelper.rootRef().updateChildren(mapComment)
	}
	companion object {
		private const val mTag = "PostMessagePresenter"
	}
}