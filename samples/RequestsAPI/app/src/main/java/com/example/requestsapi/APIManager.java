package com.example.requestsapi;

import static com.example.requestsapi.Secret.API_URL;
import static com.example.requestsapi.Secret.HEADERS;


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
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Map;

public class APIManager {
    String apiUrl = API_URL;
    String[] leagueIds = new String[] {"39", "140", "78", "135", "61", "144", "88"};
    Context context;
    FirebaseDatabase realtimeDb;

    APIManager(Context context) {
        this.context = context;
        this.realtimeDb = FirebaseDatabase.getInstance();
        //configConstructor();
    }

    /*void configConstructor() throws XmlPullParserException, IOException {
        Resources res = this.context.getResources();
        XmlResourceParser parser = res.getXml(R.xml.api_config);
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tagName = parser.getName();
                switch (tagName) {
                    case "api_url":
                        this.apiUrl = parser.nextText();
                        break;
                    case "api_key":
                        this.apiKey = parser.nextText();
                        break;
                    case "league":
                        leagueIds.add(parser.getAttributeValue(null, "id"));
                        break;
                }
            }
            eventType = parser.next();
        }
    }*/

    void pushLeagues() {
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
                                    String leagueKey = leagueData.getString("id");
                                    String leagueName = leagueData.getString("name");
                                    String leagueLogo = leagueData.getString("logo");

                                    League league = new League(leagueName, leagueLogo);
                                    realtimeDb.getReference("leagues").child(leagueKey).setValue(league);
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
