package com.example.requestsapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LeaguesActivity extends AppCompatActivity {
    private List<League> leaguesList;
    private LeagueAdapter leagueAdapter;
    private RecyclerView leaguesListing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }
}

/*
leaguesList = new ArrayList<>();
        leagueAdapter = new LeagueAdapter(this, leaguesList);

        leaguesListing = findViewById(R.id.leagues_listing);
        leaguesListing.setLayoutManager(new LinearLayoutManager(this));
        leaguesListing.setAdapter(leagueAdapter);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                leaguesList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    League league = dataSnapshot.getValue(League.class);
                    leaguesList.add(league);
                }
                leagueAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
 */