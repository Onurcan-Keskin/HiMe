package com.huawei.hime.livestreamStreamaxia.pickphoto

import android.content.Context
import android.graphics.Bitmap
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import com.esafirm.imagepicker.model.Image
import com.huawei.hime.R

import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader


class GridViewAdapter(
	private val context : Context,
	private val imageUrls :String,
    private val urlSize: ArrayList<String>,
    //Variable to check if gridview is to setup for Custom Gallery or not
	private val isCustomGalleryActivity : Boolean
) :
    BaseAdapter() {
    private val mSparseBooleanArray //Variable to store selected Images
            : SparseBooleanArray = SparseBooleanArray()
    private val options: DisplayImageOptions = DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .resetViewBeforeLoading(true).cacheOnDisk(true)
        .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
        .build()

    private lateinit var mCheckBox: CheckBox

    //Method to return selected Images

    override fun getCount(): Int {
        return urlSize.size
    }

    override fun getItem(i: Int): Any {
        return imageUrls[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View? {
        var mView: View? = view
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (mView == null) mView =
            inflater.inflate(R.layout.custom_gallerygridviewitem, viewGroup, false) //Inflate layout
       // mCheckBox = mView!!.findViewById(R.id.selectCheckBox) as CheckBox
        val imageView: ImageView = mView!!.findViewById(R.id.galleryImageView) as ImageView

        //If Context is MainActivity then hide checkbox
//        if (!isCustomGalleryActivity) mCheckBox.visibility = View.GONE
        ImageLoader.getInstance().displayImage(
            "file://$imageUrls",
            imageView,
            options
        ) //Load Images over ImageView
        return mView
    }
}