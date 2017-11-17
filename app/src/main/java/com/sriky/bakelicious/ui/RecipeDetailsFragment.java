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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sriky.bakelicious.R;
import com.sriky.bakelicious.adaptor.RecipeDetailPagerAdaptor;
import com.sriky.bakelicious.databinding.FragmentRecipeDetailsBinding;
import com.sriky.bakelicious.event.Message;
import com.sriky.bakelicious.utils.BakeliciousUtils;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

/**
 * Recipe Details Fragment.
 */

public class RecipeDetailsFragment extends Fragment
        implements ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener {

    private FragmentRecipeDetailsBinding mFragmentRecipeDetailsBinding;
    private int mRecipeId;
    private boolean mRecipeFavorite;
    private String mRecipeName;

    public RecipeDetailsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentRecipeDetailsBinding =
                FragmentRecipeDetailsBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        if (bundle == null) throw new RuntimeException("RecipeInfo bundle is null!");

        mRecipeId = BakeliciousUtils.validateBundleAndGetRecipeId(bundle,
                RecipeDetailsFragment.class.getSimpleName());

        mRecipeName = bundle.getString(BakeliciousUtils.RECIPE_NAME_BUNDLE_KEY);
        mRecipeFavorite = bundle.getInt(BakeliciousUtils.RECIPE_FAVORITE_BUNDLE_KEY) > 0;

        Timber.d("onCreateView(), Recipe Id: %d, Name: %s, Favorite: %b",
                mRecipeId, mRecipeName, mRecipeFavorite);

        // initialize and setup the RecipeDetails and set it to the ViewPager.
        RecipeDetailPagerAdaptor recipeDetailPagerAdaptor =
                new RecipeDetailPagerAdaptor(getChildFragmentManager(),
                        bundle, BakeliciousUtils.RECIPE_DETAILS_TAB_COUNT);

        mFragmentRecipeDetailsBinding.vpRecipeDetails.setAdapter(recipeDetailPagerAdaptor);
        mFragmentRecipeDetailsBinding.vpRecipeDetails.setOffscreenPageLimit(BakeliciousUtils.RECIPE_DETAILS_TAB_COUNT);

        // add the listeners to the ViewPager and TabLayout.
        mFragmentRecipeDetailsBinding.vpRecipeDetails.addOnPageChangeListener(RecipeDetailsFragment.this);
        mFragmentRecipeDetailsBinding.tlRecipeDetails.addOnTabSelectedListener(RecipeDetailsFragment.this);
        mFragmentRecipeDetailsBinding.vpRecipeDetails.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(mFragmentRecipeDetailsBinding.tlRecipeDetails));

        setHasOptionsMenu(true);

        return mFragmentRecipeDetailsBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.recipe_detail_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
         /* set the icon to indicate whether the movie has been favorited by the user or not */
        if (mRecipeFavorite) {
            MenuItem favorite = menu.findItem(R.id.action_favorite);
            if (favorite != null) {
                favorite.setIcon(R.drawable.ic_favorite_black);
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_favorite: {
                mRecipeFavorite = !mRecipeFavorite;
                if (mRecipeFavorite) {
                    item.setIcon(R.drawable.ic_favorite_black);
                } else {
                    item.setIcon(R.drawable.ic_favorite_border_black);
                }

                //update the record.
                BakeliciousUtils.updateFavoriteRecipe(getContext(),
                        mRecipeId, mRecipeName, mRecipeFavorite);
                break;
            }

            default: {
                Timber.e("Unsupported action detected: %s",
                        new RuntimeException().getStackTrace().toString());
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        //Timber.d("onPageSelected: position: % d", position);
        mFragmentRecipeDetailsBinding.vpRecipeDetails.reMeasureCurrentPage(
                mFragmentRecipeDetailsBinding.vpRecipeDetails.getCurrentItem());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        //Timber.d("onTabSelected: tab.position: %d", position);
        
        //broadcast an event.
        EventBus.getDefault().post(new Message.EventRecipeDetailsTabChanged());
        mFragmentRecipeDetailsBinding.vpRecipeDetails.setCurrentItem(position);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
}
