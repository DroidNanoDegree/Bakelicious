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

package com.sriky.bakelicious.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.sriky.bakelicious.model.Ingredient;
import com.sriky.bakelicious.model.Recipe;
import com.sriky.bakelicious.model.Step;
import com.sriky.bakelicious.provider.BakeliciousContentProvider;
import com.sriky.bakelicious.provider.BakeliciousContentProvider.RecipeEntry;
import com.sriky.bakelicious.provider.RecipeContract;
import com.sriky.bakelicious.utils.BakeliciousUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import timber.log.Timber;

/**
 * Helper class containing methods to facilitate network sync tasks.
 */

public final class BakeliciousSyncTask {

    private static ArrayList<Integer> sFavoriteRecipeIds;

    /**
     * Fetches the recipe data and updates the local database.
     *
     * @param context The context.
     */
    synchronized public static void fetchRecipes(Context context) {
        List<Recipe> recipes = BakeliciousRetrofitClient.getRecipes();
        if (recipes != null && recipes.size() > 0) {
            ContentResolver contentResolver = context.getContentResolver();

            cacheFavoriteRecipeIds(contentResolver);

            // add recipes to recipe table
            addEntries(contentResolver, RecipeEntry.CONTENT_URI, 0, recipes);

            updatedFavoriteRecipesFlag(contentResolver);
        }
    }

    /**
     * Cache the favorited recipe IDs
     *
     * @param contentResolver The {@link BakeliciousContentProvider}
     */
    private static void cacheFavoriteRecipeIds(ContentResolver contentResolver) {
        sFavoriteRecipeIds = new ArrayList<>();

        // query for the favorite recipe IDs
        Cursor cursor = contentResolver.query(RecipeEntry.CONTENT_URI,
                new String[]{RecipeContract.COLUMN_RECIPE_ID},
                RecipeContract.COLUMN_RECIPE_FAVORITE + " =? ",
                new String[]{"1"},
                null);

        // generate the array of ids
        if (cursor != null) {
            while (cursor.moveToNext()) {
                sFavoriteRecipeIds.add(cursor.getInt(0));
            }
        }
        cursor.close();
    }

    /**
     * Update the favorite flag in the recipe table for the cached favorite recipe IDs
     *
     * @param contentResolver The {@link BakeliciousContentProvider}
     */
    private static void updatedFavoriteRecipesFlag(ContentResolver contentResolver) {
        for (int recipeId : sFavoriteRecipeIds) {
            ContentValues cv = new ContentValues();
            cv.put(RecipeContract.COLUMN_RECIPE_FAVORITE, 1);
            int updateCount = contentResolver.update(RecipeEntry.CONTENT_URI,
                    cv,
                    RecipeContract.COLUMN_RECIPE_ID + " =? ",
                    new String[]{Integer.toString(recipeId)});

            if (updateCount == -1) {
                Timber.e("Error while update favorite flag for recipeId: %d", recipeId);
            }
        }
    }

    /**
     * Add entries to the table specified by the Uri.
     *
     * @param contentResolver The {@link BakeliciousContentProvider}
     * @param uri             The Uri specifying the directory.
     * @param recipeId        The RecipeID.
     * @param c               The List of {@link Recipe}, {@link Ingredient} or {@link Step}
     */
    private static void addEntries(ContentResolver contentResolver, Uri uri, int recipeId, Collection<?> c) {
        ContentValues[] contentValuesArry = BakeliciousUtils.getContentValues(c, recipeId);

        if (contentValuesArry != null && contentValuesArry.length > 0) {
            int insertedCount = contentResolver.bulkInsert(uri, contentValuesArry);

            // sanity check if recipe entries were inserted correctly
            if (insertedCount != contentValuesArry.length) {
                Timber.e("Error inserting entries! RecipeId : %d", recipeId);
            }
        }
    }
}
