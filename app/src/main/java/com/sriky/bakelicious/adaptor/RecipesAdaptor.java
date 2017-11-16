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

package com.sriky.bakelicious.adaptor;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sriky.bakelicious.R;
import com.sriky.bakelicious.event.Message;
import com.sriky.bakelicious.utils.BakeliciousUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Adaptor for the Recipes {@link RecyclerView} used in the
 * {@link com.sriky.bakelicious.ui.MasterListFragment}
 */

public class RecipesAdaptor extends RecyclerView.Adapter<RecipesAdaptor.RecipesViewHolder> {

    private Cursor mRecipesCursor;

    public RecipesAdaptor(Cursor cursor) {
        mRecipesCursor = cursor;
    }

    /**
     * Swap the cursor used by the adaptor.
     *
     * @param cursor
     */
    public void swapCursor(Cursor cursor) {
        mRecipesCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public RecipesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recipes_list_item, parent, false);
        return new RecipesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipesViewHolder holder, int position) {
        if (mRecipesCursor != null && mRecipesCursor.moveToPosition(position)) {
            /* set the recipe name */
            String recipeName = mRecipesCursor.getString(
                    BakeliciousUtils.INDEX_PROJECTION_MASTER_LIST_FRAGMENT_RECIPE_NAME);
            holder.recipeName.setText(recipeName);

            /* set the recipe servings number */
            holder.serves.setText(mRecipesCursor.getString(
                    BakeliciousUtils.INDEX_PROJECTION_MASTER_LIST_FRAGMENT_RECIPE_SERVINGS));

            /* set the RecipeID as a tag so it can be passed to the onRecipeItemClicked Listener */
            int recipeId = mRecipesCursor.getInt(
                    BakeliciousUtils.INDEX_PROJECTION_MASTER_LIST_FRAGMENT_RECIPE_ID);

            /* set the bundle with recipeId */
            Bundle bundle = new Bundle();
            bundle.putInt(BakeliciousUtils.RECIPE_ID_BUNDLE_KEY, recipeId);
            bundle.putString(BakeliciousUtils.RECIPE_NAME_BUNDLE_KEY, recipeName);
            bundle.putInt(BakeliciousUtils.RECIPE_FAVORITE_BUNDLE_KEY,
                    mRecipesCursor.getInt(
                            BakeliciousUtils.INDEX_PROJECTION_MASTER_LIST_FRAGMENT_RECIPE_FAVORITE));
            holder.itemView.setTag(bundle);

            /* trigger an event to pass the recipeId of the first item in the list,
             * which is used in the TwoPane mode for tablets.
             */
            if (position == 0) {
                EventBus.getDefault().post(new Message.EventRecipeDataLoaded(bundle));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mRecipesCursor == null) return 0;
        return mRecipesCursor.getCount();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mRecipesCursor.close();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    /**
     * ViewHolder for RecipesAdaptor.
     */
    class RecipesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView recipeName;
        public TextView serves;

        public RecipesViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.tv_recipe_name);
            serves = itemView.findViewById(R.id.tv_serves);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            EventBus.getDefault().post(new Message.EventRecipeItemClicked((Bundle) view.getTag()));
        }
    }
}
