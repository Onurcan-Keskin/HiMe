package com.huawei.hime.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.huawei.hime.livestreamStreamaxia.pickphoto.PickPhotoFragment
import com.huawei.hime.ui.fragments.CaptureVideoFragment
import com.huawei.hime.livestreamStreamaxia.takephoto.TakePhotoFragment

class LiveStreamPagerAdapter internal constructor(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = TakePhotoFragment()
            1 -> fragment = CaptureVideoFragment()
            2 -> fragment = PickPhotoFragment()
        }
        return fragment!!
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

    override fun getCount(): Int {
        return 3
    }
}