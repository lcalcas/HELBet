package com.example.helbet;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

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

    public <T extends DBModel> void storeObject(T o, String pathRef) {
        storeObject(o , pathRef, null) ;
    }

    public <T extends DBModel> void storeObject(T o, String pathRef, OnCompleteListener listener) {
        String key = o.getId();
        Task t = dbRealtime.getReference(pathRef).child(key).setValue(o.toMap());

        if (listener != null) {
            t.addOnCompleteListener(listener);
        }
    }

    public <T extends DBModel> void fetchQuery(Query q, Class<T> _class, boolean multiple, OnFetchCompleteListener listener) {
        q.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<T> result = new ArrayList<>();
                if (multiple) {
                    snapshot.getChildren().forEach(snapshotChild -> {
                        T resultItem = snapshotChild.getValue(_class);
                        resultItem.setId(snapshotChild.getKey());
                        result.add(resultItem);
                    });
                } else {
                    T resultItem = snapshot.getValue(_class);
                    resultItem.setId(snapshot.getKey());
                    result.add(resultItem);
                }

                listener.onFetchComplete(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // FETCH - KEY
    public <T extends DBModel> void fetch(String refPath, String key, Class<T> _class, OnFetchCompleteListener listener) {
        Query q = dbRealtime.getReference(refPath).child(key);
        fetchQuery(q, _class, false, listener);
    }

    // FETCH - FILTER
    public <T extends DBModel> void fetch(String refPath, String filterKey, String filterValue, Class<T> _class, OnFetchCompleteListener listener) {
        Query q = dbRealtime.getReference(refPath).orderByChild(filterKey).equalTo(filterValue);
        fetchQuery(q, _class, true, listener);
    }

    // FETCH - ENTIRE REFERENCE
    public <T extends DBModel> void fetch(String refPath, Class<T> _class, OnFetchCompleteListener listener) {
        Query q = dbRealtime.getReference(refPath);
        fetchQuery(q, _class, true, listener);
    }
}

class PathRefs {
    public static final String USERS_PATHREF = "users";
    public static final String LEAGUES_PATHREF = "leagues";
    public static final String CLUBS_PATHREF = "clubs";
}

interface OnFetchCompleteListener<T> {
    <T extends DBModel> void onFetchComplete(ArrayList<T> fetchResult);
}
