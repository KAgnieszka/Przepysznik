package com.example.przepysznik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.przepysznik.register.LogIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;


    private Button przycisk, settings;

    // --- Menu boczne --- //
    DrawerLayout drawerLayout;
    NavigationView sideMenu;


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
        przycisk = findViewById(R.id.przycisk);
        settings = findViewById(R.id.settings);
        sideMenu = findViewById(R.id.side_menu);

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
            FirebaseUser currentUser = mAuth.getCurrentUser();

            String userId = currentUser.getUid();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            DatabaseReference currentUserRef = usersRef.child(userId);
            currentUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        //pobieranie danych z bazy
                        String nickname = snapshot.child("nickname").getValue(String.class);
                        String mail = snapshot.child("email").getValue(String.class);

                        // Znajdź element menu w widoku nawigacji
                        MenuItem profileEmail = sideMenu.getMenu().findItem(R.id.e_mail);
                        MenuItem profileNick = sideMenu.getMenu().findItem(R.id.ksywka);

                        //wstaw pobrane z bazy dane do menu
                        profileNick.setTitle(nickname);
                        profileEmail.setTitle(mail);
                    }


                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

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


        // --- MENU BOCZNE --- //
        drawerLayout = findViewById(R.id.drawer_layout);
        sideMenu = findViewById(R.id.side_menu);
        sideMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.cookbook)
                {
                    Toast.makeText(MainActivity.this, "Wybrano książkę kucharską", Toast.LENGTH_SHORT).show();
                }
                if(id == R.id.phoneCall)
                {
                    Toast.makeText(MainActivity.this, "Wybrano telefon", Toast.LENGTH_SHORT).show();
                }
                if(id == R.id.ustawienia)
                {
                    Intent intent = new Intent(MainActivity.this, UserSettings.class);
                    startActivity(intent);
                }
                if(id == R.id.logout)
                {
                    Intent intent = new Intent(MainActivity.this, LogIn.class);
                    startActivity(intent);
                }
                if(id == R.id.share)
                {
                    Toast.makeText(MainActivity.this, "Wybrano udostępnij", Toast.LENGTH_SHORT).show();
                }
                if(id == R.id.about)
                {
                    Intent intent = new Intent(MainActivity.this, AboutUs.class);
                    startActivity(intent);
                }
                if(id == R.id.rate_us)
                {
                    Toast.makeText(MainActivity.this, "Wybrano Ocen nas", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

    }

}
