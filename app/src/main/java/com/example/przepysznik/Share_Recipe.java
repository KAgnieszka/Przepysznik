package com.example.przepysznik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class Share_Recipe extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_recipe);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("recipes");

        Button addRecipeButton = findViewById(R.id.addRecipeButton);
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
                    if (recipe != null) {
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

    private View createRecipeView(final Recipe recipe) {
        View recipeView = getLayoutInflater().inflate(R.layout.recipe_item, null);

        // Inicjalizacja elementów widoku przepisu
        Button viewRecipeButton = recipeView.findViewById(R.id.viewRecipeButton);
        Button editRecipeButton = recipeView.findViewById(R.id.editRecipeButton);
        Button deleteRecipeButton = recipeView.findViewById(R.id.deleteRecipeButton);

        viewRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Share_Recipe.this, ViewRecipeActivity.class);
                intent.putExtra("recipeId", recipe.getRecipeId());
                startActivity(intent);
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getUid().equals(recipe.getUserId())) {
            // Umożliwienie właścicielowi edycję i usuwanie przepisu
            editRecipeButton.setVisibility(View.VISIBLE);
            deleteRecipeButton.setVisibility(View.VISIBLE);

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
        } else {
            // Jeśli użytkownik nie jest właścicielem przepisu, ukrywamy przyciski edycji i usuwania
            editRecipeButton.setVisibility(View.GONE);
            deleteRecipeButton.setVisibility(View.GONE);
        }

        return recipeView;
    }

    private void deleteRecipe(String recipeId) {
        mDatabase.child(recipeId).removeValue();
        Toast.makeText(this, "Przepis został usunięty", Toast.LENGTH_SHORT).show();
    }
}
