package com.sriky.bakelicious.model;

import android.content.ContentValues;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sriky.bakelicious.provider.IngredientContract;

import timber.log.Timber;

public class Ingredient {

    @SerializedName("quantity")
    @Expose
    private float quantity;
    @SerializedName("measure")
    @Expose
    private String measure;
    @SerializedName("ingredient")
    @Expose
    private String ingredient;

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    /**
     * Validates fields and generates {@link ContentValues} for Ingredient table.
     *
     * @param recipeId The Recipe ID to associate the ingredient to.
     * @return {@link ContentValues} for Ingredient table.
     */
    public ContentValues getContentValues(int recipeId) {
        if (recipeId < 0 || recipeId >= Integer.MAX_VALUE) {
            Timber.e("Invalid RecipeId detected! %s", Log.getStackTraceString(new Exception()));
            return null;
        }

        if (ingredient == null || ingredient.isEmpty()) {
            Timber.e("Invalid ingredient name for ingredient with RecipeId: %d %s",
                    recipeId, Log.getStackTraceString(new Exception()));
            return null;
        }

        if (measure == null || measure.isEmpty()) {
            Timber.e("Invalid measure attribute for ingredient with RecipeId: %d %s",
                    recipeId, Log.getStackTraceString(new Exception()));
            return null;
        }

        if (quantity < 0 || quantity >= Float.MAX_VALUE) {
            Timber.e("Invalid units for ingredient with RecipeId: %d %s",
                    recipeId, Log.getStackTraceString(new Exception()));
            return null;
        }

        ContentValues cv = new ContentValues();
        cv.put(IngredientContract.COLUMN_RECIPE_ID, recipeId);
        cv.put(IngredientContract.COLUMN_INGREDIENT_NAME, ingredient);
        cv.put(IngredientContract.COLUMN_INGREDIENT_MEASURE, measure);
        cv.put(IngredientContract.COLUMN_INGREDIENT_QUANTITY, quantity);
        return cv;
    }
}
