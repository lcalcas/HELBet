package com.example.helbet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class TestActivity extends AppCompatActivity {

    Button app = findViewById(R.id.app);
    Button logout = findViewById(R.id.logout);
    Button delete = findViewById(R.id.delete);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        if (!AuthManager.getInstance().isAuthenticated()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        app.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        logout.setOnClickListener(view -> {

        });
    }
}