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

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.sriky.bakelicious.R;
import com.sriky.bakelicious.databinding.ActivityBakeliciousBinding;
import com.sriky.bakelicious.event.Message;
import com.sriky.bakelicious.idling_resource.BakeliciousIdlingResource;
import com.sriky.bakelicious.provider.RecipeContract;
import com.sriky.bakelicious.sync.BakeliciousSyncUtils;
import com.sriky.bakelicious.utils.BakeliciousUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import timber.log.Timber;

public class BakeliciousActivity extends AppCompatActivity
        implements Drawer.OnDrawerItemClickListener {

    private static final String MASTER_LIST_FRAGMENT_TAG = "master_list_fragment";
    private static final String RECIPE_DETAILS_FRAGMENT_TAG = "recipe_details_fragment";
    private static final String ABOUT_FRAGMENT_TAG = "about_fragment";
    private static final String DRAWER_SELECTED_ITEM_POSITION_BUNDLE_KEY = "drawer_selected_item_position";
    private static final String LAYOUT_DIVIDER_VISIBILITY_BUNDLE_KEY = "divider_view_visibility";
    private static final String LAYOUT_RECIPE_DETAILS_VISIBILITY_BUNDLE_KEY = "recipe_details_layout_visibility";

    private Drawer mNavigationDrawer;
    private ActivityBakeliciousBinding mActivityBakeliciousBinding;
    private RecipeDetailsFragment mRecipeDetailsFragment;
    private boolean mIsTwoPane;
    private boolean mCanReplaceDetailsFragment;
    private int mSelectedRecipeId;
    private Bundle mSelectedBundleArgs;
    private int mPreviousSelectedRecipeId;
    private MasterListFragment mMasterListFragment;
    private LibsSupportFragment mAboutFragment;
    private int mRestoredSelectedItemPosition;
    // the idling resource used for UI testing.
    @Nullable
    private BakeliciousIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityBakeliciousBinding = DataBindingUtil.setContentView(BakeliciousActivity.this,
                R.layout.activity_bakelicious);

        mIsTwoPane = findViewById(R.id.fl_recipe_details) != null;

        if (savedInstanceState == null) {
            // add the MasterListFragment
            addMasterListFragment(null);
            Timber.plant(new Timber.DebugTree());
            mCanReplaceDetailsFragment = true;
            mRestoredSelectedItemPosition = 0;
        } else {
            restoreStates(savedInstanceState);
        }

        // add the navigation drawer
        addNavigationDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // initiate data fetch
        BakeliciousSyncUtils.initDataSync(BakeliciousActivity.this, getIdlingResource());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // register to get events only when in TwoPane.
        EventBus.getDefault().register(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(DRAWER_SELECTED_ITEM_POSITION_BUNDLE_KEY,
                mNavigationDrawer.getCurrentSelectedPosition());

        if (mIsTwoPane) {
            outState.putInt(LAYOUT_DIVIDER_VISIBILITY_BUNDLE_KEY,
                    mActivityBakeliciousBinding.divider.getVisibility());

            outState.putInt(LAYOUT_RECIPE_DETAILS_VISIBILITY_BUNDLE_KEY,
                    mActivityBakeliciousBinding.flRecipeDetails.getVisibility());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        switch (view.getId()) {
            case R.id.drawer_action_discover: {
                Timber.d("action_discover");
                //set the actionbar title
                mActivityBakeliciousBinding.toolbarTitle.setText(getString(R.string.action_discover));

                if (mIsTwoPane) {
                    //remove the old details fragment.
                    removeRecipeDetailsFragment();
                    //remove the details fragment.
                    removeAboutFragment();
                    //show the views.
                    mActivityBakeliciousBinding.flRecipeDetails.setVisibility(View.VISIBLE);
                    mActivityBakeliciousBinding.divider.setVisibility(View.VISIBLE);
                }

                // add the MasterFragment with all recipes
                addMasterListFragment(null);
                break;
            }

            case R.id.drawer_action_favorite: {
                Timber.d("action_favorite");

                //remove the old details fragment.
                if (mIsTwoPane) {
                    removeRecipeDetailsFragment();
                    removeAboutFragment();
                    mActivityBakeliciousBinding.flRecipeDetails.setVisibility(View.VISIBLE);
                    mActivityBakeliciousBinding.divider.setVisibility(View.VISIBLE);
                }

                Bundle bundle = new Bundle();
                /* set the selection query */
                bundle.putString(MasterListFragment.SELECTION_BUNDLE_KEY,
                        RecipeContract.COLUMN_RECIPE_FAVORITE + " =? ");

                /* set the selectionArgs for the query */
                bundle.putStringArray(MasterListFragment.SELECTION_ARGS_BUNDLE_KEY,
                        new String[]{"1"});

                mActivityBakeliciousBinding.toolbarTitle.setText(getString(R.string.action_favorite));
                /* add the favorites recipes fragment */
                addMasterListFragment(bundle);
                break;
            }

            case R.id.drawer_action_about: {
                Timber.d("action_about");

                //remove masterlist.
                removeMasterListFragment();

                mActivityBakeliciousBinding.toolbarTitle.setText(getString(R.string.action_about));

                //add the about fragment.
                mAboutFragment = new LibsBuilder()
                        .withAboutAppName(getString(R.string.app_name))
                        .withAboutDescription(getString(R.string.about_the_app))
                        .withAboutIconShown(true)
                        .withAboutVersionShown(true)
                        .withAboutVersionShownCode(true)
                        .withAutoDetect(true)
                        .supportFragment();

                if (mIsTwoPane) {
                    //remove detail fragments.
                    removeRecipeDetailsFragment();
                    //add the about fragment.
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fl_main, mAboutFragment, ABOUT_FRAGMENT_TAG)
                            .commit();
                    //hide views.
                    mActivityBakeliciousBinding.flRecipeDetails.setVisibility(View.GONE);
                    mActivityBakeliciousBinding.divider.setVisibility(View.GONE);
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fl_master_list, mAboutFragment, ABOUT_FRAGMENT_TAG)
                            .commit();
                }
                break;
            }

            default: {
                Timber.e("Unsupported action: %s", ((Nameable) drawerItem).getName());
                return true;
            }
        }
        return false;
    }

    /**
     * Event receiver to listen to event when data is loaded into MasterListFragment.
     *
     * @param event {@link Message.EventRecipeDataLoaded}
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecipeDataLoaded(Message.EventRecipeDataLoaded event) {
        //set the idle state to true so UI testing can resume.
        getIdlingResource().setIdleState(true);

        Bundle bundle = event.getBundle();
        mSelectedRecipeId = bundle.getInt(BakeliciousUtils.RECIPE_ID_BUNDLE_KEY);
        mSelectedBundleArgs = bundle;

        Timber.d("onRecipeDataLoaded() selected recipeid: %d", mSelectedRecipeId);

        if (mIsTwoPane && (mRecipeDetailsFragment == null || mCanReplaceDetailsFragment)) {
            mActivityBakeliciousBinding.flRecipeDetails.setVisibility(View.VISIBLE);
            mActivityBakeliciousBinding.divider.setVisibility(View.VISIBLE);
            updateRecipeDetailsFragment();
        }
    }

    /**
     * Event receiver to process {@link View.OnClickListener} when recipe items are clicked.
     *
     * @param event {@link Message.EventRecipeItemClicked}
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecipeItemClicked(Message.EventRecipeItemClicked event) {
        Bundle bundle = event.getBundle();
        Timber.d("onRecipeItemClicked() recipeId: %d", bundle.getInt(BakeliciousUtils.RECIPE_ID_BUNDLE_KEY));

        if (mIsTwoPane) {
            mSelectedRecipeId = bundle.getInt(BakeliciousUtils.RECIPE_ID_BUNDLE_KEY, 0);
            mSelectedBundleArgs = bundle;
            updateRecipeDetailsFragment();
        } else {
            Intent intent = new Intent(BakeliciousActivity.this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.RECIPE_INFO_BUNDLE_KEY, bundle);
            startActivity(intent);
        }
    }

    /**
     * Event receiver to process recipes removed from the favorites.
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecipesListUpdated(Message.EventRecipesAdaptorEmpty event) {
        Timber.d("onRecipesListUpdated()");

        //remove the details fragment because it should be displaying details of the recipe item
        // that was removed from favorites.
        if (mIsTwoPane) {
            removeRecipeDetailsFragment();
            mActivityBakeliciousBinding.flRecipeDetails.setVisibility(View.GONE);
            mActivityBakeliciousBinding.divider.setVisibility(View.GONE);
        }
    }

    /**
     * Create or returns an instance of idling resource to test {@link MasterListFragment}
     *
     * @return {@link BakeliciousIdlingResource} instance.
     */
    @VisibleForTesting
    @NonNull
    public BakeliciousIdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new BakeliciousIdlingResource();
        }
        return mIdlingResource;
    }

    @VisibleForTesting
    public boolean isTwoPane() {
        return mIsTwoPane;
    }

    private void addNavigationDrawer() {
        // add toolbar
        setSupportActionBar(mActivityBakeliciousBinding.toolbar);

        //disable the tile.
        getSupportActionBar().setTitle("");

        //set the actionbar title
        mActivityBakeliciousBinding.toolbarTitle.setText(getString(R.string.action_discover));

        // Create the drawer
        mNavigationDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mActivityBakeliciousBinding.toolbar)
                .withSelectedItemByPosition(mRestoredSelectedItemPosition)
                .inflateMenu(R.menu.navigation_drawer_menu)
                .withOnDrawerItemClickListener(BakeliciousActivity.this)
                .build();
    }

    private void addMasterListFragment(Bundle args) {
        mMasterListFragment = new MasterListFragment();
        // set the arguments for the MasterListFragment
        mMasterListFragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fl_master_list, mMasterListFragment, MASTER_LIST_FRAGMENT_TAG)
                .commit();
    }

    private void updateRecipeDetailsFragment() {
        Timber.d("updateRecipeDetailsFragment(), mSelectedRecipeId: %d", mSelectedRecipeId);

        if (mSelectedRecipeId != mPreviousSelectedRecipeId) {

            mPreviousSelectedRecipeId = mSelectedRecipeId;

            //add the new fragment.
            mRecipeDetailsFragment = new RecipeDetailsFragment();
            mRecipeDetailsFragment.setArguments(mSelectedBundleArgs);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_recipe_details, mRecipeDetailsFragment, RECIPE_DETAILS_FRAGMENT_TAG)
                    .commit();
        }
    }

    /**
     * Restores the states of the components after a configuration change.
     */
    private void restoreStates(Bundle savedInstanceState) {
        //set the value for the fields after a configuration change, this way fragments can
        //fragment transactions can be done properly.
        FragmentManager fm = getSupportFragmentManager();
        mMasterListFragment =
                (MasterListFragment) fm.findFragmentByTag(MASTER_LIST_FRAGMENT_TAG);

        mRecipeDetailsFragment =
                (RecipeDetailsFragment) fm.findFragmentByTag(RECIPE_DETAILS_FRAGMENT_TAG);

        mAboutFragment = (LibsSupportFragment) fm.findFragmentByTag(ABOUT_FRAGMENT_TAG);

        //get the previous selected item position of the drawer.
        if (savedInstanceState.containsKey(DRAWER_SELECTED_ITEM_POSITION_BUNDLE_KEY)) {
            mRestoredSelectedItemPosition =
                    savedInstanceState.getInt(DRAWER_SELECTED_ITEM_POSITION_BUNDLE_KEY);
        }

        if (mIsTwoPane) {
            if (savedInstanceState.containsKey(LAYOUT_RECIPE_DETAILS_VISIBILITY_BUNDLE_KEY)) {
                mActivityBakeliciousBinding.flRecipeDetails.setVisibility(
                        savedInstanceState.getInt(LAYOUT_RECIPE_DETAILS_VISIBILITY_BUNDLE_KEY));
            }

            if (savedInstanceState.containsKey(LAYOUT_DIVIDER_VISIBILITY_BUNDLE_KEY)) {
                mActivityBakeliciousBinding.divider.setVisibility(
                        savedInstanceState.getInt(LAYOUT_DIVIDER_VISIBILITY_BUNDLE_KEY));
            }
        }
    }

    /**
     * Removes the {@link RecipeDetailsFragment}
     */
    private void removeRecipeDetailsFragment() {
        if (mRecipeDetailsFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(mRecipeDetailsFragment)
                    .commit();
            mPreviousSelectedRecipeId = -1;
            mRecipeDetailsFragment = null;
        }
    }

    /**
     * Removes the {@link MasterListFragment}.
     */
    private void removeMasterListFragment() {
        if (mMasterListFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(mMasterListFragment)
                    .commit();
            mMasterListFragment = null;
        }
    }

    /**
     * Removes the About fragment.
     */
    private void removeAboutFragment() {
        if (mAboutFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(mAboutFragment)
                    .commit();
            mAboutFragment = null;
        }
    }
}
