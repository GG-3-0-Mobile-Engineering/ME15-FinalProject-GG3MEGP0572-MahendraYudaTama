package com.example.finalprojectgigih

import android.view.View
import android.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityInstrumentedTest {

    @Before
    fun setup() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun checkSearchSuggestion() {
        Espresso.onView(withId(R.id.search_view)).perform(SearchViewActionExtension.typeText("jawa barat"))
        Espresso.onView(ViewMatchers.withText("Jawa Barat")).perform(click())
    }

    @Test
    fun checkFilterActivity() {
        Espresso.onView(withId(R.id.btn_search_flood)).perform(click())
        Espresso.onView(ViewMatchers.withText("flood"))
    }

    @Test
    fun checkSettingActivity() {
        Intents.init()
        Espresso.onView(withId(R.id.fab_setting)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(SettingActivity::class.java.name))
        Espresso.onView(withId(R.id.content_preference)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }


}

class SearchViewActionExtension{
    companion object{
        fun submitText(text:String):ViewAction{
            return object : ViewAction {
                override fun getConstraints(): Matcher<View> {
                    return allOf(isDisplayed(), isAssignableFrom(SearchView::class.java))
                }

                override fun getDescription(): String {
                    return "Set text and submit"
                }

                override fun perform(uiController: UiController?, view: View?) {
                    (view as SearchView).setQuery(text,true)
                }
            }
        }
        fun typeText(text: String):ViewAction{
            return object :ViewAction{
                override fun getConstraints(): Matcher<View> {
                    return allOf(isDisplayed(), isAssignableFrom(SearchView::class.java))
                }

                override fun getDescription(): String {
                    return "Set text"
                }

                override fun perform(uiController: UiController?, view: View?) {
                    (view as SearchView).setQuery(text,false)
                }
            }
        }
    }
}