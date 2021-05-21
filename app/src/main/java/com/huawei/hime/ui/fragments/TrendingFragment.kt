package com.huawei.hime.trending

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
import com.google.firebase.database.Query
import com.huawei.hime.R
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.models.RecyclerTypes
import nl.joery.animatedbottombar.AnimatedBottomBar

/**
 * A simple [Fragment] subclass.
 */
class TrendingFragment : Fragment(), TrendingContract {

	private lateinit var mView : View

	private lateinit var context : FragmentActivity

	private lateinit var mRecycler : RecyclerView
	private lateinit var mUploadsDBRef : DatabaseReference
	private lateinit var query : Query

	private lateinit var mLayoutManager : LinearLayoutManager

	private var list : MutableList<String> = ArrayList()

	private var bottomBarTitle = "Home"
	private var type : String = ""

	private val trendingPresenter : TrendingPresenter by lazy {
		TrendingPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
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
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {
		// Inflate the layout for this fragment
		mView = inflater.inflate(R.layout.fragment_trending, container, false)
		trendingPresenter.onCreateViews()
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
		mRecycler = mView.findViewById(R.id.trending_recycler)
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
                query = mUploadsDBRef.orderByChild("popularity")
                RecyclerTypes.setupRecyclerHybrid(query, context, mRecycler)
            }
            "Travel" -> {
                query = mUploadsDBRef.orderByChild("popularity")
                RecyclerTypes.setupRecyclerMap(query, context, mRecycler)
            }
            "Events" -> {
                query = mUploadsDBRef.orderByChild("popularity")
                RecyclerTypes.setupRecyclerEvent(query, context, mRecycler)
            }
            "Gallery" -> {
                query = mUploadsDBRef.orderByChild("popularity")
                RecyclerTypes.setupRecyclerGallery(query, context, mRecycler)
            }
		}
	}
}
