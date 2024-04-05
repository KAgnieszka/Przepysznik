package com.example.przepysznik;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewRecipeActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String recipeId;
    private ImageView imageView;
    private TextView averageRatingTextView; // TextView do wyświetlania średniej oceny przepisu
    private TextView commentTextView; // TextView do wyświetlania komentarza
    private TextView commentTimeTextView; // TextView do wyświetlania czasu dodania komentarza
    private EditText commentEditText;
    private Button addCommentButton;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recipe);
        imageView = findViewById(R.id.recipeImageView);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ImageView recipeImageView = findViewById(R.id.recipeImageView);
        TextView recipeNameTextView = findViewById(R.id.recipeNameTextView);
        TextView recipeIngredientsTextView = findViewById(R.id.recipeIngredientsTextView);
        TextView recipeInstructionsTextView = findViewById(R.id.recipeInstructionsTextView);
        Button rateRecipeButton = findViewById(R.id.rateRecipeButton);
        averageRatingTextView = findViewById(R.id.averageRatingTextView); // Inicjalizacja TextView do wyświetlania średniej oceny
        commentTextView = findViewById(R.id.commentTextView); // Inicjalizacja TextView do wyświetlania komentarza
        commentTimeTextView = findViewById(R.id.commentTimeTextView); // Inicjalizacja TextView do wyświetlania czasu dodania komentarza
        commentEditText = findViewById(R.id.commentEditText);
        addCommentButton = findViewById(R.id.addCommentButton);
        recyclerView = findViewById(R.id.recyclerViewComments);

        // Pobieranie ID przepisu przekazanego z poprzedniego activity
        recipeId = getIntent().getStringExtra("recipeId");

        // Pobieranie informacji o przepisie z bazy danych
        mDatabase.child("recipes").child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    // Wyświetlanie komentarza i czasu dodania
                    if (recipe.getComment() != null && recipe.getCommentTime() != null) {
                        commentTextView.setText(recipe.getComment());
                        commentTimeTextView.setText(recipe.getCommentTime());
                    }
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

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });

        // Inicjalizacja RecyclerView do wyświetlania komentarzy
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadComments();
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
        DatabaseReference userRatingRef = mDatabase.child("recipes").child(recipeId).child("userRatings").child(userId);
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

    // Metoda dodająca komentarz do przepisu
    private void addComment() {
        String comment = commentEditText.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, "Wpisz komentarz!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid(); // Pobieramy ID bieżącego użytkownika
        DatabaseReference userCommentRef = mDatabase.child("recipes").child(recipeId).child("userComments").push(); // Tworzymy nowy losowy identyfikator dla komentarza
        userCommentRef.child("userId").setValue(userId); // Ustawiamy ID użytkownika
        userCommentRef.child("comment").setValue(comment); // Ustawiamy treść komentarza
        userCommentRef.child("timestamp").setValue(ServerValue.TIMESTAMP) // Ustawiamy timestamp
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ViewRecipeActivity.this, "Komentarz został dodany!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ViewRecipeActivity.this, "Błąd podczas dodawania komentarza", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Czyszczenie pola EditText po dodaniu komentarza
        commentEditText.setText("");
    }

    private void loadComments() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot x = dataSnapshot.child("recipes").child(recipeId).child("userComments");
                List<UserComment> commentList = new ArrayList<>();
                for (DataSnapshot snapshot : x.getChildren()) {
           String timestamp = snapshot.child("timestamp").getValue().toString();
                    String comment = snapshot.child("comment").getValue().toString();
                   String userId = snapshot.child("userId").getValue(String.class);
                    String nickname = dataSnapshot.child("Users").child(userId).child("nickname").getValue().toString();
                    UserComment newComment = new UserComment(userId, nickname,comment, timestamp);

                    // Dodawanie nowego komentarza do listy
                    commentList.add(newComment);

                }

                // Tworzenie adaptera komentarzy i ustawienie go na RecyclerView
                CommentAdapter commentAdapter = new CommentAdapter(commentList);
                recyclerView.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewRecipeActivity.this, "Błąd pobierania komentarzy", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
