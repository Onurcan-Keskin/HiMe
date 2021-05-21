package com.huawei.hime.findfollower

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.util.SharedPreferencesManager

class FindFollowerFragment : Fragment(), FindFollowerContractor {

    private lateinit var mView: View
    private lateinit var context: FragmentActivity

    private lateinit var mStorageRef: StorageReference
    private lateinit var currentUID: String
    private lateinit var query: Query
    private lateinit var mRecycler: RecyclerView
    private lateinit var mFollowerNumsRef: DatabaseReference

    private val findFollowerPresenter: FindFollowerPresenter by lazy {
        FindFollowerPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        context = activity!!
        val sharedPrefs = SharedPreferencesManager(context)
        if (sharedPrefs.loadNightModeState()) {
            context.setTheme(R.style.DarkMode)
        } else {
            context.setTheme(R.style.LightMode)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_find_follower, container, false)

        findFollowerPresenter.onViewCreate()

        return mView
    }

    override fun initViews() {
        context = activity!!

        mStorageRef = FirebaseStorage.getInstance().reference
        currentUID = AppUser.getUserId()
        query = FirebaseDatabase.getInstance().reference.child("UserInfo")

        mFollowerNumsRef = FirebaseDBHelper.getFollowingNumbers(currentUID)
        mFollowerNumsRef.keepSynced(true)

        query.keepSynced(true)

        mRecycler = mView.findViewById(R.id.find_follower_recycler)
        mRecycler.layoutManager = LinearLayoutManager(context)
        mRecycler.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()
        findFollowerPresenter.setupRecycler(context, currentUID, query, mRecycler)
    }

    companion object {
        private const val mTAG = "FindFollowerFragment"
    }
}