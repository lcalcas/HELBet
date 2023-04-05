package com.example.requestsapi;

import static com.example.requestsapi.APIConfig.API_URL;
import static com.example.requestsapi.APIConfig.HEADERS;


import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class APIManager {
    private String apiUrl = API_URL;
    private Context context;

    public APIManager(Context context) {
        this.context = context;
    }

    public void getLeagues(String[] leagueIds) {
        for (String leagueId: leagueIds) {
            String requestUrl = this.apiUrl.concat("leagues?current=true&id=" + leagueId);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray responseData = response.getJSONArray("response");
                                System.out.println(responseData);
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
                        }
                    },
                    new Response.ErrorListener() {
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
}
