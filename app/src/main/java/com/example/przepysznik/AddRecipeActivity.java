package com.example.przepysznik;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.przepysznik.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText recipeNameEditText;
    private EditText ingredientsEditText;
    private EditText instructionsEditText;
    private Button addRecipeButton;
    private Button addIngredientButton;
    private List<String> ingredientsList;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");

        recipeNameEditText = findViewById(R.id.recipeNameEditText);
        ingredientsEditText = findViewById(R.id.ingredientsEditText);
        instructionsEditText = findViewById(R.id.instructionsEditText);
        addRecipeButton = findViewById(R.id.addRecipeButton);
        addIngredientButton = findViewById(R.id.addIngredientButton);

        ingredientsList = new ArrayList<>();

        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecipe();
            }
        });

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient();
            }
        });
    }

    private void addRecipe() {
        String recipeName = recipeNameEditText.getText().toString().trim();
        String instructions = instructionsEditText.getText().toString().trim();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            if (!recipeName.isEmpty() && !ingredientsList.isEmpty() && !instructions.isEmpty()) {
                String recipeId = mDatabase.push().getKey();
                Recipe recipe = new Recipe(recipeId, userId, recipeName, ingredientsList.toString(), instructions);

                if (recipeId != null) {
                    mDatabase.child(recipeId).setValue(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddRecipeActivity.this, "Przepis dodany pomyślnie", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddRecipeActivity.this, "Błąd podczas dodawania przepisu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            } else {
                Toast.makeText(AddRecipeActivity.this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Jeśli użytkownik nie jest zalogowany, możesz przekierować go do ekranu logowania lub wyświetlić komunikat
            Toast.makeText(AddRecipeActivity.this, "Aby dodać przepis, musisz być zalogowany", Toast.LENGTH_SHORT).show();
        }
    }

    private void addIngredient() {
        String ingredient = ingredientsEditText.getText().toString().trim();
        if (!ingredient.isEmpty()) {
            ingredientsList.add(ingredient);
            ingredientsEditText.setText("");
        }
    }
}
