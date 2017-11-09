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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.sriky.bakelicious.databinding.FragmentRecipeDetailBinding;
import com.sriky.bakelicious.utils.LoggerUtils;


/**
 * Recipe Detail Fragment.
 */

public class RecipeDetailFragment extends Fragment {
    public static final String RECIPE_ID_BUNDLE_KEY = "recipe_id";

    private FragmentRecipeDetailBinding mFragmentRecipeDetailBinding;
    private int mRecipeId;

    public RecipeDetailFragment() {
        LoggerUtils.initLogger(RecipeDetailFragment.class.getSimpleName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentRecipeDetailBinding = FragmentRecipeDetailBinding.inflate(inflater,
                container, false);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(RECIPE_ID_BUNDLE_KEY)) {
            mRecipeId = bundle.getInt(RECIPE_ID_BUNDLE_KEY);
            Logger.d("RecipeId:" + mRecipeId);
        } else {
            throw new RuntimeException("RecipeId not set!");
        }
        return mFragmentRecipeDetailBinding.getRoot();
    }
}
