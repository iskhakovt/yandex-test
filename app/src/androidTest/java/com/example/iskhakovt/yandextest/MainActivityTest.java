/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.internal.util.Checks;
import android.test.ActivityInstrumentationTestCase2;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;


@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private MainActivity mainActivity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mainActivity = getActivity();
    }

    @Test
    public void listViewTest() {
        Espresso.onView(withId(R.id.list_view)).perform(swipeDown());

        Espresso.onView(withText("Daft Punk")).perform(click());
        Espresso.pressBack();

        // TODO: // FIXME: 22/04/16
        // Espresso.onData(artistWithName("Imagine Dragons")).inAdapterView(withId(R.id.list_view)).perform(scrollTo(), click());
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Matcher<Object> artistWithName(String expectedName) {
        Checks.checkNotNull(expectedName);
        return artistWithName(equalTo(expectedName));
    }

    public static Matcher<Object> artistWithName(final Matcher<String> itemMatcher) {
        Checks.checkNotNull(itemMatcher);

        return new BoundedMatcher<Object, ArtistItem>(ArtistItem.class) {
            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("ArtistTime with name: ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(ArtistItem artistItem) {
                return itemMatcher.matches(artistItem.getName());
            }
        };
    }
}
