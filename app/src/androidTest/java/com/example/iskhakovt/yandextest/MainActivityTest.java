/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.internal.util.Checks;
import android.support.test.uiautomator.UiDevice;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.EditText;

import com.robotium.solo.SystemUtils;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;


public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private MainActivity mainActivity;

    private SystemAnimations systemAnimations;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        wakeUp();

        systemAnimations = new SystemAnimations(getInstrumentation().getContext());
        systemAnimations.disableAll();

        setNetworkState(true);

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mainActivity = getActivity();
    }

    public void wakeUp() {
        // Wake up
        final UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Point[] coordinates = new Point[4];
        coordinates[0] = new Point(248, 1520);
        coordinates[1] = new Point(248, 929);
        coordinates[2] = new Point(796, 1520);
        coordinates[3] = new Point(796, 429);
        try {
            if (!uiDevice.isScreenOn()) {
                uiDevice.wakeUp();
                uiDevice.swipe(coordinates, 10);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setNetworkState(boolean state) {
        final SystemUtils systemUtils = new SystemUtils(getInstrumentation());
        systemUtils.setMobileData(state);
        systemUtils.setWiFiData(state);
    }

    private boolean isNetworkConnected() {
        final ConnectivityManager cm = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Test
    public void testUpdateSwipe() {
        Espresso.onView(withId(R.id.list_view)).perform(swipeDown());
    }

    @Test
    public void testListView() {
        if (!isNetworkConnected()) {
            return;
        }

        // Load artists
        SystemClock.sleep(1000 * 30);

        Espresso.onView(withText("Daft Punk")).perform(click());
        Espresso.pressBack();

        Espresso.onData(artistWithName("Imagine Dragons")).inAdapterView(withId(R.id.list_view)).perform(click());
        Espresso.pressBack();
    }

    @Test
    public void testSearch() {
        if (!isNetworkConnected()) {
            return;
        }

        // Load artists
        SystemClock.sleep(1000 * 30);

        Espresso.onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        Espresso.onView(withText("alternative")).perform(click());
        Espresso.onView(withId(R.id.action_search)).perform(click());
        Espresso.onView(isAssignableFrom(EditText.class)).perform(typeText("mu"), pressKey(KeyEvent.KEYCODE_ENTER));
        SystemClock.sleep(1000);
        Espresso.onView(withText("Muse")).perform(click());
        Espresso.pressBack();
        Espresso.pressBack();
    }

    @Test
    public void testWebsite() {
        if (!isNetworkConnected()) {
            return;
        }

        // Load artists
        SystemClock.sleep(1000 * 30);

        Espresso.onView(withText("Daft Punk")).perform(click());
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        Espresso.onView(withText(R.string.website)).perform(click());
        Espresso.pressBack();
        Espresso.pressBack();
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

    @After
    @Override
    public void tearDown() throws Exception {
        final Intent intent = new Intent(getActivity(), getActivity().getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mainActivity.startActivity(intent);

        systemAnimations.enableAll();

        super.tearDown();
    }
}
