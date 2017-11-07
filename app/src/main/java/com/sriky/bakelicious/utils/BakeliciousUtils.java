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

import com.sriky.bakelicious.model.Ingredient;
import com.sriky.bakelicious.model.Recipe;
import com.sriky.bakelicious.model.Step;
import com.sriky.bakelicious.provider.IngredientContract;
import com.sriky.bakelicious.provider.InstructionContract;
import com.sriky.bakelicious.provider.RecipeContract;

import java.util.Collection;

/**
 * Utility class to process and convert data to different types.
 */

public final class BakeliciousUtils {

    /**
     * Generates content values from the list of objects. Supported list types are {@link Recipe},
     * {@link Ingredient} & {@link Step}
     *
     * @param c        Collection of either {@link Recipe}, {@link Ingredient} or {@link Step}
     * @param recipeId The recipeID.
     * @return {@link ContentValues} for either {@link Recipe}, {@link Ingredient} or {@link Step}
     */
    public static ContentValues[] getContentValues(Collection<?> c, int recipeId) {
        ContentValues[] retContentValuesArray = new ContentValues[c.size()];
        int idx = 0;
        for (Object o : c) {
            ContentValues cv = new ContentValues();
            if (o instanceof Recipe) {
                Recipe recipe = (Recipe) o;
                cv.put(RecipeContract.COLUMN_RECIPE_ID, recipe.getId());
                cv.put(RecipeContract.COLUMN_RECIPE_NAME, recipe.getName());
                cv.put(RecipeContract.COLUMN_RECIPE_SERVES, recipe.getServings());
                cv.put(RecipeContract.COLUMN_RECIPE_IMAGE_URL, recipe.getImage());
                retContentValuesArray[idx++] = cv;
            } else if (o instanceof Ingredient) {
                Ingredient ingredient = (Ingredient) o;
                cv.put(IngredientContract.COLUMN_RECIPE_ID, recipeId);
                cv.put(IngredientContract.COLUMN_INGREDIENT_MEASURE, ingredient.getMeasure());
                cv.put(IngredientContract.COLUMN_INGREDIENT_QUANTITY, ingredient.getQuantity());
                cv.put(IngredientContract.COLUMN_INGREDIENT_NAME, ingredient.getIngredient());
                retContentValuesArray[idx++] = cv;
            } else if (o instanceof Step) {
                Step step = (Step) o;
                cv.put(InstructionContract.COLUMN_RECIPE_ID, recipeId);
                cv.put(InstructionContract.COLUMN_INSTRUCTION_NUMBER, step.getId());
                cv.put(InstructionContract.COLUMN_INSTRUCTION_SHORT, step.getShortDescription());
                cv.put(InstructionContract.COLUMN_INSTRUCTION_LONG, step.getDescription());
                cv.put(InstructionContract.COLUMN_INSTRUCTION_IMAGE_URL, step.getThumbnailURL());
                cv.put(InstructionContract.COLUMN_INSTRUCTION_VIDEO_URL, step.getVideoURL());
                retContentValuesArray[idx++] = cv;
            } else {
                throw new RuntimeException("UnSupported type:" + o.getClass());
            }
        }
        return retContentValuesArray;
    }
}
