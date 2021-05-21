package com.huawei.hime.interests

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.huawei.hime.R
import com.huawei.hime.models.InterestsType
import com.huawei.hime.ui.activities.InterestsActivity


data class GridCustomAdapter(var imgList: List<InterestsType>, var activity: InterestsActivity) :
    BaseAdapter() {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(activity, R.layout.single_interests_card, null)
        val tvExp = view.findViewById<TextView>(R.id.single_interest_txt) as TextView
        val imgExp = view.findViewById<ImageView>(R.id.single_interest_img)
        val checkExp = view.findViewById<CheckBox>(R.id.single_interest_checkBox)

        tvExp.text = imgList[position].imgName
        val imgPic = imgList[position].imgIcon
        imgExp.setImageResource(imgPic)

        checkExp.isChecked = imgList[position].isSelectedChck

        checkExp.setOnCheckedChangeListener { _, isChecked ->
            imgList[position].isSelectedChck = isChecked

        }

        return view
    }

    override fun getItem(position: Int): Any {
        return imgList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return imgList.size
    }
}