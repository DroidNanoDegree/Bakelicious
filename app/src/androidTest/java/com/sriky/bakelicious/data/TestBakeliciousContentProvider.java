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

package com.sriky.bakelicious.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;

import com.sriky.bakelicious.provider.BakeliciousContentProvider;
import com.sriky.bakelicious.provider.BakeliciousContentProvider.IngredientEntry;
import com.sriky.bakelicious.provider.BakeliciousContentProvider.RecipeEntry;
import com.sriky.bakelicious.provider.BakeliciousContentProvider.InstructionEntry;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * This class tests various database operations that are supported by BakeliciousContentProvider
 * The tests validates accurate working of the following to be:
 * <p>
 * 1). Insertion of single entries into recipe, ingredient & instruction tables.
 * 2). Bulk insertion into recipe, ingredient & instruction tables.
 * 3).
 * </p>
 */

public class TestBakeliciousContentProvider {
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    @Before
    public void before() {
        clearRecipesTable();
        clearIngredientsTable();
        clearInstructionsTable();
    }

    /**
     * Tests insertion of data into the recipes table via the content provider.
     */
    @Test
    public void testSingleInsertionTo_RecipeTable() {

        TestUtilities.insert(mContext.getContentResolver(),
                RecipeEntry.CONTENT_URI,
                TestUtilities.createRecipeContentValues());
    }

    /**
     * This test tests the bulkInsert feature of the ContentProvider for recipe table
     */
    @Test
    public void testBulkInsert_IntoRecipeTable() {

        clearRecipesTable();

        TestUtilities.bulkInsert(mContext.getContentResolver(),
                RecipeEntry.CONTENT_URI,
                TestUtilities.createRecipeContentValuesArray());
    }

    /**
     * Tests insertion of data into the ingredient table via the content provider.
     */
    @Test
    public void testSingleInsertion_IntoIngredientTable() {

        TestUtilities.insert(mContext.getContentResolver(),
                IngredientEntry.CONTENT_URI,
                TestUtilities.createIngredientContentValues());
    }

    /**
     * This test tests the bulkInsert feature of the ContentProvider for ingredient table
     */
    @Test
    public void testBulkInsert_IntoIngredientTable() {

        clearIngredientsTable();

        TestUtilities.bulkInsert(mContext.getContentResolver(),
                IngredientEntry.CONTENT_URI,
                TestUtilities.createIngredientContentValuesArray());
    }

    /**
     * Tests insertion of data into the instruction table via the content provider.
     */
    @Test
    public void testSingleInsertion_IntoInstructionTable() {

        TestUtilities.insert(mContext.getContentResolver(),
                InstructionEntry.CONTENT_URI,
                TestUtilities.createInstructionContentValues());
    }

    /**
     * This test tests the bulkInsert feature of the ContentProvider for instruction table
     */
    @Test
    public void testBulkInsert_IntoInstructionTable() {

        clearInstructionsTable();

        TestUtilities.bulkInsert(mContext.getContentResolver(),
                InstructionEntry.CONTENT_URI,
                TestUtilities.createInstructionContentValuesArray());
    }

    /**
     * This test deletes all records from the recipes table using the ContentProvider.
     */
    @Test
    public void testDeleteAllRecordsFromProvider() {
        clearRecipesTable();

        /* Bulk insert into recipes table. */
        testBulkInsert_IntoRecipeTable();

        /* Using ContentResolver to access to the content model to perform the queries */
        ContentResolver contentResolver = mContext.getContentResolver();

        /* Delete all of the rows of data from the recipe table */
        contentResolver.delete(
                RecipeEntry.CONTENT_URI,
                null,
                null);

        /* Perform a query of the data that we've just deleted and the cursor count should be 0. */
        Cursor shouldBeEmptyCursor = contentResolver.query(
                RecipeEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        /* assert if the returned cursor is null. */
        String cursorWasNull = "Cursor was null.";
        assertNotNull(cursorWasNull, shouldBeEmptyCursor);

        /* check for cursor count = 0. */
        String allRecordsWereNotDeleted =
                "Error: All records were not deleted from recipes table during delete";
        assertEquals(allRecordsWereNotDeleted,
                0,
                shouldBeEmptyCursor.getCount());

        /* Always close your cursor */
        shouldBeEmptyCursor.close();
    }

    /**
     * delete all items from the recipes table
     */
    private void clearRecipesTable() {
        TestUtilities.deleteAllEntries(mContext.getContentResolver(),
                BakeliciousContentProvider.RecipeEntry.CONTENT_URI);
    }

    /**
     * delete all items from the ingredients table
     */
    private void clearIngredientsTable() {
        TestUtilities.deleteAllEntries(mContext.getContentResolver(),
                BakeliciousContentProvider.IngredientEntry.CONTENT_URI);
    }

    /**
     * delete all items from the instruction table
     */
    private void clearInstructionsTable() {
        TestUtilities.deleteAllEntries(mContext.getContentResolver(),
                BakeliciousContentProvider.InstructionEntry.CONTENT_URI);
    }
}
