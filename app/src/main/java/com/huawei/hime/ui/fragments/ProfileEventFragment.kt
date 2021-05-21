package com.huawei.hime.profile.profileevent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.activities.EventAdderActivity

class ProfileEventFragment : Fragment(), ProfileEventContract {

	private lateinit var context : FragmentActivity
	private lateinit var mView : View

	private lateinit var imageView : ImageView

	private val currentID = AppUser.getUserId()

	private lateinit var mRecyclerView : RecyclerView

	private val profileEventPresenter : ProfileEventPresenter by lazy {
		ProfileEventPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {
		mView = inflater.inflate(R.layout.fragment_profile_event, container, false)
		profileEventPresenter.onViewsCreate()
		return mView
	}

	override fun initViews() {
		context = activity!!
		imageView = mView.findViewById(R.id.profile_event_switch_calendar)
		mRecyclerView = mView.findViewById(R.id.profile_event_recycler)
		mRecyclerView.layoutManager = LinearLayoutManager(context)
		mRecyclerView.setHasFixedSize(true)
		initDB()
	}

	override fun switchEvent() {
		imageView.setOnClickListener {
			context.startActivity(
                Intent(
                    context.applicationContext,
                    EventAdderActivity::class.java
                )
            )
		}
	}

	override fun initDB() {
		val mDB = FirebaseDBHelper.getEventFeedRef(currentID)
		profileEventPresenter.setupRecycler(mDB,mRecyclerView,currentID)
	}

}
