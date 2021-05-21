package com.huawei.hime.search.placesearch

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hime.R
import com.huawei.hime.util.NumberConvertor

class PlacesSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val parent: View = itemView
    private var countryName: TextView
    private var countryTotalPost: TextView

    fun bindView(cName: String, cTotal: String) {
        countryName.text = cName
        if (cTotal != null && cTotal != "") {
            val pTotal = NumberConvertor.prettyCount(cTotal.toLong())
            countryTotalPost.text = pTotal
        } else countryTotalPost.text = "0"
    }

    init {
        countryName = parent.findViewById(R.id.country_name)
        countryTotalPost = parent.findViewById(R.id.country_total_post)
    }
}