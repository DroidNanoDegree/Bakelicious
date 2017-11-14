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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sriky.bakelicious.model.Step;
import com.sriky.bakelicious.ui.RecipeInstructionFragment;

import java.util.List;
import java.util.Locale;

/**
 * The {@link android.support.v4.view.ViewPager} Adaptor for recipe instructions.
 */

public class RecipeInstructionPagerAdaptor extends FragmentStatePagerAdapter {
    private List<Step> mInstructions;

    public RecipeInstructionPagerAdaptor(FragmentManager fm) {
        super(fm);
    }

    public void updateInstructions(List<Step> instructions) {
        mInstructions = instructions;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        Step instruction = mInstructions.get(position);

        Bundle bundle = new Bundle();
        bundle.putString(RecipeInstructionFragment.INSTRUCTION_SHORT_DESCRIPTION_BUNDLE_KEY,
                instruction.getShortDescription());
        bundle.putString(RecipeInstructionFragment.INSTRUCTION_DESCRIPTION_BUNDLE_KEY,
                instruction.getDescription());
        bundle.putString(RecipeInstructionFragment.INSTRUCTION_VIDEO_URL_BUNDLE_KEY,
                instruction.getVideoURL());
        bundle.putString(RecipeInstructionFragment.INSTRUCTION_THUMBNAIL_URL_BUNDLE_KEY,
                instruction.getThumbnailURL());

        RecipeInstructionFragment recipeInstructionFragment = new RecipeInstructionFragment();
        recipeInstructionFragment.setArguments(bundle);
        return recipeInstructionFragment;
    }

    @Override
    public int getCount() {
        return mInstructions == null ? 0 : mInstructions.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(mInstructions == null) return null;

        return String.format(Locale.getDefault(),
                "%d: %s", mInstructions.get(position).getId() + 1,
                mInstructions.get(position).getShortDescription()).toUpperCase();
    }
}
