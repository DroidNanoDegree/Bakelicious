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
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sriky.bakelicious.R;
import com.sriky.bakelicious.model.Ingredient;
import com.sriky.bakelicious.provider.BakeliciousContentProvider;
import com.sriky.bakelicious.provider.RecipeContract;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

import static android.view.View.VISIBLE;

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
            RecipeContract.COLUMN_RECIPE_INGREDIENTS};
    private static final int INDEX_RECIPE_NAME = 0;
    private static final int INDEX_RECIPE_INGREDIENTS = 1;
    Context mContext;
    List<List<Ingredient>> mIngredientsList;
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

        if (mIngredientsList == null) {
            mIngredientsList = new ArrayList<>();
        }
        mIngredientsList.clear();

        while (mCursor.moveToNext()) {
            Gson gson = new Gson();

            Type listType = new TypeToken<ArrayList<Ingredient>>() {
            }.getType();
            List<Ingredient> ingredientList =
                    gson.fromJson(mCursor.getString(INDEX_RECIPE_INGREDIENTS), listType);

            mIngredientsList.add(ingredientList);
        }
    }

    @Override
    public void onDestroy() {
        if (mCursor == null) return;
        mCursor.close();
    }

    @Override
    public int getCount() {
        if(mIngredientsList == null) return 0;

        //get the total count of all ingredients as they will be displayed as a list in the
        //gridview.
        int ret = 0;
        for(List<Ingredient> ingredients : mIngredientsList) {
            ret += ingredients.size();
            ret += 1; //plus one for the header for the header.
        }
        return ret;
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (mIngredientsList == null || mIngredientsList.size() == 0) return null;

        int count = 0, idx = 0, header = 1;
        for(List<Ingredient> ingredients : mIngredientsList) {
            int ingredientsSize = ingredients.size();

            if((header + count + ingredientsSize) > position) {
                break;
            }
            count += (ingredientsSize + header);
            idx++;
        }

        int normalizedPosition = position - count;
        Timber.d("getViewAt(), position: %d, count: %d, normalizedPosition: %d",
                position, count, normalizedPosition);

        //if it is the first item then display the recipename.
        if(normalizedPosition == 0) {

            RemoteViews views = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_recipe_ingredient_list_item_header);

            mCursor.moveToPosition(idx);
            views.setTextViewText(R.id.tv_widget_recipe_name, mCursor.getString(INDEX_RECIPE_NAME));

            return views;
        } else {

            normalizedPosition--; //accounting for the header.
            RemoteViews views = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_recipe_ingredient_list_item);

            Ingredient ingredient = mIngredientsList.get(idx).get(normalizedPosition);

            // set the value for the TextViews
            views.setTextViewText(R.id.tv_widget_ingredient,
                    ingredient.getIngredient().toUpperCase());
            views.setTextViewText(R.id.tv_widget_units, String.format(Locale.getDefault(),
                    "%s", ingredient.getQuantity()));
            views.setTextViewText(R.id.tv_widget_measure, ingredient.getMeasure().toLowerCase());

            return views;
        }
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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

