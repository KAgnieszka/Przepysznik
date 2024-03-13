package com.example.przepysznik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.przepysznik.register.LogIn;
import com.example.przepysznik.register.SignUp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);
        Intent x = new Intent(MainActivity.this, LogIn.class);
        startActivity(x);
    }
}