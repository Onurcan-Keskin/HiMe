package com.huawei.hime.profile.profileinbox

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.ui.activities.ChatActivity
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.helpers.VerticalSpacingItemDecorator
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.util.showLogError
import com.huawei.hime.util.showLogInfo

class ProfileInboxFragment : Fragment(), ProfileInboxContract {

    private lateinit var mView: View

    private lateinit var mMessageDBRef: DatabaseReference
    private lateinit var mReceiverDBRef: DatabaseReference
    private lateinit var mSenderDBRef: DatabaseReference
    private lateinit var mRecyclerQuery: DatabaseReference
    private lateinit var mQuery: Query
    private lateinit var lottieLinearLayout: LinearLayout
    private lateinit var lottieAnimationView: LottieAnimationView
    private var currentID = AppUser.getUserId()

    private lateinit var mRecycler: RecyclerView

    private lateinit var context: FragmentActivity

    private var senderName: String = ""
    private var senderImage: String = ""
    private var size = 0

    private var receiverName: String = ""
    private var receiverImage: String = ""

    private var mSenderKeyArray: MutableList<String> = ArrayList()
    private var mSenderNameArray: MutableList<String> = ArrayList()

    private val profileInboxPresenter: ProfileInboxPresenter by lazy {
        ProfileInboxPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile_inbox, container, false)

        try {
            profileInboxPresenter.onViewsCreate()
        } catch (e: Exception) {
            showLogError(mTAG, "$e")
        }
        return mView
    }

    override fun initViews() {
        context = activity!!
        mRecycler = mView.findViewById(R.id.profile_inbox_recycler)
       //lottieLinearLayout = mView.findViewById(R.id.profile_inbox_lottie_lyt)
       //lottieAnimationView = mView.findViewById(R.id.inbox_lottie)

        mRecycler.layoutManager = LinearLayoutManager(context)
        val itemDecorator =
            VerticalSpacingItemDecorator(
                10
            )
        mRecycler.addItemDecoration(itemDecorator)
        mRecycler.setHasFixedSize(true)

    }

    override fun initDB() {
        mMessageDBRef = FirebaseDBHelper.getMessage(currentID)
        mMessageDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    //lottieLinearLayout.visibility = View.GONE
                    for (postSnap in p0.children) {
                        val key = postSnap.key!!
                        showLogDebug(mTAG, key)
                        mSenderKeyArray.add(key)

                        mReceiverDBRef = FirebaseDBHelper.getUserInfo(currentID)
                        mReceiverDBRef.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                receiverImage = p0.child("photoUrl").value.toString()
                                receiverName = p0.child("nameSurname").value.toString()

                                showLogDebug(mTAG, "Receive: $receiverName \n $receiverImage")
                            }
                        })
                        size = mSenderKeyArray.size
                    } // For
                    showLogDebug(mTAG, "Size for: $size")
                    var i = 0
                    while (i <= size - 1) {
                        showLogDebug(mTAG, "Size Line: $size, $i")
                        mRecyclerQuery = FirebaseDBHelper.getMessage(currentID).child(mSenderKeyArray[i])
                        mQuery = mRecyclerQuery.limitToLast(2)

                        mSenderDBRef = FirebaseDBHelper.getUserInfo(mSenderKeyArray[i])
                        mSenderDBRef.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                senderImage = p0.child("photoUrl").value.toString()
                                senderName = p0.child("nameSurname").value.toString()

                                mSenderNameArray.add(senderName)
                                showLogDebug(mTAG, "Sender: $senderName, \n $senderImage")
                            }
                        })
                        setupRecycler()
                        i++
                    }
                } else {
                    //lottieLinearLayout.visibility=View.VISIBLE
                    showLogInfo(mTAG, "Child is empty.")
                }
            }
        })
    }

    override fun setupRecycler() {
        val options = FirebaseRecyclerOptions.Builder<ProfileInboxData>()
            .setQuery(mRecyclerQuery, ProfileInboxData::class.java).build()

        val adapterFire =
            object :
                FirebaseRecyclerAdapter<ProfileInboxData, ProfileInboxHolderView>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ProfileInboxHolderView {
                    val view = LayoutInflater.from(context)
                        .inflate(R.layout.single_inbox_holder, parent, false)
                    return ProfileInboxHolderView(view)
                }

                override fun onBindViewHolder(
                    holder: ProfileInboxHolderView,
                    position: Int,
                    model: ProfileInboxData
                ) {
                    /* Messages Recycler */

//                    profileInboxPresenter.messagesRecycler(
//                        mQuery,
//                        mRecyclerMessage,
//                        context,
//                        currentID,
//                        receiverName,
//                        receiverImage,
//                        senderName,
//                        senderImage
//                    )
                    if (model.from == currentID)
                        holder.bindReceiver(
                            receiverImage,
                            receiverName,
                            model.message,
                            model.time
                        )
                    else{
                        holder.bindSender(senderImage,senderName,model.message,model.time)
                    }

                    holder.bindBetween("$receiverName - ${mSenderNameArray[position]}")

                    holder.parent!!.setOnClickListener {
                        startActivity(
                            Intent(
                                context.applicationContext,
                                ChatActivity::class.java
                            ).putExtra("visitorID", mSenderKeyArray[position])
                        )
                    }
                }
            }
        adapterFire.startListening()
        mRecycler.adapter = adapterFire
    }

    companion object {
        private const val mTAG = "ProfileInboxFragment"
    }

}
