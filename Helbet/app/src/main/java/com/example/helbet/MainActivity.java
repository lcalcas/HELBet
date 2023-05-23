package com.example.helbet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

        System.out.println("MAINACTIVITY");

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
        System.out.println("APPLYING LOGGED PROTOCOL");
    }

    void checkUpdates() {
        data.updateIfNecessary(new OnDownloadCompleteListener() {
            @Override
            public void onDownloadComplete(ArrayList downloadResult) {
                System.out.println("Displaying");
                displayGames(session.getCurrentUser());
            }
        });
    }

    @Override
    protected void userUnLogged() {
        // TODO AFFICHAGE PARIS SANS OPT
        System.out.println("APPLYING UNLOGGED PROTOCOL");
    }

    private void displayGames(User user) {
        data.fetchGamesToDisplay(new Date(), TimeZone.getDefault(), new OnGamesFetchedListener() {
            @Override
            public void onDateSpecifiedGamesFetched(List<Game> gameList, Date dateSpecified) {
                System.out.println("Date[" + dateSpecified +"] games: " + gameList );
                System.out.println(gameList.size());
                gameListLength = gameList.size();
                    for (Game g: gameList) {
                        String homeId = g.getHomeClubId();
                        String awayId = g.getAwayClubId();
                        db.fetch(PathRefs.CLUBS_PATHREF, homeId, Club.class, new OnFetchCompleteListener<Club>() {
                            @Override
                            public void onFetchComplete(ArrayList<Club> fetchResult) {
                                Club home = fetchResult.get(0);
                                db.fetch(PathRefs.CLUBS_PATHREF, awayId, Club.class, new OnFetchCompleteListener<Club>() {
                                    @Override
                                    public void onFetchComplete(ArrayList<Club> fetchResult) {
                                        Club away = fetchResult.get(0);
                                        db.fetch(PathRefs.ODDS_PATHREF, g.getId(), Odd.class, new OnFetchCompleteListener<Odd>() {
                                            @Override
                                            public void onFetchComplete(ArrayList<Odd> fetchResult) {
                                                Odd odds = fetchResult.get(0);
                                                System.out.println("[MainActivity - onDateSpecifiedGamesFetched] odds -> " + odds);

                                                GameItemDataModel gameItemDataModel = new GameItemDataModel(g, home, away, odds);
                                                gameItemDataModel.setId(g.getId());
                                                gamesForAdapter.add(gameItemDataModel);
//                                                if (checkGamesFetch()) {
//                                                    System.out.println("checkGamesFetched");
//
//                                                }
                                                setAdapter(user);
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

    private boolean checkGamesFetch() {
        System.out.println(gameListLength);
        System.out.println(gamesForAdapter.size());
        return gameListLength == gamesForAdapter.size();
    }


    private void setAdapter(User user) {
        adapter = new GameItemAdapter(gamesForAdapter, TimeZone.getDefault());
        recyclerView.setAdapter(adapter);
    }
}