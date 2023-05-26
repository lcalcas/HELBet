package com.example.helbet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity{
    Session session;

    APIManager api;
    AuthManager auth;
    DataManager data;
    DBManager db;

    ImageView backArrow;
    TextView fragTitle;
    TextView balance;
    ImageView balanceIcon;
    BottomNavigationView bottomNav;

    MutableLiveData<User> userLiveData;

    NotificationManagerCompat notificationManager;


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
        balance = findViewById(R.id.fb_balance_amount);
        balanceIcon = findViewById(R.id.fb_balance_image);
        bottomNav = findViewById(R.id.bottom_nav);

        userLiveData = new MutableLiveData<>();

        notificationManager = NotificationManagerCompat.from(this);

        createNotificationChannel();
    }

    public abstract int getContentLayoutId();

    public abstract String getCurrentTitle();

    public abstract int getBottomNavSelectItemId();

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

        bottomNav.setSelectedItemId(getBottomNavSelectItemId());
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent i = null;
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        i = new Intent(getApplicationContext(), MainActivity.class);
                        break;
                    case R.id.menu_search:
                        i = new Intent(getApplicationContext(), ListingActivity.class);
                        break;
                    case R.id.menu_profile:
                        i = new Intent(getApplicationContext(), ProfileActivity.class);
                        break;
                }
                if (i != null) {
                    startActivity(i);
                    finish();
                }
                return true;
            }
        });

        setToolBar();
        checkUser();

        userLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                // Update the TextView with the new user data
                balance.setText(user.getStringBalance());
            }
        });
    }

    private void checkUser() {
        if (auth.isAuthenticated()) { // Firebase authentification ok ?
            User user = session.getCurrentUser();
            if (user != null && user.getId().equals(auth.getUser().getUid())) { // Realtime Database User ok ?
                userLogged();
            } else {
                db.fetch(Constants.DBPathRefs.USERS, auth.getUser().getUid(), User.class, new OnFetchCompleteListener<User>() {
                    @Override
                    public void onFetchComplete(ArrayList<User> fetchResult) {
                        if (fetchResult.size() == 1) {
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
            userUnLogged();
        }
    }

    public void goToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    protected void userLogged() {
        userLiveData.setValue(session.getCurrentUser());
    }
    protected void userUnLogged() {
        setBalanceDisplay(View.INVISIBLE);
    }

    public void triggerBalanceDisplay() {
        if (balance.isShown()) {
            setBalanceDisplay(View.INVISIBLE);
        } else {
            setBalanceDisplay(View.VISIBLE);
        }
    }

    public void setBalanceDisplay(int visibility) {
        balance.setVisibility(visibility);
        balanceIcon.setVisibility(visibility);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Paris";
            String description = "Helbet Paris";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constants.InitialValues.NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}