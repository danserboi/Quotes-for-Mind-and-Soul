package ro.danserboi.quotesformindandsoul


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class BrowseCategoryTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest2() {
        val recyclerView = onView(
                allOf(withId(R.id.categoryRecyclerView),
                        childAtPosition(
                                withId(R.id.relative_layout),
                                0)))
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(6, click()))

        val recyclerView2 = onView(
                allOf(withId(R.id.quotes_recyclerview),
                        childAtPosition(
                                withClassName(`is`("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                                1)))
        recyclerView2.perform(actionOnItemAtPosition<ViewHolder>(2, click()))

        val appCompatToggleButton = onView(
                allOf(withId(R.id.favoriteButtonSingle),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                1),
                        isDisplayed()))
        appCompatToggleButton.perform(click())

        val appCompatButton = onView(
                allOf(withId(R.id.changeFontButtonSingle),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                3),
                        isDisplayed()))
        appCompatButton.perform(click())

        val appCompatButton2 = onView(
                allOf(withId(R.id.changeFontButtonSingle),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                3),
                        isDisplayed()))
        appCompatButton2.perform(click())

        val appCompatButton3 = onView(
                allOf(withId(R.id.changeFontButtonSingle),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                3),
                        isDisplayed()))
        appCompatButton3.perform(click())

        val appCompatButton4 = onView(
                allOf(withId(R.id.changeFontButtonSingle),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                3),
                        isDisplayed()))
        appCompatButton4.perform(click())

        val appCompatImageButton = onView(
                allOf(withId(R.id.changeBackgroundButtonSingle), withContentDescription("Change Background"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                4),
                        isDisplayed()))
        appCompatImageButton.perform(click())

        val appCompatImageButton2 = onView(
                allOf(withId(R.id.changeBackgroundButtonSingle), withContentDescription("Change Background"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                4),
                        isDisplayed()))
        appCompatImageButton2.perform(click())

        val appCompatImageButton3 = onView(
                allOf(withId(R.id.changeBackgroundButtonSingle), withContentDescription("Change Background"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                4),
                        isDisplayed()))
        appCompatImageButton3.perform(click())

        val appCompatImageButton4 = onView(
                allOf(withId(R.id.changeBackgroundButtonSingle), withContentDescription("Change Background"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                4),
                        isDisplayed()))
        appCompatImageButton4.perform(click())

        val appCompatImageButton5 = onView(
                allOf(withId(R.id.changeBackgroundButtonSingle), withContentDescription("Change Background"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                4),
                        isDisplayed()))
        appCompatImageButton5.perform(click())

        val appCompatButton5 = onView(
                allOf(withId(R.id.copyButtonSingle),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                0),
                        isDisplayed()))
        appCompatButton5.perform(click())
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

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
