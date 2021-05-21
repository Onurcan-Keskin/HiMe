package com.huawei.hime.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.huawei.hime.R
import com.huawei.hime.models.ImageData
import github.hellocsl.cursorwheel.CursorWheelLayout

class WheelImageAdapter(mContext: Context?, menuItems: List<ImageData?>?) : CursorWheelLayout.CycleWheelAdapter() {

    private var mContext: Context? = null
    private var menuItems: List<ImageData>? = null
    private var inflater: LayoutInflater? = null
    private val gravity = 0

    init{
        this.mContext = mContext
        this.menuItems = menuItems as List<ImageData>?
        inflater = LayoutInflater.from(mContext)
    }

    override fun getCount(): Int {
        return menuItems!!.size
    }

    override fun getView(parent: View?, position: Int): View? {
        val data = getItem(position)
        val root: View = inflater!!.inflate(R.layout.wheel_image_layout, null, false)
        val imageView =
            root.findViewById<View>(R.id.wheel_menu_item_iv) as ImageView
        imageView.setImageResource(data.ImageResource)
        return root
    }

    override fun getItem(position: Int): ImageData {
        return menuItems!![position]
    }
}

