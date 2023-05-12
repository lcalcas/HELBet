package com.example.helbet;

import static java.text.DateFormat.getDateInstance;

import android.content.Context;
import android.os.Handler;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.util.ISO8601Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class APIManager {
    private static APIManager singleton;
    private Context context;
//    private boolean requestLock;
//    private ArrayList<Request> requestQueue;

    public final static String API_URL = APIConfig.API_URL;
    public final static HashMap<String, String> HEADERS = APIConfig.HEADERS;

    private APIManager(Context context) {
        this.context = context;
//        this.requestLock = true;
//        this.requestQueue = new ArrayList<>();
    }

    public static APIManager getInstance(Context context) {
        if (singleton == null || singleton.getContext() != context) {
            singleton = new APIManager(context);
        }

        return singleton;
    }

    public Context getContext() {
        return context;
    }

    private void appendRequest(Request request) {
//        if (requestLock) {
//            requestLock = false;
            Volley.newRequestQueue(context).add(request);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    requestLock = true;
//                    if (!requestQueue.isEmpty()) {
//                        appendRequest(requestQueue.remove(0));
//                    }
//                }
//            }, 10000);
//        } else {
//            requestQueue.add(request);
//        }
    }

    public void dlLeagues(ArrayList<String> leagueIds, OnDownloadCompleteListener<League> listener) {
        String requestUrl = API_URL.concat("leagues?current=true");
        ArrayList<League> result = new ArrayList<>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                response -> {
                    try {
                        JSONObject responseData = response.getJSONArray("response").getJSONObject(0);
                        JSONObject leagueData = responseData.getJSONObject("league");
                        JSONObject seasonData = responseData.getJSONArray("seasons").getJSONObject(0);

                        String leagueId = leagueData.getString("id");
                        String leagueName = leagueData.getString("name");
                        String leagueLogo = leagueData.getString("logo");
                        int seasonYear = seasonData.getInt("year");
                        League league = new League(leagueName, leagueLogo, seasonYear);
                        league.setId(leagueId);
                        result.add(league);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    listener.onDownloadComplete(result);
                }, e -> e.printStackTrace()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = HEADERS;
                return params;
            }
        };
    }

    public void dlLeagueClubs(String leagueId, int seasonYear, OnDownloadCompleteListener<Club> listener) {
        String requestUrl = API_URL.concat("teams?season=" + seasonYear + "&league=" + leagueId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Club> clubs = new ArrayList<>();
                        try {
                            JSONArray responseData = response.getJSONArray("response");
                            for (int i = 0; i < responseData.length(); i++) {
                                JSONObject clubData = new JSONObject(responseData.get(i).toString()).getJSONObject("team");
                                String clubId = clubData.getString("id");
                                String clubName = clubData.getString("name");
                                String clubLogo = clubData.getString("logo");
                                String clubCode = clubData.getString("code");

                                Club club = new Club(clubName, clubLogo, leagueId, clubCode);
                                club.setId(clubId);
                                clubs.add(club);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        listener.onDownloadComplete(clubs);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = HEADERS;
                return params;
            }
        };

        appendRequest(request);
    }

    public void dlGames(Date date, OnDownloadCompleteListener<Game> listener) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(date);
        String requestUrl = API_URL.concat("fixtures?&date=" + formattedDate);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Game> games = new ArrayList<>();
                        try {
                            JSONArray responseData = response.getJSONArray("response");
                            for (int i = 0; i < responseData.length(); i++) {
                                JSONObject gameData = new JSONObject(responseData.get(i).toString());
                                JSONObject fixtureData = gameData.getJSONObject("fixture");
                                JSONObject leagueData = gameData.getJSONObject("league");
                                JSONObject teamsData = gameData.getJSONObject("teams");
                                JSONObject goalsData = gameData.getJSONObject("goals");

                                String leagueId = leagueData.getString("id");
                                long timeStamp = fixtureData.getLong("timestamp");
                                String homeClubId = teamsData.getJSONObject("home").getString("id");
                                String awayClubId = teamsData.getJSONObject("away").getString("id");

                                timeStamp *= 1000;

                                Game game;
                                if (goalsData.isNull("home") | goalsData.isNull("away")) {
                                    game = new Game(leagueId, timeStamp, homeClubId, awayClubId);
                                } else {
                                    int homeResult = goalsData.optInt("home", 0);
                                    int awayResult = goalsData.optInt("away", 0);
                                    game = new Game(leagueId, timeStamp, homeClubId, awayClubId, homeResult, awayResult);
                                }
                                game.setId(fixtureData.getString("id"));
                                games.add(game);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        listener.onDownloadComplete(games);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = HEADERS;
                return params;
            }
        };

        appendRequest(request);
    }

    public void dlOdd(String gameId, OnDownloadCompleteListener<Odd> listener) {
        String requestUrl = API_URL.concat("odds?&fixture=" + gameId);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Odd> result = new ArrayList<>();
                        try {
                            JSONObject responseData = response.getJSONArray("response").getJSONObject(0);
                            double homeOdd = 0;
                            double awayOdd = 0;
                            double drawOdd = 0;
                            JSONArray bookmakers = responseData.getJSONArray("bookmakers");
                            for (int i = 0; i < bookmakers.length(); i++) {
                                JSONObject bookMakerData = bookmakers.getJSONObject(i);
                                JSONArray bets = bookMakerData.getJSONArray("bets");
                                for (int j = 0; j < bets.length(); j++) {
                                    JSONObject betData = bets.getJSONObject(j);
                                    String betId = betData.getString("id");
                                    if (
                                            homeOdd == 0
                                            || awayOdd == 0
                                            || drawOdd == 0
                                    ) {
                                        if (betId.equals("1")) {
                                            JSONArray betValues = betData.getJSONArray("values");
                                            for (int k = 0; k < betValues.length(); k++) {
                                                JSONObject valueJSONObject = betValues.getJSONObject(k);
                                                String value = valueJSONObject.getString("value");
                                                double odd = valueJSONObject.getDouble("odd");

                                                if (homeOdd == 0 && value.equals("Home")) {
                                                    homeOdd = odd;
                                                } else if (awayOdd == 0 && value.equals("Away")) {
                                                    awayOdd = odd;
                                                } else if (drawOdd == 0 && value.equals("Draw")) {
                                                    drawOdd = odd;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Odd resultOdd = new Odd(homeOdd, awayOdd, drawOdd);
                            resultOdd.setId(gameId);
                            result.add(resultOdd);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        listener.onDownloadComplete(result);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = HEADERS;
                return params;
            }
        };

        appendRequest(request);
    }

//    public void dlCLubs(OnDownloadCompleteListener<Club> listener) {
//        String requestUrl = API_URL.concat("teams?saison=");
//        ArrayList<League> result = new ArrayList<>();
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
//                response -> {
//                    System.out.println(response);
//                    try {
//                        JSONObject responseData = response.getJSONArray("response").getJSONObject(0);
//                        JSONObject leagueData = responseData.getJSONObject("league");
//                        JSONObject seasonData = responseData.getJSONArray("seasons").getJSONObject(0);
//
//                        String leagueId = leagueData.getString("id");
//                        if(LIST_LEAGUE_IDS.contains(leagueId)) {
//                            String leagueName = leagueData.getString("name");
//                            String leagueLogo = leagueData.getString("logo");
//                            int seasonYear = seasonData.getInt("year");
//                            League league = new League(leagueName, leagueLogo, seasonYear);
//                            league.setId(leagueId);
//                            result.add(league);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    listener.onDownloadComplete(result);
//                }, e -> e.printStackTrace()
//        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = HEADERS;
//                return params;
//            }
//        };
//    }

//    public void dlLeague(String leagueId, OnDownloadCompleteListener<League> listener) {
//        String requestUrl = API_URL.concat("leagues?current=true&id=" + leagueId);
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
//                response -> {
//                    System.out.println(response);
//                    try {
//                        JSONObject responseData = response.getJSONArray("response").getJSONObject(0);
//                        JSONObject leagueData = responseData.getJSONObject("league");
//                        JSONObject seasonData = responseData.getJSONArray("seasons").getJSONObject(0);
//
//                        String leagueId1 = leagueData.getString("id");
//                        String leagueName = leagueData.getString("name");
//                        String leagueLogo = leagueData.getString("logo");
//
//                        int seasonYear = seasonData.getInt("year");
//
//                        League league = new League(leagueName, leagueLogo, seasonYear);
//                        league.setId(leagueId1);
//
//                        listener.onDownloadComplete(new ArrayList<League>() {{ add(league); }});
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }, e -> e.printStackTrace()
//        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = HEADERS;
//                return params;
//            }
//        };
//
//        appendRequest(request);
//    }

//    public void dlLeagues(String[] leagueIds, OnDownloadCompleteListener<League> listener) {
//        String requestUrl = API_URL.concat("leagues?current=true");
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
//                response -> {
//                    System.out.println(response);
//                    try {
//                        JSONObject responseData = response.getJSONArray("response").getJSONObject(0);
//                        JSONObject leagueData = responseData.getJSONObject("league");
//                        JSONObject seasonData = responseData.getJSONArray("seasons").getJSONObject(0);
//
//                        String leagueId1 = leagueData.getString("id");
//                        String leagueName = leagueData.getString("name");
//                        String leagueLogo = leagueData.getString("logo");
//
//                        int seasonYear = seasonData.getInt("year");
//
//                        League league = new League(leagueName, leagueLogo, seasonYear);
//                        league.setId(leagueId1);
//
//                        listener.onDownloadComplete(new ArrayList<League>() {{ add(league); }});
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }, e -> e.printStackTrace()
//        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = HEADERS;
//                return params;
//            }
//        };
//
//        appendRequest(request);
//    }
}

interface OnDownloadCompleteListener<T extends DBModel> {
    void onDownloadComplete(ArrayList<T> downloadResult);
}
