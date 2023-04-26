package com.example.helbet;

import static com.example.helbet.PathRefs.USERS_PATHREF;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity {
    public final static Class<MainActivity> INDEX = MainActivity.class;
    Session session;

    APIManager api;
    AuthManager auth;
    DataManager data;
    DBManager db;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        session = Session.getInstance();

        api = APIManager.getInstance(getApplicationContext());
        auth = AuthManager.getInstance();
        data = DataManager.getInstance(getApplicationContext());
        db = DBManager.getInstance();

        user = session.getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUser();
    }

    private void checkUser() {
        if (auth.isAuthenticated()) { // Firebase authentification ok ?
            if (user != null && user.getId().equals(auth.getUser().getUid())) { // Realtime Database User ok ?
                userLogged();
            } else {
                db.fetch(USERS_PATHREF, auth.getUser().getUid(), User.class, new OnFetchCompleteListener<User>() {
                    @Override
                    public <T extends DBModel> void onFetchComplete(ArrayList<T> fetchResult) {
                        if (fetchResult.size() == 1) {
                            session.setCurrentUser((User) fetchResult.get(0));
                            recreate();
                        } else {
                            userUnLogged();
                        }
                    }
                });
            }
        } else {
            userUnLogged();
        }
    }

    public void goToMain() {
        Intent intent = new Intent(getApplicationContext(), INDEX);
        startActivity(intent);
        finish();
    }

    protected abstract void userLogged();
    protected abstract void userUnLogged();
}