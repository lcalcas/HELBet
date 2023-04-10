package com.example.requestsapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TestActivity extends AppCompatActivity {
    FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
    User currentUser;
    Button logout, gotoApp, delete;

    @Override
    protected void onStart() {
        super.onStart();

        if (authUser == null) {
            startActivity(new Intent(TestActivity.this, Login.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        logout = findViewById(R.id.logout);
        gotoApp = findViewById(R.id.goto_app);
        delete = findViewById(R.id.deleteAccount);

        DBManager.getInstance().fetch("users", authUser.getUid(), User.class, new OnFetchSingCompleteListener() {
            @Override
            public void onFetchComplete(Object result) {

                currentUser = (User) result;

                logout.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(TestActivity.this, Login.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                );

                gotoApp.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(TestActivity.this, LeaguesActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                );

                delete.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.delete().addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    DBManager.getInstance().removeUser(user.getUid());
                                                }
                                            }
                                        }
                                );
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(TestActivity.this, Login.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                );
            }
        });
    }
}