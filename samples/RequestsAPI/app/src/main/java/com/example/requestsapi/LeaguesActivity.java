package com.example.requestsapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class LeaguesActivity extends AppCompatActivity {
    private DBManager dbManager = DBManager.getInstance();
    private FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
    private User currentUser;
    private List<League> leagues;
    private LeagueAdapter leaguesAdapter;
    private RecyclerView leaguesView;

    protected void onStart() {
        super.onStart();

        if (authUser == null) {
            Intent intent = new Intent(LeaguesActivity.this, Login.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leagues);
        dbManager.fetch("users", authUser.getUid(), User.class, new OnFetchSingCompleteListener() {
            @Override
            public void onFetchComplete(Object result) {
                currentUser = (User) result;

                dbManager.fetchRef("leagues", League.class, new OnFetchMultCompleteListener() {
                    @Override
                    public void onFetchMultComplete(List result) {
                        List<League> leagues = new ArrayList<>();

                        for (Object resultItem: result) {
                            if (resultItem instanceof League) {
                                 leagues.add((League) resultItem);
                            }
                        }

                        leaguesAdapter = new LeagueAdapter(LeaguesActivity.this, leagues);

                        leaguesView = findViewById(R.id.leagues_view);
                        leaguesView.setLayoutManager(new LinearLayoutManager(LeaguesActivity.this));
                        leaguesView.setAdapter(leaguesAdapter);
                    }
                });
            }
        });
    }
}