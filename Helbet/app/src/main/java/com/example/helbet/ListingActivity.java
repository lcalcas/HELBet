package com.example.helbet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class ListingActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ArrayList<LeagueCollectionDataModel> leaguesForAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView = findViewById(R.id.leagues_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_listing;
    }

    @Override
    public String getCurrentTitle() {
        return "Parcourir";
    }

    @Override
    protected void userLogged() {
        leaguesForAdapter = new ArrayList<>();
        db.fetch(PathRefs.LEAGUES_PATHREF, League.class, new OnFetchCompleteListener<League>() {
            public <T extends DBModel> void onFetchComplete(ArrayList<T> result) {
                for (T o: result) {
                    League l = (League) o;

                    db.fetch(PathRefs.CLUBS_PATHREF, "leagueId", l.getId(), Club.class, new OnFetchCompleteListener<Club>() {
                        @Override
                        public <T extends DBModel> void onFetchComplete(ArrayList<T> fetchResult) {
                            ArrayList<ClubItemDataModel> clubsForAdapter = new ArrayList<>();
                            for (T otherO: fetchResult) {
                                Club c = (Club) otherO;
                                ClubItemDataModel clubDataModel = new ClubItemDataModel(c);
                                clubDataModel.setId(c.getId());
                                clubsForAdapter.add(clubDataModel);
                            }
                            LeagueCollectionDataModel leagueDataModel = new LeagueCollectionDataModel(l, clubsForAdapter);
                            leagueDataModel.setId(l.getId());
                            leaguesForAdapter.add(leagueDataModel);
                            if (checkLeaguesFetch()) {
                                setAdapter();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean checkLeaguesFetch() {
        return APIConfig.LEAGUE_IDS.length == leaguesForAdapter.size();
    }

    private void setAdapter() {
        User user = session.getCurrentUser();
        LeagueCollectionAdapter leaguesAdapter = new LeagueCollectionAdapter(this, leaguesForAdapter, user);
        recyclerView.setAdapter(leaguesAdapter);
    }

    @Override
    protected void userUnLogged() {
        goToMain();
    }
}