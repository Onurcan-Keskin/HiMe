package com.huawei.hime.masterfollowers.masterU.following

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.activities.MasterProfileFollowerActivity
import com.huawei.hime.util.FollowDataClass
import com.huawei.hime.util.showLogDebug
import kotlinx.android.synthetic.main.fragment_followers.*

class FollowingFragment : Fragment(), FollowingContract {

    private lateinit var mView: View
    private lateinit var context: FragmentActivity

    private lateinit var mFollowingDBRef: DatabaseReference
    private lateinit var mFollowingsUserInfoDBRef: DatabaseReference
    private var mRecyclerView: RecyclerView? = null
    private lateinit var userID: String
    private var currentID = AppUser.getUserId()

    private var mFollowingArray: MutableList<String> = ArrayList()

    private val followingPresenter: FollowingPresenter by lazy {
        FollowingPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val activity: MasterProfileFollowerActivity = activity as MasterProfileFollowerActivity
        userID = activity.getUID()
        showLogDebug(mTAG, userID)
        mView = inflater.inflate(R.layout.fragment_following, container, false)
        followingPresenter.onViewsCreate()
        initDB()
        return mView
    }

    companion object {
        private const val mTAG = "FollowingFragment"
    }

    override fun initViews() {
        context = activity!!

        mRecyclerView = mView.findViewById(R.id.masterFollowingRecyclerView)
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)
        mRecyclerView!!.setHasFixedSize(true)
    }

    override fun initDB() {
        mFollowingDBRef = FirebaseDBHelper.getFollowingNumbers(userID)
        mFollowingDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    follow_video_lottie_lyt.visibility = View.GONE
                    for (postSnap in p0.children) {
                        val key = postSnap.key!!
                        showLogDebug(mTAG, "Fragment Key -> $key")
                        mFollowingArray.add(key)
                    }
                    if (mFollowingArray.size > 0) {
                        var i = 0
                        while (i <= mFollowingArray.size) {
                            mFollowingsUserInfoDBRef =
                                FirebaseDatabase.getInstance().reference.child("Following")
                                    .child(userID)
                            setupRecycler()
                            i++
                        }
                    }
                } else {
                    // No Following
                    follow_video_lottie_lyt.visibility = View.VISIBLE
                    mRecyclerView!!.visibility = View.GONE
                }
            }
        })
    }

    override fun setupRecycler() {
        val options = FirebaseRecyclerOptions.Builder<FollowDataClass>()
            .setQuery(mFollowingsUserInfoDBRef, FollowDataClass::class.java).build()

        val adapterFire =
            object : FirebaseRecyclerAdapter<FollowDataClass, FollowingViewHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): FollowingViewHolder {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.single_master_followers, parent, false)
                    return FollowingViewHolder(view)
                }

                override fun onBindViewHolder(
                    holder: FollowingViewHolder,
                    position: Int,
                    model: FollowDataClass
                ) {
                    val lisResUid = getRef(position).key

                    holder.setAge(model.age)
                    holder.setFollowing(model.following_since)
                    holder.setImage(model.image)
                    holder.setLovely(model.lovely)
                    holder.setName(model.name)

                    showLogDebug(mTAG, "Size-> " + mFollowingArray.size)
//                    val i = 0
//                    while (i <= mFollowingArray.size) {
//                        val db = DbUtils.getUserInfo(mFollowingArray[i])
//                        db.addValueEventListener(object: ValueEventListener{
//                            override fun onCancelled(p0: DatabaseError) {
//                            }
//
//                            override fun onDataChange(p0: DataSnapshot) {
//                                //showLogDebug(mTAG,p0.child("photoUrl").value.toString())
//                            }
//                        })
//                    }

                    showLogDebug(mTAG, lisResUid.toString())

                    if (userID != currentID) {
                        holder.setRemoveVisibilityGone()
                    }

//                    holder.parent.setOnClickListener {
//                        startActivity(
//                            Intent(
//                                context,
//                                MasterProfileActivity::class.java
//                            ).putExtra("userID", lisResUid)
//                        )
//                    }

                    showLogDebug(
                        mTAG, "model.age " + model.age +
                                "\nmodel.lovely " + model.lovely +
                                "\nmodel.photoUrl " + model.image +
                                "\nmodel.nameSurname " + model.name
                    )
                }
            }
        adapterFire.startListening()
        mRecyclerView!!.adapter = adapterFire
    }
}
