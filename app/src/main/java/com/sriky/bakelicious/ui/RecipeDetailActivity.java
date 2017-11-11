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
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.sriky.bakelicious.R;
import com.sriky.bakelicious.adaptor.RecipeInstructionPagerAdaptor;
import com.sriky.bakelicious.databinding.ActivityRecipeDetailBinding;
import com.sriky.bakelicious.loader.RecipeDetailsCursorLoader;
import com.sriky.bakelicious.utils.BakeliciousUtils;

import timber.log.Timber;

/**
 * Recipe Detail Activity.
 */

public class RecipeDetailActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener {

    public static final String RECIPE_INFO_BUNDLE_KEY = "recipe_info";

    private ActivityRecipeDetailBinding mActivityRecipeDetailBinding;
    private int mRecipeId;
    private RecipeInstructionPagerAdaptor mRecipeInstructionPagerAdaptor;
    private RecipeDetailsCursorLoader mRecipeDetailsCursorLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityRecipeDetailBinding = DataBindingUtil.setContentView(RecipeDetailActivity.this,
                R.layout.activity_recipe_detail);

        setSupportActionBar(mActivityRecipeDetailBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if(intent == null) throw new RuntimeException("Intent empty!");
        if(!intent.hasExtra(RECIPE_INFO_BUNDLE_KEY)) {
            throw new RuntimeException("RecipeInfo bundle not set to intent!");
        }

        Bundle bundle = intent.getBundleExtra(RECIPE_INFO_BUNDLE_KEY);
        if(bundle == null) throw new RuntimeException("RecipeInfo bundle is null!");

        mRecipeId = bundle.getInt(BakeliciousUtils.RECIPE_ID_BUNDLE_KEY, 0);
        String recipeName = bundle.getString(BakeliciousUtils.RECIPE_NAME_BUNDLE_KEY);
        Timber.d("recipeId: %d, RecipeName: %s", mRecipeId, recipeName);

        /* set the action bar title to the recipeName */
        actionBar.setTitle(recipeName);

        /* initialize and setup the RecipeInstructionPagerAdaptor and set it to the ViewPager */
        mRecipeInstructionPagerAdaptor = new RecipeInstructionPagerAdaptor(getSupportFragmentManager());
        mActivityRecipeDetailBinding.pagerRecipeInstructions.setAdapter(mRecipeInstructionPagerAdaptor);
        mActivityRecipeDetailBinding.pagerRecipeInstructions.addOnPageChangeListener(this);

        /* init the cursor loader to query instruction data */
        mRecipeDetailsCursorLoader = new RecipeDetailsCursorLoader(RecipeDetailActivity.this,
                mRecipeInstructionPagerAdaptor,
                mRecipeId);

        /* init the loader */
        getLoaderManager().initLoader(BakeliciousUtils.RECIPE_INSTRUCTION_LOADER_ID,
                null,
                mRecipeDetailsCursorLoader);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                NavUtils.navigateUpFromSameTask(this);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Timber.d("onTabSelected: tab.position: % d", position);
        mActivityRecipeDetailBinding.pagerRecipeInstructions.setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
