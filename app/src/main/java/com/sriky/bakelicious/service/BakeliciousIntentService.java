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

package com.sriky.bakelicious.service;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;

import com.sriky.bakelicious.sync.BakeliciousSyncTask;

/**
 * Background service used to fetch data from API.
 */

public class BakeliciousIntentService extends IntentService {

    public BakeliciousIntentService() {
        super(BakeliciousIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        BakeliciousSyncTask.fetchRecipes(this);
    }
}
