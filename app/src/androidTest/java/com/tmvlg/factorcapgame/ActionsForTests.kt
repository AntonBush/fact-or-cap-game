package com.tmvlg.factorcapgame

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId


open class ActionsForTests {

    fun click(btn: Int){
        onView(withId(btn)).perform(click())
    }

    fun isDisplayed(elem: Int){
        onView(withId(elem)).check(matches(isDisplayed()))
    }

    fun checkDisplayedAll(vararg elements: Int){
        for(elem in elements) {
            isDisplayed(elem)
        }
    }
}