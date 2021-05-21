package com.huawei.hime.profile.profilegallery.profilegalleryvideo

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AnyRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.ui.activities.BigPlayerProfActivity
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.util.ProfileVideosDataClass
import com.huawei.hime.util.showLogDebug
import com.klinker.android.simple_videoview.SimpleVideoView


class ProfileGVideoFragment : Fragment(), ProfileGVideoContract {

    private lateinit var mView: View

    private var currentId = AppUser.getUserId()
    private lateinit var mRecycler: RecyclerView
    private var mLayoutManager: LinearLayoutManager? = null
    private lateinit var context: FragmentActivity
    private lateinit var mUserFeedVidRef: DatabaseReference
    private var mUserInfoRef = FirebaseDBHelper.getUserInfo(currentId)

    private lateinit var posterName: String
    private lateinit var posterImage: String
    private var shareStat: String = ""
    private var commentsAllowed: String = ""
    private var mChildSize: Int = 0

    private lateinit var mFollowedDBRef: DatabaseReference
    private var mFollowerCounter: String = ""

    private val profileGVideoPresenter: ProfileGVideoPresenter by lazy {
        ProfileGVideoPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_profile_gallery_video, container, false)
        profileGVideoPresenter.onViewsCreate()
        setupRecycler()
        return mView
    }

    override fun initViews() {
        context = activity!!
        mRecycler = mView.findViewById(R.id.profile_g_vid_recycler)
        mRecycler.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(context)
        mRecycler.layoutManager = mLayoutManager
//        mRecycler.layoutManager = LinearLayoutManager(context)
//        val itemDecorator =
//            VerticalSpacingItemDecorator(
//                10
//            )
//        mRecycler.addItemDecoration(itemDecorator)
//        mRecycler.setHasFixedSize(true)
    }

    override fun initDB() {
        mUserFeedVidRef = FirebaseDBHelper.getVideoFeed(currentId)

        mUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                posterName = p0.child("nameSurname").value.toString()
                posterImage = p0.child("photoUrl").value.toString()
            }
        })

        mFollowedDBRef = FirebaseDBHelper.getFollowedNumbers(currentId)
        mFollowedDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    mFollowerCounter = p0.childrenCount.toString()
                }
            }
        })


        mUserFeedVidRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    mChildSize = p0.childrenCount.toInt()
                    showLogDebug(mTag, "Child_Size: $mChildSize")
                }
            }
        })
    }

    fun setupRecycler() {
        val options = FirebaseRecyclerOptions.Builder<ProfileVideosDataClass>()
            .setQuery(mUserFeedVidRef, ProfileVideosDataClass::class.java).build()
        val adapterFire = object :
            FirebaseRecyclerAdapter<ProfileVideosDataClass, ProfileGVideoViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ProfileGVideoViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.single_post_card_video, parent, false)
                return ProfileGVideoViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: ProfileGVideoViewHolder,
                position: Int,
                model: ProfileVideosDataClass
            ) {
                val lisResUid = getRef(position).key

                holder.setPosterImage(posterImage)
                holder.setPosterName(posterName)
                holder.setFooter(model.footer)
                holder.setLovely(model.video_lovely)

                holder.setTag1(model.tag1)
                holder.setTag2(model.tag2)
                holder.setTag3(model.tag3)
                holder.setTag4(model.tag4)
                holder.setTag5(model.tag5)

                holder.setFollower(mFollowerCounter)

                holder.mLovelyLyt!!.setOnClickListener {
                    holder.playHearts()
                }

                //holder.bindVideo(model.video_videoUrl)

                /* New Stuff */
                var activeAdapter = 0
                mRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val firstCompletelyVisibleItemPosition: Int =
                            mLayoutManager!!.findFirstCompletelyVisibleItemPosition()
                        Log.d(
                            mTag,
                            "onScrolled: firstCompletelyVisibleItemPosition : $firstCompletelyVisibleItemPosition"
                        )
                        if (activeAdapter != firstCompletelyVisibleItemPosition) {
                            try {
                                val vid = mLayoutManager!!.findViewByPosition(
                                    firstCompletelyVisibleItemPosition
                                )?.findViewById<SimpleVideoView>(R.id.hybrid_video_view)
                                holder.bindVideo(model.video_videoUrl)
//                                vid!!.start(model.video_videoUrl)
//                                vid.setOnClickListener {
//                                    if (vid.isPlaying) {
//                                        vid.pause()
//                                    } else {
//                                        vid.play()
//                                    }
//                                }
                                activeAdapter = firstCompletelyVisibleItemPosition
                            } catch (e: NullPointerException) {
                                // Sometimes you scroll so fast that the views are not attached so it gives a NullPointerException
                            } catch (e: ArrayIndexOutOfBoundsException) {
                            }
                            if (firstCompletelyVisibleItemPosition >= 1) {
                                try {
                                    val vidAbove = mLayoutManager!!.findViewByPosition(
                                        firstCompletelyVisibleItemPosition - 1
                                    )?.findViewById<SimpleVideoView>(R.id.hybrid_video_view)
                                    vidAbove!!.release()
                                } catch (e: NullPointerException) {
                                    // Sometimes you scroll so fast that the views are not attached so it gives a NullPointerException
                                } catch (e: ArrayIndexOutOfBoundsException) {
                                }
                            }
                            if (firstCompletelyVisibleItemPosition + 1 < mChildSize) {
                                showLogDebug(mTag, "Child: $mChildSize")
                                try {
                                    val vidBelow = mLayoutManager!!.findViewByPosition(
                                        firstCompletelyVisibleItemPosition + 1
                                    )?.findViewById<SimpleVideoView>(R.id.hybrid_video_view)
                                    vidBelow!!.release()
                                } catch (e: NullPointerException) {
                                    // Sometimes you scroll so fast that the views are not attached so it gives a NullPointerException
                                } catch (e: ArrayIndexOutOfBoundsException) {
                                }
                            }
                        }
                    }
                })
                /* --------- */

                holder.mFullscreen!!.setOnClickListener {
                    showLogDebug(mTag, "PostID $lisResUid")
                    startActivity(
                        Intent(context.applicationContext, BigPlayerProfActivity::class.java)
                            .putExtra("postID", lisResUid)
                            .putExtra("postVideo", model.video_videoUrl)
                    )
                }

                holder.parent.setOnClickListener {
                    showLogDebug(mTag, "PostID $lisResUid")

                    val feedDb = FirebaseDBHelper.getVideoFeed(currentId).child(lisResUid!!)
                    feedDb.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            shareStat = p0.child("shareStat").value.toString()
                            commentsAllowed = p0.child("commentsAllowed").value.toString()
                        }
                    })

                    if (shareStat == "1") {
                        val uploadDB = FirebaseDBHelper.rootRef().child("uploads").child("Shareable")
                            .child(lisResUid)
                        holder.setCircleMenu(
                            context,
                            uploadDB,
                            feedDb,
                            shareStat,
                            commentsAllowed,
                            lisResUid
                        )
                    } else if (shareStat == "0") {
                        val uploadDB =
                            FirebaseDBHelper.rootRef().child("uploads").child("NShareable")
                                .child(lisResUid)
                        holder.setCircleMenu(
                            context,
                            uploadDB,
                            feedDb,
                            shareStat,
                            commentsAllowed,
                            lisResUid
                        )
                    }
                    showLogDebug(mTag, "ShareStat $shareStat\nComments $commentsAllowed")

                }
            }
        }
        adapterFire.startListening()
        mRecycler.adapter = adapterFire
    }

    companion object {
        private const val mTag = "ProfileGVideoFragment"
        fun getUriToDrawable(
            context: Context,
            @AnyRes drawableId: Int
        ): String {
            val imageUri: Uri = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://" + context.resources.getResourcePackageName(drawableId)
                        + '/' + context.resources.getResourceTypeName(drawableId)
                        + '/' + context.resources.getResourceEntryName(drawableId)
            )
            return imageUri.toString()
        }
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onPause() {
        super.onPause()

    }
}