package com.example.firebaselogin;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RealtimeDatabaseManager {
    static String usersPathString = "users";
    static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    static DatabaseReference mRef = mDatabase.getReference();

    public static void insertUser(String id, User u) {
        mRef.child(usersPathString).child(id).setValue(u);
    }
}
