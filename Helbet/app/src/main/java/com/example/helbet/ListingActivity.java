package com.example.helbet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class ListingActivity extends AppCompatActivity {
    private AuthManager auth;
    private DBManager db;

    private User user;

    private RecyclerView recyclerView;
    private ArrayList<LeagueCollectionDataModel> leaguesForAdapter;
    private LeagueCollectionAdapter leaguesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);

        auth = AuthManager.getInstance();
        db = DBManager.getInstance();

        leaguesAdapter = new LeagueCollectionAdapter(leaguesForAdapter, user);

        recyclerView = findViewById(R.id.leagues_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (auth.isAuthenticated()) { // Firebase authentication ok ?
            Bundle extras = getIntent().getExtras();
            System.out.println(extras);
            user = (extras == null) ? null: (User) extras.getSerializable("user");
            if (user != null && user.getId().equals(auth.getUser().getUid())) { // Realtime Database User ok ?
                db.fetch(PathRefs.LEAGUES_PATHREF, League.class, new OnFetchCompleteListener<League>() {
                    public <T extends DBModel> void onFetchComplete(ArrayList<T> result) {
                        leaguesForAdapter = new ArrayList<>();
                        for (T o: result) {
                            League l = (League) o;
                            db.fetch(PathRefs.CLUBS_PATHREF, "leagueId", l.getId(), Club.class, new OnFetchCompleteListener<Club>() {
                                @Override
                                public <T extends DBModel> void onFetchComplete(ArrayList<T> fetchResult) {
                                    System.out.println(fetchResult);
                                    ArrayList<ClubItemDataModel> clubsForAdapter = new ArrayList<>();
                                    for (T otherO: fetchResult) {
                                        Club c = (Club) otherO;
                                        ClubItemDataModel clubDataModel = new ClubItemDataModel(c);
                                        clubsForAdapter.add(clubDataModel);
                                    }

//                                    System.out.println(l);
//                                    System.out.println(clubsForAdapter);

                                    leaguesForAdapter.add(new LeagueCollectionDataModel(l, clubsForAdapter));
                                    leaguesAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            } else {
                //todo
            }
        } else {
            //todo
        }
    }
}