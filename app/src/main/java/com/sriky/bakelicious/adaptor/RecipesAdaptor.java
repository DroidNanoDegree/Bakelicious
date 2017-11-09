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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sriky.bakelicious.R;
import com.sriky.bakelicious.event.MessageEvent;
import com.sriky.bakelicious.provider.RecipeContract;

import org.greenrobot.eventbus.EventBus;

/**
 * Adaptor for the Recipes {@link RecyclerView} used in the
 * {@link com.sriky.bakelicious.ui.MasterListFragment}
 */

public class RecipesAdaptor extends RecyclerView.Adapter<RecipesAdaptor.RecipesViewHolder> {

    private Cursor mRecipesCursor;
    private OnRecipeItemClickedListener mRecipeItemClickedListener;

    public RecipesAdaptor(Cursor cursor, OnRecipeItemClickedListener listener) {
        mRecipesCursor = cursor;
        mRecipeItemClickedListener = listener;
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
            holder.recipeName.setText(mRecipesCursor.getString(
                    mRecipesCursor.getColumnIndex(RecipeContract.COLUMN_RECIPE_NAME)));

            holder.serves.setText(mRecipesCursor.getString(
                    mRecipesCursor.getColumnIndex(RecipeContract.COLUMN_RECIPE_SERVES)));

            /* set the RecipeID as a tag so it can be passed to the onRecipeItemClicked Listener */
            int recipeId = mRecipesCursor.getInt(
                    mRecipesCursor.getColumnIndex(RecipeContract.COLUMN_RECIPE_ID));
            if(position == 0){
                EventBus.getDefault().post(new MessageEvent.RecipeDataLoaded(recipeId));
            }
            holder.itemView.setTag(recipeId);
        }
    }

    @Override
    public int getItemCount() {
        if (mRecipesCursor == null) return 0;
        return mRecipesCursor.getCount();
    }

    /* interface to handle the click events on the RecyclerView items */
    public interface OnRecipeItemClickedListener {
        void onRecipeItemClicked(int recipeId);
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
            mRecipeItemClickedListener.onRecipeItemClicked((int) view.getTag());
        }
    }
}
