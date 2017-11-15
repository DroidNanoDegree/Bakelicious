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

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sriky.bakelicious.adaptor.RecipeInstructionsPagerAdaptor;
import com.sriky.bakelicious.databinding.FragmentRecipeInstructionsBinding;
import com.sriky.bakelicious.model.Step;
import com.sriky.bakelicious.provider.BakeliciousContentProvider;
import com.sriky.bakelicious.provider.RecipeContract;
import com.sriky.bakelicious.utils.BakeliciousUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


/**
 * Recipe Instructions Fragment. Displays instructions as individual fragments using a
 * {@link android.support.v4.view.ViewPager}
 */

public class RecipeInstructionsFragment extends Fragment
        implements ViewPager.OnPageChangeListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String SELECTED_PAGE_NUMBER_BUNDLE_KEY = "selected_tab";

    private FragmentRecipeInstructionsBinding mFragmentRecipeInstructionsBinding;
    private RecipeInstructionsPagerAdaptor mRecipeInstructionsPagerAdaptor;
    private int mRecipeId;
    private int mSelectedPageNumber = -1;

    public RecipeInstructionsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentRecipeInstructionsBinding = FragmentRecipeInstructionsBinding.inflate(inflater,
                container, false);

        mRecipeId = BakeliciousUtils.validateBundleAndGetRecipeId(getArguments(),
                RecipeInstructionsFragment.class.getSimpleName());

        mRecipeInstructionsPagerAdaptor = new RecipeInstructionsPagerAdaptor(getChildFragmentManager());
        mFragmentRecipeInstructionsBinding.pagerRecipeInstructions.setAdapter(mRecipeInstructionsPagerAdaptor);

        mFragmentRecipeInstructionsBinding.pagerRecipeInstructions.addOnPageChangeListener(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_PAGE_NUMBER_BUNDLE_KEY)) {
            mSelectedPageNumber = savedInstanceState.getInt(SELECTED_PAGE_NUMBER_BUNDLE_KEY);
            Timber.d("onCreateView() mSelectedPageNumber: %d", mSelectedPageNumber);
        }

        getLoaderManager().initLoader(BakeliciousUtils.RECIPE_INSTRUCTIONS_FRAGMENT_LOADER_ID,
                null, RecipeInstructionsFragment.this);

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case BakeliciousUtils.RECIPE_INSTRUCTIONS_FRAGMENT_LOADER_ID: {
                return new CursorLoader(getContext(),
                        BakeliciousContentProvider.RecipeEntry.CONTENT_URI,
                        BakeliciousUtils.PROJECTION_RECIPE_INSTRUCTIONS_FRAGMENT,
                        RecipeContract.COLUMN_RECIPE_ID + " =? ",
                        new String[]{Integer.toString(mRecipeId)},
                        null);
            }

            default: {
                throw new RuntimeException("Unsupported loader id: " + id);
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        Timber.d("onLoadFinished() loaderid: %d, cursorSize: %d", id, data.getCount());

        switch (id) {
            case BakeliciousUtils.RECIPE_INSTRUCTIONS_FRAGMENT_LOADER_ID: {

                if (!data.moveToFirst()) throw new RuntimeException("Invalid cursor returned!");

                /* load instructions */
                Gson gson = new Gson();

                String instructions = data.getString(
                        BakeliciousUtils.INDEX_PROJECTION_RECIPE_INSTRUCTIONS_FRAGMENT_INSTRUCTIONS);

                Type listType = new TypeToken<ArrayList<Step>>() {
                }.getType();
                List<Step> instructionList = gson.fromJson(instructions, listType);
                Timber.d("%d instructions loaded for recipeId: %d", instructionList.size(), mRecipeId);

                mRecipeInstructionsPagerAdaptor.updateInstructions(instructionList);

                if (mSelectedPageNumber != -1) {
                    mFragmentRecipeInstructionsBinding.pagerRecipeInstructions.setCurrentItem(
                            mSelectedPageNumber, true);
                }
                break;
            }

            default: {
                throw new RuntimeException("Unsupported impl. for loader id: " + id);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
