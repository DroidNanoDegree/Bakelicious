/*
 * Copyright (C) 2017 Srikanth Basappa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.sriky.bakelicious.ui;

import android.app.Activity;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.DisplayMetrics;
import android.view.Gravity;

import com.sriky.bakelicious.R;
import com.sriky.bakelicious.ui.BakeliciousActivity;
import com.sriky.bakelicious.ui.RecipeDetailActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assume.assumeTrue;

/**
 * Class to test intents used in Bakelicious.
 */

@RunWith(AndroidJUnit4.class)
public class BakeliciousIntentTests {

    private Activity mActivity;

    @Rule
    public IntentsTestRule<BakeliciousActivity> mActivityRule = new IntentsTestRule<>(
            BakeliciousActivity.class);
    private IdlingResource mIdlingResource;


    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
        mActivity = mActivityRule.getActivity();
        IdlingRegistry.getInstance().register(mIdlingResource);
        //Assumption this test should only run on phones and should be ignored on tables.
        assumeTrue(!isScreenSw600dp());
    }

    @After
    public void unregisterIdlingResources() {
        IdlingRegistry.getInstance().unregister(mIdlingResource);
    }

    @Test
    public void test_LaunchRecipeDetailsWithCorrectIntent() {
        //open the navigation drawer and toggle to discover recipes.
        onView(withId(R.id.material_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))); // Left Drawer should be closed.
        DrawerActions.openDrawer(R.id.material_drawer_layout);
        onView(withId(R.id.drawer_action_discover)).perform(click());

        onView(withId(R.id.rv_recipes)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        intended(hasExtraWithKey(RecipeDetailActivity.RECIPE_INFO_BUNDLE_KEY));
    }

    private boolean isScreenSw600dp() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float widthDp = displayMetrics.widthPixels / displayMetrics.density;
        float heightDp = displayMetrics.heightPixels / displayMetrics.density;
        float screenSw = Math.min(widthDp, heightDp);
        return screenSw >= 600;
    }
}
