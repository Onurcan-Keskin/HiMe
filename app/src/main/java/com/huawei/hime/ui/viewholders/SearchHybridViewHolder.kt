package com.huawei.hime.util.viewHolders

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.huawei.hime.R
import com.klinker.android.simple_videoview.SimpleVideoView
import com.squareup.picasso.Picasso
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class SearchHybridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val parent: View = itemView

    private var layout:LinearLayout
    private var params : LinearLayout.LayoutParams

    private var mImageCard: MaterialCardView
    private var mMapCard: MaterialCardView
    private var mVideoCard: MaterialCardView

    private var mEventCard: MaterialCardView
    private var mViewPager : ViewPager
    private var dotsIndicator: DotsIndicator

    private var mImageView: ImageView
    private var mMapView: ImageView
    private var mVideoView: SimpleVideoView

    init {
        layout = parent.findViewById(R.id.hybridCardRelative)
        params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)

        mImageCard = parent.findViewById(R.id.single_hybrid_imageCard)
        mMapCard = parent.findViewById(R.id.single_hybrid_mapCard)
        mVideoCard = parent.findViewById(R.id.single_hybrid_videoCard)

        mImageView = parent.findViewById(R.id.single_hybrid_imageView)
        mMapView = parent.findViewById(R.id.single_hybrid_mapView)
        mVideoView = parent.findViewById(R.id.single_hybrid_videoView)

        mViewPager = parent.findViewById(R.id.single_hybrid_pager)
        dotsIndicator = parent.findViewById(R.id.events_dots)
        mEventCard = parent.findViewById(R.id.single_hybrid_eventCard)
    }

    fun bindImageHolder(string: String) {
        mImageCard.visibility = View.VISIBLE
        mMapCard.visibility = View.GONE
        mVideoCard.visibility = View.GONE

        Picasso.get().load(string).fit().centerCrop().into(mImageView)
    }

    fun bindEventHolder(coverImage:String,
                        photoImage:String,
                        e0 : String,
                        e1 : String,
                        e2 : String,
                        e3 : String,
                        e4 : String){
        mImageCard.visibility = View.GONE
        mMapCard.visibility = View.GONE
        mVideoCard.visibility = View.GONE
        mEventCard.visibility=View.VISIBLE

        val imageUrls = ArrayList<String>()
        if (coverImage.isNotEmpty()) imageUrls.add(coverImage)
        if (photoImage.isNotEmpty()) imageUrls.add(photoImage)
        if (e0.isNotEmpty()) imageUrls.add(e0)
        if (e1.isNotEmpty()) imageUrls.add(e1)
        if (e2.isNotEmpty()) imageUrls.add(e2)
        if (e3.isNotEmpty()) imageUrls.add(e3)
        if (e4.isNotEmpty()) imageUrls.add(e4)
        val adapter = EventViewPagerAdapter(parent.context,imageUrls)
        mViewPager.adapter = adapter
        dotsIndicator.setViewPager(mViewPager)
    }

    fun bindMapHolder(string: String) {
        mImageCard.visibility = View.GONE
        mMapCard.visibility = View.VISIBLE
        mVideoCard.visibility = View.GONE
        mEventCard.visibility=View.GONE

        Picasso.get().load(string).fit().centerCrop().into(mMapView)
    }

    fun bindVideoHolder(string: String) {
        mImageCard.visibility = View.GONE
        mMapCard.visibility = View.GONE
        mEventCard.visibility=View.GONE
        mVideoCard.visibility = View.VISIBLE

        mVideoView.start(Uri.parse(string))
    }

    fun emptyHolder(){
//        params.height=0
//        parent.layoutParams=params
        parent.visibility=View.GONE
        mImageCard.visibility = View.GONE
        mMapCard.visibility = View.GONE
        mVideoCard.visibility = View.GONE
    }
}