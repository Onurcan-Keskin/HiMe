package com.huawei.hime.profile.profileevent.caption

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.huawei.hime.R
import com.huawei.hime.models.CaptionInterestType

class CaptionInterestsCustomAdapter(var imgList: List<CaptionInterestType>, var context: FragmentActivity): BaseAdapter() {
	override fun getCount() : Int {
		return imgList.size
	}

	override fun getItem(position : Int) : Any {
		return imgList[position]
	}

	override fun getItemId(position : Int) : Long {
		return position.toLong()
	}

	@SuppressLint("ViewHolder")
	override fun getView(position : Int, convertView : View?, parent : ViewGroup?) : View {
		//val view:View = View.inflate(context.applicationContext, R.layout.single_interests_card,null)
		val view = LayoutInflater.from(context).inflate(R.layout.single_interests_card,null)
		val tvExp = view.findViewById<TextView>(R.id.single_interest_txt)
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
}