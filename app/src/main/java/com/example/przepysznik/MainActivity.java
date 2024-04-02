package com.example.przepysznik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.przepysznik.register.LogIn;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
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

    private Button logout;
    private Button przycisk, settings, gps, chat;

    // --- Menu boczne --- //
    DrawerLayout drawerLayout;
    NavigationView sideMenu;
    ActionBarDrawerToggle drawerToggle;


    //Link do dokumentacji googla: https://developers.google.com/ml-kit/language/translation/android
    /*public TranslatorOptions options =
            new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.POLISH)
                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                    .build();
    public final Translator polishEnglishTranslator =
            Translation.getClient(options);
     */

    // --- REKLAMY --- //
    private InterstitialAd mInterstitialAd;
    private Button adBtn;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.AppTheme); // Ustawienie tematu z ActionBar
        setContentView(R.layout.activity_main);
        przycisk = findViewById(R.id.przycisk);
        chat = findViewById(R.id.chat);
        settings = findViewById(R.id.settings);
        sideMenu = findViewById(R.id.side_menu);
        gps = findViewById(R.id.gps);

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


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Chat.class);
                startActivity(intent);
            }
        });


        // Sklepy w pobliżu - GPS
        gps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + "&destination=sklepy spożywcze");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });


        // --- MENU BOCZNE --- //
        drawerLayout = findViewById(R.id.drawer_layout);
        sideMenu = findViewById(R.id.side_menu);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                    final String appPackageName = getPackageName();
                    Toast.makeText(MainActivity.this, "Wybrano udostępnij", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "Sprawdź tą apkę\n" + "https://play.google.com/store/apps/details?id" + appPackageName);
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent, "Udostępnij tą aplikację."));
                }
                if(id == R.id.about)
                {
                    Intent intent = new Intent(MainActivity.this, AboutUs.class);
                    startActivity(intent);
                }
                if(id == R.id.rate_us)
                {
                    Intent intent = new Intent(MainActivity.this, RateUs.class);
                    startActivity(intent);
                }

                return false;
            }
        });

        // --- REKLAMY --- //
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adBtn = findViewById(R.id.reklama);
        AdView mAdView = findViewById(R.id.reklama1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });

        adBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                }
            }
        });
    }

    @Override
    public void onBackPressed()
        {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }
}
