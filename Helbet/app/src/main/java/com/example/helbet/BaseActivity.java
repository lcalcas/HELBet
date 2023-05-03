package com.example.helbet;

import static com.example.helbet.PathRefs.USERS_PATHREF;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity {
    public final static Class<MainActivity> INDEX = MainActivity.class;
    Session session;

    APIManager api;
    AuthManager auth;
    DataManager data;
    DBManager db;

    ImageView backArrow;
    TextView fragTitle;

    @Override
    public void setContentView(int layoutResID) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = coordinatorLayout.findViewById(R.id.layout_container);
        ConstraintLayout main = (ConstraintLayout) getLayoutInflater().inflate(layoutResID, activityContainer, false);
        activityContainer.removeAllViews();
        activityContainer.addView(main);
        super.setContentView(coordinatorLayout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayoutId());

        session = Session.getInstance();

        api = APIManager.getInstance(getApplicationContext());
        auth = AuthManager.getInstance();
        data = DataManager.getInstance(getApplicationContext());
        db = DBManager.getInstance();

        backArrow = findViewById(R.id.fb_back);
        fragTitle = findViewById(R.id.fb_title);
    }

    public abstract int getContentLayoutId();

    public abstract String getCurrentTitle();

    private void setCurrentTitle() {
        fragTitle.setText(getCurrentTitle());
    }

    private void setBackArrow() {
        backArrow.setOnClickListener(view -> finish());
    }

    private void setToolBar() {
        setCurrentTitle();
        setBackArrow();
    }

    @Override
    protected void onStart() {
        super.onStart();

        setToolBar();
        checkUser();
    }

    private void checkUser() {
        if (auth.isAuthenticated()) { // Firebase authentification ok ?
            System.out.println("AUTHENTICATED");
            User user = session.getCurrentUser();
            if (user != null && user.getId().equals(auth.getUser().getUid())) { // Realtime Database User ok ?
                System.out.println("LOGGED");
                userLogged();
            } else {
                System.out.println("NOT LOGGED");
                db.fetch(USERS_PATHREF, auth.getUser().getUid(), User.class, new OnFetchCompleteListener<User>() {
                    @Override
                    public <T extends DBModel> void onFetchComplete(ArrayList<T> fetchResult) {
                        if (fetchResult.size() == 1) {
                            System.out.println("USER FETCHED FROM DB");
                            session.setCurrentUser((User) fetchResult.get(0));
                            goToMain();
                        } else {
                            auth.logoutUser();
                            userUnLogged();
                        }
                    }
                });

            }
        } else {
            System.out.println("NOT AUTHENTICATED");
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