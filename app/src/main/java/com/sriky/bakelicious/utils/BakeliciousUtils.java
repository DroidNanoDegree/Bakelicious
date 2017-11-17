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
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.sriky.bakelicious.R;
import com.sriky.bakelicious.model.Ingredient;
import com.sriky.bakelicious.model.Recipe;
import com.sriky.bakelicious.model.Step;
import com.sriky.bakelicious.provider.BakeliciousContentProvider;
import com.sriky.bakelicious.provider.RecipeContract;

import java.util.Collection;

import timber.log.Timber;

/**
 * Utility class to process and convert data to different types.
 */

public final class BakeliciousUtils {

    /* loader ids */
    public static final int MASTER_LIST_FRAGMENT_LOADER_ID = 1;

    /* recipe details number of tabs, currently set to 2, #1 for ingredients & #2 for instructions */
    public static final int RECIPE_DETAILS_TAB_COUNT = 2;

    /* bundle keys */
    public static final String RECIPE_ID_BUNDLE_KEY = "recipe_id";
    public static final String RECIPE_NAME_BUNDLE_KEY = "recipe_name";
    public static final String RECIPE_FAVORITE_BUNDLE_KEY = "recipe_favorite";
    public static final String RECIPE_INGREDIENTS_BUNDLE_KEY = "recipe_ingredients";
    public static final String RECIPE_INSTRUCTIONS_BUNDLE_KEY = "recipe_instructions";

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

    /**
     * Validates the bundle and returns the RecipeID.
     *
     * @param args      {@link Bundle} that was passed to the activity or fragment.
     * @param className The class from where the method was called.
     * @return RecipeID if the bundle is validate and contains the entry.
     */
    public static int validateBundleAndGetRecipeId(Bundle args, String className) {
        if (args == null)
            throw new RuntimeException(String.format("No arguments bundle set for %s", className));

        if (!args.containsKey(BakeliciousUtils.RECIPE_ID_BUNDLE_KEY)) {
            throw new RuntimeException(String.format("RecipeId not set in bundle for %s", className));
        }

        return args.getInt(BakeliciousUtils.RECIPE_ID_BUNDLE_KEY);
    }

    /**
     * Updates the DB record for the specified recipeId and shows a Toast if the update was successful.
     *
     * @param context    The context.
     * @param recipeId   The RecipeID for which the record needs to be updated.
     * @param favorite   true/false.
     * @param recipeName The name of the recipe for which the record is being updated.
     */
    public static void updateFavoriteRecipe(final Context context,
                                            final int recipeId,
                                            final String recipeName,
                                            final boolean favorite) {

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(RecipeContract.COLUMN_RECIPE_FAVORITE, favorite ? 1 : 0);
                return context.getContentResolver().update(
                        BakeliciousContentProvider.RecipeEntry.CONTENT_URI,
                        contentValues,
                        RecipeContract.COLUMN_RECIPE_ID + " =? ",
                        new String[]{Integer.toString(recipeId)});
            }

            @Override
            protected void onPostExecute(Integer count) {
                super.onPostExecute(count);

                Timber.d("updateRecord() count = %d", count);
                if (count <= 0) {
                    throw new RuntimeException("Unable to update record with id: " + recipeId);
                }

                int formatId =
                        (favorite) ? R.string.recipe_added_to_favorites
                                : R.string.recipe_removed_from_favorites;

                Toast.makeText(context,
                        String.format(context.getString(formatId), recipeName),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }.execute();
    }
}
