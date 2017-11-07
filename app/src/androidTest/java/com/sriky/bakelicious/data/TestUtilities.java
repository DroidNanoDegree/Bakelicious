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
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.sriky.bakelicious.provider.BakeliciousContentProvider;
import com.sriky.bakelicious.provider.IngredientContract;
import com.sriky.bakelicious.provider.InstructionContract;
import com.sriky.bakelicious.provider.RecipeContract;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Class contains utility methods to support the Test cases.
 */

public class TestUtilities {

    /**
     * Creates and returns ContentValues that represents an item in the "recipes" table.
     *
     * @return ContentValue.
     */
    public static ContentValues createRecipeContentValues() {
        /* generate a random recipe ID */
        final int min = 20;
        final int max = 80;
        final Random random = new Random();
        int recipeId = random.nextInt((max - min) + 1) + min;

        ContentValues cv = new ContentValues();
        cv.put(RecipeContract.COLUMN_RECIPE_ID, recipeId);
        cv.put(RecipeContract.COLUMN_RECIPE_NAME, "Special Brownies");
        cv.put(RecipeContract.COLUMN_RECIPE_SERVES, 8);
        return cv;
    }

    /**
     * Creates and returns ContentValues Array where each element represents an item in the "recipes" table.
     *
     * @return ContentValue[]
     */
    public static ContentValues[] createRecipeContentValuesArray() {
        ContentValues[] cvArray = new ContentValues[3];
        cvArray[0] = createRecipeContentValues();
        cvArray[1] = createRecipeContentValues();
        cvArray[2] = createRecipeContentValues();
        return cvArray;
    }

    /**
     * Creates and returns ContentValues that represents an item in the "ingredient" table.
     *
     * @return ContentValue.
     */
    public static ContentValues createIngredientContentValues() {
        /* generate a random recipe ID */
        final int min = 20;
        final int max = 80;
        final Random random = new Random();
        int recipeId = random.nextInt((max - min) + 1) + min;

        ContentValues cv = new ContentValues();
        cv.put(IngredientContract.COLUMN_RECIPE_ID, recipeId);
        cv.put(IngredientContract.COLUMN_INGREDIENT_NAME, "Eggs");
        cv.put(IngredientContract.COLUMN_INGREDIENT_QUANTITY, 3);
        cv.put(IngredientContract.COLUMN_INGREDIENT_MEASURE, "UNIT");
        return cv;
    }

    /**
     * Creates and returns ContentValues Array where each element represents an item in the "ingredient" table.
     *
     * @return ContentValue[]
     */
    public static ContentValues[] createIngredientContentValuesArray() {
        ContentValues[] cvArray = new ContentValues[3];
        cvArray[0] = createIngredientContentValues();
        cvArray[1] = createIngredientContentValues();
        cvArray[2] = createIngredientContentValues();
        return cvArray;
    }

    /**
     * Creates and returns ContentValues that represents an item in the "instruction" table.
     *
     * @return ContentValue.
     */
    public static ContentValues createInstructionContentValues() {
        /* generate a random recipe ID */
        final int min = 20;
        final int max = 80;
        final Random random = new Random();
        int recipeId = random.nextInt((max - min) + 1) + min;

        ContentValues cv = new ContentValues();
        cv.put(InstructionContract.COLUMN_RECIPE_ID, recipeId);
        cv.put(InstructionContract.COLUMN_INSTRUCTION_SHORT, "Short Desc.");
        cv.put(InstructionContract.COLUMN_INSTRUCTION_LONG, "Longggggggggggggggggggg...");
        cv.put(InstructionContract.COLUMN_INSTRUCTION_NUMBER, 1);
        return cv;
    }

    /**
     * Creates and returns ContentValues Array where each element represents an item in the "instruction" table.
     *
     * @return ContentValue[]
     */
    public static ContentValues[] createInstructionContentValuesArray() {
        ContentValues[] cvArray = new ContentValues[3];
        cvArray[0] = createInstructionContentValues();
        cvArray[1] = createInstructionContentValues();
        cvArray[2] = createInstructionContentValues();
        return cvArray;
    }

