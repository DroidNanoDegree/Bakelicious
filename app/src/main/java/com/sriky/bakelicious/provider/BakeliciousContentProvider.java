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

package com.sriky.bakelicious.provider;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Uses the Schematic (https://github.com/SimonVT/schematic) to create a content provider and
 * define URIs for the provider.
 */

@ContentProvider(authority = BakeliciousContentProvider.AUTHORITY,
        database = BakeliciousDatabase.class)
public final class BakeliciousContentProvider {

    /* Authority for the Bakelicious' Content Provider */
    public static final String AUTHORITY = "com.sriky.bakelicious";

    /* path for the recipes directory */
    public static final String PATH_RECIPES = "recipes";

    /* The base content URI = "content://" + <authority> */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * Recipe Entry specifics
     */
    @TableEndpoint(table = BakeliciousDatabase.Recipes)
    public static final class RecipeEntry {

        /* The base CONTENT_URI used to query the Recipe table from the content provider */
        @ContentUri(
                path = PATH_RECIPES,
                type = "vnd.android.cursor.dir/" + PATH_RECIPES,
                defaultSort = RecipeContract._ID + " ASC")
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();
    }
}
