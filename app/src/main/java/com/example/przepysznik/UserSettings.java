package com.example.przepysznik;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.przepysznik.MainActivity;
import com.example.przepysznik.register.LogIn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserSettings extends AppCompatActivity {

    private EditText editKsywka;
    private EditText editHaslo1;
    private EditText editHaslo2;


    private Button zapiszDane;
    private Button powrotHome;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting);

         mAuth = FirebaseAuth.getInstance();

        editKsywka = findViewById(R.id.edit_nickname);
        editHaslo1 = findViewById(R.id.edit_password1);
        editHaslo2 = findViewById(R.id.edit_password2);
        zapiszDane = findViewById(R.id.zapiszDaneButton);
        powrotHome = findViewById(R.id.backToHomeButton);

        zapiszDane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zapiszNoweDane();
                // Przenosimy użytkownika spowrotem na główną
                startActivity(new Intent(UserSettings.this, MainActivity.class));
            }
        });

        powrotHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Przenosimy użytkownika spowrotem na główną
                startActivity(new Intent(UserSettings.this, MainActivity.class));
                finish();
            }
        });
    }

    void zapiszNoweDane(){
        String new_nick = editKsywka.getText().toString().trim();
        String new_password1 = editHaslo1.getText().toString().trim();
        String new_password2 = editHaslo2.getText().toString().trim();

        if (mAuth.getCurrentUser() == null) {
            // Jeśli użytkownik nie jest zalogowany, otwórz ekran logowania
            Intent loginIntent = new Intent(UserSettings.this, LogIn.class);
            startActivity(loginIntent);
            finish();
        } else {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String userId = currentUser.getUid();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            DatabaseReference currentUserRef = usersRef.child(userId);

            if (!TextUtils.isEmpty(new_nick)){
                currentUserRef.child("nickname").setValue(new_nick)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Powiadom użytkownika o sukcesie aktualizacji
                                Toast.makeText(UserSettings.this, "Zaktualizowano ksywke", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Obsłuż błąd aktualizacji
                                Toast.makeText(UserSettings.this, "Błąd podczas aktualizacji ksywki", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            if(!TextUtils.isEmpty(new_password1) && !new_password1.equals(new_password2)){
                Toast.makeText(UserSettings.this, "Hasła muszą być takie same", Toast.LENGTH_SHORT).show();
            }
            else if(!TextUtils.isEmpty(new_password1) && new_password1.equals(new_password2) && new_password1.length() < 8){
                Toast.makeText(UserSettings.this, "Nowe hasło za krótkie", Toast.LENGTH_SHORT).show();
            }
            else if(!TextUtils.isEmpty(new_password1) && new_password1.equals(new_password2) && new_password1.length() >= 8){
                currentUser.updatePassword(new_password1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Powiadom użytkownika o sukcesie aktualizacji
                                Toast.makeText(UserSettings.this, "Zaktualizowano hasło", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Obsłuż błąd aktualizacji
                                Toast.makeText(UserSettings.this, "Błąd podczas aktualizacji hasła", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}