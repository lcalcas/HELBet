package com.example.helbet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {

    RecyclerView recyclerView;
    TextView tv;
    Button button;
    Button button2;
    GameItemAdapter adapter;
    ArrayList<GameItemDataModel> gamesForAdapter;
    int gameListLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView = findViewById(R.id.games_ot_day_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        tv = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        gamesForAdapter = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        button2.setText("download");
        button2.setOnClickListener(view -> {

        });
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
        tv.setText("Bonjour, " + Session.getInstance().getCurrentUser().getEmail());
        // Récupération des anciens et nouveaux matchs :
        data.fetchGames(new OnGamesFetchedListener() {
            @Override
            public void onOldGamesFetched(List<Game> gameList) {

            }

            @Override
            public void onRecentGamesFetched(List<Game> gameList) {


                gameListLength = gameList.size();
                for (Game g: gameList) {
                    String homeId = g.getHomeClubId();
                    String awayId = g.getAwayClubId();
                    db.fetch(PathRefs.CLUBS_PATHREF, homeId, Club.class, new OnFetchCompleteListener<Club>() {
                        @Override
                        public <T extends DBModel> void onFetchComplete(ArrayList<T> fetchResult) {
                            Club home = (Club) fetchResult.get(0);
                            db.fetch(PathRefs.CLUBS_PATHREF, awayId, Club.class, new OnFetchCompleteListener<Club>() {
                                @Override
                                public <T extends DBModel> void onFetchComplete(ArrayList<T> fetchResult) {
                                    Club away = (Club) fetchResult.get(0);

                                    HashMap<String, Club> homeNAwayClubs = new HashMap<>();
                                    homeNAwayClubs.put("home", home);
                                    homeNAwayClubs.put("away", away);

                                    GameItemDataModel gameItemDataModel = GameItemDataModelFactory.getInstance().makeGameItemDataModel(
                                            g,
                                            homeNAwayClubs
                                    );
                                    gameItemDataModel.setId(g.getId());
                                    gamesForAdapter.add(gameItemDataModel);
                                    if (checkGamesFetch()) {
                                        setAdapter();
                                    }
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

    private void setAdapter() {
        User user = session.getCurrentUser();
        adapter = new GameItemAdapter(gamesForAdapter);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void userUnLogged() {
        // TODO AFFICHAGE PARIS SANS OPT
        System.out.println("APPLYING UNLOGGED PROTOCOL");
        tv.setText("Bonjour, invité !");
        button.setText("S'authentifier");
        button.setOnClickListener(view -> {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        });
    }
}