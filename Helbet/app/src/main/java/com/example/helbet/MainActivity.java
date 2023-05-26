package com.example.helbet;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends BaseActivity {

    RecyclerView recyclerView;
    GameItemAdapter adapter;
    ArrayList<GameItemDataModel> gamesForAdapter;
    int gameListLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView = findViewById(R.id.games_ot_day_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        gamesForAdapter = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkUpdates();
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public String getCurrentTitle() {
        return "Accueil";
    }

    @Override
    public int getBottomNavSelectItemId() {
        return R.id.menu_home;
    }

    @Override
    protected void userLogged() {
        // TODO AFFICHAGE PARIS + OPT PARIS
        super.userLogged();
    }

    void checkUpdates() {
        data.updateIfNecessary(new OnDownloadCompleteListener() {
            @Override
            public void onDownloadComplete(ArrayList downloadResult) {
                displayGames(session.getCurrentUser());
            }
        });
    }

    @Override
    protected void userUnLogged() {
        // TODO AFFICHAGE PARIS SANS OPT
        super.userUnLogged();
    }

    private void displayGames(User user) {
        data.fetchGamesToDisplay(new Date(), TimeZone.getDefault(), new OnGamesFetchedListener() {
            @Override
            public void onDateSpecifiedGamesFetched(List<Game> gameList, Date dateSpecified) {
                gameListLength = gameList.size();
                    for (Game g: gameList) {
                        String homeId = g.getHomeClubId();
                        String awayId = g.getAwayClubId();
                        db.fetch(Constants.DBPathRefs.CLUBS, homeId, Club.class, new OnFetchCompleteListener<Club>() {
                            @Override
                            public void onFetchComplete(ArrayList<Club> fetchResult) {
                                Club home = fetchResult.get(0);
                                db.fetch(Constants.DBPathRefs.CLUBS, awayId, Club.class, new OnFetchCompleteListener<Club>() {
                                    @Override
                                    public void onFetchComplete(ArrayList<Club> fetchResult) {
                                        Club away = fetchResult.get(0);
                                        db.fetch(Constants.DBPathRefs.ODDS, g.getId(), Odd.class, new OnFetchCompleteListener<Odd>() {
                                            @Override
                                            public void onFetchComplete(ArrayList<Odd> fetchResult) {
                                                Odd odds = fetchResult.get(0);

                                                GameItemDataModel gameItemDataModel = new GameItemDataModel(g, home, away, odds);
                                                gameItemDataModel.setId(g.getId());
                                                gamesForAdapter.add(gameItemDataModel);
                                                setAdapter();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
            }
        });
    }

    private void setAdapter() {
        adapter = new GameItemAdapter(this, gamesForAdapter, TimeZone.getDefault(), session.getCurrentUser());
        recyclerView.setAdapter(adapter);
    }
}