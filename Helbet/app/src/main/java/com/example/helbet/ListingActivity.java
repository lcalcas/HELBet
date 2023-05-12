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
    public int getBottomNavSelectItemId() {
        return R.id.menu_search;
    }

    @Override
    protected void userLogged() {
        leaguesForAdapter = new ArrayList<>();
        db.fetch(PathRefs.LEAGUES_PATHREF, true, League.class, new OnFetchCompleteListener<League>() {
            public void onFetchComplete(ArrayList<League> result) {
                for (League l: result) {

                    db.fetch(PathRefs.CLUBS_PATHREF, "leagueId", l.getId(), Club.class, new OnFetchCompleteListener<Club>() {
                        @Override
                        public void onFetchComplete(ArrayList<Club> fetchResult) {
                            ArrayList<ClubItemDataModel> clubsForAdapter = new ArrayList<>();
                            for (Club c: fetchResult) {
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