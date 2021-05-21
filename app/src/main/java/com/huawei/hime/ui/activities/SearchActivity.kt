package com.huawei.hime.ui.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.helpers.Constants
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.search.SearchFragmentPagerAdapter
import com.huawei.hime.search.placesearch.PlacesSearchFragment
import com.huawei.hime.ui.fragments.TagSearchFragment
import com.huawei.hime.search.usersearch.UserSearchFragment
import com.huawei.hime.ui.interfaces.ISearch
import com.huawei.hime.ui.presenters.SearchPresenter
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.SharedPreferencesManager
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity(), ISearch.ViewSearch {

	private var fid : String? = ""
	private var context : Context? = null

	private val searchPresenter : SearchPresenter by lazy {
		SearchPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {

		val sharedPrefs = SharedPreferencesManager(this)
		if (sharedPrefs.loadNightModeState()) {
			setTheme(R.style.DarkMode)
		} else {
			setTheme(R.style.LightMode)
		}

		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_search)

		searchPresenter.onViewsCreate()
	}

	override fun onStart() {
		super.onStart()
		FirebaseDBHelper.onConnect(Constants.fOnlineDBRef)
	}

	override fun onStop() {
		super.onStop()
		FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
	}

	override fun onPause() {
		super.onPause()
		FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
	}

	companion object {
		private const val mTAG = "SearchActivity"
	}

	override fun initViews() {
		context = applicationContext
		fid = if (intent.getStringExtra("fid") != null)
			intent.getStringExtra("fid")
		else "t"
	}

	override fun setupViewPager() {
		val adapterL = SearchFragmentPagerAdapter(supportFragmentManager)

		val dbTag = FirebaseDBHelper.getTagRootRef()
		dbTag.addValueEventListener(object : ValueEventListener {
			override fun onDataChange(p0 : DataSnapshot) {
				if (p0.hasChildren()) {
					val pTotal = NumberConvertor.prettyCount(p0.childrenCount)
					adapterL.addFragment(TagSearchFragment(), "$pTotal " + getString(R.string.tag))
				} else {
					adapterL.addFragment(TagSearchFragment(), getString(R.string.tag))
				}
			}

			override fun onCancelled(p0 : DatabaseError) {}
		})
		val dbAccount = FirebaseDBHelper.getUserInfoRoot()
		dbAccount.addValueEventListener(object : ValueEventListener {
			override fun onDataChange(p0 : DataSnapshot) {
				if (p0.hasChildren()) {
					val pTotal = NumberConvertor.prettyCount(p0.childrenCount)
					adapterL.addFragment(
						UserSearchFragment(),
						"$pTotal " + getString(R.string.account)
					)
				} else {
					adapterL.addFragment(UserSearchFragment(), getString(R.string.account))
				}
			}

			override fun onCancelled(p0 : DatabaseError) {}
		})

		adapterL.addFragment(PlacesSearchFragment(), getString(R.string.places))
		searchPager.adapter = adapterL
		search_tabs.setupWithViewPager(searchPager)

		when (fid) {
			"t" -> searchPager.currentItem = 0
			"a" -> searchPager.currentItem = 1
			"p" -> searchPager.currentItem = 2
		}
	}
}
