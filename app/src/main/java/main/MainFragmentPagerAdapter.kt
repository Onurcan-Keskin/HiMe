package main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.huawei.hime.fresh.FreshFragment
import com.huawei.hime.trending.TrendingFragment
import com.huawei.hime.discover.DiscoverFragment
import com.huawei.hime.findfollower.FindFollowerFragment

class MainFragmentPagerAdapter internal constructor(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(
        fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = DiscoverFragment()
            1 -> fragment = FreshFragment()
            2 -> fragment = TrendingFragment()
            3 -> fragment = FindFollowerFragment()
        }
        return fragment!!
    }

    fun addFragment(fragment: Fragment,title:String){
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

}