package com.example.requestsapi;

import com.google.firebase.database.FirebaseDatabase;

public class DBManager {
    private static DBManager singleton;

    private FirebaseDatabase dbRealtime;

    private DBManager() {
        this.dbRealtime = FirebaseDatabase.getInstance();
    }

    public synchronized static DBManager getInstance() {
        if (singleton == null) {
            singleton = new DBManager();
        }

        return singleton;
    }

    public void storeUser(User user) {
        String userKey = user.getId();
        dbRealtime.getReference("users").child(userKey).setValue(user.toMap());
    }

    public void removeUser(String userId) {
        dbRealtime.getReference("users").child(userId).removeValue();
    }

    public void storeLeague(League league) {
        String leagueKey = league.getId();
        dbRealtime.getReference("leagues").child(leagueKey).setValue(league.toMap());
    }
}
