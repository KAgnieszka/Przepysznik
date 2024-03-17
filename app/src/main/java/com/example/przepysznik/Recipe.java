package com.example.przepysznik;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recipe {
    private String recipeId;
    private String userId;
    private String recipeName;
    private String ingredients;
    private String instructions;
    private float averageRating;
    private List<String> userRatings;
    private boolean isShared;
    private String imageURL;

    public Recipe() {
        // Pusty konstruktor wymagany dla Firebase
    }

    public Recipe(String recipeId, String userId, String recipeName, String ingredients, String instructions) {
        this.recipeId = recipeId;
        this.userId = userId;
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.averageRating = 0.0f; // Domyślna średnia ocena
        this.userRatings = new ArrayList<>();
        this.isShared = false; // Domyślnie przepis nie jest udostępniony
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public List<String> getUserRatings() {
        return userRatings;
    }

    public void setUserRatings(List<String> userRatings) {
        this.userRatings = userRatings;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }

    // Metoda do mapowania obiektu na format zrozumiały dla Firebase
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("recipeId", recipeId);
        map.put("userId", userId);
        map.put("recipeName", recipeName);
        map.put("ingredients", ingredients);
        map.put("instructions", instructions);
        map.put("averageRating", averageRating);
        map.put("userRatings", userRatings);
        map.put("isShared", isShared);
        return map;
    }
}
