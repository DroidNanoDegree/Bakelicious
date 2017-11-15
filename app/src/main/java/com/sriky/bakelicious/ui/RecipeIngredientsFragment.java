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
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sriky.bakelicious.adaptor.IngredientsAdaptor;
import com.sriky.bakelicious.databinding.FragmentRecipeIngredientsBinding;
import com.sriky.bakelicious.model.Ingredient;
import com.sriky.bakelicious.provider.BakeliciousContentProvider;
import com.sriky.bakelicious.provider.RecipeContract;
import com.sriky.bakelicious.utils.BakeliciousUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Recipe Ingredient fragment.
 */

public class RecipeIngredientsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private FragmentRecipeIngredientsBinding mFragmentRecipeIngredientsBinding;
    private IngredientsAdaptor mIngredientsAdaptor;
    private int mRecipeId;

    public RecipeIngredientsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentRecipeIngredientsBinding = FragmentRecipeIngredientsBinding.inflate(inflater,
                container, false);

        mRecipeId = BakeliciousUtils.validateBundleAndGetRecipeId(getArguments(),
                RecipeIngredientsFragment.class.getSimpleName());

        mIngredientsAdaptor = new IngredientsAdaptor(null);
        mFragmentRecipeIngredientsBinding.rvIngredients.setAdapter(mIngredientsAdaptor);

        mFragmentRecipeIngredientsBinding.rvIngredients.setHasFixedSize(true);

        mFragmentRecipeIngredientsBinding.rvIngredients.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        getLoaderManager().initLoader(BakeliciousUtils.RECIPE_INGREDIENTS_FRAGMENT_LOADER_ID,
                null, RecipeIngredientsFragment.this);

        return mFragmentRecipeIngredientsBinding.getRoot();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case BakeliciousUtils.RECIPE_INGREDIENTS_FRAGMENT_LOADER_ID: {
                return new CursorLoader(getContext(),
                        BakeliciousContentProvider.RecipeEntry.CONTENT_URI,
                        BakeliciousUtils.PROJECTION_RECIPE_INGREDIENTS_FRAGMENT,
                        RecipeContract.COLUMN_RECIPE_ID + " =? ",
                        new String[]{Integer.toString(mRecipeId)},
                        null);

            }

            default: {
                throw new RuntimeException(id + " loader not supported yet!");
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        Timber.d("onLoadFinished() loaderid: %d, cursorSize: %d", loaderId, data.getCount());

        switch (loaderId) {
            case BakeliciousUtils.RECIPE_INGREDIENTS_FRAGMENT_LOADER_ID: {

                if (!data.moveToFirst()) throw new RuntimeException("Invalid cursor returned!");

                /* load ingredients */
                Gson gson = new Gson();

                String ingredients = data.getString(
                        BakeliciousUtils.INDEX_PROJECTION_RECIPE_INGREDIENTS_FRAGMENT_INGREDIENTS);

                Type listType = new TypeToken<ArrayList<Ingredient>>() {
                }.getType();
                List<Ingredient> ingredientList = gson.fromJson(ingredients, listType);
                Timber.d("%d ingredients loaded for recipeId: %d", ingredientList.size(), mRecipeId);

                mIngredientsAdaptor.updateIngredients(ingredientList);
                break;
            }

            default: {
                throw new RuntimeException(loaderId + " impl. not supported yet!");
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
