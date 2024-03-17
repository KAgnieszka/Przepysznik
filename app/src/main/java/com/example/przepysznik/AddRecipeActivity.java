package com.example.przepysznik;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    private Button addImageButton;
    private LinearLayout ingredientsLayout;
    private ImageView recipeImageView;

    private List<String> ingredientsList;
    private Uri imageUri; // URI zapisanego zdjęcia

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;

    private static final int REQUEST_IMAGE = 1;

    private String userId; // ID użytkownika

    @SuppressLint("MissingInflatedId")
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
        addImageButton = findViewById(R.id.addImageButton);
        ingredientsLayout = findViewById(R.id.ingredientsLayout);
        recipeImageView = findViewById(R.id.recipeImageView);

        ingredientsList = new ArrayList<>();

        userId = mAuth.getCurrentUser().getUid(); // Pobierz ID aktualnie zalogowanego użytkownika

        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecipeWithImage();
            }
        });

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient();
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });
    }

    private void addRecipeWithImage() {
        String recipeName = recipeNameEditText.getText().toString().trim();
        String instructions = instructionsEditText.getText().toString().trim();

        if (userId != null && !userId.isEmpty()) {
            // Utwórz nowy obiekt Recipe z losowym recipeId
            String recipeId = mDatabase.getReference("recipes").child(userId).push().getKey();
            Recipe recipe = new Recipe(recipeId, userId, recipeName, ingredientsList.toString(), instructions);

            // Tworzenie referencji do katalogu w pamięci Firebase Storage
            StorageReference storageRef = mStorage.getReference().child("recipe_images").child(userId).child(recipe.getRecipeId());

            // Zapis zdjęcia do Storage
            if (imageUri != null) {
                storageRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Pobierz URL przekazywany do bazy danych
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        recipe.setImageURL(imageUrl);

                                        // Zapisz przepis do bazy danych
                                        mDatabase.getReference("recipes").child(userId).child(recipe.getRecipeId()).setValue(recipe.toMap())
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
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddRecipeActivity.this, "Błąd podczas dodawania zdjęcia", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Zapisz przepis do bazy danych bez zdjęcia
                mDatabase.getReference("recipes").child(userId).child(recipe.getRecipeId()).setValue(recipe.toMap())
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
        } else {
            // Obsługa przypadku, gdy userId jest pusty
            Toast.makeText(this, "Brak identyfikatora użytkownika", Toast.LENGTH_SHORT).show();
        }
    }

    private void addIngredient() {
        String ingredient = ingredientsEditText.getText().toString().trim();
        if (!ingredient.isEmpty()) {
            ingredientsList.add(ingredient);
            ingredientsEditText.setText("");

            EditText ingredientTextView = new EditText(this);
            ingredientTextView.setText(ingredient);
            ingredientsLayout.addView(ingredientTextView);

            Toast.makeText(this, "Składnik dodany", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Wybierz zdjęcie"), REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            recipeImageView.setImageURI(imageUri); // Wyświetl wybrane zdjęcie
        }
    }
}
