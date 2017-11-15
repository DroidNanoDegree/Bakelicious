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

package com.sriky.bakelicious.adaptor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sriky.bakelicious.ui.RecipeIngredientsFragment;
import com.sriky.bakelicious.ui.RecipeInstructionsFragment;
import com.sriky.bakelicious.utils.BakeliciousUtils;

import timber.log.Timber;

/**
 * The {@link android.support.v4.view.ViewPager} Adaptor for recipe details layout.
 */

public class RecipeDetailPagerAdaptor extends FragmentPagerAdapter {

    private int mRecipeId;
    private int mNumPages;

    public RecipeDetailPagerAdaptor(FragmentManager fm, int numPages, int recipeId) {
        super(fm);
        Timber.d("RecipeDetailPagerAdaptor(), numPages: %d, recipeId: %d", numPages, recipeId);
        mRecipeId = recipeId;
        mNumPages = numPages;
    }

    @Override
    public Fragment getItem(int position) {
        Timber.d("getItem(), position: %d", position);
        Bundle bundle = new Bundle();
        bundle.putInt(BakeliciousUtils.RECIPE_ID_BUNDLE_KEY, mRecipeId);

        switch (position) {
            case 0: {
                RecipeIngredientsFragment recipeIngredientsFragment = new RecipeIngredientsFragment();
                recipeIngredientsFragment.setArguments(bundle);
                return recipeIngredientsFragment;
            }

            case 1: {
                RecipeInstructionsFragment recipeInstructionsFragment = new RecipeInstructionsFragment();
                recipeInstructionsFragment.setArguments(bundle);
                return recipeInstructionsFragment;
            }

            default:
                throw new RuntimeException(position + " unsupported!");
        }
    }

    @Override
    public int getCount() {
        return mNumPages;
    }
}
