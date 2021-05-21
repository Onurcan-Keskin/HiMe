package com.huawei.hime.search.singletagpage.tagtrending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.huawei.hime.R
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.activities.SingleTagActivity
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.models.RecyclerTypes

class TagTrendingFragment : Fragment(), TagTrendingContract {

    private lateinit var mView: View
    private lateinit var tagText: String
    private lateinit var context: FragmentActivity

    private lateinit var mRecycler: RecyclerView
    private lateinit var mTagRef: DatabaseReference
    private lateinit var query: Query

    private var mUploadArray: MutableList<String> = ArrayList()

    private val tagTrendingPresenter: TagTrendingPresenter by lazy {
        TagTrendingPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = activity as SingleTagActivity
        tagText = activity.getTag()
        showLogDebug(mTag, tagText)
        mView = inflater.inflate(R.layout.fragment_tag_trending, container, false)
        tagTrendingPresenter.onViewsCreate()
        return mView
    }

    companion object {
        private const val mTag = "TagTrendingFragment"
    }

    override fun initViews() {
        context = activity!!
        mRecycler = mView.findViewById(R.id.tt_recycler)
        mRecycler.layoutManager = GridLayoutManager(context, 3)
        mRecycler.setHasFixedSize(true)
        initDB()
    }

    override fun initDB() {
        mTagRef = FirebaseDBHelper.getShareableUploads()
        RecyclerTypes.TypeSingleSearch.setupRecyclerTagHybrid(mTagRef, context, mRecycler, tagText)
    }
}
