package com.example.helbet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ProfileActivity extends BaseActivity {

    TextView welcome;
    Button authenticate;
    RecyclerView favsRecyclerView;
    ArrayList<ClubItemDataModel> clubsForAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        welcome = findViewById(R.id.tv_welcome);
        authenticate = findViewById(R.id.btn_authentication);
        favsRecyclerView = findViewById(R.id.favorites_recycler_view);
        favsRecyclerView.setHasFixedSize(true);
        favsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        clubsForAdapter = new ArrayList<>();
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    public String getCurrentTitle() {
        return "Profile";
    }

    @Override
    public int getBottomNavSelectItemId() {
        return R.id.menu_profile;
    }

    @Override
    protected void userLogged() {
        welcome.setText("Bonjour, " + session.getCurrentUser().getEmail());
        authenticate.setText("Déconnexion");
        authenticate.setOnClickListener(view -> {
            session.setCurrentUser(null);
            auth.logoutUser();
            finish();
        });

        if (session.getCurrentUser().getFavoriteClubs() != null) {
            for (String clubId :
                    session.getCurrentUser().getFavoriteClubs()) {
                db.fetch(PathRefs.CLUBS_PATHREF, clubId, Club.class, new OnFetchCompleteListener() {
                    @Override
                    public void onFetchComplete(ArrayList fetchResult) {
                        Club club = (Club) fetchResult.get(0);
                        ClubItemDataModel clubForAdapter = new ClubItemDataModel(club);
                        clubForAdapter.setId(club.getId());
                        clubsForAdapter.add(clubForAdapter);
                        if (checkClubsFetch()) {
                            setAdapter();
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void userUnLogged() {
        welcome.setText("Bonjour, invité !");
        authenticate.setText("Connexion");
        authenticate.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        });
    }

    private boolean checkClubsFetch() {
        return session.getCurrentUser().getFavoriteClubs().size() == clubsForAdapter.size();
    }

    private void setAdapter() {
        User user = session.getCurrentUser();
        ClubItemAdapter clubsAdapter = new ClubItemAdapter(clubsForAdapter, user);
        favsRecyclerView.setAdapter(clubsAdapter);
    }
}