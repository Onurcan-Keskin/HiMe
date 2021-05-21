package com.huawei.hime.search.singleuserpage

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.huawei.hime.R
import com.huawei.hime.models.SingleUserInterestType
import com.huawei.hime.ui.activities.SingleUserActivity

class SingleUserInterestsAdapter(
	var imgList: List<SingleUserInterestType>,
	var activity: SingleUserActivity
): BaseAdapter() {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view:View = View.inflate(activity, R.layout.single_master_interests,null)
        val tvExp = view.findViewById<TextView>(R.id.master_interests_txt)
        val imgExp = view.findViewById<ImageView>(R.id.master_interests_img)

        tvExp.text = imgList[position].imgName
        val imgPic = imgList[position].imgIcon
        imgExp.setImageResource(imgPic)

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