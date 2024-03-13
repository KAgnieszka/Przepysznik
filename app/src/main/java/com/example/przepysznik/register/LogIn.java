package com.example.przepysznik.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.przepysznik.MainActivity;
import com.example.przepysznik.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button resetPasswordButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

    }


    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "Podaj poprawny adres email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Podaj hasło!", Toast.LENGTH_SHORT).show();
            return;
        }
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Przenosimy użytkownika do nowej aktywności, gdzie może zresetować hasło
                startActivity(new Intent(LogIn.this, ResetPasswordActivity.class));
            }
        });

        // Logowanie użytkownika przy użyciu adresu e-mail i hasła
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Logowanie zakończone powodzeniem, przechodzimy do głównej aktywności
                            Intent intent = new Intent(LogIn.this, MainActivity.class); // Zmiana zależy od nazwy Twojej klasy głównej
                            startActivity(intent);
                            finish();
                        } else {
                            // Logowanie nie powiodło się
                            Log.e("Login", "Logowanie nie powiodło się: " + task.getException().getMessage());
                            Toast.makeText(LogIn.this, "Logowanie nie powiodło się. Spróbuj ponownie.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "Podaj poprawny adres email!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Resetowanie hasła
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Wiadomość e-mail z linkiem do zresetowania hasła została wysłana
                            Toast.makeText(LogIn.this, "Wiadomość e-mail z linkiem do zresetowania hasła została wysłana", Toast.LENGTH_LONG).show();
                        } else {
                            // Wystąpił błąd podczas wysyłania wiadomości e-mail
                            Toast.makeText(LogIn.this, "Wystąpił błąd podczas wysyłania wiadomości e-mail", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
