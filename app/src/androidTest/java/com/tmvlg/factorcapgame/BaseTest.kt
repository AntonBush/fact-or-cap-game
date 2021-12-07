package com.tmvlg.factorcapgame

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*

open class BaseTest {

    fun click(btn: Int) {
        onView(withId(btn)).perform(click())
    }

    fun clickByText(text: String) {
        onView(withText(text)).perform(click())
    }

    fun isDisplayed(elem: Int) {
        onView(withId(elem)).check(matches(isDisplayed()))
    }

    fun isDisplayedByText(text: String) {
        onView(withText(text)).check(matches(isDisplayed()))
    }

    fun checkDisplayedAll(vararg elements: Int) {
        for (elem in elements) {
            isDisplayed(elem)
        }
    }

    fun enterData(elem: Int, text: String) {
        onView(withId(elem)).perform(click(), clearText(), typeText(text), closeSoftKeyboard())
    }

    fun enterDataByHint(hint: String, text: String) {
        onView(withHint(hint)).perform(clearText(), typeText(text), closeSoftKeyboard())
    }

    fun clickOnRecyclerViewItem(recyclerId: Int) {
        onView(withId(recyclerId))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                click()))
    }

    private fun waiting() {
        Thread.sleep(3000)
    }
}
