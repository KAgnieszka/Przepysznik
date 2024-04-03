package com.example.przepysznik;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText recipeNameEditText;
    private EditText ingredientsEditText;
    private EditText instructionsEditText;
    private Button addRecipeButton;
    private Button addIngredientButton;
    private LinearLayout ingredientsLayout; // Dodane
    private List<String> ingredientsList;

    private Button addPhotoButton;
    private ImageView imageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;

    private CheckBox shareRecipeCheckBox;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");
        storageReference = FirebaseStorage.getInstance().getReference();

        recipeNameEditText = findViewById(R.id.recipeNameEditText);
        ingredientsEditText = findViewById(R.id.ingredientsEditText);
        instructionsEditText = findViewById(R.id.instructionsEditText);
        addRecipeButton = findViewById(R.id.addRecipeButton);
        addIngredientButton = findViewById(R.id.addIngredientButton);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        imageView = findViewById(R.id.imageView);
        ingredientsLayout = findViewById(R.id.ingredientsLayout);
        shareRecipeCheckBox = findViewById(R.id.shareRecipeCheckBox);

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

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void addRecipe() {
        String recipeName = recipeNameEditText.getText().toString().trim();
        String instructions = instructionsEditText.getText().toString().trim();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            if (!recipeName.isEmpty() && !ingredientsList.isEmpty() && !instructions.isEmpty() && imageUri != null) {
                String recipeId = mDatabase.push().getKey();
                StorageReference imageReference = storageReference.child("recipe_photos").child(recipeId + ".jpg");

                // Upload zdjęcia
                imageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

                    imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String photoUrl = uri.toString();
                        boolean isShared = shareRecipeCheckBox.isChecked();

                        // Utwórz obiekt Recipe z danymi przepisu
                        Recipe recipe = new Recipe(recipeId, userId, recipeName, ingredientsList.toString(), instructions);
                        recipe.setPhotoUrl(photoUrl);
                        recipe.setShared(isShared);


                        mDatabase.child(recipeId).setValue(recipe).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddRecipeActivity.this, "Przepis dodany pomyślnie", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddRecipeActivity.this, "Błąd podczas dodawania przepisu", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(AddRecipeActivity.this, "Błąd podczas przesyłania zdjęcia", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(AddRecipeActivity.this, "Wypełnij wszystkie pola i dodaj zdjęcie", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Obsługa braku zalogowanego użytkownika
            Toast.makeText(AddRecipeActivity.this, "Aby dodać przepis, musisz być zalogowany", Toast.LENGTH_SHORT).show();
        }
    }


    private void addIngredient() {
        String ingredient = ingredientsEditText.getText().toString().trim();
        if (!ingredient.isEmpty()) {
            ingredientsList.add(ingredient);
            ingredientsEditText.setText("");
            addIngredientView(ingredient); // Dodane - wywołaj metodę do dodania widoku składnika
        }
    }

    private void addIngredientView(String ingredient) {
        View ingredientView = getLayoutInflater().inflate(R.layout.ingredient_item, null);
        TextView ingredientTextView = ingredientView.findViewById(R.id.ingredientTextView);
        Button removeIngredientButton = ingredientView.findViewById(R.id.removeIngredientButton);

        ingredientTextView.setText(ingredient);
        removeIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Usuń składnik z listy i widoku
                ingredientsList.remove(ingredient);
                ingredientsLayout.removeView(ingredientView);
            }
        });

        ingredientsLayout.addView(ingredientView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }
}
