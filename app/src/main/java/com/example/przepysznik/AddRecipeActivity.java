package com.example.przepysznik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.przepysznik.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText recipeNameEditText;
    private EditText ingredientsEditText;
    private EditText instructionsEditText;
    private Button addRecipeButton;
    private Button addIngredientButton;
    private List<String> ingredientsList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;

    private static final int REQUEST_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

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

        if (recipeName.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(this, "Wprowadź nazwę przepisu i instrukcje", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> recipeMap = new HashMap<>();
        recipeMap.put("recipeName", recipeName);
        recipeMap.put("instructions", instructions);
        recipeMap.put("ingredients", ingredientsList);

        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.getReference("recipes").child(userId).push().setValue(recipeMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddRecipeActivity.this, "Przepis dodany pomyślnie", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddRecipeActivity.this, "Błąd podczas dodawania przepisu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addIngredient() {
        String ingredient = ingredientsEditText.getText().toString().trim();
        if (!ingredient.isEmpty()) {
            ingredientsList.add(ingredient);
            ingredientsEditText.setText("");
            Toast.makeText(this, "Składnik dodany", Toast.LENGTH_SHORT).show();
        }
    }
}
