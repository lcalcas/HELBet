package com.example.requestsapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClubsActivity extends AppCompatActivity {
    private DBManager dbManager = DBManager.getInstance();
    private FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
    private Bundle bundle;
    private User currentUser;
    private List<Club> clubs;
    private ClubAdapter clubsAdapter;
    private RecyclerView clubsView;
    private TextView title;

    @Override
    protected void onStart() {
        super.onStart();

        if (authUser == null) {
            Intent intent = new Intent(ClubsActivity.this, Login.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubs);

        title = findViewById(R.id.toolbar_textview);

        bundle = getIntent().getExtras();
        if (bundle.containsKey("league_id")) {
            DBManager.getInstance().fetch("leagues", bundle.getString("league_id"), League.class, new OnFetchSingCompleteListener() {
                @Override
                public void onFetchComplete(Object result) {
                    League l = (League) result;
                    title.setText(l.getName());
                    DBManager.getInstance().fetch("users", authUser.getUid(), User.class, new OnFetchSingCompleteListener() {
                        @Override
                        public void onFetchComplete(Object result) {
                            currentUser = (User) result;
                            Map<String, String> params = new HashMap<>() {{
                                put("leagueId", l.getId());
                            }};

                            DBManager.getInstance().fetchFiltered("clubs", params, Club.class, new OnFetchMultCompleteListener() {
                                @Override
                                public void onFetchMultComplete(List result) {
                                    clubs = new ArrayList<>();
                                    for (Object o:
                                            result) {
                                        clubs.add((Club) o);
                                    }
                                    System.out.println(clubs);
                                    clubsAdapter = new ClubAdapter(ClubsActivity.this, clubs, currentUser);
                                    clubsView = findViewById(R.id.clubs_view);
                                    clubsView.setLayoutManager(new LinearLayoutManager(ClubsActivity.this));
                                    clubsView.setAdapter(clubsAdapter);
                                }
                            });
                        }
                    });
                }
            });
        } else {
            Intent intent = new Intent(ClubsActivity.this, LeaguesActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LeaguesActivity.class));
        finish();
    }
}