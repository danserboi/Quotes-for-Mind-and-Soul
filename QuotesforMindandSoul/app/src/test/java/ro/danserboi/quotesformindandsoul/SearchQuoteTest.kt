package ro.danserboi.quotesformindandsoul


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
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
class SearchQuoteTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun searchQuoteTest() {
        val floatingActionButton = onView(
                allOf(withId(R.id.fab),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.drawer_layout_main),
                                        0),
                                2),
                        isDisplayed()))
        floatingActionButton.perform(click())

        val appCompatEditText = onView(
                allOf(withId(R.id.input_text),
                        childAtPosition(
                                allOf(withId(R.id.search_dialog_layout),
                                        childAtPosition(
                                                withId(R.id.custom),
                                                0)),
                                2),
                        isDisplayed()))
        appCompatEditText.perform(replaceText("Federer"), closeSoftKeyboard())

        val appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)))
        appCompatButton.perform(scrollTo(), click())

        val appCompatToggleButton = onView(
                allOf(withId(R.id.favoriteButton), withText("Bookmark"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.LinearLayout")),
                                        2),
                                1),
                        isDisplayed()))
        appCompatToggleButton.perform(click())

        val appCompatButton2 = onView(
                allOf(withId(R.id.shareButton), withText("Share"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.LinearLayout")),
                                        2),
                                2),
                        isDisplayed()))
        appCompatButton2.perform(click())

        val recyclerView = onView(
                allOf(withId(R.id.search_quotes_recyclerview),
                        childAtPosition(
                                withClassName(`is`("android.widget.LinearLayout")),
                                0)))
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(5, click()))

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

        val appCompatButton5 = onView(
                allOf(withId(R.id.changeFontButtonSingle),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                3),
                        isDisplayed()))
        appCompatButton5.perform(click())

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

        val appCompatImageButton6 = onView(
                allOf(withId(R.id.changeBackgroundButtonSingle), withContentDescription("Change Background"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                4),
                        isDisplayed()))
        appCompatImageButton6.perform(click())

        val appCompatImageButton7 = onView(
                allOf(withId(R.id.changeBackgroundButtonSingle), withContentDescription("Change Background"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                4),
                        isDisplayed()))
        appCompatImageButton7.perform(click())

        val appCompatImageButton8 = onView(
                allOf(withId(R.id.changeBackgroundButtonSingle), withContentDescription("Change Background"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                4),
                        isDisplayed()))
        appCompatImageButton8.perform(click())

        val appCompatButton6 = onView(
                allOf(withId(R.id.copyButtonSingle),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                0),
                        isDisplayed()))
        appCompatButton6.perform(click())

        val appCompatButton7 = onView(
                allOf(withId(R.id.shareButtonSingle),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                2),
                        isDisplayed()))
        appCompatButton7.perform(click())

        val appCompatButton8 = onView(
                allOf(withId(R.id.copyButtonSingle),
                        childAtPosition(
                                allOf(withId(R.id.linearLayoutSingle),
                                        childAtPosition(
                                                withId(R.id.layout),
                                                2)),
                                0),
                        isDisplayed()))
        appCompatButton8.perform(click())
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
