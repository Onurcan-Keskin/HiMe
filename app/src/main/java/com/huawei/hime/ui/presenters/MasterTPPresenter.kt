package com.huawei.hime.masterfollowers.masterTP

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.huawei.hime.R
import com.huawei.hime.ui.activities.SingleUserActivity
import com.huawei.hime.search.usersearch.UserSearchViewHolder
import com.huawei.hime.util.FollowDataClass
import com.huawei.hime.util.FollowTypes
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.util.showLogDebug

class MasterTPPresenter constructor(private val masterTPContract : MasterTPContract) {

	fun onViewsCreate() {
		masterTPContract.initViews()
	}

	private var mFollowingSince : String = "0"
	private var mFollowerNumber : String = "0"

	fun bindTP(
		context : Context,
		tpName: String,
		textView : TextView,
		titleString : String,
		query : Query,
		recyclerView : RecyclerView,
		searchedString : String,
		currentID : String
	) {
		val mp = MediaPlayer.create(context.applicationContext, R.raw.follow)
		when(tpName){
			"tag" ->{
				textView.text = "#" + titleString
			}
			"places"->{
				textView.text = titleString
			}
		}
		val options = FirebaseRecyclerOptions.Builder<FollowDataClass>()
			.setQuery(query, FollowDataClass::class.java).build()
		val adapterFire =
			object : FirebaseRecyclerAdapter<FollowDataClass, UserSearchViewHolder>(options) {
				override fun onCreateViewHolder(
					parent : ViewGroup,
					viewType : Int
				) : UserSearchViewHolder {
					val view = LayoutInflater.from(parent.context)
						.inflate(R.layout.single_find_user_card, parent, false)
					return UserSearchViewHolder(view)
				}

				override fun onBindViewHolder(
					holder : UserSearchViewHolder,
					position : Int,
					model : FollowDataClass
				) {
					val lisResUid = getRef(position).key
					val mName = model.name.decapitalize()


					if (mName.contains(searchedString.decapitalize())) {
						if (model.name == "" || model.name.isBlank() || model.name.isEmpty()) {
							holder.setName(context.getString(R.string.himeUser))
						} else {
							if (currentID == lisResUid) {
								holder.setName(model.name + " (" + context.getString(R.string.me) + ")")
								holder.mFollowBtn!!.visibility = View.GONE
								holder.mFollowingSince!!.visibility = View.GONE
							} else {
								holder.setName(model.name)
							}
						}
						holder.setImage(model.image)
						holder.setAge(model.age)
						holder.setLovely(model.lovely)


						val mFollowerTableRef : DatabaseReference =
							FirebaseDBHelper.getFollowing(currentID, lisResUid!!)
						mFollowerTableRef.keepSynced(true)

						mFollowerTableRef.addValueEventListener(object : ValueEventListener {
							override fun onCancelled(p0 : DatabaseError) {

							}

							override fun onDataChange(p0 : DataSnapshot) {
								if (p0.hasChildren()) {
									showLogDebug("Child", p0.childrenCount.toString())
									holder.mFollowBtn!!.visibility = View.GONE
									holder.mFollowingSince!!.visibility = View.VISIBLE
									holder.mLottie.visibility = View.VISIBLE
									mFollowingSince = p0.child("following_since").value.toString()
									holder.setFollowingSince(mFollowingSince)
									showLogDebug("TAG", mFollowingSince)
								}
							}
						})

						holder.mFollowBtn!!.setOnClickListener {
							mp.start()
							holder.mFollowBtn!!.visibility = View.GONE
							holder.mLottie.visibility = View.VISIBLE
							FollowTypes.followUsers(currentID, lisResUid)
							notifyDataSetChanged()
						}
						FollowTypes.followerNumbs(currentID)
						holder.setFollower(mFollowerNumber)

						holder.parent.setOnClickListener {
							context.startActivity(
								Intent(
									context,
									SingleUserActivity::class.java
								).putExtra("userID", lisResUid)
							)
						}
					} else {
						holder.itemView.visibility = View.GONE
						holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
					}
				}
			}
		adapterFire.startListening()
		recyclerView.adapter = adapterFire
	}
}

interface MasterTPContract {
	fun initViews()
	fun initTagDB(string : String)
}