package cn.hugeterry.coordinatortablayoutdemo;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import cn.hugeterry.coordinatortablayoutdemo.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn), withText("Words Category 추가"),
                        childAtPosition(
                                withParent(withId(R.id.vp)),
                                0),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.category),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("q"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.addBtn), withText("카테고리 추가"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());

        onData(anything())
                .inAdapterView(allOf(withId(R.id.listview), isCompletelyDisplayed()))
                .atPosition(0).perform(click());

        ViewInteraction appCompatButtona = onView(
                allOf(withId(R.id.add), withText("추가요"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatButtona.perform(click());

        try{
            Thread.sleep(500);
        }catch (Exception e){

        }

        ViewInteraction appCompatEditText32 = onView(
                allOf(withId(R.id.spelling),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText32.perform(replaceText("dog"), closeSoftKeyboard());

        try{
            Thread.sleep(500);
        }catch (Exception e){

        }

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.meaning),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("개"), closeSoftKeyboard());

        try{
            Thread.sleep(500);
        }catch (Exception e){

        }

        ViewInteraction appCompatButton22 = onView(
                allOf(withId(R.id.addBtn), withText("단어 추가"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        5),
                                1),
                        isDisplayed()));
        appCompatButton22.perform(click());

        try{
            Thread.sleep(500);
        }catch (Exception e){

        }

        ViewInteraction viewPager = onView(
                allOf(withId(R.id.viewPager),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        viewPager.perform(swipeLeft());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.showMeaning), withText("뜻볼래요"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatButton3.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}