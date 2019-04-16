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

package com.sriky.bakelicious.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sriky.bakelicious.adaptor.IngredientsAdaptor;
import com.sriky.bakelicious.databinding.FragmentRecipeIngredientsBinding;
import com.sriky.bakelicious.utils.BakeliciousUtils;

/**
 * Recipe Ingredient fragment.
 */

public class RecipeIngredientsFragment extends Fragment {

    private FragmentRecipeIngredientsBinding mFragmentRecipeIngredientsBinding;
    private IngredientsAdaptor mIngredientsAdaptor;
    private int mRecipeId;

    public RecipeIngredientsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentRecipeIngredientsBinding = FragmentRecipeIngredientsBinding.inflate(inflater,
                container, false);

        Bundle args = getArguments();
        mRecipeId = BakeliciousUtils.validateBundleAndGetRecipeId(args,
                RecipeIngredientsFragment.class.getSimpleName());

        mIngredientsAdaptor =
                new IngredientsAdaptor(args.getString(BakeliciousUtils.RECIPE_INGREDIENTS_BUNDLE_KEY));
        mFragmentRecipeIngredientsBinding.rvIngredients.setAdapter(mIngredientsAdaptor);

        mFragmentRecipeIngredientsBinding.rvIngredients.setHasFixedSize(true);

        mFragmentRecipeIngredientsBinding.rvIngredients.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return mFragmentRecipeIngredientsBinding.getRoot();
    }
}
