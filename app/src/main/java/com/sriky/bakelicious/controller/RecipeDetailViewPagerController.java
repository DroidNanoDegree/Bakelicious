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

package com.sriky.bakelicious.controller;

import android.support.v4.view.ViewPager;

import timber.log.Timber;

/**
 * The controller class to handle {@link android.support.v4.view.ViewPager.OnPageChangeListener}
 */

public class RecipeDetailViewPagerController implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;

    public RecipeDetailViewPagerController(ViewPager viewPager) {
        mViewPager = viewPager;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Timber.d("onTabSelected: tab.position: % d", position);
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
