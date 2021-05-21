package main

import ads.HmsGmsAdsHelper
import android.graphics.Typeface
import com.google.android.material.tabs.TabLayout
import com.huawei.hime.R
import de.hdodenhof.circleimageview.CircleImageView
import nl.joery.animatedbottombar.AnimatedBottomBar
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig
import uk.co.deanwild.materialshowcaseview.ShowcaseTooltip

class MainPresenter constructor(
    private val mainContract: MainContract,
    private val hmsGmsAdsHelper: HmsGmsAdsHelper
) {

    var i = 0

    fun onViewCreate() {
        mainContract.initViews()
        mainContract.setUpViewPager()
        mainContract.populateViewWithDB()
    }

    fun onMainBannerClick() {
        mainContract.gotoProfile()
    }

    fun onMapItemClick() {
        mainContract.mapItemClick()
    }

    fun onShutterItemClick() {
        mainContract.shutterItemClick()
    }

    fun showAds() {
        hmsGmsAdsHelper.initAdsView()
    }

    fun displayTuto(
        context: MainActivity,
        mainCircle: CircleImageView,
        tabsLayout: TabLayout,
        animBottom: AnimatedBottomBar
    ) {
        // Tuto Showcase
//        val showcaseBannerTuto = TutoShowcase.from(context)
//            .setContentView(R.layout.tuto_main_prof)
//            .on(R.id.main_circle_img)
//            .addCircle()
//            .withBorder()
//            .on(R.id.swics)
//            .displaySwipableRight()
//            .delayed(399)
//            .animated(true)
//           .show()

        val dismissTextFace = Typeface.createFromAsset(context.assets, "fonts/aldrich.ttf")
        val tooltip = ShowcaseTooltip.build(context)
            .arrowWidth(45)
            .corner(30)
            .text("This is a <b>very funky</b> tooltip<br><br>This is a very long sentence to test how this tooltip behaves with longer strings. <br><br>Tap anywhere to continue")
            .border(R.color.red, 10F)
            .textColor(R.color.white)

        // Material Showcase
        val showcaseBanner = MaterialShowcaseView.Builder(context)
            .setSkipText(context.getString(R.string.skip))
            .setSkipStyle(dismissTextFace)
            .setTarget(mainCircle)
            .setDismissText(context.getText(R.string.proceed))
            .setDismissStyle(dismissTextFace)
            .setContentText(context.getString(R.string.tuto_main_banner))
            .setTitleText(context.getString(R.string.app_name))
            .withCircleShape()
            .setDelay(R.integer.showcase_delay)
            .singleUse(context.getString(R.string.showcase_main))
            .build()

        val showcaseDiscoverH = MaterialShowcaseView.Builder(context)
            .setSkipText(context.getString(R.string.skip))
            .setSkipStyle(dismissTextFace)
            .setTarget(tabsLayout)
            .setDismissText(context.getText(R.string.proceed))
            .setDismissStyle(dismissTextFace)
            .setContentText(context.getString(R.string.tuto_main_discover_h))
            .setTitleText(context.getString(R.string.frag_discover))
            .withRectangleShape()
            .setDelay(R.integer.showcase_delay)
            .singleUse(context.getString(R.string.showcase_main))
            .build()

        val showcaseDiscoverT = MaterialShowcaseView.Builder(context)
            .setSkipText(context.getString(R.string.skip))
            .setSkipStyle(dismissTextFace)
            .setTarget(tabsLayout)
            .setDismissText(context.getText(R.string.proceed))
            .setDismissStyle(dismissTextFace)
            .setContentText(context.getString(R.string.tuto_main_discover_t))
            .setTitleText(context.getString(R.string.frag_discover))
            .withRectangleShape()
            .setDelay(R.integer.showcase_delay)
            .singleUse(context.getString(R.string.showcase_main))
            .build()

        val showcaseDiscoverE = MaterialShowcaseView.Builder(context)
            .setSkipText(context.getString(R.string.skip))
            .setSkipStyle(dismissTextFace)
            .setTarget(tabsLayout)
            .setDismissText(context.getText(R.string.proceed))
            .setDismissStyle(dismissTextFace)
            .setContentText(context.getString(R.string.tuto_main_discover_e))
            .setTitleText(context.getString(R.string.frag_discover))
            .withRectangleShape()
            .setDelay(R.integer.showcase_delay)
            .singleUse(context.getString(R.string.showcase_main))
            .build()

        val showcaseDiscoverG = MaterialShowcaseView.Builder(context)
            .setSkipText(context.getString(R.string.skip))
            .setSkipStyle(dismissTextFace)
            .setTarget(tabsLayout)
            .setDismissText(context.getText(R.string.proceed))
            .setDismissStyle(dismissTextFace)
            .setContentText(context.getString(R.string.tuto_main_discover_g))
            .setTitleText(context.getString(R.string.frag_discover))
            .withRectangleShape()
            .setDelay(R.integer.showcase_delay)
            .singleUse(context.getString(R.string.showcase_main))
            .build()

        val showcaseFreshH = MaterialShowcaseView.Builder(context)
            .setSkipText(context.getString(R.string.skip))
            .setSkipStyle(dismissTextFace)
            .setTarget(tabsLayout)
            .setDismissText(context.getText(R.string.proceed))
            .setDismissStyle(dismissTextFace)
            .setContentText(context.getString(R.string.tuto_main_fresh_h))
            .setTitleText(context.getString(R.string.frag_fresh))
            .withRectangleShape()
            .setDelay(R.integer.showcase_delay)
            .singleUse(context.getString(R.string.showcase_main))
            .build()

        val showcaseFreshT = MaterialShowcaseView.Builder(context)
            .setSkipText(context.getString(R.string.skip))
            .setSkipStyle(dismissTextFace)
            .setTarget(tabsLayout)
            .setDismissText(context.getText(R.string.proceed))
            .setDismissStyle(dismissTextFace)
            .setContentText(context.getString(R.string.tuto_main_fresh_t))
            .setTitleText(context.getString(R.string.frag_fresh))
            .withRectangleShape()
            .setDelay(R.integer.showcase_delay)
            .singleUse(context.getString(R.string.showcase_main))
            .build()

        val showcaseFreshE = MaterialShowcaseView.Builder(context)
            .setSkipText(context.getString(R.string.skip))
            .setSkipStyle(dismissTextFace)
            .setTarget(tabsLayout)
            .setDismissText(context.getText(R.string.proceed))
            .setDismissStyle(dismissTextFace)
            .setContentText(context.getString(R.string.tuto_main_fresh_e))
            .setTitleText(context.getString(R.string.frag_fresh))
            .withRectangleShape()
            .setDelay(R.integer.showcase_delay)
            .singleUse(context.getString(R.string.showcase_main))
            .build()

        val showcaseFreshG = MaterialShowcaseView.Builder(context)
            .setSkipText(context.getString(R.string.skip))
            .setSkipStyle(dismissTextFace)
            .setTarget(tabsLayout)
            .setDismissText(context.getText(R.string.proceed))
            .setDismissStyle(dismissTextFace)
            .setContentText(context.getString(R.string.tuto_main_fresh_g))
            .setTitleText(context.getString(R.string.frag_fresh))
            .withRectangleShape()
            .setDelay(R.integer.showcase_delay)
            .singleUse(context.getString(R.string.showcase_main))
            .build()

        val showcaseTrendingH = MaterialShowcaseView.Builder(context)
            .setSkipText(context.getString(R.string.skip))
            .setSkipStyle(dismissTextFace)
            .setTarget(tabsLayout)
            .setDismissText(context.getText(R.string.proceed))
            .setDismissStyle(dismissTextFace)
            .setContentText(context.getString(R.string.tuto_main_trending_h))
            .setTitleText(context.getString(R.string.frag_trend))
            .withRectangleShape()
            .setDelay(R.integer.showcase_delay)
            .singleUse(context.getString(R.string.showcase_main))
            .build()

        val showcaseTrendingT = MaterialShowcaseView.Builder(context)
            .setSkipText(context.getString(R.string.skip))
            .setSkipStyle(dismissTextFace)
            .setTarget(tabsLayout)
            .setDismissText(context.getText(R.string.proceed))
            .setDismissStyle(dismissTextFace)
            .setContentText(context.getString(R.string.tuto_main_trending_t))
            .setTitleText(context.getString(R.string.frag_trend))
            .withRectangleShape()
            .setDelay(R.integer.showcase_delay)
            .singleUse(context.getString(R.string.showcase_main))
            .build()

        val showcaseTrendingE = MaterialShowcaseView.Builder(context)
            .setSkipText(context.getString(R.string.skip))
            .setSkipStyle(dismissTextFace)
            .setTarget(tabsLayout)
            .setDismissText(context.getText(R.string.proceed))
            .setDismissStyle(dismissTextFace)
            .setContentText(context.getString(R.string.tuto_main_trending_e))
            .setTitleText(context.getString(R.string.frag_trend))
            .withRectangleShape()
            .setDelay(R.integer.showcase_delay)
            .singleUse(context.getString(R.string.showcase_main))
            .build()

        val showcaseTrendingG = MaterialShowcaseView.Builder(context)
            .setSkipText(context.getString(R.string.skip))
            .setSkipStyle(dismissTextFace)
            .setTarget(tabsLayout)
            .setDismissText(context.getText(R.string.proceed))
            .setDismissStyle(dismissTextFace)
            .setContentText(context.getString(R.string.tuto_main_trending_g))
            .setTitleText(context.getString(R.string.frag_trend))
            .withRectangleShape()
            .setDelay(R.integer.showcase_delay)
            .singleUse(context.getString(R.string.showcase_main))
            .build()

        val showcaseFind = MaterialShowcaseView.Builder(context)
            .setSkipText(context.getString(R.string.skip))
            .setSkipStyle(dismissTextFace)
            .setTarget(tabsLayout)
            .setDismissText(context.getText(R.string.proceed))
            .setDismissStyle(dismissTextFace)
            .setContentText(context.getString(R.string.tuto_main_find))
            .setTitleText(context.getString(R.string.frag_find_hime_rs))
            .withRectangleShape()
            .setDelay(R.integer.showcase_delay)
            .singleUse(context.getString(R.string.showcase_main))
            .build()

        val showcaseLive = MaterialShowcaseView.Builder(context)
            .setSkipText(context.getString(R.string.skip))
            .setSkipStyle(dismissTextFace)
            .setTarget(animBottom)
            .setDismissText(context.getText(R.string.end_tuto))
            .setDismissStyle(dismissTextFace)
            .setContentText(context.getString(R.string.tuto_main_live))
            .setTitleText(context.getString(R.string.live_stream))
            .withRectangleShape()
            .setDelay(R.integer.showcase_delay)
            .singleUse(context.getString(R.string.showcase_main))
            .build()

        val config = ShowcaseConfig()
        config.dismissTextStyle = dismissTextFace
        config.delay = 120
        val sequence = MaterialShowcaseSequence(context)
        sequence.setConfig(config)
        // Banner
        sequence.addSequenceItem(showcaseBanner)
        // Discover
        sequence.addSequenceItem(showcaseDiscoverH)
        sequence.addSequenceItem(showcaseDiscoverT)
        sequence.addSequenceItem(showcaseDiscoverE)
        sequence.addSequenceItem(showcaseDiscoverG)
        // Fresh
        sequence.addSequenceItem(showcaseFreshH)
        sequence.addSequenceItem(showcaseFreshT)
        sequence.addSequenceItem(showcaseFreshE)
        sequence.addSequenceItem(showcaseFreshG)
        // Trending
        sequence.addSequenceItem(showcaseTrendingH)
        sequence.addSequenceItem(showcaseTrendingT)
        sequence.addSequenceItem(showcaseTrendingE)
        sequence.addSequenceItem(showcaseTrendingG)
        // Find
        sequence.addSequenceItem(showcaseFind)
        // Live
        sequence.addSequenceItem(showcaseLive)
        sequence.start()
    }
}

interface MainContract {
    fun setUpViewPager()
    fun initViews()
    fun populateViewWithDB()
    fun gotoProfile()
    fun mapItemClick()
    fun shutterItemClick()
}

data class MainUserInfoData(
    val nameSurname: String,
    val email: String,
    val userId: String,
    val photoUrl: String,
    val thumbUrl: String,
    val interests: String
)