package com.example.helbet;

import static com.example.helbet.PathRefs.USERS_PATHREF;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    AuthManager auth;
    DBManager db;

    User user;

    TextView welcome;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = AuthManager.getInstance();
        db = DBManager.getInstance();

        user = null;

        welcome = findViewById(R.id.textView);
        button = findViewById(R.id.button);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (auth.isAuthenticated()) { // Firebase authentification ok ?
            Bundle extras = getIntent().getExtras();
            user = (extras == null) ? null: (User) extras.getSerializable("user");
            if (user != null && user.getId().equals(auth.getUser().getUid())) { // Realtime Database User ok ?
                welcome.setText("Hello, " + auth.getUser().getEmail());

                button.setText("Go To Test");
                button.setOnClickListener(view -> {
                    Intent intent = new Intent(this, ListingActivity.class);

                    intent.putExtras(extras);
                    startActivity(intent);

                });
            } else {
                db.fetch(USERS_PATHREF, auth.getUser().getUid(), User.class, new OnFetchCompleteListener<User>() {
                    @Override
                    public <T extends DBModel> void onFetchComplete(ArrayList<T> fetchResult) {
                        if (fetchResult.size() == 1) {
                            User user = (User) fetchResult.get(0);
                            user.setId(auth.getUser().getUid());

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user", user);

                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Erreur récupération utilisateur base de données!", Toast.LENGTH_LONG).show();
                            recreate();
                        }
                    }
                });
            }
        } else {
            welcome.setText("Bonjour, invité");

            button.setText("S'authentifier");
            button.setOnClickListener(view -> {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}