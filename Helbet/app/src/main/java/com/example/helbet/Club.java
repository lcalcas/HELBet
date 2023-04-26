package com.example.helbet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Club extends DBModel {
    private String name;
    private String logoUrl;
    private String leagueId;

    public Club() {
    }

    public Club(String name, String logoUrl, String leagueId) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.leagueId = leagueId;
    }

    public Club(@NonNull Club c) {
        this.name = c.getName();
        this.logoUrl = c.getLogoUrl();
        this.leagueId = c.getLeagueId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    @Override
    public String toString() {
        return "Club{" +
                "name='" + name + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", leagueId='" + leagueId + '\'' +
                "} " + super.toString();
    }
}

class ClubItemDataModel extends Club {
    public ClubItemDataModel(Club club) {
        super(club);
    }
}

class ClubItemAdapter extends RecyclerView.Adapter<ClubItemAdapter.ClubItemViewHolder> {
    ArrayList<ClubItemDataModel> clubs;
    User user;

    public ClubItemAdapter(ArrayList<ClubItemDataModel> clubs, User user) {
        this.clubs = clubs;
        this.user = user;
    }

    @NonNull
    @Override
    public ClubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View clubView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_club, parent, false );
        return new ClubItemViewHolder(clubView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubItemViewHolder holder, int position) {
        ClubItemDataModel club = clubs.get(position);

        holder.clubNameView.setText(club.getName());

        Glide.with(holder.itemView.getContext())
                .load(club.getLogoUrl())
                .error(R.drawable.error_image)
                .into(holder.clubLogoView);

        holder.clubFavoriteView.setChecked(user.isClubFavorite(club.getId()));
        holder.clubFavoriteView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    user.addFavoriteClub(club.getId());
                } else {
                    user.removeFavoriteClub(club.getId());
                }

                DBManager.getInstance().storeObject(user, "users");
            }
        });
    }

    @Override
    public int getItemCount() {
        return (clubs == null)? 0: clubs.size();
    }

    public class ClubItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView clubLogoView;
        private TextView clubNameView;
        private Switch clubFavoriteView;

        public ClubItemViewHolder(@NonNull View itemView) {
            super(itemView);
            clubLogoView = itemView.findViewById(R.id.club_logo);
            clubNameView = itemView.findViewById(R.id.club_name);
            clubFavoriteView = itemView.findViewById(R.id.club_favorite);
        }
    }
}
