package com.example.przepysznik;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditRecipeActivity extends AppCompatActivity {

    private EditText nazwaPrzepisuEditText;
    private EditText skladnikiPrzepisuEditText;
    private EditText instrukcjePrzepisuEditText;
    private TextView aktualizujButton;
    private ImageView zdjeciePrzepisuImageView;

    private String recipeId;
    private Uri imageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        nazwaPrzepisuEditText = findViewById(R.id.nazwaPrzepisuEditText);
        skladnikiPrzepisuEditText = findViewById(R.id.skladnikiPrzepisuEditText);
        instrukcjePrzepisuEditText = findViewById(R.id.instrukcjePrzepisuEditText);
        aktualizujButton = findViewById(R.id.aktualizujButton);
        zdjeciePrzepisuImageView = findViewById(R.id.zdjeciePrzepisuImageView);

        recipeId = getIntent().getStringExtra("recipeId");

        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference().child("recipes").child(recipeId);

        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String recipeName = dataSnapshot.child("recipeName").getValue(String.class);
                    String ingredients = dataSnapshot.child("ingredients").getValue(String.class);
                    String instructions = dataSnapshot.child("instructions").getValue(String.class);
                    String photoUrl = dataSnapshot.child("photoUrl").getValue(String.class);

                    nazwaPrzepisuEditText.setText(recipeName);
                    skladnikiPrzepisuEditText.setText(ingredients);
                    instrukcjePrzepisuEditText.setText(instructions);
                    Picasso.get().load(photoUrl).into(zdjeciePrzepisuImageView);
                } else {
                    Toast.makeText(EditRecipeActivity.this, "Przepis nie istnieje.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditRecipeActivity.this, "Błąd podczas pobierania danych przepisu.", Toast.LENGTH_SHORT).show();
            }
        });

        zdjeciePrzepisuImageView.setOnClickListener(v -> openGallery());

        aktualizujButton.setOnClickListener(v -> updateRecipe());
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Wybierz zdjęcie"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            zdjeciePrzepisuImageView.setImageURI(imageUri);
        }
    }

    private void updateRecipe() {
        String updatedRecipeName = nazwaPrzepisuEditText.getText().toString();
        String updatedIngredients = skladnikiPrzepisuEditText.getText().toString();
        String updatedInstructions = instrukcjePrzepisuEditText.getText().toString();

        DatabaseReference recipeRefToUpdate = FirebaseDatabase.getInstance().getReference().child("recipes").child(recipeId);
        recipeRefToUpdate.child("recipeName").setValue(updatedRecipeName);
        recipeRefToUpdate.child("ingredients").setValue(updatedIngredients);
        recipeRefToUpdate.child("instructions").setValue(updatedInstructions);

        Toast.makeText(EditRecipeActivity.this, "Dane zaktualizowane.", Toast.LENGTH_SHORT).show();

        uploadImage();
    }
    private void uploadImage() {
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("recipe_images/" + System.currentTimeMillis() + ".jpg");

            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            DatabaseReference recipeRefToUpdate = FirebaseDatabase.getInstance().getReference().child("recipes").child(recipeId);
                            recipeRefToUpdate.child("photoUrl").setValue(uri.toString());
                            Toast.makeText(EditRecipeActivity.this, "Zdjęcie zaktualizowane.", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditRecipeActivity.this, "Błąd podczas aktualizacji zdjęcia.", Toast.LENGTH_SHORT).show());
        }
    }

}
