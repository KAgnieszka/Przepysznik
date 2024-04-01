package com.example.przepysznik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RateUs extends AppCompatActivity {

    RatingBar ratingbar; //gwiazdki do przydzielenia
    TextView rateCount; // do wyswietlenia zly dobry, najlepszy w zaleznosci ile gwiazdek
    EditText opinia; //miejsce do napisania opinii
    private Button ocenButton;
    private Button powrotHomeButton;
    TextView showRating;

    float rateValue; //do zapisania wartosci gwiazdek
    String przydzieloneGwiazdki;
    private Button showRatingApps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_us);

        ratingbar = findViewById(R.id.rating_bar);
        rateCount = findViewById(R.id.rateCount);
        opinia = findViewById(R.id.opinia);
        ocenButton = findViewById(R.id.rateButton);
        powrotHomeButton = findViewById(R.id.backToHome);
        showRating = findViewById(R.id.showRating);
        showRatingApps = findViewById(R.id.showRatingApps);

        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateValue = ratingBar.getRating();
                if(rateValue>0 && rateValue<=1)
                    rateCount.setText("Źle " + rateValue + "/5");
                else if (rateValue>1 && rateValue<=2)
                    rateCount.setText("Ok " + rateValue + "/5");
                else if (rateValue>2 && rateValue<=3)
                    rateCount.setText("Dobrze " + rateValue + "/5");
                else if (rateValue>3 && rateValue<=4)
                    rateCount.setText("Bardzo dobrze " + rateValue + "/5");
                else if (rateValue>4 && rateValue<=5)
                    rateCount.setText("Wspaniale " + rateValue + "/5");
            }
        });

        ocenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                przydzieloneGwiazdki = rateCount.getText().toString();
                showRating.setText("Twoja ocena: \n" + przydzieloneGwiazdki +"\n" + opinia.getText());
                opinia.setText("");
                ratingbar.setRating(0);
                rateCount.setText("");
            }
        });

        powrotHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Przenosimy użytkownika spowrotem na główną
                startActivity(new Intent(RateUs.this, MainActivity.class));
                finish();
            }
        });

        showRatingApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Przenosimy użytkownika spowrotem na główną
                startActivity(new Intent(RateUs.this, UserRatings.class));
                finish();
            }
        });
    }
}
