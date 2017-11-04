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

package com.sriky.bakelicious.provider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Using the Schematic (https://github.com/SimonVT/schematic) library to define
 * the Instruction table columns in a content provider baked by a database
 */

public interface InstructionContract {

    @DataType(INTEGER)
    @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    @AutoIncrement
    String _ID = "_id";

    @DataType(INTEGER)
    @NotNull
    String COLUMN_RECIPE_ID = "recipe_id";

    @DataType(INTEGER)
    @NotNull
    String COLUMN_INSTRUCTION_NUMBER = "instruction_number";

    @DataType(TEXT)
    @NotNull
    String COLUMN_INSTRUCTION_SHORT = "instruction_short";

    @DataType(TEXT)
    @NotNull
    String COLUMN_INSTRUCTION_LONG = "instruction_long";

    @DataType(TEXT)
    String COLUMN_INSTRUCTION_VIDEO_URL = "instruction_video_url";

    @DataType(TEXT)
    String COLUMN_INSTRUCTION_IMAGE_URL = "instruction_image_url";
}
