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

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sriky.bakelicious.databinding.IngredientHeaderItemBinding;
import com.sriky.bakelicious.databinding.IngredientListItemBinding;
import com.sriky.bakelicious.model.Ingredient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * The Adaptor for the {@link com.sriky.bakelicious.ui.RecipeIngredientsFragment}'s
 * {@link RecyclerView}
 */

public class IngredientsAdaptor extends RecyclerView.Adapter<IngredientsAdaptor.IngredientsViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_LIST_ITEM = 1;
    private List<Ingredient> mIngredients;
    private IngredientListItemBinding mIngredientListItemBinding;
    private IngredientHeaderItemBinding mIngredientHeaderItemBinding;

    public IngredientsAdaptor(String ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            throw new RuntimeException("Ingredients string empty!");
        }

        // load ingredients
        Gson gson = new Gson();

        Type listType = new TypeToken<ArrayList<Ingredient>>() {
        }.getType();
        List<Ingredient> ingredientList = gson.fromJson(ingredients, listType);

        Timber.d("%d ingredients loaded!", ingredientList.size());
        mIngredients = ingredientList;
    }

    @Override
    public IngredientsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_HEADER: {
                mIngredientHeaderItemBinding =
                        IngredientHeaderItemBinding.inflate(layoutInflater, parent, false);

                return new IngredientsViewHolder(mIngredientHeaderItemBinding.getRoot());
            }

            case VIEW_TYPE_LIST_ITEM: {
                mIngredientListItemBinding =
                        IngredientListItemBinding.inflate(layoutInflater, parent, false);

                return new IngredientsViewHolder(mIngredientListItemBinding.getRoot());
            }

            default: {
                throw new RuntimeException("Unsupported viewType for id:" + viewType);
            }
        }
    }

    @Override
    public void onBindViewHolder(IngredientsViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case VIEW_TYPE_HEADER: {
                break;
            }

            case VIEW_TYPE_LIST_ITEM: {
                Ingredient ingredient = mIngredients.get(position);

                //TODO ally support
                mIngredientListItemBinding.tvIngredient.setText(ingredient.getIngredient().toUpperCase());
                mIngredientListItemBinding.tvIngredient.refreshDrawableState();

                //TODO ally support
                mIngredientListItemBinding.tvMeasure.setText(ingredient.getMeasure().toLowerCase());

                //TODO ally support
                mIngredientListItemBinding.tvQuantity.setText(
                        String.format(Locale.getDefault(),
                                "%s", Float.toString(ingredient.getQuantity())));
                break;
            }

            default: {
                throw new RuntimeException("Unsupported viewType!");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mIngredients == null ? 0 : mIngredients.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_LIST_ITEM;
    }

    public class IngredientsViewHolder extends RecyclerView.ViewHolder {

        public IngredientsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
