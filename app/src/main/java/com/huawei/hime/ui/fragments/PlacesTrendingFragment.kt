package com.huawei.hime.search.singleplacespage.placestrending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser

class PlacesTrendingFragment : Fragment(), PlacesTrendingContract {

    private val currentID = AppUser.getUserId()

    private lateinit var mRecyclerView: RecyclerView

    private lateinit var mView: View

    private val placesTrendingPresenter: PlacesTrendingPresenter by lazy {
        PlacesTrendingPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_places_trending, container, false)


        return mView
    }

    companion object {
        private const val mTag = "PlacesTrendingFragment"
    }

    override fun initViews() {
        mRecyclerView = mView.findViewById(R.id.pt_recycler)
    }

    override fun initDB() {
        TODO("Not yet implemented")
    }
}
