package main

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.huawei.hime.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {

        val textView1 = onView(
                allOf(
                    withId(R.id.main_txt_name), withText("Hello Onurcan"),
                    childAtPosition(
                        allOf(
                            withId(R.id.tabs),
                            childAtPosition(
                                withId(R.id.main_b),
                                0
                            )
                        ),
                        0
                    ),
                    isDisplayed()
                )
                )
        textView1.check(matches(isDisplayed()))

        val textView = onView(
            allOf(
                withId(R.id.ts_txt_status), withText("FeelGood Inc. \uD83D\uDE00 "),
                childAtPosition(
                    allOf(
                        withId(R.id.tabs),
                        childAtPosition(
                            withId(R.id.main_b),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textView.check(matches(isDisplayed()))

        val imageView = onView(
            allOf(
                withId(R.id.main_circle_img),
                childAtPosition(
                    allOf(
                        withId(R.id.tabs),
                        childAtPosition(
                            withId(R.id.main_b),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        imageView.check(matches(isDisplayed()))

        val horizontalScrollView = onView(
            allOf(
                withId(R.id.ts_input_layout),
                childAtPosition(
                    allOf(
                        withId(R.id.tabs),
                        childAtPosition(
                            withId(R.id.main_b),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        horizontalScrollView.check(matches(isDisplayed()))

        val linearLayout = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.ts_input_layout),
                        childAtPosition(
                            withId(R.id.tabs),
                            3
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        linearLayout.check(matches(isDisplayed()))

        val textView2 = onView(
            allOf(
                withText("DISCOVER"),
                childAtPosition(
                    allOf(
                        withContentDescription("Discover"),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("DISCOVER")))

        val actionBarTab = onView(
        allOf(
            withContentDescription("Discover"),
            childAtPosition(
                childAtPosition(
                    withId(R.id.ts_input_layout),
                    0
                ),
                0
            ),
            isDisplayed()
        ))
        actionBarTab.check(matches(isDisplayed()))

        val actionBarTab2 = onView(
        allOf(
            withContentDescription("Fresh"),
            childAtPosition(
                childAtPosition(
                    withId(R.id.ts_input_layout),
                    0
                ),
                1
            ),
            isDisplayed()
        ))
        actionBarTab2.check(matches(isDisplayed()))

        val textView3 = onView(
            allOf(
                withText("FRESH"),
                childAtPosition(
                    allOf(
                        withContentDescription("Fresh"),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("FRESH")))

        val actionBarTab3 = onView(
        allOf(
            withContentDescription("Trending"),
            childAtPosition(
                childAtPosition(
                    withId(R.id.ts_input_layout),
                    0
                ),
                2
            ),
            isDisplayed()
        ))
        actionBarTab3.check(matches(isDisplayed()))

        val textView4 = onView(
            allOf(
                withText("TRENDING"),
                childAtPosition(
                    allOf(
                        withContentDescription("Trending"),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                            2
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView4.check(matches(withText("TRENDING")))

        val actionBarTab4 = onView(
        allOf(
            withContentDescription("Find\nHiMe'rs"),
            childAtPosition(
                childAtPosition(
                    withId(R.id.ts_input_layout),
                    0
                ),
                3
            ),
            isDisplayed()
        ))
        actionBarTab4.check(matches(isDisplayed()))

        val textView5 = onView(
            allOf(
                withText("FIND\nHIME'RS"),
                childAtPosition(
                    allOf(
                        withContentDescription("Find\nHiMe'rs"),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                            3
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView5.check(matches(withText("FIND HIME'RS")))

        val viewPager = onView(
            allOf(
                withId(R.id.main_pager),
                childAtPosition(
                    allOf(
                        withId(R.id.main_b),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.view.ViewGroup::class.java),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        viewPager.check(matches(isDisplayed()))

        val frameLayout = onView(
            allOf(
                withId(R.id.main_bottom_app_bar),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.view.ViewGroup::class.java),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        frameLayout.check(matches(isDisplayed()))

        val frameLayout2 = onView(
            allOf(
                childAtPosition(
                    childAtPosition(
                        withId(R.id.main_bottom_app_bar),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        frameLayout2.check(matches(isDisplayed()))

        val imageView2 = onView(
            allOf(
                withId(R.id.icon_view),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(androidx.recyclerview.widget.RecyclerView::class.java),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        imageView2.check(matches(isDisplayed()))

        val frameLayout3 = onView(
            allOf(
                childAtPosition(
                    childAtPosition(
                        withId(R.id.main_bottom_app_bar),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        frameLayout3.check(matches(isDisplayed()))

        val imageView3 = onView(
            allOf(
                withId(R.id.icon_view),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(androidx.recyclerview.widget.RecyclerView::class.java),
                        2
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        imageView3.check(matches(isDisplayed()))

        val imageView4 = onView(
            allOf(
                withId(R.id.icon_view),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(androidx.recyclerview.widget.RecyclerView::class.java),
                        2
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        imageView4.check(matches(isDisplayed()))

        val frameLayout4 = onView(
            allOf(
                childAtPosition(
                    childAtPosition(
                        withId(R.id.main_bottom_app_bar),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        frameLayout4.check(matches(isDisplayed()))

        val imageView5 = onView(
            allOf(
                withId(R.id.icon_view),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(androidx.recyclerview.widget.RecyclerView::class.java),
                        4
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        imageView5.check(matches(isDisplayed()))

        val imageView6 = onView(
            allOf(
                withId(R.id.icon_view),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(androidx.recyclerview.widget.RecyclerView::class.java),
                        4
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        imageView6.check(matches(isDisplayed()))

        val tabView = onView(
            allOf(
                withContentDescription("Find\nHiMe'rs"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ts_input_layout),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        tabView.perform(click())

        val constraintLayout = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.find_follower_recycler), withContentDescription("Follower"),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        constraintLayout.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
