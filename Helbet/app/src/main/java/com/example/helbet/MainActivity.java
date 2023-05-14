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
    Button button;
    GameItemAdapter adapter;
    ArrayList<GameItemDataModel> gamesForAdapter;
    int gameListLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView = findViewById(R.id.games_ot_day_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        button = findViewById(R.id.button);
        gamesForAdapter = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        checkUpdates();
        displayGames(session.getCurrentUser());
        button.setVisibility(View.INVISIBLE);
    }

    void checkUpdates() {
        data.updateIfNecessary();
    }

    @Override
    protected void userUnLogged() {
        // TODO AFFICHAGE PARIS SANS OPT
        System.out.println("APPLYING UNLOGGED PROTOCOL");
        displayGames(null);

        button.setText("S'authentifier");
        button.setOnClickListener(view -> {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        });
    }

    private void displayGames(User user) {
        data.fetchGamesToDisplay(new Date(), TimeZone.getDefault(), new OnGamesFetchedListener() {
            @Override
            public void onDateSpecifiedGamesFetched(List<Game> gameList, Date dateSpecified) {
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
                                                if (checkGamesFetch()) {
                                                    setAdapter(user);
                                                }
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
        return gameListLength == gamesForAdapter.size();
    }


    private void setAdapter(User user) {
        adapter = new GameItemAdapter(gamesForAdapter, TimeZone.getDefault());
        recyclerView.setAdapter(adapter);
    }
}