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

package com.sriky.bakelicious.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sriky.bakelicious.R;
import com.sriky.bakelicious.provider.BakeliciousContentProvider;
import com.sriky.bakelicious.provider.RecipeContract;
import com.sriky.bakelicious.ui.RecipeDetailActivity;
import com.sriky.bakelicious.utils.BakeliciousUtils;

/**
 * Service responsible for displaying recipes in the widget's grid view.
 */

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String[] recipesProjection = {RecipeContract.COLUMN_RECIPE_NAME,
            RecipeContract.COLUMN_RECIPE_ID,
            RecipeContract.COLUMN_RECIPE_FAVORITE,
            RecipeContract.COLUMN_RECIPE_INSTRUCTIONS,
            RecipeContract.COLUMN_RECIPE_INGREDIENTS};
    private static final int INDEX_RECIPE_NAME = 0;
    private static final int INDEX_RECIPE_ID = 1;
    private static final int INDEX_RECIPE_FAVORITE = 2;
    private static final int INDEX_RECIPE_INSTRUCTIONS = 3;
    private static final int INDEX_RECIPE_INGREDIENTS = 4;
    Context mContext;
    Cursor mCursor;

    public GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;

    }

    @Override
    public void onCreate() {

    }

    /**
     * Called on start and when notifyAppWidgetViewDataChanged is called
     */
    @Override
    public void onDataSetChanged() {
        // Get all recipes.
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(
                BakeliciousContentProvider.RecipeEntry.CONTENT_URI,
                recipesProjection,
                RecipeContract.COLUMN_RECIPE_FAVORITE + " =? ",
                new String[]{"1"},
                null);
    }

    @Override
    public void onDestroy() {
        if (mCursor == null) return;
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) return null;
        mCursor.moveToPosition(position);

        String recipeName = mCursor.getString(INDEX_RECIPE_NAME);
        int recipeId = mCursor.getInt(INDEX_RECIPE_ID);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipes_widget);

        // Update the text
        views.setTextViewText(R.id.tv_widget_recipe_name, recipeName);

        // Fill in the onClick PendingIntent Template using the specific recipeId for each item individually
        Bundle extras = new Bundle();
        extras.putInt(BakeliciousUtils.RECIPE_ID_BUNDLE_KEY, recipeId);
        extras.putInt(BakeliciousUtils.RECIPE_FAVORITE_BUNDLE_KEY, mCursor.getInt(INDEX_RECIPE_FAVORITE));
        extras.putString(BakeliciousUtils.RECIPE_NAME_BUNDLE_KEY, recipeName);
        extras.putString(BakeliciousUtils.RECIPE_INSTRUCTIONS_BUNDLE_KEY, mCursor.getString(INDEX_RECIPE_INSTRUCTIONS));
        extras.putString(BakeliciousUtils.RECIPE_INGREDIENTS_BUNDLE_KEY, mCursor.getString(INDEX_RECIPE_INGREDIENTS));
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(RecipeDetailActivity.RECIPE_INFO_BUNDLE_KEY, extras);
        views.setOnClickFillInIntent(R.id.iv_widget_recipe, fillInIntent);

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

