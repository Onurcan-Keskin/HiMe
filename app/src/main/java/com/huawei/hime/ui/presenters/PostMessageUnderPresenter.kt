package com.huawei.hime.ui.presenters

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.Query
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.postmessageunder.PostMessageUnderViewHolder
import com.huawei.hime.ui.interfaces.IPostMessageUnder
import com.huawei.hime.util.PostMessageDataClass
import com.huawei.hime.util.showLogDebug
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class PostMessageUnderPresenter constructor(private val postMessageUnderContract : IPostMessageUnder.ViewPostMessageUnder) {

	fun onViewsCreate() {
		postMessageUnderContract.initViews()
		postMessageUnderContract.initDB()
	}

	fun setupRecyclerView(
		context : Context,
		query : Query,
		currentID : String,
		mRecycler : RecyclerView,
		postID : String,
		listID : String
	) {
		val mp = MediaPlayer.create(context, R.raw.heart_fall1)
		val options = FirebaseRecyclerOptions.Builder<PostMessageDataClass>()
			.setQuery(query, PostMessageDataClass::class.java).build()
		val adapterFire = object :
			FirebaseRecyclerAdapter<PostMessageDataClass, PostMessageUnderViewHolder>(options) {
			override fun onCreateViewHolder(
				parent : ViewGroup,
				viewType : Int
			) : PostMessageUnderViewHolder {
				val view = LayoutInflater.from(parent.context)
					.inflate(R.layout.single_bottom_under_message_item, parent, false)
				return PostMessageUnderViewHolder(view)
			}

			override fun onBindViewHolder(
				holder : PostMessageUnderViewHolder,
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

				var lovely = model.comment_lovely.toInt()
				val dbRef =
					FirebaseDBHelper.getPostMessageUnderRef(postID, listID).child(lisResId!!)
				holder.mLovely.setOnClickListener {
					mp.start()
					lovely++
					dbRef.child("comment_lovely").setValue(lovely.toString())
					holder.mLottieAnimationView.visibility = View.VISIBLE
					holder.mLottieAnimationView.bringToFront()
					holder.mLottieAnimationView.playAnimation()
					showLogDebug(mTAg, "KEy: $lisResId")
				}


			}
		}
		adapterFire.startListening()
		mRecycler.adapter = adapterFire
	}

	fun sendCommentsSetup(
		postId : String,
		groupID : String,
		comments : String,
		commenterName : String,
		commenterImage : String
	) {
		val commentsPush = FirebaseDBHelper.rootRef()
			.child("PostMessageUnder")
			.child(postId).push()
		val commentID = commentsPush.key.toString()

		val commentUnderRef = "PostMessageUnder/$postId/$groupID/Message/$commentID"

		val commentMap : MutableMap<String, String> = HashMap()
		commentMap["comment"] = comments
		commentMap["time"] = DateFormat.getDateTimeInstance().format(Date())
		commentMap["commenter_name"] = commenterName
		commentMap["commenter_image"] = commenterImage
		commentMap["commenter_ID"] = AppUser.getUserId()
		commentMap["type"] = "text"
		commentMap["comment_lovely"] = "0"


		val mapGroup : MutableMap<String, Any> = HashMap()
		mapGroup[commentUnderRef] = commentMap
		FirebaseDBHelper.rootRef().updateChildren(mapGroup)
	}

	companion object {
		private const val mTAg = "PostMessageUnderPresenter"
	}

}