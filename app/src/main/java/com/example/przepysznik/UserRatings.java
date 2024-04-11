package com.example.przepysznik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserRatings extends AppCompatActivity {

    TextView userRatings;
    TextView backToRating;
    TextView backToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_ratings);

        userRatings = findViewById(R.id.tablica_opinii);
        backToHome = findViewById(R.id.backToHome);
        backToRating = findViewById(R.id.backToRating);

        backToRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Przenosimy użytkownika spowrotem na główną
                startActivity(new Intent(UserRatings.this, RateUs.class));
                finish();
            }
        });
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Przenosimy użytkownika spowrotem na główną
                startActivity(new Intent(UserRatings.this, MainActivity.class));
                finish();
            }
        });

        // --- Zapisane w firebase opinnie użytkowników które można przeglądać

    }
}
