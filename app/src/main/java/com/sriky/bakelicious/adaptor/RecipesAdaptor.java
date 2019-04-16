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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sriky.bakelicious.R;
import com.sriky.bakelicious.event.Message;
import com.sriky.bakelicious.provider.RecipeContract;
import com.sriky.bakelicious.utils.BakeliciousUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

/**
 * Adaptor for the Recipes {@link RecyclerView} used in the
 * {@link com.sriky.bakelicious.ui.MasterListFragment}
 */

public class RecipesAdaptor extends RecyclerView.Adapter<RecipesAdaptor.RecipesViewHolder> {

    private Context mContext;
    private Cursor mRecipesCursor;
    private boolean mEventAlreadySent;

    public RecipesAdaptor(Context context, Cursor cursor) {
        mContext = context;
        mRecipesCursor = cursor;
    }

    /**
     * Swap the cursor used by the adaptor.
     *
     * @param cursor
     */
    public void swapCursor(Cursor cursor) {
        //in an event of an item removed from the list (which can happen in TwoPane mode, when
        // a recipe is removed from the favorite list), reset the selected item to the first item.
        if (mRecipesCursor != null && cursor != null
                && mRecipesCursor.getCount() != cursor.getCount()) {
            mEventAlreadySent = false;
        }
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
            // set the recipe name
            String recipeName = getRecipeName();
            holder.recipeName.setText(recipeName);

            // set the recipe servings number
            holder.serves.setText(String.format(Locale.getDefault(), Integer.toString(getServings())));

            //check if there an image url, if one exists then set the image for the recipe.
            String imageUrl = getRecipeImageUrlString();
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.with(mContext)
                        .load(Uri.parse(imageUrl))
                        .placeholder(R.drawable.ic_cake_loading)
                        .error(R.drawable.ic_error_pink)
                        .into(holder.imageView);
            } else {
                holder.imageView.setVisibility(View.GONE);
            }

            /* set the RecipeID as a tag so it can be passed to the onRecipeItemClicked Listener */
            int recipeId = getRecipeId();

            /* set the bundle with recipeId */
            Bundle bundle = new Bundle();
            bundle.putInt(BakeliciousUtils.RECIPE_ID_BUNDLE_KEY, recipeId);
            bundle.putInt(BakeliciousUtils.RECIPE_FAVORITE_BUNDLE_KEY, getRecipeFavorite());
            bundle.putString(BakeliciousUtils.RECIPE_NAME_BUNDLE_KEY, recipeName);
            bundle.putString(BakeliciousUtils.RECIPE_INGREDIENTS_BUNDLE_KEY, getRecipeIngredients());
            bundle.putString(BakeliciousUtils.RECIPE_INSTRUCTIONS_BUNDLE_KEY, getRecipeInstructions());
            holder.itemView.setTag(bundle);

            /* trigger an event to pass the recipeId of the first item in the list,
             * which is used in the TwoPane mode for tablets. This event should ONLY be triggered once!
             */
            if (!mEventAlreadySent && position == 0) {
                EventBus.getDefault().post(new Message.EventRecipeDataLoaded(bundle));
                mEventAlreadySent = true;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mRecipesCursor == null) return 0;

        int count = mRecipesCursor.getCount();
        if (count == 0 && !mEventAlreadySent) {
            EventBus.getDefault().post(new Message.EventRecipesAdaptorEmpty());
            mEventAlreadySent = true;
        }
        return count;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mRecipesCursor.close();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    private String getRecipeName() {
        return mRecipesCursor.getString(
                getColumnIndex(RecipeContract.COLUMN_RECIPE_NAME));
    }

    private int getRecipeId() {
        return mRecipesCursor.getInt(
                getColumnIndex(RecipeContract.COLUMN_RECIPE_ID));

    }

    private int getServings() {
        return mRecipesCursor.getInt(
                getColumnIndex(RecipeContract.COLUMN_RECIPE_SERVES));
    }

    private int getRecipeFavorite() {
        return mRecipesCursor.getInt(
                getColumnIndex(RecipeContract.COLUMN_RECIPE_FAVORITE));
    }

    private String getRecipeIngredients() {
        return mRecipesCursor.getString(
                getColumnIndex(RecipeContract.COLUMN_RECIPE_INGREDIENTS));
    }

    private String getRecipeInstructions() {
        return mRecipesCursor.getString(
                getColumnIndex(RecipeContract.COLUMN_RECIPE_INSTRUCTIONS));
    }

    private String getRecipeImageUrlString() {
        return mRecipesCursor.getString(
                getColumnIndex(RecipeContract.COLUMN_RECIPE_IMAGE_URL));
    }

    /**
     * Helper to return column index for the specified column name from the mRecipesCursor.
     *
     * @param columnName Name of the column the index we seek.
     * @return Column Index.
     */
    private int getColumnIndex(String columnName) {
        return mRecipesCursor.getColumnIndex(columnName);
    }

    /**
     * ViewHolder for RecipesAdaptor.
     */
    class RecipesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView recipeName;
        public TextView serves;
        public ImageView imageView;

        public RecipesViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.tv_recipe_name);
            serves = itemView.findViewById(R.id.tv_serves);
            imageView = itemView.findViewById(R.id.iv_recipe);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            EventBus.getDefault().post(new Message.EventRecipeItemClicked((Bundle) view.getTag()));
        }
    }
}
