package com.sriky.bakelicious.model;

import android.content.ContentValues;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sriky.bakelicious.provider.RecipeContract;

import java.util.List;

import timber.log.Timber;

public class Recipe {

    @SerializedName("id")
    @Expose
    private int id = -1;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("ingredients")
    @Expose
    private List<Ingredient> ingredients = null;
    @SerializedName("steps")
    @Expose
    private List<Step> steps = null;
    @SerializedName("servings")
    @Expose
    private int servings;
    @SerializedName("image")
    @Expose
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Validates fields and generates {@link ContentValues} for Recipe table.
     *
     * @return {@link ContentValues} for Recipe table.
     */
    public ContentValues getContentValues() {
        if (id < 0 || id >= Integer.MAX_VALUE) {
            Timber.e("Invalid RecipeId detected! %s", Log.getStackTraceString(new Exception()));
            return null;
        }

        if (name == null || name.isEmpty()) {
            Timber.e("Invalid recipe name for RecipeId: %d %s ",
                    id, Log.getStackTraceString(new Exception()));
            return null;
        }

        if (steps == null || steps.size() == 0) {
            Timber.e("No instruction found for RecipeId: %d %s ",
                    id, Log.getStackTraceString(new Exception()));
            return null;
        }

        if (ingredients == null || ingredients.size() == 0) {
            Timber.e("No ingredients found for RecipeId: %d %s ",
                    id, Log.getStackTraceString(new Exception()));
            return null;
        }

        Gson gson = new Gson();
        ContentValues cv = new ContentValues();
        cv.put(RecipeContract.COLUMN_RECIPE_ID, id);
        cv.put(RecipeContract.COLUMN_RECIPE_NAME, name);
        cv.put(RecipeContract.COLUMN_RECIPE_SERVES, servings);
        cv.put(RecipeContract.COLUMN_RECIPE_IMAGE_URL, image);
        cv.put(RecipeContract.COLUMN_RECIPE_INSTRUCTIONS, gson.toJson(steps));
        cv.put(RecipeContract.COLUMN_RECIPE_INGREDIENTS, gson.toJson(ingredients));
        return cv;
    }
}
