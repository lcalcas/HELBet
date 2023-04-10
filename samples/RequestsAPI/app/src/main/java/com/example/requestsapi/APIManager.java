package com.example.requestsapi;

import static com.example.requestsapi.APIConfig.API_URL;
import static com.example.requestsapi.APIConfig.HEADERS;
import static com.example.requestsapi.APIConfig.LEAGUE_IDS;


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

import java.util.List;
import java.util.Map;

public class APIManager {
    private String apiUrl = API_URL;
    private Context context;

    public APIManager(Context context) {
        this.context = context;
    }

    public void getLeagues(OnUploadCompleteListener callback) {
        for (String leagueId: LEAGUE_IDS) {
            String requestUrl = this.apiUrl.concat("leagues?current=true&id=" + leagueId);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray responseData = response.getJSONArray("response");
                                for (int i = 0; i < responseData.length(); i++) {
                                    JSONObject leagueData = new JSONObject(responseData.get(i).toString()).getJSONObject("league");
                                    String leagueId = leagueData.getString("id");
                                    String leagueName = leagueData.getString("name");
                                    String leagueLogo = leagueData.getString("logo");

                                    League league = new League(leagueName, leagueLogo);
                                    league.setId(leagueId);
                                    DBManager.getInstance().storeLeague(league);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            callback.onUploadComplete();
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
            Volley.newRequestQueue(this.context).add(request);
        }
    }

    public void getClubs(OnUploadCompleteListener callback) {
        DBManager.getInstance().fetchRef("leagues", League.class, new OnFetchMultCompleteListener() {
            @Override
            public void onFetchMultComplete(List result) {
                for (Object o: result) {
                    League l = (League) o;
                    String requestUrl = apiUrl.concat("teams?season=2022&league=" + l.getId());
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONArray responseData = response.getJSONArray("response");
                                        for (int i = 0; i < responseData.length(); i++) {
                                            JSONObject clubData = new JSONObject(responseData.get(i).toString()).getJSONObject("team");
                                            String clubId = clubData.getString("id");
                                            String clubName = clubData.getString("name");
                                            String clubLogo = clubData.getString("logo");

                                            Club club = new Club(clubName, clubLogo, l.getId());
                                            club.setId(clubId);
                                            DBManager.getInstance().storeClub(club);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    callback.onUploadComplete();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {

                            Map<String, String> params = HEADERS;
                            return params;
                        }
                    };
                    Volley.newRequestQueue(context).add(request);
                }
            }
        });
    }
}
