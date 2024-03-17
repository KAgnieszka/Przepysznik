package com.example.przepysznik;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ViewRecipeActivity extends AppCompatActivity {

    private ImageView recipeImageView;
    private TextView recipeNameTextView;
    private TextView ingredientsTextView;
    private TextView instructionsTextView;
    private TextView ratingInfoTextView;
    private RatingBar ratingBar;
    private Button rateButton;

    private Recipe recipe;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recipe);

        // Inicjalizacja elementów interfejsu użytkownika
        recipeImageView = findViewById(R.id.recipeImageView);
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);
        instructionsTextView = findViewById(R.id.instructionsTextView);
        ratingInfoTextView = findViewById(R.id.ratingInfoTextView);
        ratingBar = findViewById(R.id.ratingBar);
        rateButton = findViewById(R.id.rateButton);

        // Pobieranie danych przepisu z bazy danych Firebase
        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference().child("recipes").child(recipe.getRecipeId());
        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipe = dataSnapshot.getValue(Recipe.class);
                if (recipe != null) {
                    // Ustawienie nazwy przepisu
                    recipeNameTextView.setText(recipe.getRecipeName());

                    // Wyświetlenie składników
                    ingredientsTextView.setText(recipe.getIngredients());

                    // Wyświetlenie instrukcji
                    instructionsTextView.setText(recipe.getInstructions());

                    // Wyświetlenie informacji o ocenie
                    updateRatingInfo();

                    // Obsługa oceniania przepisu
                    handleRating();
                } else {
                    Toast.makeText(ViewRecipeActivity.this, "Nie udało się pobrać danych przepisu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewRecipeActivity.this, "Błąd podczas pobierania danych przepisu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRatingInfo() {
        float averageRating = recipe.getAverageRating();
        int numberOfRatings = recipe.getUserRatings().size();
        String ratingInfo = String.format("Średnia ocena: %.1f (ocenione przez %d użytkowników)", averageRating, numberOfRatings);
        ratingInfoTextView.setText(ratingInfo);
    }

    private void handleRating() {
        // Ustawienie obecnej oceny przepisu na RatingBar
        ratingBar.setRating(recipe.getAverageRating());

        // Obsługa kliknięcia przycisku oceny
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateRecipe();
            }
        });
    }

    private void rateRecipe() {
        // Pobranie obecnej oceny przepisu
        float currentRating = ratingBar.getRating();

        // Pobranie identyfikatora użytkownika
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Aktualizacja oceny przepisu w bazie danych
            DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference().child("recipes").child(recipe.getRecipeId());
            recipeRef.child("averageRating").setValue(currentRating);

            // Dodanie oceny użytkownika do listy ocen przepisu
            recipeRef.child("userRatings").child(userId).setValue(currentRating);

            // Aktualizacja informacji o ocenie
            updateRatingInfo();

            // Wyświetlenie komunikatu potwierdzającego ocenę
            Toast.makeText(ViewRecipeActivity.this, "Przepis oceniony", Toast.LENGTH_SHORT).show();
        }
    }
}
