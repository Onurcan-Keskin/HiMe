package com.huawei.hime.discover

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.huawei.hime.R
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.models.RecyclerTypes
import nl.joery.animatedbottombar.AnimatedBottomBar

class DiscoverFragment : Fragment(), DiscoverContract {

    private lateinit var mView: View

    private lateinit var mRecycler: RecyclerView
    private lateinit var mUploadsDBRef: DatabaseReference
    //private lateinit var query: Query

    private lateinit var context: FragmentActivity
    private var bottomBarTitle = "Home"

    private val discoverPresenter: DiscoverPresenter by lazy {
        DiscoverPresenter(this)
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
        mView = inflater.inflate(R.layout.fragment_discover, container, false)
        discoverPresenter.onViewsCreate()
        initDB()
        return mView
    }

    override fun initViews() {
        context = activity!!

        val bar = activity!!.findViewById<AnimatedBottomBar>(R.id.main_bottom_app_bar)
        bar.onTabSelected = {
            bottomBarTitle = it.title
            Log.d("bottom_bar_fragment", "Selected tab: " + it.title)
            initDB()
        }
        bar.onTabReselected = {
            bottomBarTitle = it.title
            initDB()
        }
        mRecycler = mView.findViewById(R.id.discover_recycler)
        mRecycler.layoutManager = LinearLayoutManager(context)
        mRecycler.setHasFixedSize(true)
    }

    override fun initDB() {
        mUploadsDBRef = FirebaseDBHelper.getShareableUploads()
        when (bottomBarTitle) {
            "Home" -> {
                RecyclerTypes.setupRecyclerHybrid(mUploadsDBRef, context, mRecycler)
                Log.d("bottomBarTitle", bottomBarTitle)
            }
            "Travel" -> {
                RecyclerTypes.setupRecyclerMap(mUploadsDBRef, context, mRecycler)
                Log.d("bottomBarTitle", bottomBarTitle)
            }
            "Events" -> {
                RecyclerTypes.setupRecyclerEvent(mUploadsDBRef,context,mRecycler)
                Log.d("bottomBarTitle", bottomBarTitle)
            }
            "Gallery" -> {
                RecyclerTypes.setupRecyclerGallery(mUploadsDBRef, context, mRecycler)
                Log.d("bottomBarTitle", bottomBarTitle)
            }
        }
    }

    companion object {
        private const val mTAG = "DiscoverFragment"
    }
}