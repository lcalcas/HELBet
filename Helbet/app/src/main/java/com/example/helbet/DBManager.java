package com.example.helbet;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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

    public <T extends DBModel> void storeObject(T o) {
        String key = o.getId();
        Task t = dbRealtime.getReference(key).setValue(o.toMap());
    }

    public <T extends DBModel> void storeObject(T o, OnCompleteListener listener) {
        String key = o.getId();
        Task t = dbRealtime.getReference(key).setValue(o.toMap());

        if (listener != null) {
            t.addOnCompleteListener(listener);
        }
    }

    public <T extends DBModel> void storeObject(T o, String pathRef) {
        storeObject(o , pathRef, null);
    }

    public <T extends DBModel> void storeObject(T o, String pathRef, OnCompleteListener listener) {
        String key = o.getId();
        Task t = dbRealtime.getReference(pathRef).child(key).setValue(o.toMap());

        if (listener != null) {
            t.addOnCompleteListener(listener);
        }
    }

//    public <T extends DBModel> void storeObject(T o, ArrayList<String> childNodes) {
//        String key = o.getId();
//
//        String pathRef = childNodes.get(0);
//
//        DatabaseReference ref = dbRealtime.getReference(pathRef);
//        for (int i = 1; i < childNodes.size(); i++) {
//            ref = ref.child(childNodes.get(i));
//        }
//        ref.setValue(o.toMap());
//    }

    public void deleteObject(String key, String pathRef) {
        dbRealtime.getReference(pathRef).child(key).setValue(null);
    }

    public <T extends DBModel> void fetchQuery(@NonNull Query q, Class<T> _class, boolean multiple, OnFetchCompleteListener listener) {
        q.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<T> result = new ArrayList<>();
                if (snapshot.exists()) {
                    if (multiple) {
                        for (DataSnapshot snapshotChild: snapshot.getChildren()) {
                            T resultItem = snapshotChild.getValue(_class);
                            resultItem.setId(snapshotChild.getKey());
                            result.add(resultItem);
                        }
                    } else {
                        T resultItem = snapshot.getValue(_class);
                        resultItem.setId(snapshot.getKey());
                        result.add(resultItem);
                    }
                    listener.onFetchComplete(result);
                }
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

    // FETCH - FILTER BOUNCED
    public <T extends DBModel> void fetch(String refPath, String timeKey, long timeMin, long timeMax, Class<T> _class, OnFetchCompleteListener listener) {
        Query q = dbRealtime.getReference(refPath).orderByChild(timeKey).startAt(timeMin).endAt(timeMax);
        fetchQuery(q, _class, true, listener);
    }

    // FETCH - ENTIRE REFERENCE
    public <T extends DBModel> void fetch(String refPath, boolean multiple, Class<T> _class, OnFetchCompleteListener listener) {
        Query q = dbRealtime.getReference(refPath);
        fetchQuery(q, _class, multiple, listener);
    }

    public void fetchGames(Date date, OnFetchCompleteListener<Game> listener) {
        this.fetch(Constants.DBPathRefs.GAMES, true, Game.class, new OnFetchCompleteListener<Game>() {

            @Override
            public void onFetchComplete(ArrayList<Game> fetchResult) {
                ArrayList<Game> otherDateGames = new ArrayList<>();
                ArrayList<Game> dateSpecifiedGames = new ArrayList<>();

                for (Game g: fetchResult) {
                    long differenceInHours = (date.getTime() - g.getTimestamp()) / (1000 * 60 * 60);

                    if (differenceInHours > 24) {
                        otherDateGames.add(g);
                    } else {
                        dateSpecifiedGames.add(g);
                    }
                }

                Collections.sort(otherDateGames);
                Collections.sort(dateSpecifiedGames);
            }
        });
    }

//    public <T extends DBModel> void moveChild(String refPathFrom, String refPathTo, String key, Class<T> _class) {
//        fetch(refPathFrom, key, _class, new OnFetchCompleteListener() {
//            @Override
//            public <T extends DBModel> void onFetchComplete(ArrayList<T> fetchResult) {
//                storeObject(fetchResult.get(0), refPathTo, task -> {
//                    deleteObject(key, refPathFrom);
//                });
//            }
//        });
//    }
}

interface OnFetchCompleteListener <T extends DBModel> {
    void onFetchComplete(ArrayList<T> fetchResult);
}