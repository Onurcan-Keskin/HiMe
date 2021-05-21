package com.huawei.hime.profile.profileevent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.ProfileEventDataClass
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.util.viewHolders.EventViewHolder

class ProfileEventPresenter constructor(private val profileEventContract : ProfileEventContract) {

	fun  onViewsCreate(){
		profileEventContract.initViews()
		profileEventContract.switchEvent()
	}

	fun setupRecycler(query : Query, recyclerView : RecyclerView,currentID:String){
		val options = FirebaseRecyclerOptions.Builder<ProfileEventDataClass>()
			.setQuery(query, ProfileEventDataClass::class.java).build()
		val adapterFire =
			object : FirebaseRecyclerAdapter<ProfileEventDataClass, EventViewHolder>(options){
				override fun onCreateViewHolder(
					parent : ViewGroup,
					viewType : Int
				) : EventViewHolder {
					val view = LayoutInflater.from(parent.context)
						.inflate(R.layout.single_post_events_holder, parent, false)
					return EventViewHolder(view)
				}

				override fun onBindViewHolder(
					holder : EventViewHolder,
					position : Int,
					model : ProfileEventDataClass
				) {
					val lisResId = getRef(position).key

					val followerDB = FirebaseDBHelper.getFollowedNumbers(currentID)
					followerDB.addValueEventListener(object : ValueEventListener {
						override fun onCancelled(p0 : DatabaseError) {

						}

						override fun onDataChange(p0 : DataSnapshot) {
							if (p0.hasChildren()) {
								val mTotalFollowers =
									NumberConvertor.prettyCount(p0.childrenCount)
								holder.setFollowers(mTotalFollowers)
							}
						}
					})

					val db = FirebaseDBHelper.getUserInfo(currentID)
					db.addValueEventListener(object :ValueEventListener{
						override fun onDataChange(p0 : DataSnapshot) {
							holder.bindProfile(
								p0.child("photoUrl").value.toString(),
								p0.child("nameSurname").value.toString()
							)
						}

						override fun onCancelled(p0 : DatabaseError) {}
					})


					holder.bindInterests(model.event_interest)
					holder.bindByProfileUserType(
						model.event_coverUrl,
						model.event_photoUrl,
						model.event_picked0,
						model.event_picked1,
						model.event_picked2,
						model.event_picked3,
						model.event_picked4,
						model.event_time,
						model.footer,
						model.event_lovely
					)
					holder.setTag1(model.tag1)
					holder.setTag2(model.tag2)
					holder.setTag3(model.tag3)
					holder.setTag4(model.tag4)
					holder.setTag5(model.tag5)
					val messageDB = FirebaseDBHelper.getPostMessageRef(lisResId!!)
					messageDB.addValueEventListener(object : ValueEventListener {
						override fun onDataChange(p0 : DataSnapshot) {
							if (p0.hasChildren())
								holder.bindTotalMessage(p0.childrenCount.toString())
							else
								holder.bindTotalMessage("0")
						}

						override fun onCancelled(p0 : DatabaseError) {
						}
					})
					val shareableDB = FirebaseDBHelper.getShareableUploads().child(lisResId!!)
					shareableDB.addValueEventListener(object : ValueEventListener {
						override fun onDataChange(p0 : DataSnapshot) {
							val upLovely = p0.child("upload_lovely").value.toString()
							if (upLovely.isNotEmpty()){
							val upLovelyToInt = upLovely.toInt()
							holder.playHearts(upLovelyToInt, shareableDB)}
						}

						override fun onCancelled(p0 : DatabaseError) {
						}
					})
				}
			}
		adapterFire.startListening()
		val snapHelper = LinearSnapHelper()
		snapHelper.attachToRecyclerView(recyclerView)
		recyclerView.adapter = adapterFire
	}
}
 interface ProfileEventContract{
	 fun initViews()
	 fun initDB()
	 fun switchEvent()
 }