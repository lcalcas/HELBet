package com.example.helbet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class TestActivity extends BaseActivity {

    Button app;
    Button update;
    Button logout;
    Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        app = findViewById(R.id.app);
        update = findViewById(R.id.update);
        logout = findViewById(R.id.logout);
        delete = findViewById(R.id.delete);
    }

    @Override
    protected void userLogged() {
        app.setOnClickListener(view -> {
            Intent i = new Intent(this, ListingActivity.class);
            startActivity(i);
            finish();
        });
    }

    @Override
    protected void userUnLogged() {
        goToMain();
    }
}