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

package com.sriky.bakelicious.sync;

import androidx.annotation.Nullable;

import com.sriky.bakelicious.model.Recipe;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import timber.log.Timber;

/**
 * Class to handle API calls using Retrofit client.
 */

public final class BakeliciousRetrofitClient {

    /* URL root */
    private static final String URL_ROOT =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    private static Retrofit sRetrofitInstance = null;

    /**
     * Gets the list of recipes from the backend.
     *
     * @return List of recipe objects
     */
    @Nullable
    public static List<Recipe> getRecipes() {
        try {
            Response<List<Recipe>> response = getApiService().getJson().execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Timber.e(response.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get Retrofit Instance
     */
    private static Retrofit getRetrofitInstance() {
        if (sRetrofitInstance == null) {
            sRetrofitInstance = new Retrofit.Builder()
                    .baseUrl(URL_ROOT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sRetrofitInstance;
    }

    /**
     * Get API Service
     *
     * @return API Service
     */
    private static BakeliciousApiService getApiService() {
        return getRetrofitInstance().create(BakeliciousApiService.class);
    }


    /**
     * Interface to the bakelicious REST API
     */
    private interface BakeliciousApiService {

        @GET("baking.json")
        Call<List<Recipe>> getJson();
    }
}
