package com.huawei.hime.search.singleuserpage.travel

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
import com.huawei.hime.models.RecyclerTypes

class TravelFragment : Fragment(), TravelContract {

    private lateinit var context: FragmentActivity
    private lateinit var mView: View

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var userID:String
    private lateinit var mGallery: Query

    private val travelPresenter: TravelPresenter by lazy {
        TravelPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = activity as SingleUserActivity
        userID = activity.mGetUserID()
        mView = inflater.inflate(R.layout.fragment_travel, container, false)
        travelPresenter.onViewsCreate()
        initDB()
        return mView
    }

    override fun initViews() {
        context = activity!!
        mRecyclerView = mView.findViewById(R.id.st_recycler)
        mRecyclerView.layoutManager = GridLayoutManager(context, 3)
        mRecyclerView.setHasFixedSize(true)

        initDB()
    }

    override fun initDB() {
        mGallery= FirebaseDBHelper.getShareableUploads().orderByChild("uploaderID").equalTo(userID)
        RecyclerTypes.TypeSingleSearch.setupRecyclerSearchMap(mGallery,context,mRecyclerView)
    }
}
