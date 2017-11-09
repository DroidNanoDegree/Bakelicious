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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.sriky.bakelicious.R;
import com.sriky.bakelicious.utils.BakeliciousUtils;

/**
 * Recipe Detail Activity.
 */

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        //Timber.plant(new Timber.DebugTree());

        //TODO: If required, fix the actionbar so we can see the back button!
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(RecipeDetailFragment.RECIPE_ID_BUNDLE_KEY)) {
                /* get the recipeId from the intent and add the RecipeDetailFragment */
                BakeliciousUtils.addRecipeDetailFragment(getSupportFragmentManager(),
                        intent.getIntExtra(RecipeDetailFragment.RECIPE_ID_BUNDLE_KEY, 0),
                        R.id.fl_recipe_detail);
            } else {
                throw new RuntimeException("RecipeId not set to intent!");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
