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

package com.sriky.bakelicious.event;

import android.os.Bundle;

import com.sriky.bakelicious.ui.RecipeInstructionsFragment;

/**
 * Events used by Bakelicious.
 */

public final class Message {

    /**
     * Event Class for sending and receiving events when
     * {@link com.sriky.bakelicious.adaptor.RecipesAdaptor.RecipesViewHolder} are clicked, which is
     * used in the {@link com.sriky.bakelicious.ui.MasterListFragment} to display the list of Recipes.
     */
    public static class EventRecipeItemClicked {
        private Bundle mBundle;

        public EventRecipeItemClicked(Bundle bundle) {
            mBundle = bundle;
        }

        public Bundle getBundle() {
            return mBundle;
        }
    }

    /**
     * Event Class for sending and receiving updates when recipes data is loaded successfully into
     * the {@link com.sriky.bakelicious.ui.MasterListFragment}. This event is used required by
     * {@link com.sriky.bakelicious.ui.BakeliciousActivity} to setup
     * {@link RecipeInstructionsFragment} during TwoPane mode(on tables).
     */
    public static class EventRecipeDataLoaded {
        private Bundle mBundle;

        public EventRecipeDataLoaded(Bundle bundle) {
            mBundle = bundle;
        }

        public Bundle getBundle() {
            return mBundle;
        }
    }

    /**
     * Event Class for sending updates when Recipes Adaptor's is empty. This event is
     * required in the TwoPane mode to maintain sync between the master and detail fragments and
     * managing the layouts.
     */
    public static class EventRecipesAdaptorEmpty {
        public EventRecipesAdaptorEmpty() {
        }
    }

    /**
     * Event Class used for sending events when user toggles between the instruction and ingredients
     * tabs. This event is required to pause videos that would continue to play in the background.
     */
    public static class EventRecipeDetailsTabChanged {
        public EventRecipeDetailsTabChanged() {
        }
    }
}
