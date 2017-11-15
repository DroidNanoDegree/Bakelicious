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

import com.sriky.bakelicious.databinding.FragmentRecipeInstructionBinding;

import timber.log.Timber;

/**
 * The fragment for an Recipe Instruction.
 */

public class RecipeInstructionFragment extends Fragment {

    public static final String INSTRUCTION_SHORT_DESCRIPTION_BUNDLE_KEY = "instruction_short_desc";
    public static final String INSTRUCTION_DESCRIPTION_BUNDLE_KEY = "instruction_desc";
    public static final String INSTRUCTION_VIDEO_URL_BUNDLE_KEY = "instruction_video_url";
    public static final String INSTRUCTION_THUMBNAIL_URL_BUNDLE_KEY = "instruction_thumbnail_url";

    private FragmentRecipeInstructionBinding mFragmentRecipeInstructionBinding;
    public RecipeInstructionFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentRecipeInstructionBinding = FragmentRecipeInstructionBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        if(bundle == null)  throw new RuntimeException("Bundle is empty!");

        String shortDesc = bundle.getString(INSTRUCTION_SHORT_DESCRIPTION_BUNDLE_KEY);
        String desc = bundle.getString(INSTRUCTION_DESCRIPTION_BUNDLE_KEY);
        String videoUrl = bundle.getString(INSTRUCTION_VIDEO_URL_BUNDLE_KEY);
        String thumbUrl = bundle.getString(INSTRUCTION_THUMBNAIL_URL_BUNDLE_KEY);

        if(shortDesc == null || shortDesc.isEmpty()) throw new RuntimeException("ShortDesc not set to bundle!");
        if(desc == null || desc.isEmpty()) throw new RuntimeException("Recipe description not set to bundle!");

        Timber.d("shortDesc: %s", shortDesc);

        return mFragmentRecipeInstructionBinding.getRoot();
    }
}
