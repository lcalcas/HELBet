package com.example.requestsapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class LoadingActivity extends AppCompatActivity {
    private APIManager apiManager = new APIManager(LoadingActivity.this);
    private ProgressBar progressBar;
    private int cbkCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        apiManager.getLeagues(new OnUploadCompleteListener() {
            @Override
            public void onUploadComplete() {
                cbkCounter++;
                checkCbkCounter();
            }
        });

        apiManager.getClubs(new OnUploadCompleteListener() {
            @Override
            public void onUploadComplete() {
                cbkCounter++;
                checkCbkCounter();
            }
        });
    }

    private void checkCbkCounter() {
        if (cbkCounter == 2) {
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(this, LeaguesActivity.class);
            startActivity(intent);
            finish();
        }
    }
}