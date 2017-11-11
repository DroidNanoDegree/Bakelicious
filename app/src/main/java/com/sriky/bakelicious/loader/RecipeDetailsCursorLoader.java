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

package com.sriky.bakelicious.loader;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sriky.bakelicious.adaptor.RecipeInstructionPagerAdaptor;
import com.sriky.bakelicious.model.Ingredient;
import com.sriky.bakelicious.model.Step;
import com.sriky.bakelicious.provider.BakeliciousContentProvider;
import com.sriky.bakelicious.provider.RecipeContract;
import com.sriky.bakelicious.utils.BakeliciousUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Loader class responsible for query recipe table for instructions and ingredients setting-up the
 * {@link RecipeDetailsCursorLoader} with instructions.
 */

public class RecipeDetailsCursorLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext;
    private RecipeInstructionPagerAdaptor mRecipeInstructionPagerAdaptor;
    private int mRecipeId;
    private List<Ingredient> mIngredients;

    public RecipeDetailsCursorLoader(Context context,
                                     RecipeInstructionPagerAdaptor adaptor, int recipeId){
        mContext = context;
        mRecipeInstructionPagerAdaptor = adaptor;
        mRecipeId = recipeId;
    }

    public List<Ingredient> getIngredients() { return mIngredients; }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case BakeliciousUtils.RECIPE_INSTRUCTION_LOADER_ID:{
                return new CursorLoader(mContext,
                        BakeliciousContentProvider.RecipeEntry.CONTENT_URI,
                        BakeliciousUtils.PROJECTION_RECIPE_DETAILS,
                        RecipeContract.COLUMN_RECIPE_ID + " =? ",
                        new String[]{Integer.toString(mRecipeId)},
                        null);
            }

            default:{
                throw new RuntimeException("Unsupported LoaderId: " + i);
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()){
            case BakeliciousUtils.RECIPE_INSTRUCTION_LOADER_ID:{
                if(cursor == null || cursor.getCount() == 0) {
                    Timber.e("Unable to get cursor with recipe details for recipeId: %d", mRecipeId);
                    return;
                }

                /* get data from the cursor */
                cursor.moveToNext();
                Gson gson = new Gson();

                /* load instructions */
                String instructions = cursor.getString(
                        BakeliciousUtils.INDEX_PROJECTION_RECIPE_DETAILS_RECIPE_INSTRUCTION);
                Type listType = new TypeToken<ArrayList<Step>>(){}.getType();
                List<Step> instructionList = gson.fromJson(instructions, listType);
                Timber.d("%d instructions loaded for recipeId: %d", instructionList.size(), mRecipeId);
                mRecipeInstructionPagerAdaptor.updateInstructions(instructionList);

                /* load ingredients */
                String ingredients = cursor.getString(
                        BakeliciousUtils.INDEX_PROJECTION_RECIPE_DETAILS_RECIPE_INGREDIENTS);
                listType = new TypeToken<ArrayList<Ingredient>>(){}.getType();
                List<Ingredient> ingredientList = gson.fromJson(ingredients, listType);
                Timber.d("%d ingredients loaded for recipeId: %d", ingredientList.size(), mRecipeId);
                mIngredients = ingredientList;
                //TODO: send an event to notify ingredients are loaded!
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
