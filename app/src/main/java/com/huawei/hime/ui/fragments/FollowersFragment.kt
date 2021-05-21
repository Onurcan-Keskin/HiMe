package com.huawei.hime.masterfollowers.masterU.followers

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
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.activities.MasterProfileFollowerActivity
import com.huawei.hime.util.FollowDataClass
import com.huawei.hime.util.showLogDebug
import kotlinx.android.synthetic.main.fragment_followers.*

class FollowersFragment : Fragment(), FollowerContract {

    private lateinit var mView: View
    private lateinit var context: FragmentActivity

    private lateinit var mFollowersDBRef: DatabaseReference
    private lateinit var mFollowersUserInfoDBRef: DatabaseReference
    private var mRecyclerView: RecyclerView? = null
    private lateinit var userID: String

    private var mFollowerArray: MutableList<String> = ArrayList()

    private val followersPresenter: FollowersPresenter by lazy {
        FollowersPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity: MasterProfileFollowerActivity = activity as MasterProfileFollowerActivity
        userID = activity.getUID()
        showLogDebug(mTAG, userID)
        mView = inflater.inflate(R.layout.fragment_followers, container, false)
        followersPresenter.onViewsCreate()
        initDB()
        return mView
    }


    companion object {
        private const val mTAG = "FollowersFragment"
    }

    override fun initViews() {
        context = activity!!

        mRecyclerView = mView.findViewById(R.id.masterFollowersRecyclerView)
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)
        mRecyclerView!!.setHasFixedSize(true)
    }

    override fun initDB() {
        mFollowersDBRef = FirebaseDBHelper.getFollowedNumbers(userID)
        mFollowersDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    follow_video_lottie_lyt.visibility = View.GONE
                    for (postSnap in p0.children) {
                        val key = postSnap.key!!
                        showLogDebug(mTAG, "Fragment Key -> $key")
                        mFollowerArray.add(key)
                    }
                    if (mFollowerArray.size > 0) {
                        var i = 0
                        while (i <= mFollowerArray.size) {
                            //mFollowersUserInfoDBRef = DbUtils.getUserInfo(mFollowerArray[0])
                            mFollowersUserInfoDBRef =
                                FirebaseDatabase.getInstance().reference.child("Followed")
                                    .child(userID)
                            setupRecycler()
                            i++
                            showLogDebug(mTAG, "FollowerCounter: $i")
                        }
                    }
                } else {
                    //No Followers
                    follow_video_lottie_lyt.visibility = View.VISIBLE
                    mRecyclerView!!.visibility = View.GONE
                }
            }
        })
    }

    override fun setupRecycler() {
        val options = FirebaseRecyclerOptions.Builder<FollowDataClass>()
            .setQuery(mFollowersUserInfoDBRef, FollowDataClass::class.java).build()

        val adapterFire =
            object : FirebaseRecyclerAdapter<FollowDataClass, FollowerViewHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): FollowerViewHolder {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.single_master_followers, parent, false)
                    return FollowerViewHolder(view)
                }

                override fun onBindViewHolder(
                    holder: FollowerViewHolder,
                    position: Int,
                    model: FollowDataClass
                ) {
                    val lisResUid = getRef(position).key

                    holder.setAge(model.age)
                    holder.setFollowing(model.following_since)
                    holder.setImage(model.image)
                    holder.setLovely(model.lovely)
                    holder.setName(model.name)

//                    holder.parent.setOnClickListener {
//                        startActivity(
//                            Intent(
//                                context,
//                                MasterProfileActivity::class.java
//                            ).putExtra("userID", lisResUid)
//                        )
//                    }

                    showLogDebug(
                        mTAG, "model.age" + model.age +
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
