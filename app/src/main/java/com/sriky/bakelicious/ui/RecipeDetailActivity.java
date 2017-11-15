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
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.sriky.bakelicious.R;
import com.sriky.bakelicious.databinding.ActivityRecipeDetailBinding;
import com.sriky.bakelicious.utils.BakeliciousUtils;

import timber.log.Timber;

/**
 * Recipe Detail Activity.
 */

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String RECIPE_INFO_BUNDLE_KEY = "recipe_info";

    private ActivityRecipeDetailBinding mActivityRecipeDetailBinding;

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
        int recipeId = BakeliciousUtils.validateBundleAndGetRecipeId(bundle, RecipeDetailActivity.class.getSimpleName());

        if (!bundle.containsKey(BakeliciousUtils.RECIPE_NAME_BUNDLE_KEY)) {
            throw new RuntimeException("Recipe Name not set to bundle!");
        }

        String recipeName = bundle.getString(BakeliciousUtils.RECIPE_NAME_BUNDLE_KEY);
        Timber.d("recipeId: %d, RecipeName: %s", recipeId, recipeName);

        // set the action bar title to the recipeName.
        actionBar.setTitle(recipeName);

        if(savedInstanceState == null) {
            RecipeDetailsFragment recipeDetailsFragment = new RecipeDetailsFragment();
            recipeDetailsFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_recipe_details, recipeDetailsFragment)
                    .commit();
        }
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
}
