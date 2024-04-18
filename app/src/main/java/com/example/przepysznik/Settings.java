package com.example.przepysznik;

import android.os.Bundle;

import com.example.przepysznik.register.LogIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.przepysznik.R;
import java.util.ArrayList;

public class Settings extends AppCompatActivity{
    private TextView jezyk, trybJasny, trybCiemny, powrotHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        jezyk = findViewById(R.id.jezyk);
        trybJasny = findViewById(R.id.trybJasny);
        trybCiemny = findViewById(R.id.trybCiemny);
        powrotHome = findViewById(R.id.backToHome);


        //Po nacisnieciu przycisku, otwieraja sie ustawienia systemowe do zmiany jezyka systemu.
        //Docelowo bedzie uzyteczne, jesli translacja bedzie zrobiona recznie
        // - czyli pliki strings.resources, w ktorych beda translacje na inne jezyki
        jezyk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS));
            }
        });

        trybJasny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        trybCiemny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });

        powrotHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Przenosimy użytkownika spowrotem na główną
                startActivity(new Intent(Settings.this, MainActivity.class));
                finish();
            }
        });
    }


}
