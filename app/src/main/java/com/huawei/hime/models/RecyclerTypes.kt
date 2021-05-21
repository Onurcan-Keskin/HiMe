package com.huawei.hime.models

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.ui.activities.BigPlayerActivity
import com.huawei.hime.ui.activities.PostMessageActivity
import com.huawei.hime.util.*
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.util.viewHolders.*


object RecyclerTypes {

	fun setupRecyclerImage(query : Query, context : Context, recyclerView : RecyclerView) {
		val options = FirebaseRecyclerOptions.Builder<UploadImageDataClass>()
			.setQuery(query, UploadImageDataClass::class.java).build()
		val adapterFire =
			object : FirebaseRecyclerAdapter<UploadImageDataClass, ImageViewHolder>(options) {
				override fun onCreateViewHolder(
					parent : ViewGroup,
					viewType : Int
				) : ImageViewHolder {
					val view = LayoutInflater.from(parent.context)
						.inflate(R.layout.single_posted_card, parent, false)
					return ImageViewHolder(view)
				}

				override fun onBindViewHolder(
					holder : ImageViewHolder,
					position : Int,
					model : UploadImageDataClass
				) {
					val lisResId = getRef(position).key
					when (model.uploadType) {
						"image" -> {
							holder.bindImage(
								model.upload_posterImage,
								model.upload_posterName,
								model.upload_imageUrl,
								model.upload_footer,
								model.upload_lovely,
								model.popularity
							)
							holder.setTag1(model.tag1)
							holder.setTag2(model.tag2)
							holder.setTag3(model.tag3)
							holder.setTag4(model.tag4)
							holder.setTag5(model.tag5)

							val followerDB = FirebaseDBHelper.getFollowedNumbers(model.uploaderID)
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

							holder.mMessage.setOnClickListener {
								context.startActivity(
									Intent(
										context,
										PostMessageActivity::class.java
									).putExtra("postID", lisResId)
										.putExtra("comment", model.commentsAllowed)
										.putExtra("uploadType", model.uploadType)
										.putExtra("upload_imageUrl", model.upload_imageUrl)
										.putExtra("uploaderID", model.uploaderID)
								)
								showLogDebug(
									mTAG,
									"PostID: $lisResId"
								)
							}

							holder.parent.setOnClickListener {
								showLogDebug(
									mTAG,
									"PostID: $lisResId, " +
											"Popularity: ${model.popularity}, " +
											"Model: ${model.uploadType}"
								)
								context.startActivity(
									Intent(
										context.applicationContext,
										BigPlayerActivity::class.java
									)
										.putExtra("postID", lisResId)
										.putExtra("postUploader", model.uploaderID)
										.putExtra("postType", model.uploadType)
										.putExtra("postImage", model.upload_imageUrl)
								)
							}

							showLogDebug(
								mTAG,
								"\nTag1 " + model.tag1
										+ "\nTag2 " + model.tag2
										+ "\nTag3 " + model.tag3
										+ "\nTag4 " + model.tag4
										+ "\nTag5 " + model.tag5
										+ "\nuploadType " + model.uploadType
							)
						}
					}
				}
			}
		adapterFire.startListening()
		val snapHelper : SnapHelper = LinearSnapHelper()
		snapHelper.attachToRecyclerView(recyclerView)
		recyclerView.adapter = adapterFire
	}

