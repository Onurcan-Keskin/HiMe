package com.huawei.hime.helpers

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object FirebaseDBHelper {

	/* Database References */
	fun rootRef() : DatabaseReference {
		return FirebaseDatabase.getInstance().reference
	}

	fun getUser(userID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("User").child(userID)
	}

	fun onDisconnect(db : DatabaseReference) : Task<Void> {
		return db.child("onlineStatus").onDisconnect().setValue(ServerValue.TIMESTAMP)
	}

	fun onConnect(db : DatabaseReference) : Task<Void> {
		return db.child("onlineStatus").setValue("true")
	}

	fun getUserInfo(userID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("UserInfo").child(userID)
	}

	fun getUserInfoRoot(): DatabaseReference{
		return FirebaseDatabase.getInstance().reference.child("UserInfo")
	}

	fun getUserFeedRootRef() : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("User Feed/")
	}

	fun getVideoFeed(userID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("User Feed/Videos/$userID")
	}

	fun getProfileFeed(userID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("User Feed/Profile Pictures/$userID")
	}

	fun getPhotoFeed(userID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("User Feed/Photos/$userID")
	}

	fun getTagFollowingFeed(tagText : String, currentID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("FollowingTags/$tagText/$currentID")
	}

	fun getTagFollowingNumbers(tagText : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("FollowingTags/$tagText")
	}

	fun getPlacesFollowingFeed(country : String, currentID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("FollowingPlaces/$country/$currentID")
	}

	fun getPlacesFollowingNumbers(country : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("FollowingPlaces/$country")
	}

	fun getTravelFeed(userID : String, country : String, listID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("User Feed/Travel/$userID/$country/$listID")
	}

	fun getTravelRootFeed(userID : String, country : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("User Feed/Travel/$userID/$country")
	}

	fun getLiveFeed(videoID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("Live Streams/Videos").child(videoID)
	}

	fun getFollowing(currentID : String, userID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("Following").child(currentID)
			.child(userID)
	}

	fun getFollowingNumbers(userID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("Following").child(userID)
	}

	fun getFollowed(userID : String, currentID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("Followed").child(userID)
			.child(currentID)
	}

	fun getFollowedNumbers(followerID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("Followed").child(followerID)
	}

	fun getLovelyGiven(userID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("LovelyGiven").child(userID)
	}

	fun getLovelyTaken(takenID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("LovelyTaken").child(takenID)
	}

	fun getLocation(userID : String, userLocationId : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("Location").child(userID)
			.child(userLocationId)
	}

	fun getPostMessageRootRef() : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("PostMessage")
	}

	fun getPostMessageRef(listID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("PostMessage/$listID")
	}

	fun getPostMessageUnderRef(postID : String, listID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("PostMessageUnder/$postID/$listID/Message")
	}

	fun getShareableUploads() : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("uploads/Shareable")
	}

	fun getTagReference(tagText : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("Tag/$tagText")
	}

	fun getTagRootRef(): DatabaseReference{
		return FirebaseDatabase.getInstance().reference.child("Tag")
	}

	fun getEventFeedRef(currentID : String): DatabaseReference{
		return FirebaseDatabase.getInstance().reference.child("User Feed/Events/$currentID")
	}

	fun getActivity(userID : String, userPostId : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("Post").child(userID)
			.child(userPostId)
	}

	fun getMessage(userID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("Messages").child(userID)
	}

	fun sendMessage(userID : String, receiverID : String, messageID : String) : DatabaseReference {
		return FirebaseDatabase.getInstance().reference.child("Messages").child(userID)
			.child(receiverID).child(messageID)
	}

	/* Storage References*/
	fun getFilePathImage(userID : String) : StorageReference {
		return FirebaseStorage.getInstance().reference.child("User").child(userID)
			.child(System.currentTimeMillis().toString())
	}

	fun getThumbFilePathImage(userID : String) : StorageReference {
		return FirebaseStorage.getInstance().reference.child("User").child("Thumbs").child(userID)
			.child(System.currentTimeMillis().toString())
	}
}
