package com.sriky.bakelicious.model;

import android.content.ContentValues;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orhanobut.logger.Logger;
import com.sriky.bakelicious.provider.InstructionContract;

public class Step {

    @SerializedName("id")
    @Expose
    private int id = -1;
    @SerializedName("shortDescription")
    @Expose
    private String shortDescription;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("videoURL")
    @Expose
    private String videoURL;
    @SerializedName("thumbnailURL")
    @Expose
    private String thumbnailURL;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    /**
     * Validates fields and generates {@link ContentValues} for Instruction table.
     *
     * @param recipeId The Recipe ID to associate the instruction to.
     * @return {@link ContentValues} for Instruction table.
     */
    public ContentValues getContentValues(int recipeId) {
        if (recipeId < 0 || recipeId >= Integer.MAX_VALUE) {
            Logger.e("Invalid RecipeId detected! "
                    + Log.getStackTraceString(new Exception()));
            return null;
        }

        if (id < 0 || id >= Integer.MAX_VALUE) {
            Logger.e("Invalid step number for instruction with RecipeId: " + recipeId + " "
                    + Log.getStackTraceString(new Exception()));
            return null;
        }

        if (shortDescription == null || shortDescription.isEmpty()) {
            Logger.e("Invalid shortDescription for instruction with RecipeId: " + recipeId
                    + " and step number: " + id + Log.getStackTraceString(new Exception()));
            return null;
        }

        if (description == null || description.isEmpty()) {
            Logger.e("Invalid Description for instruction with RecipeId: " + recipeId
                    + " and step number: " + id + Log.getStackTraceString(new Exception()));
            return null;
        }

        ContentValues cv = new ContentValues();
        cv.put(InstructionContract.COLUMN_RECIPE_ID, recipeId);
        cv.put(InstructionContract.COLUMN_INSTRUCTION_NUMBER, id);
        cv.put(InstructionContract.COLUMN_INSTRUCTION_SHORT, shortDescription);
        cv.put(InstructionContract.COLUMN_INSTRUCTION_LONG, description);
        cv.put(InstructionContract.COLUMN_INSTRUCTION_IMAGE_URL, thumbnailURL);
        cv.put(InstructionContract.COLUMN_INSTRUCTION_VIDEO_URL, videoURL);
        return cv;
    }
}
