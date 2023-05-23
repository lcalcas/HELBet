package com.example.helbet;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DataManager  {
    private static DataManager singleton;
    private APIManager apiManager;
    private DBManager dbManager;
    public final static ArrayList<String> LIST_LEAGUE_IDS = new ArrayList<>(Arrays.asList(APIConfig.LEAGUE_IDS));

    private DataManager(Context context) {
        apiManager = APIManager.getInstance(context);
        dbManager = DBManager.getInstance();
    }

    public static DataManager getInstance(Context context) {
       if (singleton == null) {
           singleton = new DataManager(context);
       }

       return singleton;
    }

    public void dlAndStoreLeagues(OnDownloadCompleteListener listener) {
        System.out.println("Called");
        apiManager.dlLeagues(LIST_LEAGUE_IDS, new OnDownloadCompleteListener<League>() {
            @Override
            public void onDownloadComplete(ArrayList<League> downloadResult) {
                System.out.println("[dm - 43] " + downloadResult);
                for (League l: downloadResult) {
                    if (LIST_LEAGUE_IDS.contains(l.getId())) {
                        dbManager.storeObject(l, PathRefs.LEAGUES_PATHREF);
                    }
                }

                listener.onDownloadComplete(new ArrayList());
            }
        });
    }

    public void dlAndStoreLeagueClubs(String leagueId) {
        dbManager.fetch(PathRefs.LEAGUES_PATHREF, leagueId, League.class,
                new OnFetchCompleteListener<League>() {
                    @Override
                    public void onFetchComplete(ArrayList<League> fetchResult) {
                        int seasonYear = fetchResult.get(0).getSeasonYear();

                        apiManager.dlLeagueClubs(leagueId, seasonYear, new OnDownloadCompleteListener<Club>() {
                            @Override
                            public void onDownloadComplete(ArrayList<Club> downloadResult) {
                                for (Club c: downloadResult) {
                                    dbManager.storeObject(c, PathRefs.CLUBS_PATHREF);
                                }
                            }
                        });
                    }
                }
        );
    }

    public void dlAndStoreClubs() {
        for (String leagueId: LIST_LEAGUE_IDS) {
            dlAndStoreLeagueClubs(leagueId);
        }
    }

    public void dlAndStoreGames(Date date) {
        dlAndStoreGames(date, null);
    }

    public void dlAndStoreGames(Date date, OnDownloadCompleteListener<Game> listener) {
        apiManager.dlGames(date, new OnDownloadCompleteListener<Game>() {
            @Override
            public void onDownloadComplete(ArrayList<Game> downloadResult) {
                System.out.println("date: " + date);
                System.out.println("Games: " + downloadResult);
                for (Game g: downloadResult) {
                    System.out.println("****Game: " + g);
                    if (LIST_LEAGUE_IDS.contains(g.getLeagueId())) {
                        dbManager.storeObject(g, PathRefs.GAMES_PATHREF,
                                task -> System.out.println("[DBSTORE] game{ " + g.getId() + " } : " + g.getLeagueId() + ", " + g.getHomeClubId() + " vs " + g.getAwayClubId() + "' - successfully downloaded and uploaded to db. ref=" + PathRefs.GAMES_PATHREF)
                        );
                        apiManager.dlOdd(g.getId(), new OnDownloadCompleteListener<Odd>() {
                            @Override
                            public void onDownloadComplete(ArrayList<Odd> downloadResult) {
                                Odd gOdd;
                                gOdd = downloadResult.get(0);
                                gOdd.setId(g.getId());
                                dbManager.storeObject(gOdd, PathRefs.ODDS_PATHREF,
                                        task -> {
                                    dbManager.storeObject(g, PathRefs.GAMES_PATHREF);
                                });
                            }
                        });
                    } else {
                        System.out.println("[DBSTORE] - not stored " + g);
                    }
                }

                if (listener != null) {
                    listener.onDownloadComplete(downloadResult);
                }
            }
        });
    }

//    public void dlAndStoreLeague(String leagueId) {
//        dlAndStoreLeague(leagueId, null);
//    }

//    public void dlAndStoreLeague(String leagueId, OnDataUpdatedListener listener) {
//        apiManager.dlLeague(leagueId, new OnDownloadCompleteListener<League>() {
//            @Override
//            public <T extends DBModel> void onDownloadComplete(ArrayList<T> downloadResult) {
//                League league = (League) downloadResult.get(0);
//                dbManager.storeObject(league, PathRefs.LEAGUES_PATHREF,
//                        task -> {
//                            System.out.println("[LOG] '" + league.getName() + ", " + league.getSeasonYear() + "' - successfully downloaded and uploaded to db. ref=" + PathRefs.LEAGUES_PATHREF);
//                            if (listener != null) {
//                                listener.onDataUpdated();
//                            }
//                        }
//                );
//            }
//        });
//    }

//    public void dlAndStoreLeagues(String[] leagueIds) {
//        for (String leagueId: leagueIds) {
//            dlAndStoreLeague(leagueId);
//        }
//    }

//    public void dlAndStoreLeagues() {
//        dlAndStoreLeagues(LEAGUE_IDS);
//    }

//    public void dlAndStoreLeaguesAndClubs(String[] leagueIds) {
//        for (String leagueId: leagueIds) {
//            dlAndStoreLeague(leagueId, () -> {
//                dlAndStoreLeagueClubs(leagueId);
//            });
//        }
//    }

//    public void dlAndStoreLeaguesAndClubs() {
//        dlAndStoreLeaguesAndClubs(LEAGUE_IDS);
//    }

    public void updateIfNecessary(OnDownloadCompleteListener listener) {
        // TODO CHECK PATHREF
        dbManager.fetch(PathRefs.UPDATETIMER_KEY, false, UpdateTimer.class, new OnFetchCompleteListener<UpdateTimer>() {
            @Override
            public void onFetchComplete(ArrayList<UpdateTimer> fetchResult) {
                UpdateTimer lastUpdate = fetchResult.get(0);
                System.out.println(lastUpdate);
                if (lastUpdate.hasTimeGapPassed(48, 0)) {
                    System.out.println("UPDATE LEAGUES, CLUBS AND GAMES");
                    dlAndStoreLeagues(new OnDownloadCompleteListener() {
                        @Override
                        public void onDownloadComplete(ArrayList downloadResult) {
                            dlAndStoreClubs();
                            dlAndStoreGames(new Date(), new OnDownloadCompleteListener<Game>() {
                                @Override
                                public void onDownloadComplete(ArrayList<Game> downloadResult) {
                                    updateTimer(listener);
                                }
                            });
                        }
                    });
                }
                // TODO REM true
                else if (lastUpdate.hasTimeGapPassed(12, 0)) {
                    System.out.println("UPDATE GOTD");
                    dlAndStoreGames(new Date(), new OnDownloadCompleteListener<Game>() {
                        @Override
                        public void onDownloadComplete(ArrayList<Game> downloadResult) {
                            updateTimer(listener);
                        }
                    });
                }
                else {
                    listener.onDownloadComplete(new ArrayList());
                }
            }
        });
    }

    public void updateTimer(OnDownloadCompleteListener listener) {
        UpdateTimer latestUpdate = new UpdateTimer(new Date());
        latestUpdate.setId(PathRefs.UPDATETIMER_KEY);
        dbManager.storeObject(latestUpdate, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)  {
                listener.onDownloadComplete(new ArrayList());
            }
        });
    }

    public void fetchGamesToDisplay(Date date, TimeZone timeZone, OnGamesFetchedListener listener) {
        System.out.println("Fetching games for: " + date);
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(date);
        System.out.println(calendar);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long timeMin = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long timeMax = calendar.getTimeInMillis();
        dbManager.fetch(PathRefs.GAMES_PATHREF, "timestamp", timeMin, timeMax, Game.class, new OnFetchCompleteListener<Game>() {
            @Override
            public void onFetchComplete(ArrayList<Game> fetchResult) {
                System.out.println("DataManager: " + fetchResult);
                if (!fetchResult.isEmpty()) {
                    ArrayList<Game> result = new ArrayList<>();
                    for (Game g: fetchResult) {
                        result.add(g);
                    }
                    Collections.sort(result);
                    listener.onDateSpecifiedGamesFetched(result, date);
                } else {
                    Date newDate = new Date(date.getTime() + (1000 * 60 * 60 * 24));
                    dlAndStoreGames(newDate, new OnDownloadCompleteListener<Game>() {
                        @Override
                        public void onDownloadComplete(ArrayList<Game> downloadResult) {
                            fetchGamesToDisplay(newDate, timeZone, listener);
                        }
                    });
                }
            }
        });
    }
}

interface OnDataUpdatedListener {
    void onDataUpdated();
}

interface OnGamesFetchedListener {
    void onDateSpecifiedGamesFetched(List<Game> gameList, Date dateSpecified);
}