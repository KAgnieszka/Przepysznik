package com.example.przepysznik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.Map;

public class Share_Recipe extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_recipe);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("recipes");

        TextView addRecipeButton = findViewById(R.id.addRecipeButton);
        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Share_Recipe.this, AddRecipeActivity.class));
            }

        });

        fetchRecipes();
    }

    private void fetchRecipes() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LinearLayout recipeListView = findViewById(R.id.recipeListView);
                recipeListView.removeAllViews();

                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    if (recipe != null && (recipe.isShared() || isCurrentUserOwner(recipe))) {
                        View recipeView = createRecipeView(recipe);
                        recipeListView.addView(recipeView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Share_Recipe.this, "Błąd pobierania przepisów", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean isCurrentUserOwner(Recipe recipe) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null && currentUser.getUid().equals(recipe.getUserId());
    }

    private View createRecipeView(final Recipe recipe) {
        View recipeView = getLayoutInflater().inflate(R.layout.recipe_item, null);

        // Inicjalizacja elementów widoku przepisu
        TextView recipeNameTextView = recipeView.findViewById(R.id.recipeNameTextView);
        ImageView recipeImageView = recipeView.findViewById(R.id.recipeImageView);
        TextView viewRecipeButton = recipeView.findViewById(R.id.viewRecipeButton);
        TextView editRecipeButton = recipeView.findViewById(R.id.editRecipeButton);
        TextView deleteRecipeButton = recipeView.findViewById(R.id.deleteRecipeButton);

        // Ustawienie nazwy przepisu i wyświetlenie zdjęcia (jeśli dostępne)
        recipeNameTextView.setText(recipe.getRecipeName());
        if (recipe.getPhotoUrl() != null && !recipe.getPhotoUrl().isEmpty()) {
            Glide.with(this).load(recipe.getPhotoUrl()).into(recipeImageView);
        } else {
            // Możesz ustawić domyślne zdjęcie, jeśli nie ma dostępnego zdjęcia przepisu
            recipeImageView.setImageResource(R.drawable.default_recipe_image);
        }

        // Logika wyświetlania przycisków edycji i usuwania
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (isCurrentUserOwner(recipe)) {
            editRecipeButton.setVisibility(View.VISIBLE);
            deleteRecipeButton.setVisibility(View.VISIBLE);
        } else {
            editRecipeButton.setVisibility(View.GONE);
            deleteRecipeButton.setVisibility(View.GONE);
        }

        // Ustawienie akcji dla przycisków
        viewRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Share_Recipe.this, ViewRecipeActivity.class);
                intent.putExtra("recipeId", recipe.getRecipeId());
                startActivity(intent);
            }
        });

        editRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Share_Recipe.this, EditRecipeActivity.class);
                intent.putExtra("recipeId", recipe.getRecipeId());
                startActivity(intent);
            }
        });

        deleteRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecipe(recipe.getRecipeId());
            }
        });

        return recipeView;
    }
    private void deleteRecipe(String recipeId) {
        if (recipeId != null) {
            mDatabase.child(recipeId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Share_Recipe.this, "Przepis został usunięty", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Share_Recipe.this, "Wystąpił błąd podczas usuwania przepisu", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Nie można usunąć przepisu - brak ID przepisu", Toast.LENGTH_SHORT).show();
        }
    }

}
