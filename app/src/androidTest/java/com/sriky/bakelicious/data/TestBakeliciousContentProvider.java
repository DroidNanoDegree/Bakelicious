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
import android.support.test.InstrumentationRegistry;

import com.sriky.bakelicious.provider.BakeliciousContentProvider;
import com.sriky.bakelicious.provider.BakeliciousContentProvider.RecipeEntry;
import com.sriky.bakelicious.utils.LoggerUtils;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * This class tests various database operations that are supported by BakeliciousContentProvider
 * The tests validates accurate working of the following to be:
 * <p>
 * 1). Insertion of single entries
 * </p>
 */

public class TestBakeliciousContentProvider {
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    @Before
    public void before() {
        /* delete all items from the recipes table */
        mContext.getContentResolver().delete(
                BakeliciousContentProvider.RecipeEntry.CONTENT_URI, null, null);

        /* delete all items from the ingredients table */
        mContext.getContentResolver().delete(
                BakeliciousContentProvider.IngredientEntry.CONTENT_URI, null, null);

        /* delete all items from the instruction table */
        mContext.getContentResolver().delete(
                BakeliciousContentProvider.InstructionEntry.CONTENT_URI, null, null);

        LoggerUtils.initLogger(TestBakeliciousContentProvider.class.getSimpleName());
    }

    /**
     * Tests insertion of data into the recipes table via the content provider.
     */
    @Test
    public void testBasicMoviesQuery() {

        /* Obtain movie values from TestUtilities */
        ContentValues contentValues = TestUtilities.createRecipeContentValues();
        ContentResolver contentResolver = mContext.getContentResolver();

        /* Insert ContentValues into database and get a row ID back */
        Uri uri = contentResolver.insert(RecipeEntry.CONTENT_URI, contentValues);

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, uri != null);

        /* Query the movies table */
        Cursor cursor = mContext.getContentResolver().query(
                RecipeEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        /* Validate the cursor with the contentValues we used to make the entry. */
        TestUtilities.validateCursorWithContentValues(insertFailed, cursor, contentValues);

        /* close the cursor. */
        cursor.close();
    }

}
