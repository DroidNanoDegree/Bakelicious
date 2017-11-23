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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sriky.bakelicious.adaptor.RecipeInstructionsPagerAdaptor;
import com.sriky.bakelicious.databinding.FragmentRecipeInstructionsBinding;
import com.sriky.bakelicious.utils.BakeliciousUtils;

import timber.log.Timber;


/**
 * Recipe Instructions Fragment. Displays instructions as individual fragments using a
 * {@link android.support.v4.view.ViewPager}
 */

public class RecipeInstructionsFragment extends Fragment
        implements ViewPager.OnPageChangeListener {

    private static final String SELECTED_PAGE_NUMBER_BUNDLE_KEY = "selected_tab";

    private FragmentRecipeInstructionsBinding mFragmentRecipeInstructionsBinding;
    private RecipeInstructionsPagerAdaptor mRecipeInstructionsPagerAdaptor;

    public RecipeInstructionsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentRecipeInstructionsBinding = FragmentRecipeInstructionsBinding.inflate(inflater,
                container, false);

        Bundle args = getArguments();
        int recipeId = BakeliciousUtils.validateBundleAndGetRecipeId(args,
                RecipeInstructionsFragment.class.getSimpleName());

        mRecipeInstructionsPagerAdaptor =
                new RecipeInstructionsPagerAdaptor(getChildFragmentManager(),
                        args.getString(BakeliciousUtils.RECIPE_INSTRUCTIONS_BUNDLE_KEY));
        mFragmentRecipeInstructionsBinding.pagerRecipeInstructions.setAdapter(mRecipeInstructionsPagerAdaptor);

        mFragmentRecipeInstructionsBinding.pagerRecipeInstructions.addOnPageChangeListener(this);

        // restore the selected page upon a configuration change.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(SELECTED_PAGE_NUMBER_BUNDLE_KEY)) {
            int selectedPageNumber = savedInstanceState.getInt(SELECTED_PAGE_NUMBER_BUNDLE_KEY);
            Timber.d("onCreateView() selectedPageNumber: %d", selectedPageNumber);

            mFragmentRecipeInstructionsBinding.pagerRecipeInstructions.setCurrentItem(
                    selectedPageNumber, true);
        }

        return mFragmentRecipeInstructionsBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_PAGE_NUMBER_BUNDLE_KEY,
                mFragmentRecipeInstructionsBinding.pagerRecipeInstructions.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Timber.d("onPageSelected(), position: %d", position);
        mFragmentRecipeInstructionsBinding.pagerRecipeInstructions.setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
