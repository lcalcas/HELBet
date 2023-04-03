package com.example.requestsapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mRef;
    private APIManager mApi;
    private List<League> leaguesList;
    private LeagueAdapter leagueAdapter;
    private RecyclerView leaguesListing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRef = FirebaseDatabase.getInstance().getReference("leagues");
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
    }
}