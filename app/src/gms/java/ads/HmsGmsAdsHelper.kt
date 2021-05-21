package ads

import android.content.Context
import android.util.Log
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.huawei.hime.R
import main.MainActivity
import main.MainAdsHelper

class HmsGmsAdsHelper(context: MainActivity) : MainAdsHelper {

    private var mContext: Context = context
    private val view = context.findViewById<View>(R.id.bannerViewInc)
    private lateinit var adsView: AdView

    override fun initAdsView() {
//        adsView = AdView(mContext)
//        adsView.adSize = AdSize.SMART_BANNER
//        adsView.adUnitId = mContext.getString(R.string.admob_banner_dummyID)

        MobileAds.initialize(
            mContext
        ) {
            adsView = view.findViewById(R.id.bannerView)
            val adRequest = AdRequest.Builder().build()
            adsView.loadAd(adRequest)
        }

    }

    override fun adsClicked() {
        adsView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(mTag, "Ad Status: onAdLoaded")
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.d(mTag, "Ad Status: onAdClicked")
            }

            override fun onAdLeftApplication() {
                super.onAdLeftApplication()
                Log.d(mTag, "Ad Status: onAdLeftApplication")
            }
        }
    }

    companion object {
        private const val mTag = "HmsGmsAdsHelper"
    }
}