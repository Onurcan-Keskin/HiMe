package com.huawei.hime.fresh

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.huawei.hime.R
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.models.RecyclerTypes
import nl.joery.animatedbottombar.AnimatedBottomBar

class FreshFragment : Fragment(),FreshContract {

    private lateinit var context: FragmentActivity
    private lateinit var mView: View

    private lateinit var mRecycler: RecyclerView
    private lateinit var mUploadsDBRef: DatabaseReference
    private lateinit var query: Query

    private lateinit var mLayoutManager: LinearLayoutManager

    private var bottomBarTitle = "Home"

    private val freshPresenter: FreshPresenter by lazy {
        FreshPresenter(this)
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
        mView = inflater.inflate(R.layout.fragment_fresh, container, false)
        freshPresenter.onViewsCreate()
        initDB()
        return mView
    }

    override fun initViews() {
        context = activity!!
        val bar = context.findViewById<AnimatedBottomBar>(R.id.main_bottom_app_bar)
        bar.onTabSelected = {
            bottomBarTitle = it.title
            Log.d("bottom_bar_fragment", "Selected tab: " + it.title)
            initDB()
        }
        bar.onTabReselected = {
            bottomBarTitle = it.title
            initDB()
        }
        bar.onTabReselected = {
            bottomBarTitle = it.title
            initDB()
        }
        mRecycler = mView.findViewById(R.id.fresh_recycler)
        mLayoutManager = LinearLayoutManager(context)
        mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = true
        mRecycler.layoutManager = mLayoutManager
        mRecycler.setHasFixedSize(true)
    }

    override fun initDB() {
        mUploadsDBRef = FirebaseDBHelper.getShareableUploads()
        when (bottomBarTitle) {
            "Home" -> {
                query = mUploadsDBRef.orderByChild("upload_millis")
                RecyclerTypes.setupRecyclerHybrid(query, context, mRecycler)
            }
            "Travel" -> {
                query = mUploadsDBRef.orderByChild("upload_millis")
                RecyclerTypes.setupRecyclerMap(query, context, mRecycler)
            }
            "Events" -> {
                query = mUploadsDBRef.orderByChild("upload_millis")
                RecyclerTypes.setupRecyclerEvent(query, context, mRecycler)
            }
            "Gallery" -> {
                query = mUploadsDBRef.orderByChild("upload_millis")
                RecyclerTypes.setupRecyclerGallery(query, context, mRecycler)
            }
        }
    }
}
