package com.example.requestsapi;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBManager<T> {
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

    public void storeClub(Club club) {
        String clubKey = club.getId();
        dbRealtime.getReference("clubs").child(clubKey).setValue(club.toMap());
    }

    public <T extends DBModel> void storeObject(T o, String pathRef) {
        String key = o.getId();
        dbRealtime.getReference(pathRef).child(key).setValue(o.toMap());
    }

    public void fetchRef(String refPath, Class<T> _class, OnFetchMultCompleteListener listener) {
        DatabaseReference ref = dbRealtime.getReference(refPath);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<T> result = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    T resultItem = dataSnapshot.getValue(_class);
                    if (resultItem instanceof DBModel) {
                        ((DBModel) resultItem).setId(dataSnapshot.getKey());
                    }
                    result.add(resultItem);
                }
                listener.onFetchMultComplete(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void fetch(String refPath, String key, Class<T> _class, OnFetchSingCompleteListener listener) {
        DatabaseReference ref = dbRealtime.getReference(refPath).child(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                T result = snapshot.getValue(_class);
                if (result instanceof DBModel) {
                    ((DBModel) result).setId(snapshot.getKey());
                }
                listener.onFetchComplete(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    public void fetchFiltered(String refPath, Map<String, String> params, Class<T> _class, OnFetchMultCompleteListener listener) {
        DatabaseReference ref = dbRealtime.getReference(refPath);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<T> result = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    boolean add = true;
                    for (Map.Entry<String, String> entry: params.entrySet()) {
                        if (dataSnapshot.hasChild(entry.getKey())) {
                            if (!dataSnapshot.child(entry.getKey()).getValue(String.class).equals(entry.getValue())) {
                                add = false;
                            }
                        } else {
                            add = false;
                        }
                    }
                    if (add) {
                        T resultItem = dataSnapshot.getValue(_class);
                        if (resultItem instanceof DBModel) {
                            ((DBModel) resultItem).setId(dataSnapshot.getKey());
                        }
                        result.add(resultItem);
                    }
                }
                listener.onFetchMultComplete(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
