package com.huawei.hime.profile.profilegallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.profile.profilegallery.profilegalleryphotos.ProfileGPhotosFragment
import com.huawei.hime.profile.profilegallery.profilegallerypictures.ProfileGPicturesFragment
import com.huawei.hime.profile.profilegallery.profilegalleryvideo.ProfileGVideoFragment
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.showLogDebug

class ProfileGalleryFragment : Fragment(), ProfileGalleryContract {

    private var mView: View? = null
    private var tabs: TabLayout? = null
    private var pager: ViewPager? = null

    private val currentID = AppUser.getUserId()

    private lateinit var mUserInfoRootDB: DatabaseReference
    private lateinit var totalPPRef:DatabaseReference
    private lateinit var totalPRef:DatabaseReference
    private lateinit var totalVRef:DatabaseReference

    private var totalPP: String? = ""
    private var totalP: String? = ""
    private var totalV: String? = ""

    private lateinit var context: FragmentActivity
    private lateinit var sharedPrefs: SharedPreferencesManager

    private val profileGalleryPresenter: ProfileGalleryPresenter by lazy {
        ProfileGalleryPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        context = activity!!
        sharedPrefs = SharedPreferencesManager(context)
        if (sharedPrefs.loadNightModeState()) {
            context.setTheme(R.style.DarkMode)
        } else {
            context.setTheme(R.style.LightMode)
        }
        super.onCreate(savedInstanceState)
        profileGalleryPresenter.onViewsCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_profile_gallery, container, false)
        context = activity!!
        try {
            profileGalleryPresenter.onViewsCreate()
            setupViewPager()
        } catch (e: Exception) {
            showLogDebug(mTag, e.message!!)
        }
        return mView
    }

    override fun initDB() {
        mUserInfoRootDB = FirebaseDBHelper.getUserFeedRootRef()

        /* Total PP */
        totalPPRef = mUserInfoRootDB.child("Profile Pictures/$currentID")
        totalPPRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    totalPP = NumberConvertor.prettyCount(p0.childrenCount)
                } else {
                    totalPP = ""
                }
            }
        })

        /* Total P */
        totalPRef = mUserInfoRootDB.child("Photos/$currentID")
        totalPRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    totalP = NumberConvertor.prettyCount(p0.childrenCount)
                } else {
                    totalP = ""
                }
            }
        })
        showLogDebug(mTag, "tpp  $totalPP, tp $totalP")
    }

    override fun setupViewPager() {
        tabs = mView!!.findViewById(R.id.profile_master_gallery_tabs)
        pager = mView!!.findViewById(R.id.profile_master_gallery_view_pager)

        if (sharedPrefs.loadNightModeState()) {
            tabs!!.background = context.getDrawable(R.drawable.ic_fragment_dark)
        } else {
            tabs!!.background = context.getDrawable(R.drawable.ic_fragment_background_gallery)
        }
        val adapterL = ProfileGalleryFragmentPagerAdapter(fragmentManager!!)
        adapterL.addFragment(
            ProfileGPicturesFragment(),
            "$totalPP " + getString(R.string.prof_profile)
        )
        adapterL.addFragment(
            ProfileGPhotosFragment(),
            "$totalP " + getString(R.string.prof_photo)
        )
        adapterL.addFragment(ProfileGVideoFragment(), getString(R.string.prof_video))
        pager!!.adapter = adapterL
        tabs!!.setupWithViewPager(pager)
    }

    companion object {
        private const val mTag = "profileGalleryPresenter"
    }
}
