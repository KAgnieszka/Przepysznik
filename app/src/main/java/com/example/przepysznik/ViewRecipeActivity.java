package com.example.przepysznik;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ViewRecipeActivity extends AppCompatActivity {

    private ImageView recipeImageView;
    private TextView recipeNameTextView;
    private TextView ingredientsTextView;
    private TextView instructionsTextView;
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
        ratingBar = findViewById(R.id.ratingBar);
        rateButton = findViewById(R.id.rateButton);

        // Pobieranie danych przepisu z poprzedniej aktywności
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("recipe")) {
            recipe = (Recipe) bundle.getSerializable("recipe");
            if (recipe != null) {
                // Ustawienie nazwy przepisu
                recipeNameTextView.setText(recipe.getRecipeName());

                // Wyświetlenie składników
                ingredientsTextView.setText(recipe.getIngredients());

                // Wyświetlenie instrukcji
                instructionsTextView.setText(recipe.getInstructions());

                // Obsługa oceniania przepisu
                handleRating();
            }
        }
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

            // Wyświetlenie komunikatu potwierdzającego ocenę
            Toast.makeText(ViewRecipeActivity.this, "Przepis oceniony", Toast.LENGTH_SHORT).show();
        }
    }
}
