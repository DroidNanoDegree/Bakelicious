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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.facebook.stetho.Stetho;
import com.orhanobut.logger.Logger;
import com.sriky.bakelicious.data.TestUtilities;
import com.sriky.bakelicious.model.Recipe;
import com.sriky.bakelicious.provider.BakeliciousContentProvider;
import com.sriky.bakelicious.provider.RecipeContract;
import com.sriky.bakelicious.utils.LoggerUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertTrue;

import com.sriky.bakelicious.provider.BakeliciousContentProvider.RecipeEntry;

/**
 * Class to handle testing of data sync operations.
 */

public final class TestBakeliciousSyncOperations {
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    @Before
    public void before() {
        TestUtilities.clearAllTables(mContext);
        LoggerUtils.initLogger(TestBakeliciousSyncOperations.class.getSimpleName());
        Stetho.initializeWithDefaults(mContext);
    }

    @Test
    public void getRecipesJson() {
        List<Recipe> recipes = BakeliciousRetrofitClient.getRecipes();
        String errorMsg = "Unable to get recipes!";
        assertTrue(errorMsg, recipes != null && recipes.size() > 0);
        Logger.d(recipes.size() + " recipes found!");
    }

    /**
     * Test the {@link BakeliciousSyncTask#fetchRecipes(Context)} to ensure favorited recipes
     * state is restored after a sync operation.
     */
    @Test
    public void testFetchTask() {
        /* make an entry into the db after setting the favorite flag to 1. */
        ContentValues contentValues = TestUtilities.createRecipeContentValues();
        contentValues.put(RecipeContract.COLUMN_RECIPE_FAVORITE, 1);

        ContentResolver contentResolver = mContext.getContentResolver();

        Uri uri = contentResolver.insert(RecipeEntry.CONTENT_URI, contentValues);
        Logger.d("uri:" + uri);

        int recipeId = (int)contentValues.get(RecipeContract.COLUMN_RECIPE_ID);
        Logger.d("recipeId:" + recipeId);

        /* get data from network and retain the favorited recipes */
        BakeliciousSyncTask.fetchRecipes(mContext);

        /* check to see if the recipeId inserted at the beginning still exists with the state intact */
        Cursor cursor = contentResolver.query(RecipeEntry.CONTENT_URI,
                null,
                RecipeContract.COLUMN_RECIPE_ID + " =? ",
                new String[]{ Integer.toString(recipeId) }, null);

        Logger.d("cursor.size():"+cursor.getCount());
        assertTrue("Unable to retrieve record for recipeId: " + recipeId,
                cursor != null && cursor.getCount() > 0);

        cursor.moveToNext();
        assertTrue("Boolean state not retained after a sync!",
                cursor.getInt(cursor.getColumnIndex(RecipeContract.COLUMN_RECIPE_FAVORITE)) == 1);

        cursor.close();
    }
}