    /**
     * Validates the supplied cursor with contentValues and asserts if they don't match or
     * cursor is null or empty!
     *
     * @param error         The error message to display.
     * @param cursor        The cursor returned after a query.
     * @param contentValues The contentValues to compare the cursor items with.
     */
    public static void validateCursorWithContentValues(String error,
                                                       Cursor cursor, ContentValues contentValues) {

        /* sanity checks for valid cursor */
        assertNotNull("This cursor is null!!!", cursor);
        if (cursor.getPosition() == -1) {
            assertTrue("Empty cursor returned! " + error, cursor.moveToFirst());
        }

        Set<Map.Entry<String, Object>> valueSet = contentValues.valueSet();
        int counter = 0;
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            counter++;
            int index = cursor.getColumnIndex(columnName);

            /* Test to see if the column is contained within the cursor */
            String columnNotFoundError = "Column '" + columnName + "' not found. "
                    + error;
            assertFalse(columnNotFoundError, index == -1);

            /* Test to see if the expected value equals the actual value (from the Cursor) */
            String expectedValue = entry.getValue().toString();
            String actualValue = cursor.getString(index);

            String valuesDontMatchError = "For ColumnName:" + columnName + ", actual value '" + actualValue
                    + "' did not match the expected value '" + expectedValue + "'. "
                    + error;

            assertEquals(valuesDontMatchError,
                    expectedValue,
                    actualValue);
        }
    }

    public static void deleteAllEntries(ContentResolver contentResolver, Uri uri) {
        contentResolver.delete(uri, null, null);
    }

    public static void insert(ContentResolver contentResolver, Uri uri,
                              ContentValues contentValues) {

        /* Insert ContentValues into database and get a row ID back */
        Uri resultUri = contentResolver.insert(uri, contentValues);

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, resultUri != null);

        /* Query the recipes table */
        Cursor cursor = contentResolver.query(
                uri,
                null,
                null,
                null,
                null);

        /* Validate the cursor with the contentValues we used to make the entry. */
        TestUtilities.validateCursorWithContentValues(insertFailed, cursor, contentValues);

        /* close the cursor. */
        cursor.close();
    }

    public static void bulkInsert(ContentResolver contentResolver,
                                  Uri uri,
                                  ContentValues[] bulkInsertTestContentValues) {

        /* bulkInsert will return the number of records that were inserted. */
        int insertCount = contentResolver.bulkInsert(uri, bulkInsertTestContentValues);

        /*
         * Verify the value returned by the ContentProvider after bulk insert with the number of
         * item in the bulkInsertTestContentValues. They should match! */
        String expectedAndActualInsertedRecordCountDoNotMatch =
                "Number of expected records inserted does not match actual inserted record count";
        assertEquals(expectedAndActualInsertedRecordCountDoNotMatch,
                insertCount,
                bulkInsertTestContentValues.length);

        /* Query the recipe table and verify if all the entries match with
         * bulkInsertTestContentValues array */
        Cursor cursor = contentResolver.query(
                uri,
                null,
                null,
                null,
                null);

        /* For sanity, we can verify the items in the cursor as well. */
        assertEquals(cursor.getCount(), bulkInsertTestContentValues.length);

        /*
         * We now loop through and validate each record in the Cursor with the expected values from
         * bulkInsertTestContentValues.
         */
        for (int i = 0; i < bulkInsertTestContentValues.length; i++, cursor.moveToNext()) {
            TestUtilities.validateCursorWithContentValues(
                    "testBulkInsert. Error validating RecipeEntry " + i,
                    cursor,
                    bulkInsertTestContentValues[i]);
        }
        cursor.close();
    }


    /**
     * Clears all the tables used in Bakelicious
     *
     * @param context The context.
     */
    public static void clearAllTables(Context context) {
        clearRecipesTable(context);
        clearIngredientsTable(context);
        clearInstructionsTable(context);
    }

    /**
     * delete all items from the recipes table
     */
    public static void clearRecipesTable(Context context) {
        TestUtilities.deleteAllEntries(context.getContentResolver(),
                BakeliciousContentProvider.RecipeEntry.CONTENT_URI);
    }

    /**
     * delete all items from the ingredients table
     */
    public static void clearIngredientsTable(Context context) {
        TestUtilities.deleteAllEntries(context.getContentResolver(),
                BakeliciousContentProvider.IngredientEntry.CONTENT_URI);
    }

    /**
     * delete all items from the instruction table
     */
    public static void clearInstructionsTable(Context context) {
        TestUtilities.deleteAllEntries(context.getContentResolver(),
                BakeliciousContentProvider.InstructionEntry.CONTENT_URI);
    }
}
