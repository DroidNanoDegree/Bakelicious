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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sriky.bakelicious.databinding.IngredientListItemBinding;
import com.sriky.bakelicious.model.Ingredient;

import java.util.List;

/**
 * The Adaptor for the {@link com.sriky.bakelicious.ui.RecipeIngredientsFragment}'s
 * {@link android.support.v7.widget.RecyclerView}
 */

public class IngredientsAdaptor extends RecyclerView.Adapter<IngredientsAdaptor.IngredientsViewHolder> {

    private List<Ingredient> mIngredients;
    private IngredientListItemBinding mIngredientListItemBinding;

    public IngredientsAdaptor(List<Ingredient> ingredients) {
        mIngredients = ingredients;
    }

    public void updateIngredients(List<Ingredient> ingredients) {
        mIngredients = ingredients;
        notifyDataSetChanged();
    }

    @Override
    public IngredientsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        mIngredientListItemBinding =
                IngredientListItemBinding.inflate(layoutInflater, parent, false);

        return new IngredientsViewHolder(mIngredientListItemBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(IngredientsViewHolder holder, int position) {
        Ingredient ingredient = mIngredients.get(position);

        //TODO ally support
        mIngredientListItemBinding.tvIngredient.setText(ingredient.getIngredient());

        //TODO ally support
        mIngredientListItemBinding.tvMeasure.setText(ingredient.getMeasure());

        //TODO ally support
        mIngredientListItemBinding.tvQuantity.setText(Float.toString(ingredient.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return mIngredients == null ? 0 : mIngredients.size();
    }

    public class IngredientsViewHolder extends RecyclerView.ViewHolder {

        public IngredientsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
