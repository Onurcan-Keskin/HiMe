package com.huawei.hime.util

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.helpers.FirebaseDBHelper
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

object FollowTypes {

    fun unFollowUsers(currentID: String, userID: String) {
        val mFollowing = FirebaseDBHelper.getFollowing(currentID, userID)
        val mFollower = FirebaseDBHelper.getFollowed(userID, currentID)

        mFollowing.removeValue()
        mFollower.removeValue()
    }

    fun unFollowTags(tag: String, currentID: String) {
        val mFollowingTag = FirebaseDBHelper.getTagFollowingFeed(tag, currentID)
        mFollowingTag.removeValue()
    }

    fun unFollowPlaces(country: String,userID: String){
        val mFollowingPlaces = FirebaseDBHelper.getPlacesFollowingFeed(country,userID)
        mFollowingPlaces.removeValue()
    }

    fun followTags(currentID: String, tag: String) {
        val time = DateFormat.getDateTimeInstance().format(Date())
        val followingTags = "FollowingTags"

        val mFollowingTagsUserRef = FirebaseDBHelper.getUserInfo(currentID)
        mFollowingTagsUserRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val age = p0.child("age").value.toString()
                val image = p0.child("photoUrl").value.toString()
                val name = p0.child("nameSurname").value.toString()
                val lovely = p0.child("lovely").value.toString()

                val followedMap: MutableMap<String, String> = HashMap()
                followedMap["following_since"] = time
                followedMap["age"] = age
                followedMap["image"] = image
                followedMap["name"] = name
                followedMap["lovely"] = lovely

                val mapFollowed: MutableMap<String, Any> = HashMap()
                mapFollowed["$followingTags/$tag/$currentID"] = followedMap
                FirebaseDBHelper.rootRef().updateChildren(mapFollowed)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun followPlaces(currentID: String, country: String) {
        val time = DateFormat.getDateTimeInstance().format(Date())
        val followingPlaces = "FollowingPlaces"

        val mFollowingTagsUserRef = FirebaseDBHelper.getUserInfo(currentID)
        mFollowingTagsUserRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val age = p0.child("age").value.toString()
                val image = p0.child("photoUrl").value.toString()
                val name = p0.child("nameSurname").value.toString()
                val lovely = p0.child("lovely").value.toString()

                val followedMap: MutableMap<String, String> = HashMap()
                followedMap["following_since"] = time
                followedMap["age"] = age
                followedMap["image"] = image
                followedMap["name"] = name
                followedMap["lovely"] = lovely

                val mapFollowed: MutableMap<String, Any> = HashMap()
                mapFollowed["$followingPlaces/$country/$currentID"] = followedMap
                FirebaseDBHelper.rootRef().updateChildren(mapFollowed)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun followUsers(currentID: String, lisResID: String) {
        val timeDate = DateFormat.getDateTimeInstance().format(Date())
        val followingRef = "Following"
        val followedRef = "Followed"

        /* FollowedUserData */
        val mFollowerUserInfoRef = FirebaseDBHelper.getUserInfo(currentID)
        mFollowerUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val age = p0.child("age").value.toString()
                val image = p0.child("photoUrl").value.toString()
                val name = p0.child("nameSurname").value.toString()
                val lovely = p0.child("lovely").value.toString()

                /* Followed */
                val followedMap: MutableMap<String, String> = HashMap()
                followedMap["following_since"] = timeDate
                followedMap["age"] = age
                followedMap["image"] = image
                followedMap["name"] = name
                followedMap["lovely"] = lovely

                val mapFollowed: MutableMap<String, Any> = HashMap()
                mapFollowed["$followedRef/$lisResID/$currentID"] = followedMap

                FirebaseDBHelper.rootRef().updateChildren(mapFollowed)
            }
        })

        /* FollowingUserData */
        val mFollowingUserInfoRef = FirebaseDBHelper.getUserInfo(lisResID)
        mFollowingUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val fAge = p0.child("age").value.toString()
                val fImage = p0.child("photoUrl").value.toString()
                val fName = p0.child("nameSurname").value.toString()
                val fLovely = p0.child("lovely").value.toString()

                /* Following */
                val followingMap: MutableMap<String, String> = HashMap()
                followingMap["following_since"] = timeDate
                followingMap["age"] = fAge
                followingMap["image"] = fImage
                followingMap["name"] = fName
                followingMap["lovely"] = fLovely

                val mapFollowing: MutableMap<String, Any> = HashMap()
                mapFollowing["$followingRef/$currentID/$lisResID"] = followingMap

                FirebaseDBHelper.rootRef().updateChildren(mapFollowing)
            }
        })
    }

    fun followerNumbs(lisResID: String) {
        val mFollowedDBRef = FirebaseDBHelper.getFollowedNumbers(lisResID)
        var mFollowerNumber: String
        mFollowedDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (postSnapshot in p0.children) {
                        showLogDebug("mFollowerNumber", "key: " + postSnapshot.key!!)
                        mFollowerNumber = p0.childrenCount.toString()
                        showLogDebug(
                            "mFollowerNumber ",
                            "$lisResID, FollowerNumber: $mFollowerNumber"
                        )
                    }
                } else {
                    mFollowerNumber = "0"
                }
            }
        })
    }

}