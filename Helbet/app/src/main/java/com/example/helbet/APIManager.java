package com.example.helbet;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class APIManager {
    private static APIManager singleton;
    private Context context;

    public final static String API_URL = APIConfig.API_URL;
    public final static HashMap<String, String> HEADERS = APIConfig.HEADERS;

    private APIManager(Context context) {
        this.context = context;
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
        Volley.newRequestQueue(context).add(request);
    }

    public void dlLeague(String leagueId, OnDownloadCompleteListener<League> listener) {
        String requestUrl = API_URL.concat("leagues?current=true&id=" + leagueId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                response -> {
                    try {
                        JSONObject responseData = response.getJSONArray("response").getJSONObject(0);
                        JSONObject leagueData = responseData.getJSONObject("league");
                        JSONObject seasonData = responseData.getJSONArray("seasons").getJSONObject(0);

                        String leagueId1 = leagueData.getString("id");
                        String leagueName = leagueData.getString("name");
                        String leagueLogo = leagueData.getString("logo");

                        int seasonYear = seasonData.getInt("year");

                        League league = new League(leagueName, leagueLogo, seasonYear);
                        league.setId(leagueId1);

                        listener.onDownloadComplete(new ArrayList<League>() {{ add(league); }});
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, e -> e.printStackTrace()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = HEADERS;
                return params;
            }
        };

        appendRequest(request);
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

                                Club club = new Club(clubName, clubLogo, leagueId);
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
}

interface OnDownloadCompleteListener<T> {
    <T extends DBModel> void onDownloadComplete(ArrayList<T> downloadResult);
}
