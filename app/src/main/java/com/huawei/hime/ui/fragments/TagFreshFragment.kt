package com.huawei.hime.search.singletagpage.tagfresh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.huawei.hime.R
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.activities.SingleTagActivity
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.models.RecyclerTypes

class TagFreshFragment : Fragment(), TagFreshContract {

    private lateinit var mView: View
    private lateinit var tagText: String
    private lateinit var context: FragmentActivity

    private lateinit var mRecycler: RecyclerView
    private lateinit var mTagRef: DatabaseReference
    private lateinit var query: Query

    private var mUploadArray: MutableList<String> = ArrayList()

    private val tagFreshPresenter: TagFreshPresenter by lazy {
        TagFreshPresenter(this)
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
        mView = inflater.inflate(R.layout.fragment_tag_fresh, container, false)
        tagFreshPresenter.onViewsCreate()
        return mView
    }

    companion object {
        private const val mTag = "TagFreshFragment"
    }

    override fun initViews() {
        context = activity!!
        mRecycler = mView.findViewById(R.id.tf_recycler)
        mRecycler.layoutManager = GridLayoutManager(context, 3)
        mRecycler.setHasFixedSize(true)
        initDb()
    }

    override fun initDb() {
        mTagRef = FirebaseDBHelper.getShareableUploads()
        RecyclerTypes.TypeSingleSearch.setupRecyclerTagHybrid(mTagRef, context, mRecycler, tagText)
    }
}
