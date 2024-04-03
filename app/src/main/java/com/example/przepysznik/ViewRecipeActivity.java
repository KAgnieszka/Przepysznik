package com.example.przepysznik;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewRecipeActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String recipeId;
    private ImageView imageView;
    private TextView averageRatingTextView; // TextView do wyświetlania średniej oceny przepisu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recipe);
        imageView = findViewById(R.id.recipeImageView);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("recipes");

        ImageView recipeImageView = findViewById(R.id.recipeImageView);
        TextView recipeNameTextView = findViewById(R.id.recipeNameTextView);
        TextView recipeIngredientsTextView = findViewById(R.id.recipeIngredientsTextView);
        TextView recipeInstructionsTextView = findViewById(R.id.recipeInstructionsTextView);
        Button rateRecipeButton = findViewById(R.id.rateRecipeButton);
        averageRatingTextView = findViewById(R.id.averageRatingTextView); // Inicjalizacja TextView do wyświetlania średniej oceny

        // Pobieranie ID przepisu przekazanego z poprzedniego activity
        recipeId = getIntent().getStringExtra("recipeId");

        // Pobieranie informacji o przepisie z bazy danych
        mDatabase.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                if (recipe != null) {
                    recipeNameTextView.setText(recipe.getRecipeName());
                    recipeIngredientsTextView.setText(recipe.getIngredients());
                    recipeInstructionsTextView.setText(recipe.getInstructions());

                    // Pobieranie i wyświetlanie zdjęcia
                    foodPhoto(recipe.getPhotoUrl());

                    // Obliczanie i wyświetlanie średniej oceny przepisu
                    float averageRating = recipe.calculateAverageRating();
                    averageRatingTextView.setText(String.format("Średnia ocena: %.1f", averageRating));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewRecipeActivity.this, "Błąd pobierania przepisu", Toast.LENGTH_SHORT).show();
            }
        });

        rateRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateRecipe();
            }
        });
    }

    private void rateRecipe() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Dodajemy dialog wyboru oceny
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewRecipeActivity.this);
            builder.setTitle("Oceń przepis");

            // Lista dostępnych ocen
            CharSequence[] ratings = new CharSequence[]{"1 gwiazdka", "2 gwiazdki", "3 gwiazdki", "4 gwiazdki", "5 gwiazdek"};
            builder.setItems(ratings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Zapisujemy ocenę w bazie danych
                    int ratingValue = which + 1; // Indeksowanie od 0, dlatego dodajemy 1
                    saveRating(currentUser.getUid(), ratingValue);
                }
            });

            builder.show();
        } else {
            Toast.makeText(ViewRecipeActivity.this, "Aby ocenić przepis, musisz być zalogowany", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveRating(String userId, int rating) {
        DatabaseReference userRatingRef = mDatabase.child(recipeId).child("userRatings").child(userId);
        userRatingRef.setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ViewRecipeActivity.this, "Dziękujemy za ocenę przepisu!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewRecipeActivity.this, "Błąd podczas zapisywania oceny", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void foodPhoto(String photoUrl) {
        Picasso.get().load(photoUrl).into(imageView);
    }
}
