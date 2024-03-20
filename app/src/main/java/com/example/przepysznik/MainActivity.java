package com.example.przepysznik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.przepysznik.register.LogIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button logout;
    private Button przycisk, settings;

    // --- Menu boczne --- //
   // DrawerLayout drawerLayout;


    //Link do dokumentacji googla: https://developers.google.com/ml-kit/language/translation/android
    /*public TranslatorOptions options =
            new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.POLISH)
                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                    .build();
    public final Translator polishEnglishTranslator =
            Translation.getClient(options);
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logout = findViewById(R.id.logout);
        przycisk = findViewById(R.id.przycisk);
        settings = findViewById(R.id.settings);

        mAuth = FirebaseAuth.getInstance();

        /*DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        polishEnglishTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener)*/

        if (mAuth.getCurrentUser() == null) {
            // Jeśli użytkownik nie jest zalogowany, otwórz ekran logowania
            Intent loginIntent = new Intent(MainActivity.this, LogIn.class);
            startActivity(loginIntent);
        } else {

        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogIn.class);
                startActivity(intent);
            }
        });

        przycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Share_Recipe.class);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });
    }

}
