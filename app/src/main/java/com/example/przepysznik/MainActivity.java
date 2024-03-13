package com.example.przepysznik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.przepysznik.register.SignUp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        Intent x = new Intent(MainActivity.this, SignUp.class);
        startActivity(x);
    }
}