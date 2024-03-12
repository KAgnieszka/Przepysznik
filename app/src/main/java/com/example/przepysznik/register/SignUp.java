package com.example.przepysznik.register;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;


import androidx.appcompat.app.AppCompatActivity;

import com.example.przepysznik.R;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText nicknameEditText;
    private EditText birthDateEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nicknameEditText = findViewById(R.id.nicknameEditText);
        birthDateEditText = findViewById(R.id.birthDateEditText);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

    }

    private void registerUser() {
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        final String nickname = nicknameEditText.getText().toString().trim();
        final String birthDate = birthDateEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "Podaj poprawny adres email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 8) {
            Toast.makeText(getApplicationContext(), "Hasło musi mieć co najmniej 8 znaków!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(getApplicationContext(), "Podaj swój pseudonim!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(birthDate)) {
            Toast.makeText(getApplicationContext(), "Podaj datę urodzenia!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Rejestracja użytkownika w Firebase Authentication
        Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Pobranie identyfikatora nowo zarejestrowanego użytkownika
                        String userId = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentUserRef = usersRef.child(userId);
                        // Tworzenie mapy zawierającej dane użytkownika
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("email", email);
                        userMap.put("nickname", nickname);
                        userMap.put("birthDate", birthDate);
                        // Zapis danych użytkownika do bazy danych Firebase
                        currentUserRef.setValue(userMap);

                        // Wyświetlenie komunikatu o powodzeniu rejestracji
                        Toast.makeText(SignUp.this, "Rejestracja zakończona pomyślnie!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Wyświetlenie komunikatu o niepowodzeniu rejestracji
                        Toast.makeText(SignUp.this, "Rejestracja nie powiodła się! Spróbuj ponownie później", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
