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

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.sriky.bakelicious.R;
import com.sriky.bakelicious.databinding.FragmentRecipeInstructionBinding;

import timber.log.Timber;

/**
 * The fragment for an Recipe Instruction.
 */

public class RecipeInstructionFragment extends Fragment implements ExoPlayer.EventListener {

    public static final String INSTRUCTION_SHORT_DESCRIPTION_BUNDLE_KEY = "instruction_short_desc";
    public static final String INSTRUCTION_DESCRIPTION_BUNDLE_KEY = "instruction_desc";
    public static final String INSTRUCTION_VIDEO_URL_BUNDLE_KEY = "instruction_video_url";
    public static final String INSTRUCTION_THUMBNAIL_URL_BUNDLE_KEY = "instruction_thumbnail_url";

    private FragmentRecipeInstructionBinding mFragmentRecipeInstructionBinding;
    private SimpleExoPlayer mExoPlayer;
    private boolean mIsPlayerSetup;
    private String mShortDesc;

    public RecipeInstructionFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentRecipeInstructionBinding = FragmentRecipeInstructionBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        if (bundle == null) throw new RuntimeException("Bundle is empty!");

        mShortDesc = bundle.getString(INSTRUCTION_SHORT_DESCRIPTION_BUNDLE_KEY);
        String desc = bundle.getString(INSTRUCTION_DESCRIPTION_BUNDLE_KEY);
        String videoUrl = bundle.getString(INSTRUCTION_VIDEO_URL_BUNDLE_KEY);
        String thumbUrl = bundle.getString(INSTRUCTION_THUMBNAIL_URL_BUNDLE_KEY);

        if (mShortDesc == null || mShortDesc.isEmpty()) {
            throw new RuntimeException("ShortDesc not set to bundle!");
        }

        if (desc == null || desc.isEmpty()) {
            throw new RuntimeException("Recipe description not set to bundle!");
        }

        Timber.d("shortDesc: %s", mShortDesc);

        //set the video, if url exists.
        if (videoUrl != null && !videoUrl.isEmpty()) {

            Timber.d("VideoUrl: %s", videoUrl);
            initializePlayer(Uri.parse(videoUrl));

        } else if (thumbUrl != null && !thumbUrl.isEmpty()) {

            Picasso.with(getContext())
                    .load(Uri.parse(thumbUrl))
                    //TODO: add place holder art!
                    //.placeholder(R.drawable.loading)
                    //.error(R.drawable.error)
                    .into(mFragmentRecipeInstructionBinding.ivInstruction);
            mFragmentRecipeInstructionBinding.ivInstruction.setVisibility(View.VISIBLE);

        } else {
            mFragmentRecipeInstructionBinding.playerView.setVisibility(View.GONE);
            mFragmentRecipeInstructionBinding.ivInstruction.setVisibility(View.GONE);
        }

        //set the desc.
        String regexMatchingNonWordChars = "^\\d+\\W+";
        mFragmentRecipeInstructionBinding.tvInstruction.setText(desc.replaceAll(regexMatchingNonWordChars, ""));

        return mFragmentRecipeInstructionBinding.getRoot();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mIsPlayerSetup) {
            if (isVisibleToUser) {
                Timber.d("Playing video playback %s", mShortDesc);
                mExoPlayer.setPlayWhenReady(true);
            } else {
                Timber.d("Stopping video playback %s", mShortDesc);
                mExoPlayer.setPlayWhenReady(false);
            }
        }
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mFragmentRecipeInstructionBinding.playerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(RecipeInstructionFragment.this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
            mIsPlayerSetup = true;
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }
}
