package com.example.helbet;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.nio.file.Path;
import java.util.ArrayList;

public class DataManager {
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
        apiManager.dlLeague(leagueId, new OnDownloadCompleteListener<League>() {
            @Override
            public <T extends DBModel> void onDownloadComplete(ArrayList<T> downloadResult) {
                League league = (League) downloadResult.get(0);
                dbManager.storeObject(league, PathRefs.LEAGUES_PATHREF,
                        task -> System.out.println("'" + league.getName() + ", " + league.getSeasonYear() + "' - successfully downloaded and uploaded to db. ref=" + PathRefs.LEAGUES_PATHREF)
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
                                            task -> System.out.println("'" + club.getName() + ", " + seasonYear + "' - successfully downloaded and uploaded to db. ref=" + PathRefs.CLUBS_PATHREF)
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
            dlAndStoreLeague(leagueId);
            dlAndStoreLeagueClubs(leagueId);
        }
    }

    public void dlAndStoreLeaguesAndClubs() {
        dlAndStoreLeaguesAndClubs(LEAGUE_IDS);
    }
}