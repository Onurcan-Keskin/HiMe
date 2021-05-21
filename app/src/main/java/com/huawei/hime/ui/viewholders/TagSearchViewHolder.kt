package com.huawei.hime.search.tagsearch

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hime.R

class TagSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val parent: View = itemView

    private var tagName: TextView
    private var totalPost: TextView

    fun bindTags(
        mTagName: String,
        mTotalPost: String
    ) {
        tagName.text = mTagName
        totalPost.text = mTotalPost

    }
    init {
        tagName = parent.findViewById(R.id.tag_name)
        totalPost = parent.findViewById(R.id.tag_total_post)
    }
}