package com.example.helbet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class TestActivity extends BaseActivity {

    Button app;
    Button update;
    Button logout;
    Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = findViewById(R.id.app);
        update = findViewById(R.id.update);
        logout = findViewById(R.id.logout);
        delete = findViewById(R.id.delete);
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    public String getCurrentTitle() {
        return "Accueil";
    }

    @Override
    public int getBottomNavSelectItemId() {
        return R.id.menu_profile;
    }

    private void logout() {

    }

    @Override
    protected void userLogged() {
        app.setOnClickListener(view -> {
            Intent i = new Intent(this, ListingActivity.class);
            startActivity(i);
        });

        update.setOnClickListener(view -> {
            data.dlAndStoreLeaguesAndClubs();
        });

        logout.setOnClickListener(view -> {
            logout();
        });

        delete.setOnClickListener(view -> {
            User user = session.getCurrentUser();
            db.deleteObject(user.getId(), PathRefs.USERS_PATHREF);
            auth.getUser().delete();
            logout();
        });
    }

    @Override
    protected void userUnLogged() {
        goToMain();
    }
}