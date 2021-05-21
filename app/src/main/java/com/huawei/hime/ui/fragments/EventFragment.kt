package com.huawei.hime.search.singleuserpage.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.Query
import com.huawei.hime.R
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.activities.SingleUserActivity
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.models.RecyclerTypes

class EventFragment : Fragment(), EventContract {

    private lateinit var context: FragmentActivity
    private lateinit var mView: View
    private lateinit var uploaderQuery: Query
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var userID: String

    private val eventPresenter: EventPresenter by lazy {
        EventPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLogDebug(mTAG,"onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val activity = activity as SingleUserActivity
        userID = activity.mGetUserID()
        mView = inflater.inflate(R.layout.fragment_event, container, false)
        eventPresenter.onViewsCreate()
        initDB()
        return mView
    }

    override fun initViews() {
        context = activity!!
        mRecyclerView = mView.findViewById(R.id.event_recycler)
        mRecyclerView.layoutManager = GridLayoutManager(context,3)
        mRecyclerView.setHasFixedSize(true)

        initDB()
    }

    override fun initDB() {
        uploaderQuery = FirebaseDBHelper.getShareableUploads().orderByChild("uploaderID").equalTo(userID)
        RecyclerTypes.TypeSingleSearch.setupRecyclerSearchEvent(uploaderQuery,context,mRecyclerView)
    }

    companion object {
        private const val mTAG = "EventFragment"
    }

}
