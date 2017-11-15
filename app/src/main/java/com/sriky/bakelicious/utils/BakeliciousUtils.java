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

import com.sriky.bakelicious.model.Ingredient;
import com.sriky.bakelicious.model.Recipe;
import com.sriky.bakelicious.model.Step;
import com.sriky.bakelicious.provider.RecipeContract;

import java.util.Collection;

/**
 * Utility class to process and convert data to different types.
 */

public final class BakeliciousUtils {

    /* loader ids */
    public static final int MASTER_LIST_FRAGMENT_LOADER_ID = 1;
    public static final int RECIPE_INGREDIENTS_FRAGMENT_LOADER_ID = 2;
    public static final int RECIPE_INSTRUCTIONS_FRAGMENT_LOADER_ID = 3;

    /* recipe details number of tabs, currently set to 2, #1 for ingredients & #2 for instructions */
    public static final int RECIPE_DETAILS_TAB_COUNT = 2;

    /* bundle keys */
    public static final String RECIPE_ID_BUNDLE_KEY = "recipe_id";
    public static final String RECIPE_NAME_BUNDLE_KEY = "recipe_name";

    /* projection array and indexes to query RecipeID and RecipeName from the recipe table */
    public static final String[] PROJECTION_MASTER_LIST_FRAGMENT = {
            RecipeContract.COLUMN_RECIPE_ID,
            RecipeContract.COLUMN_RECIPE_NAME,
            RecipeContract.COLUMN_RECIPE_SERVES,
    };
    public static final int INDEX_PROJECTION_MASTER_LIST_FRAGMENT_RECIPE_ID = 0;
    public static final int INDEX_PROJECTION_MASTER_LIST_FRAGMENT_RECIPE_NAME = 1;
    public static final int INDEX_PROJECTION_MASTER_LIST_FRAGMENT_RECIPE_SERVINGS = 2;

    /* projection array and indexes to query RecipeID and RecipeName from the recipe table */
    public static final String[] PROJECTION_RECIPE_INGREDIENTS_FRAGMENT = {
            RecipeContract.COLUMN_RECIPE_INGREDIENTS
    };
    public static final int INDEX_PROJECTION_RECIPE_INGREDIENTS_FRAGMENT_INGREDIENTS = 0;

    /* projection array and indexes to query RecipeID and RecipeName from the recipe table */
    public static final String[] PROJECTION_RECIPE_INSTRUCTIONS_FRAGMENT = {
            RecipeContract.COLUMN_RECIPE_INSTRUCTIONS
    };
    public static final int INDEX_PROJECTION_RECIPE_INSTRUCTIONS_FRAGMENT_INSTRUCTIONS = 0;

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

    public static int validateBundleAndGetRecipeId(Bundle args, String className) {
        if (args == null)
            throw new RuntimeException(String.format("No arguments bundle set for %s", className));

        if (!args.containsKey(BakeliciousUtils.RECIPE_ID_BUNDLE_KEY)) {
            throw new RuntimeException(String.format("RecipeId not set in bundle for %s", className));
        }

        return args.getInt(BakeliciousUtils.RECIPE_ID_BUNDLE_KEY);
    }
}
