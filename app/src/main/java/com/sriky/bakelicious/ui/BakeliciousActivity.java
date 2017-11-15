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
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.sriky.bakelicious.R;
import com.sriky.bakelicious.databinding.ActivityBakeliciousBinding;
import com.sriky.bakelicious.event.Message;
import com.sriky.bakelicious.provider.RecipeContract;
import com.sriky.bakelicious.sync.BakeliciousSyncUtils;
import com.sriky.bakelicious.utils.BakeliciousUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import timber.log.Timber;

public class BakeliciousActivity extends AppCompatActivity
        implements Drawer.OnDrawerItemClickListener {

    private static final String SELECTED_TAB_INDEX_BUNDLE_KEY = "selected_tab";
    private Drawer mNavigationDrawer;
    private ActivityBakeliciousBinding mActivityBakeliciousBinding;
    private RecipeDetailsFragment mRecipeDetailsFragment;
    private boolean mIsTwoPane;
    private boolean mCanReplaceDetailsFragement;
    private int mSelectedRecipeId;
    private int mPreviousSelectedRecipeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initiate data fetch
        BakeliciousSyncUtils.initDataSync(BakeliciousActivity.this);

        mActivityBakeliciousBinding = DataBindingUtil.setContentView(BakeliciousActivity.this,
                R.layout.activity_bakelicious);

        if (savedInstanceState == null) {
            // add the MasterListFragment
            addMasterListFragment(null);
            Timber.plant(new Timber.DebugTree());
            mCanReplaceDetailsFragement = true;
        }

        mIsTwoPane = findViewById(R.id.fl_recipe_details) != null;

        // add the navigation drawer
        addNavigationDrawer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // register to get events only when in TwoPane.
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        switch (view.getId()) {
            case R.id.action_discover: {
                Timber.d("action_discover");

                // add the MasterFragment with all recipes
                addMasterListFragment(null);
                break;
            }

            case R.id.action_favorite: {
                Timber.d("action_favorite");
                Bundle bundle = new Bundle();
                /* set the selection query */
                bundle.putString(MasterListFragment.SELECTION_BUNDLE_KEY,
                        RecipeContract.COLUMN_RECIPE_FAVORITE + " =? ");

                /* set the selectionArgs for the query */
                bundle.putStringArray(MasterListFragment.SELECTION_ARGS_BUNDLE_KEY,
                        new String[]{"1"});

                /* add the favorites recipes fragment */
                addMasterListFragment(bundle);
                break;
            }

            case R.id.action_about: {
                Timber.d("action_about");
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
        mSelectedRecipeId = event.getRecipeId();
        Timber.d("onRecipeDataLoaded() recipeId: %d", mSelectedRecipeId);

        if (mIsTwoPane && mCanReplaceDetailsFragement) {
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
            updateRecipeDetailsFragment();
        } else {
            Intent intent = new Intent(BakeliciousActivity.this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.RECIPE_INFO_BUNDLE_KEY, bundle);
            startActivity(intent);
        }
    }

    private void addNavigationDrawer() {
        // add toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.drawer_item_menu_drawer);

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.ic_launcher_background)
                .build();

        // Create the drawer
        mNavigationDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .inflateMenu(R.menu.navigation_drawer_menu)
                .withOnDrawerItemClickListener(BakeliciousActivity.this)
                .build();
    }

    private void addMasterListFragment(Bundle args) {
        MasterListFragment masterListFragment = new MasterListFragment();
        // set the arguments for the MasterListFragment
        masterListFragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fl_master_list, masterListFragment)
                .commit();
    }

    private void updateRecipeDetailsFragment() {
        Timber.d("updateRecipeDetailsFragment(), mSelectedRecipeId: %d", mSelectedRecipeId);

        if(mSelectedRecipeId != mPreviousSelectedRecipeId) {

            mPreviousSelectedRecipeId = mSelectedRecipeId;

            //add the new fragment.
            Bundle bundle = new Bundle();
            bundle.putInt(BakeliciousUtils.RECIPE_ID_BUNDLE_KEY, mSelectedRecipeId);

            mRecipeDetailsFragment = new RecipeDetailsFragment();
            mRecipeDetailsFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_recipe_details, mRecipeDetailsFragment)
                    .commit();
        }
    }
}