	fun setupRecyclerMap(query : Query, context : Context, recyclerView : RecyclerView) {
		val options = FirebaseRecyclerOptions.Builder<UploadTravelDataClass>()
			.setQuery(query, UploadTravelDataClass::class.java).build()
		val adapterFire =
			object : FirebaseRecyclerAdapter<UploadTravelDataClass, TravelViewHolder>(options) {
				override fun onCreateViewHolder(
					parent : ViewGroup,
					viewType : Int
				) : TravelViewHolder {
					val view = LayoutInflater.from(parent.context)
						.inflate(R.layout.single_posted_travel_card, parent, false)
					return TravelViewHolder(view)
				}

				override fun onBindViewHolder(
					holder : TravelViewHolder,
					position : Int,
					model : UploadTravelDataClass
				) {
					val lisResId = getRef(position).key

					when (model.uploadType) {
						"map" -> {
							holder.bindView(
								model.upload_footer,
								model.upload_travel_latitude,
								model.upload_travel_longitude,
								model.upload_travel_address,
								model.popularity,
								model.upload_posterName,
								model.upload_posterImage,
								model.upload_imageUrl,
								model.tag1,
								model.tag2,
								model.tag3,
								model.tag4,
								model.tag5,
								model.upload_lovely
							)
							val followerDB = FirebaseDBHelper.getFollowedNumbers(model.uploaderID)
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
									val upLovelyToInt = upLovely.toInt()
									holder.playHearts(upLovelyToInt, shareableDB)
								}

								override fun onCancelled(p0 : DatabaseError) {
								}
							})
							holder.shareMapContent(
								model.upload_travel_latitude,
								model.upload_travel_longitude
							)
							holder.postMessage!!.setOnClickListener {
								context.startActivity(
									Intent(
										context,
										PostMessageActivity::class.java
									).putExtra("postID", lisResId)
										.putExtra("comment", model.commentsAllowed)
										.putExtra("uploadType", model.uploadType)
										.putExtra("upload_imageUrl", model.upload_imageUrl)
										.putExtra("uploaderID", model.uploaderID)
								)
								showLogDebug(
									mTAG,
									"PostID: $lisResId"
								)
							}

							holder.parent.setOnClickListener {
								showLogDebug(
									mTAG,
									"PostID: $lisResId, " +
											"Popularity: ${model.popularity}, " +
											"Model: ${model.uploadType}"
								)
								context.startActivity(
									Intent(
										context.applicationContext,
										BigPlayerActivity::class.java
									)
										.putExtra("postID", lisResId)
										.putExtra("postUploader", model.uploaderID)
										.putExtra("postType", model.uploadType)
										.putExtra("postImage", model.upload_imageUrl)
										.putExtra("upload_lat", model.upload_travel_latitude)
										.putExtra("upload_long", model.upload_travel_longitude)
								)
							}
						}
						else -> {
							holder.itemView.visibility = View.GONE
							holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
							recyclerView.requestLayout()
						}
					}
				}
			}
		adapterFire.startListening()
		val snapHelper : SnapHelper = LinearSnapHelper()
		snapHelper.attachToRecyclerView(recyclerView)
		recyclerView.adapter = adapterFire
	}

	fun setupRecyclerEvent(query : Query, context : Context, recyclerView : RecyclerView) {
		val options = FirebaseRecyclerOptions.Builder<UploadEventDataClass>()
			.setQuery(query, UploadEventDataClass::class.java).build()
		val adapterFire =
			object : FirebaseRecyclerAdapter<UploadEventDataClass, EventViewHolder>(options) {
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
					model : UploadEventDataClass
				) {
					val lisResId = getRef(position).key

					when (model.uploadType) {
						"event" -> {
							val followerDB = FirebaseDBHelper.getFollowedNumbers(model.uploaderID)
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
							holder.bindInterests(model.upload_event_interest)
							holder.bindEvents(
								model.upload_event_coverUrl,
								model.upload_event_photoUrl,
								model.upload_event_picked0,
								model.upload_event_picked1,
								model.upload_event_picked2,
								model.upload_event_picked3,
								model.upload_event_picked4,
								model.upload_event_time,
								model.upload_footer,
								model.upload_lovely,
								model.popularity,
								model.upload_posterName,
								model.upload_posterImage
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
									val upLovelyToInt = upLovely.toInt()
									holder.playHearts(upLovelyToInt, shareableDB)
								}

								override fun onCancelled(p0 : DatabaseError) {
								}
							})
							holder.mMessage.setOnClickListener {
								context.startActivity(
									Intent(
										context,
										PostMessageActivity::class.java
									).putExtra("postID", lisResId)
										.putExtra("comment", model.commentsAllowed)
										.putExtra("uploadType", model.uploadType)
										.putExtra("upload_imageUrl", model.upload_event_coverUrl)
										.putExtra("uploaderID", model.uploaderID)
								)
							}
						}
						else -> {
							holder.itemView.visibility = View.GONE
							holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
							recyclerView.requestLayout()
						}
					}
				}
			}
		adapterFire.startListening()
		val snapHelper : SnapHelper = LinearSnapHelper()
		snapHelper.attachToRecyclerView(recyclerView)
		recyclerView.adapter = adapterFire
	}

	fun setupRecyclerVideos(query : Query, context : Context, recyclerView : RecyclerView) {
		val options = FirebaseRecyclerOptions.Builder<UploadVideoDataClass>()
			.setQuery(query, UploadVideoDataClass::class.java).build()
		val adapterFire =
			object : FirebaseRecyclerAdapter<UploadVideoDataClass, VideoViewHolder>(options) {
				override fun onCreateViewHolder(
					parent : ViewGroup,
					viewType : Int
				) : VideoViewHolder {
					val view = LayoutInflater.from(parent.context)
						.inflate(R.layout.single_post_card_video, parent, false)
					return VideoViewHolder(view)
				}

				override fun onBindViewHolder(
					holder : VideoViewHolder,
					position : Int,
					model : UploadVideoDataClass
				) {
					val lisResId = getRef(position).key
					when (model.uploadType) {
						"video" -> {
							holder.setPosterImage(model.upload_posterImage)
							holder.setPosterName(model.upload_posterName)
							holder.setTag1(model.tag1)
							holder.setTag2(model.tag2)
							holder.setTag3(model.tag3)
							holder.setTag4(model.tag4)
							holder.setTag5(model.tag5)
							holder.setFooter(model.upload_footer)
							holder.bindVideo(model.upload_videoUrl)
							holder.setLovely(model.upload_lovely)
							holder.setPopularity(model.popularity)
//                    holder.mLovelyLyt!!.setOnClickListener {
//                        holder.playHearts()
//                    }
							val followerDB = FirebaseDBHelper.getFollowedNumbers(model.uploaderID)
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
							holder.mFullscreen!!.setOnClickListener {
								showLogDebug(
									mTAG,
									"PostID $lisResId"
								)
								context.startActivity(
									Intent(
										context.applicationContext,
										BigPlayerActivity::class.java
									)
										.putExtra("postID", lisResId)
										.putExtra("postVideo", model.upload_videoUrl)
										.putExtra("postUploader", model.uploaderID)
								)
							}
							holder.mMessage!!.setOnClickListener {
								context.startActivity(
									Intent(
										context,
										PostMessageActivity::class.java
									).putExtra("postID", lisResId)
										.putExtra("comment", model.commentsAllowed)
										.putExtra("uploadType", model.uploadType)
										.putExtra("upload_videoUrl", model.upload_videoUrl)
										.putExtra("uploaderID", model.uploaderID)
								)
								showLogDebug(
									mTAG,
									"PostID: $lisResId"
								)
							}
						}
						else -> {
							holder.itemView.visibility = View.GONE
							holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
							recyclerView.requestLayout()
						}
					}
				}
			}
		adapterFire.startListening()
		val snapHelper : SnapHelper = LinearSnapHelper()
		snapHelper.attachToRecyclerView(recyclerView)
		recyclerView.adapter = adapterFire
	}

	fun setupRecyclerHybrid(query : Query, context : Context, recyclerView : RecyclerView) {

		val options = FirebaseRecyclerOptions.Builder<HybridUploadDataClass>()
			.setQuery(query, HybridUploadDataClass::class.java).build()
		val adapterFire =
			object : FirebaseRecyclerAdapter<HybridUploadDataClass, HybridViewHolder>(options) {
				override fun onCreateViewHolder(
					parent : ViewGroup,
					viewType : Int
				) : HybridViewHolder {
					val view = LayoutInflater.from(parent.context)
						.inflate(R.layout.single_post_hybrid_card, parent, false)
					return HybridViewHolder(
						view
					)
				}

				override fun onBindViewHolder(
					holder : HybridViewHolder,
					position : Int,
					model : HybridUploadDataClass
				) {
					val lisResId = getRef(position).key
					holder.bindGlobalValues(
						model.upload_posterName,
						model.upload_posterImage,
						model.popularity,
						model.upload_lovely,
						model.upload_footer,
						model.tag1,
						model.tag2,
						model.tag3,
						model.tag4,
						model.tag5
					)
					val followerDB = FirebaseDBHelper.getFollowedNumbers(model.uploaderID)
					followerDB.addValueEventListener(object : ValueEventListener {
						override fun onCancelled(p0 : DatabaseError) {

						}

						override fun onDataChange(p0 : DataSnapshot) {
							if (p0.hasChildren()) {
								val mTotalFollowers = NumberConvertor.prettyCount(p0.childrenCount)
								holder.bindFollowers(mTotalFollowers)
							}
						}
					})
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
							val upLovelyToInt = upLovely.toInt()
							holder.giveLovely(upLovelyToInt, shareableDB)
						}

						override fun onCancelled(p0 : DatabaseError) {
						}
					})
					when (model.uploadType) {
						"image" -> {
							holder.parent.setOnClickListener {
								showLogDebug(
									mTAG,
									"PostID: $lisResId, " +
											"Popularity: ${model.popularity}, " +
											"Model: ${model.uploadType}"
								)
								context.startActivity(
									Intent(
										context.applicationContext,
										BigPlayerActivity::class.java
									)
										.putExtra("postID", lisResId)
										.putExtra("postUploader", model.uploaderID)
										.putExtra("postType", model.uploadType)
										.putExtra("postImage", model.upload_imageUrl)
										.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
								)
							}
							holder.shareImageContent(model.upload_imageUrl)
							holder.bindImage(model.upload_imageUrl)
							holder.mMessage.setOnClickListener {
								context.startActivity(
									Intent(
										context,
										PostMessageActivity::class.java
									).putExtra("postID", lisResId)
										.putExtra("comment", model.commentsAllowed)
										.putExtra("uploadType", model.uploadType)
										.putExtra("upload_imageUrl", model.upload_imageUrl)
										.putExtra("uploaderID", model.uploaderID)

								)
								showLogDebug(
									mTAG,
									"PostID: $lisResId"
								)
							}
						}
						"video" -> {
							holder.parent.setOnClickListener {
								showLogDebug(
									mTAG,
									"PostID: $lisResId, " +
											"Popularity: ${model.popularity}, " +
											"Model: ${model.uploadType}"
								)
							}
							holder.bindVideo(model.upload_videoUrl)
							holder.mMessage.setOnClickListener {
								context.startActivity(
									Intent(
										context,
										PostMessageActivity::class.java
									).putExtra("postID", lisResId)
										.putExtra("comment", model.commentsAllowed)
										.putExtra("uploadType", model.uploadType)
										.putExtra("upload_videoUrl", model.upload_videoUrl)
										.putExtra("uploaderID", model.uploaderID)
								)
								showLogDebug(
									mTAG,
									"PostID: $lisResId"
								)
							}
							holder.mFullscreen.setOnClickListener {
								showLogDebug(
									mTAG,
									"PostID $lisResId"
								)
								context.startActivity(
									Intent(
										context.applicationContext,
										BigPlayerActivity::class.java
									)
										.putExtra("postID", lisResId)
										.putExtra("postUploader", model.uploaderID)
										.putExtra("postType", model.uploadType)
										.putExtra("postVideo", model.upload_videoUrl)
								)
							}
						}
						"map" -> {
							holder.parent.setOnClickListener {
								showLogDebug(
									mTAG,
									"PostID: $lisResId, " +
											"Popularity: ${model.popularity}, " +
											"Model: ${model.uploadType}"
								)
								holder.shareMapContent(
									model.upload_travel_latitude,
									model.upload_travel_longitude
								)
								context.startActivity(
									Intent(
										context.applicationContext,
										BigPlayerActivity::class.java
									)
										.putExtra("postID", lisResId)
										.putExtra("postUploader", model.uploaderID)
										.putExtra("postType", model.uploadType)
										.putExtra("postImage", model.upload_imageUrl)
										.putExtra("upload_lat", model.upload_travel_latitude)
										.putExtra("upload_long", model.upload_travel_longitude)
								)
							}
							holder.bindMap(
								model.upload_travel_latitude,
								model.upload_travel_longitude,
								model.upload_travel_address,
								model.upload_imageUrl
							)
							holder.mMessage.setOnClickListener {
								context.startActivity(
									Intent(
										context,
										PostMessageActivity::class.java
									).putExtra("postID", lisResId)
										.putExtra("comment", model.commentsAllowed)
										.putExtra("uploadType", model.uploadType)
										.putExtra("upload_imageUrl", model.upload_imageUrl)
										.putExtra("uploaderID", model.uploaderID)
										.putExtra(
											"upload_lat",
											model.upload_travel_latitude
										)
										.putExtra(
											"upload_long",
											model.upload_travel_longitude
										)
								)
								showLogDebug(
									mTAG,
									"PostID: $lisResId"
								)
							}
						}
						"event" -> {
							holder.parent.setOnClickListener {
								showLogDebug(
									mTAG,
									"PostID: $lisResId, " +
											"Popularity: ${model.popularity}, " +
											"Model: ${model.uploadType}"
								)
								context.startActivity(
									Intent(
										context.applicationContext,
										BigPlayerActivity::class.java
									)
										.putExtra("postID", lisResId)
										.putExtra("postUploader", model.uploaderID)
										.putExtra("postType", model.uploadType)
										.putExtra("postImage", model.upload_event_coverUrl)
										.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
								)
							}

							holder.mViewPager.setOnClickListener {
								context.startActivity(
									Intent(
										context.applicationContext,
										BigPlayerActivity::class.java
									)
										.putExtra("postID", lisResId)
										.putExtra("postUploader", model.uploaderID)
										.putExtra("postType", model.uploadType)
										.putExtra("postImage", model.upload_event_coverUrl)
										.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
								)
							}
							holder.bindEvents(
								model.upload_event_coverUrl,
								model.upload_event_photoUrl,
								model.upload_event_picked0,
								model.upload_event_picked1,
								model.upload_event_picked2,
								model.upload_event_picked3,
								model.upload_event_picked4,
								model.upload_event_time
							)
							holder.bindInterests(model.upload_event_interest)
							holder.mMessage.setOnClickListener {
								context.startActivity(
									Intent(
										context,
										PostMessageActivity::class.java
									).putExtra("postID", lisResId)
										.putExtra("comment", model.commentsAllowed)
										.putExtra("uploadType", model.uploadType)
										.putExtra("upload_imageUrl", model.upload_event_coverUrl)
										.putExtra("uploaderID", model.uploaderID)
								)
								showLogDebug(
									mTAG,
									"PostID: $lisResId"
								)
							}
						}
						else -> {
							holder.itemView.visibility = View.GONE
							holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
							recyclerView.requestLayout()
						}
					}
				}
			}
		adapterFire.startListening()
		val snapHelper : SnapHelper = LinearSnapHelper()
		snapHelper.attachToRecyclerView(recyclerView)
		recyclerView.adapter = adapterFire
	}

	fun setupRecyclerGallery(query : Query, context : Context, recyclerView : RecyclerView) {
		val options = FirebaseRecyclerOptions.Builder<HybridUploadDataClass>()
			.setQuery(query, HybridUploadDataClass::class.java).build()
		val adapterFire =
			object : FirebaseRecyclerAdapter<HybridUploadDataClass, HybridViewHolder>(options) {
				override fun onCreateViewHolder(
					parent : ViewGroup,
					viewType : Int
				) : HybridViewHolder {
					val view = LayoutInflater.from(parent.context)
						.inflate(R.layout.single_post_hybrid_card, parent, false)
					return HybridViewHolder(
						view
					)
				}

				override fun onBindViewHolder(
					holder : HybridViewHolder,
					position : Int,
					model : HybridUploadDataClass
				) {
					val lisResId = getRef(position).key

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

					when (model.uploadType) {
						"image" -> {
							holder.bindGlobalValues(
								model.upload_posterName,
								model.upload_posterImage,
								model.popularity,
								model.upload_lovely,
								model.upload_footer,
								model.tag1,
								model.tag2,
								model.tag3,
								model.tag4,
								model.tag5
							)
							val followerDB = FirebaseDBHelper.getFollowedNumbers(model.uploaderID)
							followerDB.addValueEventListener(object : ValueEventListener {
								override fun onCancelled(p0 : DatabaseError) {

								}

								override fun onDataChange(p0 : DataSnapshot) {
									if (p0.hasChildren()) {
										val mTotalFollowers =
											NumberConvertor.prettyCount(p0.childrenCount)
										holder.bindFollowers(mTotalFollowers)
									}
								}
							})
							holder.parent.setOnClickListener {
								showLogDebug(
									mTAG,
									"PostID: $lisResId, " +
											"Popularity: ${model.popularity}, " +
											"Model: ${model.uploadType}"
								)
								context.startActivity(
									Intent(
										context.applicationContext,
										BigPlayerActivity::class.java
									)
										.putExtra("postID", lisResId)
										.putExtra("postUploader", model.uploaderID)
										.putExtra("postType", model.uploadType)
										.putExtra("postImage", model.upload_imageUrl)
										.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
								)
							}
							holder.bindImage(model.upload_imageUrl)
							holder.mMessage.setOnClickListener {
								context.startActivity(
									Intent(
										context,
										PostMessageActivity::class.java
									).putExtra("postID", lisResId)
										.putExtra("comment", model.commentsAllowed)
										.putExtra("uploadType", model.uploadType)
										.putExtra("upload_imageUrl", model.upload_imageUrl)
										.putExtra("uploaderID", model.uploaderID)

								)
								showLogDebug(
									mTAG,
									"PostID: $lisResId"
								)
							}
						}
						"video" -> {
							holder.bindGlobalValues(
								model.upload_posterName,
								model.upload_posterImage,
								model.popularity,
								model.upload_lovely,
								model.upload_footer,
								model.tag1,
								model.tag2,
								model.tag3,
								model.tag4,
								model.tag5
							)
							val followerDB = FirebaseDBHelper.getFollowedNumbers(model.uploaderID)
							followerDB.addValueEventListener(object : ValueEventListener {
								override fun onCancelled(p0 : DatabaseError) {

								}

								override fun onDataChange(p0 : DataSnapshot) {
									if (p0.hasChildren()) {
										val mTotalFollowers =
											NumberConvertor.prettyCount(p0.childrenCount)
										holder.bindFollowers(mTotalFollowers)
									}
								}
							})
							holder.parent.setOnClickListener {
								showLogDebug(
									mTAG,
									"PostID: $lisResId, " +
											"Popularity: ${model.popularity}, " +
											"Model: ${model.uploadType}"
								)
							}
							holder.bindVideo(model.upload_videoUrl)
							holder.mMessage.setOnClickListener {
								context.startActivity(
									Intent(
										context,
										PostMessageActivity::class.java
									).putExtra("postID", lisResId)
										.putExtra("comment", model.commentsAllowed)
										.putExtra("uploadType", model.uploadType)
										.putExtra("upload_videoUrl", model.upload_videoUrl)
										.putExtra("uploaderID", model.uploaderID)
								)
								showLogDebug(
									mTAG,
									"PostID: $lisResId"
								)
							}
							holder.mFullscreen.setOnClickListener {
								showLogDebug(
									mTAG,
									"PostID $lisResId"
								)
								context.startActivity(
									Intent(
										context.applicationContext,
										BigPlayerActivity::class.java
									)
										.putExtra("postID", lisResId)
										.putExtra("postUploader", model.uploaderID)
										.putExtra("postType", model.uploadType)
										.putExtra("postVideo", model.upload_videoUrl)
								)
							}
						}
						else -> {
							holder.itemView.visibility = View.GONE
							holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
							recyclerView.requestLayout()
						}
					}
				}
			}
		adapterFire.startListening()
		val snapHelper : SnapHelper = LinearSnapHelper()
		snapHelper.attachToRecyclerView(recyclerView)
		recyclerView.adapter = adapterFire
	}

	object TypeSingleSearch {

		fun setupRecyclerSearchGallery(
			query : Query,
			context : Context,
			recyclerView : RecyclerView
		) {
			val options = FirebaseRecyclerOptions.Builder<HybridUploadDataClass>()
				.setQuery(query, HybridUploadDataClass::class.java).build()
			val adapterFire =
				object :
					FirebaseRecyclerAdapter<HybridUploadDataClass, SearchHybridViewHolder>(options) {
					override fun onCreateViewHolder(
						parent : ViewGroup,
						viewType : Int
					) : SearchHybridViewHolder {
						val view = LayoutInflater.from(parent.context)
							.inflate(R.layout.single_hybrid_card, parent, false)
						return SearchHybridViewHolder(view)
					}

					override fun onBindViewHolder(
						holder : SearchHybridViewHolder,
						position : Int,
						model : HybridUploadDataClass
					) {
						val lisResId = getRef(position).key

						when (model.typeGroup) {
							"1" -> {
								/* Video - Image */
								when (model.uploadType) {
									"image" -> {
										holder.bindImageHolder(model.upload_imageUrl)
										holder.parent.setOnClickListener {
											context.startActivity(
												Intent(
													context.applicationContext,
													BigPlayerActivity::class.java
												)
													.putExtra("postID", lisResId)
													.putExtra("postUploader", model.uploaderID)
													.putExtra("postType", model.uploadType)
													.putExtra("postImage", model.upload_imageUrl)
													.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
											)
										}
									}
									"video" -> {
										holder.bindVideoHolder(model.upload_videoUrl)
										holder.parent.setOnClickListener {
											context.startActivity(
												Intent(
													context.applicationContext,
													BigPlayerActivity::class.java
												)
													.putExtra("postID", lisResId)
													.putExtra("postUploader", model.uploaderID)
													.putExtra("postType", model.uploadType)
													.putExtra("postVideo", model.upload_videoUrl)
													.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
											)
										}
									}
									else -> {
										holder.emptyHolder()
										holder.itemView.visibility = View.GONE
										holder.itemView.layoutParams =
											RecyclerView.LayoutParams(0, 0)
										recyclerView.requestLayout()
									}
								}
							}
							else -> {
								holder.emptyHolder()
								holder.itemView.visibility = View.GONE
								holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
								recyclerView.requestLayout()
							}
						}
					}
				}
			adapterFire.startListening()
			recyclerView.adapter = adapterFire
		}

		fun setupRecyclerSearchEvent(
			query : Query,
			context : Context,
			recyclerView : RecyclerView
		) {
			val options = FirebaseRecyclerOptions.Builder<HybridUploadDataClass>()
				.setQuery(query, HybridUploadDataClass::class.java).build()
			val adapterFire =
				object :
					FirebaseRecyclerAdapter<HybridUploadDataClass, SearchHybridViewHolder>(options) {
					override fun onCreateViewHolder(
						parent : ViewGroup,
						viewType : Int
					) : SearchHybridViewHolder {
						val view = LayoutInflater.from(parent.context)
							.inflate(R.layout.single_hybrid_card, parent, false)
						return SearchHybridViewHolder(view)
					}

					override fun onBindViewHolder(
						holder : SearchHybridViewHolder,
						position : Int,
						model : HybridUploadDataClass
					) {
						val lisResId = getRef(position).key

						when (model.typeGroup) {
							"2" -> {
								/* Video - Image */
								when (model.uploadType) {
									"image" -> {

									}
									"video" -> {

									}
									"map" -> {

									}
									else -> {
										holder.emptyHolder()
										holder.itemView.visibility = View.GONE
										holder.itemView.layoutParams =
											RecyclerView.LayoutParams(0, 0)
										recyclerView.requestLayout()
									}
								}
							}
							else -> {
								holder.emptyHolder()
								holder.itemView.visibility = View.GONE
								holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
								recyclerView.requestLayout()
							}
						}
					}
				}
			adapterFire.startListening()
			recyclerView.adapter = adapterFire
		}

		fun setupRecyclerSearchMap(query : Query, context : Context, recyclerView : RecyclerView) {
			val options = FirebaseRecyclerOptions.Builder<HybridUploadDataClass>()
				.setQuery(query, HybridUploadDataClass::class.java).build()
			val adapterFire =
				object :
					FirebaseRecyclerAdapter<HybridUploadDataClass, SearchHybridViewHolder>(options) {
					override fun onCreateViewHolder(
						parent : ViewGroup,
						viewType : Int
					) : SearchHybridViewHolder {
						val view = LayoutInflater.from(parent.context)
							.inflate(R.layout.single_hybrid_card, parent, false)
						return SearchHybridViewHolder(view)
					}

					override fun onBindViewHolder(
						holder : SearchHybridViewHolder,
						position : Int,
						model : HybridUploadDataClass
					) {
						val lisResId = getRef(position).key

						when (model.typeGroup) {
							"0" -> {
								/* Map */
								holder.bindMapHolder(model.upload_imageUrl)
								holder.parent.setOnClickListener {
									context.startActivity(
										Intent(
											context.applicationContext,
											BigPlayerActivity::class.java
										)
											.putExtra("postID", lisResId)
											.putExtra("postUploader", model.uploaderID)
											.putExtra("postType", model.uploadType)
											.putExtra("postImage", model.upload_imageUrl)
											.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
									)
								}
							}
							else -> {
								holder.emptyHolder()
								holder.itemView.visibility = View.GONE
								holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
								recyclerView.requestLayout()
							}
						}
					}

					fun removeAt(position : Int) {
						//try to store this item
						recyclerView.removeViewAt(position)
						notifyItemRemoved(position)
					}
				}
			adapterFire.startListening()
			recyclerView.adapter = adapterFire
		}

		fun setupRecyclerTagHybrid(
			query : Query,
			context : Context,
			recyclerView : RecyclerView,
			tagText : String
		) {

			val options = FirebaseRecyclerOptions.Builder<HybridUploadDataClass>()
				.setQuery(query, HybridUploadDataClass::class.java).build()
			val adapterFire =
				object :
					FirebaseRecyclerAdapter<HybridUploadDataClass, SearchHybridViewHolder>(options) {
					override fun onCreateViewHolder(
						parent : ViewGroup,
						viewType : Int
					) : SearchHybridViewHolder {
						val view = LayoutInflater.from(parent.context)
							.inflate(R.layout.single_hybrid_card, parent, false)
						return SearchHybridViewHolder(view)
					}

					override fun onBindViewHolder(
						holder : SearchHybridViewHolder,
						position : Int,
						model : HybridUploadDataClass
					) {
						val lisResId = getRef(position).key
						if (model.tag1 == tagText
							|| model.tag2 == tagText
							|| model.tag3 == tagText
							|| model.tag4 == tagText
							|| model.tag5 == tagText
						) {
							when (model.uploadType) {
								"image" -> {
									holder.bindImageHolder(model.upload_imageUrl)
									holder.parent.setOnClickListener {
										context.startActivity(
											Intent(
												context.applicationContext,
												BigPlayerActivity::class.java
											)
												.putExtra("postID", lisResId)
												.putExtra("postUploader", model.uploaderID)
												.putExtra("postType", model.uploadType)
												.putExtra("postImage", model.upload_imageUrl)
												.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
										)
									}
								}
								"map" -> {
									holder.bindMapHolder(model.upload_imageUrl)
									holder.parent.setOnClickListener {
										context.startActivity(
											Intent(
												context.applicationContext,
												BigPlayerActivity::class.java
											)
												.putExtra("postID", lisResId)
												.putExtra("postUploader", model.uploaderID)
												.putExtra("postType", model.uploadType)
												.putExtra("postImage", model.upload_imageUrl)
										)
									}
								}
								"video" -> {
									holder.bindVideoHolder(model.upload_videoUrl)
									holder.parent.setOnClickListener {
										context.startActivity(
											Intent(
												context.applicationContext,
												BigPlayerActivity::class.java
											)
												.putExtra("postID", lisResId)
												.putExtra("postUploader", model.uploaderID)
												.putExtra("postType", model.uploadType)
												.putExtra("postVideo", model.upload_videoUrl)
										)
									}
								}
								"event" -> {
									holder.bindEventHolder(
										model.upload_event_coverUrl,
										model.upload_event_photoUrl,
										model.upload_event_picked0,
										model.upload_event_picked1,
										model.upload_event_picked2,
										model.upload_event_picked3,
										model.upload_event_picked4
									)
									holder.parent.setOnClickListener {
										context.startActivity(
											Intent(
												context.applicationContext,
												BigPlayerActivity::class.java
											)
												.putExtra("postID", lisResId)
												.putExtra("postUploader", model.uploaderID)
												.putExtra("postType", model.uploadType)
												.putExtra("postImage", model.upload_event_coverUrl)
										)
									}
								}
							}
						} else {
							holder.emptyHolder()
							holder.parent.visibility = View.GONE
							holder.parent.layoutParams = RecyclerView.LayoutParams(0, 0)
						}
					}

					override fun onDataChanged() {
						super.onDataChanged()
						notifyDataSetChanged()
					}
				}
			adapterFire.startListening()
			recyclerView.adapter = adapterFire
		}
	}

	const val mTAG = "RecyclerTypes"
}
