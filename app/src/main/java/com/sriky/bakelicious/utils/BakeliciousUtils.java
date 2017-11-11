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

package com.sriky.bakelicious.utils;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.sriky.bakelicious.model.Ingredient;
import com.sriky.bakelicious.model.Recipe;
import com.sriky.bakelicious.model.Step;
import com.sriky.bakelicious.provider.RecipeContract;
import com.sriky.bakelicious.ui.RecipeInstructionFragment;

import java.util.Collection;

import timber.log.Timber;

/**
 * Utility class to process and convert data to different types.
 */

public final class BakeliciousUtils {

    /* loader ids */
    public static final int MASTER_LIST_FRAGMENT_LOADER_ID = 1;
    public static final int RECIPE_INSTRUCTION_LOADER_ID = 2;

    /* projection array and indexes to query RecipeID and RecipeName from the recipe table */
    public static final String[] PROJECTION_MASTER_LIST_FRAGMENT = {
            RecipeContract.COLUMN_RECIPE_ID,
            RecipeContract.COLUMN_RECIPE_NAME,
            RecipeContract.COLUMN_RECIPE_SERVES,
    };
    public static final int INDEX_PROJECTION_MASTER_LIST_FRAGMENT_RECIPE_ID = 0;
    public static final int INDEX_PROJECTION_MASTER_LIST_FRAGMENT_RECIPE_NAME = 1;
    public static final int INDEX_PROJECTION_MASTER_LIST_FRAGMENT_RECIPE_SERVINGS = 2;

    /* projection array and indexes to query recipe details */
    public static final String[] PROJECTION_RECIPE_DETAILS = {
            RecipeContract.COLUMN_RECIPE_INGREDIENTS,
            RecipeContract.COLUMN_RECIPE_INSTRUCTIONS
    };
    public static final int INDEX_PROJECTION_RECIPE_DETAILS_RECIPE_INGREDIENTS = 0;
    public static final int INDEX_PROJECTION_RECIPE_DETAILS_RECIPE_INSTRUCTION = 1;

    /* bundle keys */
    public static final String RECIPE_ID_BUNDLE_KEY = "recipe_id";
    public static final String RECIPE_NAME_BUNDLE_KEY = "recipe_name";

    /**
     * Generates content values from the list of objects. Supported list types are {@link Recipe},
     * {@link Ingredient} & {@link Step}
     *
     * @param c        Collection of either {@link Recipe}, {@link Ingredient} or {@link Step}
     * @param recipeId The recipeID.
     * @return {@link ContentValues} for either {@link Recipe}, {@link Ingredient} or {@link Step}
     */
    @Nullable
    public static ContentValues[] getContentValues(Collection<?> c, int recipeId) {
        // bail out early if the collection is null or empty.
        if (c == null || c.size() == 0) return null;

        ContentValues[] retContentValuesArray = new ContentValues[c.size()];
        int idx = 0;

        for (Object o : c) {
            if (o instanceof Recipe) {
                Recipe recipe = (Recipe) o;
                ContentValues cv = (recipe != null) ? recipe.getContentValues() : null;
                if (cv != null) {
                    retContentValuesArray[idx++] = cv;
                }
            } else {
                throw new RuntimeException("UnSupported type:" + o.getClass());
            }
        }
        return retContentValuesArray;
    }

    /*
    public static void addRecipeInstructionFragment(FragmentManager fragmentManager,
                                                    int recipeId, int resourceId) {
        Timber.d("recipeId: %d", recipeId);

        // set the recipeId to a bundle and set it to the RecipeDetailFragment
        Bundle bundle = new Bundle();
        bundle.putInt(BakeliciousUtils.RECIPE_ID_BUNDLE_KEY, recipeId);
        RecipeInstructionFragment recipeInstructionFragment = new RecipeInstructionFragment();
        recipeInstructionFragment.setArguments(bundle);

        // add the RecipeInstructionFragment
        fragmentManager.beginTransaction()
                .add(resourceId, recipeInstructionFragment)
                .commit();
    }
    */
}
