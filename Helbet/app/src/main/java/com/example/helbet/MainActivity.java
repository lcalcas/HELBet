package com.example.helbet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

    TextView tv;
    Button button;
    Button button2;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tv = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        logout = findViewById(R.id.buttonLogout);
    }

    @Override
    protected void onStart() {
        super.onStart();

        button2.setText("download");
        button2.setOnClickListener(view -> {
            data.dlAndStoreLeaguesAndClubs();
            data.dlAndStoreGamesOfTheDay();
        });
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public String getCurrentTitle() {
        return "Accueil";
    }

    @Override
    protected void userLogged() {
        // TODO AFFICHAGE PARIS + OPT PARIS
        System.out.println("APPLYING LOGGED PROTOCOL");
        tv.setText("Bonjour, " + Session.getInstance().getCurrentUser().getEmail());
        logout.setText("Déconnexion");
        logout.setOnClickListener(view -> {
            session.setCurrentUser(null);
            auth.logoutUser();
            goToMain();
        });

        button.setText("Parcourir");
        button.setOnClickListener(view -> {
            Intent i = new Intent(this, ListingActivity.class);
            startActivity(i);
        });
    }

    @Override
    protected void userUnLogged() {
        // TODO AFFICHAGE PARIS SANS OPT
        System.out.println("APPLYING UNLOGGED PROTOCOL");
        tv.setText("Bonjour, invité !");
        button.setText("S'authentifier");
        button.setOnClickListener(view -> {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        });

        logout.setVisibility(View.INVISIBLE);
    }
}