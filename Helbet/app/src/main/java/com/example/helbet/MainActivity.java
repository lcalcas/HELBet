package com.example.helbet;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends BaseActivity {

    RecyclerView recyclerView;
    GameItemAdapter adapter;
    ArrayList<GameItemDataModel> gamesForAdapter;
    int gameListLength;
    int expensed;
    int gained;
    int benefits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView = findViewById(R.id.games_ot_day_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        gamesForAdapter = new ArrayList<>();
        expensed = 0;
        gained = 0;
        benefits = 0;
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

        ConstraintLayout main = (ConstraintLayout) getLayoutInflater().inflate(R.layout.frame_betresults, popUp, false);
        popUp.removeAllViews();
        popUp.addView(main);

        if (session.getCurrentUser().getBets() != null && !session.getCurrentUser().getBets().isEmpty()) {
            for (int i = 0; i < session.getCurrentUser().getBets().size(); i++) {
                Bet b = session.getCurrentUser().getBets().get(i);
                if (b != null) {
                    String gameId = b.getId();

                    expensed += b.getAmount();
                    ((TextView) popUp.findViewById(R.id.result_expenses_value)).setText(String.valueOf(expensed));

                    if (b.isChecked()) continue;
                    else {
                        db.fetch(Constants.DBPathRefs.GAMES, gameId, Game.class, new OnFetchCompleteListener<Game>() {
                            @Override
                            public void onFetchComplete(ArrayList<Game> fetchResult) {

                                if (popUp.getVisibility() == View.GONE) {
                                    popUp.setVisibility(View.VISIBLE);

                                    popUp.findViewById(R.id.close_results).setOnClickListener(
                                            v -> {
                                                popUp.setVisibility(View.GONE);
                                            }
                                    );
                                }

                                Game g = fetchResult.get(0);
                                if (g.getResult() != Results.NONE) {
                                    if (g.getResult() == b.getAnnouncedResult()) {
                                        // PARIS GAGNE

                                        gained += b.getAmount() * b.getOddValue();
                                        benefits = gained - expensed;

                                        ((TextView) popUp.findViewById(R.id.result_gains_value)).setText(String.valueOf(gained));
                                        ((TextView) popUp.findViewById(R.id.result_benefits_value)).setText(String.valueOf(benefits));

                                        session.getCurrentUser().addMoney((int) (b.getAmount() * b.getOddValue()));
                                        userLiveData.setValue(session.getCurrentUser());
                                    } else {
                                        // PARIS PERDU
                                    }
                                    b.setChecked(true);
                                    db.storeObject(session.getCurrentUser(), Constants.DBPathRefs.USERS);
                                }
                            }
                        });
                    }
                }
            }
        }
    }


    void checkUpdates() {
        data.updateIfNecessary(new OnDownloadCompleteListener() {
            @Override
            public void onDownloadComplete(ArrayList downloadResult) {
                displayGames();
            }
        });
    }

    @Override
    protected void userUnLogged() {
        // TODO AFFICHAGE PARIS SANS OPT
        super.userUnLogged();
    }

    private void displayGames() {
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

                                                Geocoder geocoder = new Geocoder(getApplicationContext());
                                                List<Address> addresses;

                                                try {
                                                    addresses = geocoder.getFromLocationName(g.getApproximateAddress(), 1);
                                                    if (addresses != null && !addresses.isEmpty()) {
                                                        double latitude = addresses.get(0).getLatitude();
                                                        double longitude = addresses.get(0).getLongitude();

                                                        GameItemDataModel gameItemDataModel = new GameItemDataModel(g, home, away, odds, latitude, longitude);
                                                        gameItemDataModel.setId(g.getId());
                                                        gamesForAdapter.add(gameItemDataModel);
                                                        setAdapter();
                                                    }
                                                } catch (IOException e) {
                                                    Log.d("MAPS", "ERROR");
                                                    e.printStackTrace();
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

    private void setAdapter() {
        adapter = new GameItemAdapter(this, gamesForAdapter, TimeZone.getDefault(), session.getCurrentUser(), userLiveData);
        recyclerView.setAdapter(adapter);
    }
}