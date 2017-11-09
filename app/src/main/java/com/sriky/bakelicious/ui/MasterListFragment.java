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

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.sriky.bakelicious.adaptor.RecipesAdaptor;
import com.sriky.bakelicious.databinding.FragmentMasterListBinding;
import com.sriky.bakelicious.provider.BakeliciousContentProvider;
import com.sriky.bakelicious.utils.BakeliciousUtils;
import com.sriky.bakelicious.utils.LoggerUtils;

/**
 * Fragment class responsible for displaying the recipes.
 */

public class MasterListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String SELECTION_BUNDLE_KEY = "selection";
    public static final String SELECTION_ARGS_BUNDLE_KEY = "selection_args";
    /* the amount time to wait prior to displaying an error message if data isn't loader by the
    * time(millis) specified here. */
    private static final long DATA_LOAD_TIMEOUT_LIMIT = 30000;
    private static final long COUNT_DOWN_INTERVAL = 1000;
    private RecipesAdaptor mRecipesAdaptor;
    private FragmentMasterListBinding mMasterListBinding;
    private RecipesAdaptor.OnRecipeItemClickedListener mOnRecipeItemClickedListener;
    /* RecyclerView position */
    private int mPosition = RecyclerView.NO_POSITION;
    /* CountDownTimer used to issue a timeout when data doesn't load within the specified time. */
    private CountDownTimer mDataFetchTimer;

    /* mandatory empty constructor */
    public MasterListFragment() {
        LoggerUtils.initLogger(MasterListFragment.class.getSimpleName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mMasterListBinding = FragmentMasterListBinding.inflate(inflater, container, false);

        showProgressBarAndHideErrorMessage();

        /* setup the recipes RecyclerView */
        mRecipesAdaptor = new RecipesAdaptor(null, mOnRecipeItemClickedListener);
        mMasterListBinding.rvRecipes.setAdapter(mRecipesAdaptor);

        mMasterListBinding.rvRecipes.setHasFixedSize(true);

        mMasterListBinding.rvRecipes.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        /* trigger the loader to get data from local db */
        getLoaderManager().initLoader(BakeliciousUtils.MASTER_LIST_FRAGMENT_LOADER_ID,
                getArguments(),
                MasterListFragment.this);

        return mMasterListBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the mOnRecipeItemClickedListener
        // If not, it throws an exception
        try {
            mOnRecipeItemClickedListener = (RecipesAdaptor.OnRecipeItemClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement mOnRecipeItemClickedListener!");
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case BakeliciousUtils.MASTER_LIST_FRAGMENT_LOADER_ID: {
                String selection = null;
                String[] selectionArgs = null;
                if (args != null) {
                    //TODO check for favorite and set the favorite fragment flag.
                    selection = args.getString(SELECTION_BUNDLE_KEY);
                    selectionArgs = args.getStringArray(SELECTION_ARGS_BUNDLE_KEY);
                }

                return new CursorLoader(getContext(),
                        BakeliciousContentProvider.RecipeEntry.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        null);
            }

            default: {
                throw new RuntimeException("Unsupported loaderId: " + id);
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Logger.d("onLoadFinished: data.length() = " + data.getCount());

        mRecipesAdaptor.swapCursor(data);

        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;

        mMasterListBinding.rvRecipes.smoothScrollToPosition(mPosition);

        if (data.getCount() > 0) {
            onDataLoadComplete();
            //TODO else check if it is favorites and display no favorites yet msg!
        } else {
            /* if the timer was running then cancel it. */
            cancelDataFetchTimer();
            /* will there is no data set up a countdown to display an error message in case
             * the data doesn't load in the specified time.
             */
            mDataFetchTimer = new CountDownTimer(DATA_LOAD_TIMEOUT_LIMIT, COUNT_DOWN_INTERVAL) {

                public void onTick(long millisUntilFinished) {
                    Logger.i("waiting on data "
                            + millisUntilFinished / COUNT_DOWN_INTERVAL
                            + "secs remaining for timeout:!");
                }

                public void onFinish() {
                    onDataLoadFailed();
                }
            };
            mDataFetchTimer.start();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * If there were issues downloading data from TMDB, then hide the progress bar view and
     * display an error message to the user.
     */
    private void onDataLoadFailed() {
        Logger.d("onDataLoadFailed()");
        hideProgressBarAndShowErrorMessage();
    }

    /**
     * Displays the progress bar and hides the error message views.
     */
    private void showProgressBarAndHideErrorMessage() {
        //mPopularMoviesBinding.pbPopularMovies.setVisibility(View.VISIBLE);
        //mPopularMoviesBinding.tvErrorMsg.setVisibility(View.INVISIBLE);
    }

    /**
     * Hides the progress bar view and makes the the error message view VISIBLE.
     */
    private void hideProgressBarAndShowErrorMessage() {
        //mPopularMoviesBinding.pbPopularMovies.setVisibility(View.INVISIBLE);
        //mPopularMoviesBinding.tvErrorMsg.setVisibility(View.VISIBLE);
    }

    /**
     * On successfully downloading data from the API
     */
    private void onDataLoadComplete() {
        Logger.d("onDataLoadComplete()");
        /* hide the progress bar & the error msg view. */
        //mPopularMoviesBinding.pbPopularMovies.setVisibility(View.INVISIBLE);
        //mPopularMoviesBinding.tvErrorMsg.setVisibility(View.INVISIBLE);
        /* if the timer was running then cancel it. */
        cancelDataFetchTimer();
    }

    /**
     * If a {@link CountDownTimer} already exist, then cancel it.
     */
    private void cancelDataFetchTimer() {
        if (mDataFetchTimer != null) {
            Logger.d("cancelDataFetchTimer()");
            mDataFetchTimer.cancel();
            mDataFetchTimer = null;
        }
    }
}
