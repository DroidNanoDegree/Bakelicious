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

import com.orhanobut.logger.Logger;
import com.sriky.bakelicious.model.Ingredient;
import com.sriky.bakelicious.model.Recipe;
import com.sriky.bakelicious.model.Step;
import com.sriky.bakelicious.ui.RecipeDetailFragment;

import java.util.Collection;

/**
 * Utility class to process and convert data to different types.
 */

public final class BakeliciousUtils {

    public static final int MASTER_LIST_FRAGMENT_LOADER_ID = 1;

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
        /* bail out early if the collection is null or empty */
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
            } else if (o instanceof Ingredient) {
                Ingredient ingredient = (Ingredient) o;
                ContentValues cv = (ingredient != null) ? ingredient.getContentValues(recipeId) : null;
                if (cv != null) {
                    retContentValuesArray[idx++] = cv;
                }
            } else if (o instanceof Step) {
                Step step = (Step) o;
                ContentValues cv = (step != null) ? step.getContentValues(recipeId) : null;
                if (cv != null) {
                    retContentValuesArray[idx++] = cv;
                }
            } else {
                throw new RuntimeException("UnSupported type:" + o.getClass());
            }
        }
        return retContentValuesArray;
    }

    public static void addRecipeDetailFragment(FragmentManager fragmentManager,
                                               int recipeId, int resourceId) {
        Logger.d("recipeId:" + recipeId);

        /* set the recipeId to a bundle and set it to the RecipeDetailFragment */
        Bundle bundle = new Bundle();
        bundle.putInt(RecipeDetailFragment.RECIPE_ID_BUNDLE_KEY, recipeId);
        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        recipeDetailFragment.setArguments(bundle);

        /* add the RecipeDetailFragment */
        fragmentManager.beginTransaction()
                .add(resourceId, recipeDetailFragment)
                .commit();
    }
}
