package com.huawei.hime.search.tagsearch

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.Query
import com.huawei.hime.R
import com.huawei.hime.ui.activities.SingleTagActivity
import com.huawei.hime.util.HybridUploadDataClass
import com.huawei.hime.util.showLogDebug

class TagSearchPresenter constructor(private val tagSearchContract: TagSearchContract) {

    fun onViewsCreate() {
        tagSearchContract.initViews()
        //tagSearchContract.setPrefs()
    }

    fun setupHashtagRecycler(
        query: Query,
        arrSize: Int,
        tagList: MutableList<String>,
        recyclerView: RecyclerView,
        context: FragmentActivity,
        string: String,
        size: String
    ) {
        val options = FirebaseRecyclerOptions.Builder<HybridUploadDataClass>()
            .setQuery(query, HybridUploadDataClass::class.java).build()
        val adapterFire =
            object : FirebaseRecyclerAdapter<HybridUploadDataClass, TagSearchViewHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): TagSearchViewHolder {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.single_tag_layout, parent, false)
                    return TagSearchViewHolder(view)
                }

                override fun onBindViewHolder(
                    holder: TagSearchViewHolder,
                    position: Int,
                    model: HybridUploadDataClass
                ) {
                    val listResId = getRef(position).key

                    // showLogDebug(mTAG, model.uploaderID+"/n"+model.tag1)

                    holder.bindTags(model.tag1, size)

                    var i = 0
                    while (i <= arrSize - 1) {
                        showLogDebug("TagSearch", string + " " + tagList[i])
                        //5if (tagList[i].contains(string))
                        holder.bindTags("#${tagList[i]}", size)
                        i++
                    }
                    holder.parent.setOnClickListener {
                        showLogDebug("TagSearchFragment keys", listResId!!)
                        context.startActivity(
                            Intent(context.applicationContext, SingleTagActivity::class.java)
                                .putExtra("tag", listResId)
                                .putExtra("size", size)
                        )
                    }
                }
            }
        adapterFire.startListening()
        recyclerView.adapter = adapterFire
    }

    companion object {
        private const val mTAG = "setupHashtagRecycler"
    }
}

interface TagSearchContract {
    fun initViews()
    fun initDB(string: String)
    fun setPrefs()
}