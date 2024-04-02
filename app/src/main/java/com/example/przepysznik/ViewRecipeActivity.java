package com.example.przepysznik;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.przepysznik.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ViewRecipeActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String recipeId;
    private ImageView imageView;

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
            Toast.makeText(ViewRecipeActivity.this, "Oceniasz przepis...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ViewRecipeActivity.this, "Aby ocenić przepis, musisz być zalogowany", Toast.LENGTH_SHORT).show();
        }
    }

    private void foodPhoto(String photoUrl) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl);

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri.toString()).into(imageView);
        }).addOnFailureListener(exception -> {
            // Obsłuż błąd pobierania zdjęcia
            Toast.makeText(ViewRecipeActivity.this, "Błąd pobierania zdjęcia", Toast.LENGTH_SHORT).show();
        });
    }
}
