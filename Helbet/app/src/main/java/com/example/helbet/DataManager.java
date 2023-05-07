package com.example.helbet;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataManager  {
    private static DataManager singleton;
    private APIManager apiManager;
    private DBManager dbManager;
    private Context context;
    public final static String[] LEAGUE_IDS = APIConfig.LEAGUE_IDS;

    private DataManager(Context context) {
        apiManager = APIManager.getInstance(context);
        dbManager = DBManager.getInstance();
    }

    public static DataManager getInstance(Context context) {
       if (singleton == null || singleton.getContext() != context) {
           singleton = new DataManager(context);
       }

       return singleton;
    }

    public Context getContext() {
        return context;
    }

    public void dlAndStoreLeague(String leagueId) {
        dlAndStoreLeague(leagueId, null);
    }

    public void dlAndStoreLeague(String leagueId, OnDataUpdatedListener listener) {
        apiManager.dlLeague(leagueId, new OnDownloadCompleteListener<League>() {
            @Override
            public <T extends DBModel> void onDownloadComplete(ArrayList<T> downloadResult) {
                League league = (League) downloadResult.get(0);
                dbManager.storeObject(league, PathRefs.LEAGUES_PATHREF,
                        task -> {
                            System.out.println("[LOG] '" + league.getName() + ", " + league.getSeasonYear() + "' - successfully downloaded and uploaded to db. ref=" + PathRefs.LEAGUES_PATHREF);
                            if (listener != null) {
                                listener.onDataUpdated();
                            }
                        }
                );
            }
        });
    }

    public void dlAndStoreLeagueClubs(String leagueId) {
        dbManager.fetch(PathRefs.LEAGUES_PATHREF, leagueId, League.class,
                new OnFetchCompleteListener<League>() {
                    @Override
                    public <T extends DBModel> void onFetchComplete(ArrayList<T> fetchResult) {
                        int seasonYear = ((League) fetchResult.get(0)).getSeasonYear();

                        apiManager.dlLeagueClubs(leagueId, seasonYear, new OnDownloadCompleteListener<Club>() {
                            @Override
                            public <T extends DBModel> void onDownloadComplete(ArrayList<T> downloadResult) {
                                for (T tClub: downloadResult) {
                                    Club club = (Club) tClub;
                                    dbManager.storeObject(club, PathRefs.CLUBS_PATHREF,
                                            task -> System.out.println("[LOG] '" + club.getName() + ", " + seasonYear + "' - successfully downloaded and uploaded to db. ref=" + PathRefs.CLUBS_PATHREF)
                                    );
                                }
                            }
                        });
                    }
                }
        );
    }

    public void dlAndStoreLeagues(String[] leagueIds) {
        for (String leagueId: leagueIds) {
            dlAndStoreLeague(leagueId);
        }
    }

    public void dlAndStoreLeagues() {
        dlAndStoreLeagues(LEAGUE_IDS);
    }

    public void dlAndStoreLeaguesAndClubs(String[] leagueIds) {
        for (String leagueId: leagueIds) {
            dlAndStoreLeague(leagueId, () -> {
                dlAndStoreLeagueClubs(leagueId);
            });
        }
    }

    public void dlAndStoreLeaguesAndClubs() {
        dlAndStoreLeaguesAndClubs(LEAGUE_IDS);
    }

    public void dlAndStoreGamesOfTheDay() {
        dbManager.fetch(PathRefs.LEAGUES_PATHREF, League.class, new OnFetchCompleteListener<League>() {
            @Override
            public <T extends DBModel> void onFetchComplete(ArrayList<T> fetchResult) {
                for (T genLeague:
                     fetchResult) {
                    League league = (League) genLeague;
                    apiManager.dlLeagueGamesOfTheDay(league.getId(), league.getSeasonYear(), new OnDownloadCompleteListener<Game>() {
                        @Override
                        public <T extends DBModel> void onDownloadComplete(ArrayList<T> downloadResult) {
                            for (T genGame:
                            downloadResult) {
                                Game game = (Game) genGame;
                                dbManager.storeObject(game, PathRefs.GAMESOTDAY_PATHREF,
                                        task -> System.out.println("[LOG] '" + game.getLeagueId() + ", " + game.getHomeClubId() + " vs " + game.getAwayClubId() + "' - successfully downloaded and uploaded to db. ref=" + PathRefs.GAMESOTDAY_PATHREF)
                                );
                            }
                        }
                    });
                }
            }
        });
    }

    public void fetchGames(OnGamesFetchedListener listener) {
        dbManager.fetch(PathRefs.GAMESOTDAY_PATHREF, Game.class, new OnFetchCompleteListener<Game>() {

            @Override
            public <T extends DBModel> void onFetchComplete(ArrayList<T> fetchResult) {
                ArrayList<Game> oldGames = new ArrayList<>();
                ArrayList<Game> recentGames = new ArrayList<>();

                for (T gameGen: fetchResult) {
                    Game g = (Game) gameGen;

                    Calendar currentDateTime = Calendar.getInstance();
                    long differenceInHours = (currentDateTime.getTimeInMillis() - g.getDateTime().getTimeInMillis()) / (1000 * 60 * 60);
                    System.out.println("---\n" + g);

                    if (differenceInHours > 24) {
                        oldGames.add(g);
                    } else {
                        recentGames.add(g);
                    }
                }

                listener.onOldGamesFetched(oldGames);
                listener.onRecentGamesFetched(recentGames);
            }
        });
    }
}

interface OnDataUpdatedListener {
    void onDataUpdated();
}

interface OnGamesFetchedListener {
    void onOldGamesFetched(List<Game> gameList);
    void onRecentGamesFetched(List<Game> gameList);
}