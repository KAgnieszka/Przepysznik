package com.example.przepysznik;

import java.util.HashMap;
import java.util.Map;

public class Recipe {
    private String recipeId;
    private String userId;
    private String recipeName;
    private String ingredients;
    private String instructions;
    private float averageRating;
    private Map<String, Integer> userRatings;
    private boolean isShared;
    private String photoUrl;

    private String comment;
    private String commentTime;

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
        this.userRatings = new HashMap<>();
        this.isShared = false; // Domyślnie przepis nie jest udostępniony
    }

    public String getRecipeId() {
        return recipeId;
    }

    // Metoda do pobierania komentarza
    public String getComment() {
        return comment;
    }

    // Metoda do ustawiania komentarza
    public void setComment(String comment) {
        this.comment = comment;
    }

    // Metoda do pobierania czasu dodania komentarza
    public String getCommentTime() {
        return commentTime;
    }

    // Metoda do ustawiania czasu dodania komentarza
    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getUserId() {
        return userId;
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

    public Map<String, Integer> getUserRatings() {
        return userRatings;
    }

    public void setUserRatings(Map<String, Integer> userRatings) {
        this.userRatings = userRatings;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    // Metoda dodająca ocenę użytkownika
    public void addUserRating(String userId, int rating) {
        userRatings.put(userId, rating);
    }

    // Metoda obliczająca średnią ocenę przepisu
    public float calculateAverageRating() {
        if (userRatings == null || userRatings.isEmpty()) {
            return 0.0f; // Jeśli brak ocen lub mapa jest nullem, zwróć 0
        }

        int totalRating = 0;
        int numRatings = 0;

        // Iteracja po mapie ocen użytkowników
        for (Map.Entry<String, Integer> entry : userRatings.entrySet()) {
            totalRating += entry.getValue(); // Dodanie oceny
            numRatings++; // Zwiększenie liczby ocen
        }

        // Obliczenie średniej oceny
        return (float) totalRating / numRatings;
    }


    // Metoda do mapowania obiektu
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
        map.put("photoUrl", photoUrl);
        return map;
    }
}
