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

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import com.sriky.bakelicious.R;
import com.sriky.bakelicious.helper.RecyclerViewMatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;

/**
 * Class to test Bakelicious UI
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BakeliciousInstrumentedTests {

    private static final String FIRST_ITEM_IN_RECIPES_RECYCLER_VIEW = "Nutella Pie";
    @Rule
    public ActivityTestRule<BakeliciousActivity> mBakeliciousActivityTestRule =
            new ActivityTestRule<>(BakeliciousActivity.class);
    private boolean mIsTwoPane;
    private IdlingResource mIdlingResource;

    // Convenience helper
    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mBakeliciousActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
        mIsTwoPane = mBakeliciousActivityTestRule.getActivity().isTwoPane();
    }

    @After
    public void unregisterIdlingResources() {
        IdlingRegistry.getInstance().unregister(mIdlingResource);
    }

    /**
     * Test to verify if the {@link android.support.v7.widget.RecyclerView} data is loading correctly.
     */
    @Test
    public void testA_recipeDataLoading() {
        onView(withRecyclerView(R.id.rv_recipes).atPosition(0))
                .check(matches(hasDescendant(withText(FIRST_ITEM_IN_RECIPES_RECYCLER_VIEW))));
    }

    /**
     * If the test above {@link BakeliciousInstrumentedTests#testA_recipeDataLoading()} passed, then we
     * can perform a click event on the first item(i.e "Nutella Pie") and verify it opens up the
     * recipes details. On the phones, it should launch {@link RecipeDetailActivity} which should
     * instantiate {@link RecipeDetailsFragment} whereas on tables, i.e. in TwoPane mode,
     * it should instantiate {@link RecipeDetailsFragment} as well.
     */
    @Test
    public void testB_recipesRecyclerViewClick() {
        onView(withId(R.id.rv_recipes)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.vp_recipe_details)).check(matches(withTagValue(is((Object) FIRST_ITEM_IN_RECIPES_RECYCLER_VIEW))));
    }

    /**
     * Test adding the first item in the Recipes List to favorites.
     */
    @Test
    public void testC_AddRecipeToFavorites() {
        //open the navigation drawer and toggle to discover recipes.
        onView(withId(R.id.material_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))); // Left Drawer should be closed.
        DrawerActions.openDrawer(R.id.material_drawer_layout);
        onView(withId(R.id.drawer_action_discover)).perform(click());

        //select the first item to open the recipe detail for the same.
        onView(withId(R.id.rv_recipes)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        //add it to favorites list.
        onView(withId(R.id.action_favorite)).perform(click());

        //navigate out of RecipeDetailsActivity.
        if (!mIsTwoPane) {
            onView(withContentDescription("Navigate up")).perform(click());
        }

        //open the navigation drawer and toggle to favorites.
        onView(withId(R.id.material_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))); // Left Drawer should be closed.
        DrawerActions.openDrawer(R.id.material_drawer_layout);
        onView(withId(R.id.drawer_action_favorite)).perform(click());

        //check if the item is in the favorites list.
        onView(withRecyclerView(R.id.rv_recipes).atPosition(0))
                .check(matches(hasDescendant(withText(FIRST_ITEM_IN_RECIPES_RECYCLER_VIEW))));
    }

    /**
     * Test removing favorited recipe from the Recipes List. This test relies on the previous test
     * {@link BakeliciousInstrumentedTests#testC_AddRecipeToFavorites()} to be successful in adding
     * the first item to favorite lists.
     */
    @Test
    public void testD_RemoveRecipeFromFavorites() {
        //open the navigation drawer and toggle to favorites.
        // Open Drawer to click on navigation.
        onView(withId(R.id.material_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))); // Left Drawer should be closed.
        DrawerActions.openDrawer(R.id.material_drawer_layout);
        onView(withId(R.id.drawer_action_favorite)).perform(click());

        //check if the item is in the favorites list.
        onView(withRecyclerView(R.id.rv_recipes).atPosition(0))
                .check(matches(hasDescendant(withText(FIRST_ITEM_IN_RECIPES_RECYCLER_VIEW))));

        //select the first item to open the recipe detail for the same.
        onView(withId(R.id.rv_recipes)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        //remove it to favorites list.
        onView(withId(R.id.action_favorite)).perform(click());

        //navigate out of RecipeDetailsActivity.
        if (!mIsTwoPane) {
            onView(withContentDescription("Navigate up")).perform(click());
        }

        // Open Drawer to click on navigation.
        onView(withId(R.id.material_drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))); // Left Drawer should be closed.
        DrawerActions.openDrawer(R.id.material_drawer_layout);
        onView(withId(R.id.drawer_action_favorite)).perform(click());

        onView(withId(R.id.tv_error_msg)).check(matches(withText("No recipes added to favorites yet!")));
    }
}


