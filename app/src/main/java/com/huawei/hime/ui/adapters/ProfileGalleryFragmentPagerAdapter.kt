package com.huawei.hime.profile.profilegallery

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.huawei.hime.profile.profilegallery.profilegalleryphotos.ProfileGPhotosFragment
import com.huawei.hime.profile.profilegallery.profilegallerypictures.ProfileGPicturesFragment
import com.huawei.hime.profile.profilegallery.profilegalleryvideo.ProfileGVideoFragment

class ProfileGalleryFragmentPagerAdapter constructor(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(
        fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            2 -> fragment =
                ProfileGVideoFragment()
            0 -> fragment =
                ProfileGPicturesFragment()
            1 -> fragment =
                ProfileGPhotosFragment()
        }
        return fragment!!
    }

    fun addFragment(fragment: Fragment,title:String){
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }
}