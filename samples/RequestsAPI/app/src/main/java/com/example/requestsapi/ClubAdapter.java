package com.example.requestsapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ClubViewHolder> {
    private Context context;
    private List<Club> listClubs;
    private User user;

    public ClubAdapter(Context context, List<Club> listClubs, User user) {
        this.context = context;
        this.listClubs = listClubs;
        this.user = user;
    }

    @NonNull
    @Override
    public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View clubView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_club, parent, false );
        return new ClubViewHolder(clubView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubViewHolder holder, int position) {
        Club club = listClubs.get(position);

        Glide.with(holder.itemView.getContext())
                .load(club.getLogo())
                .error(R.drawable.error_image)
                .into(holder.logoView);
        holder.nameView.setText(club.getName());
        holder.switchView.setChecked(user.isFavorite(club));
    }

    @Override
    public int getItemCount() {
        return listClubs.size();
    }

    public class ClubViewHolder extends RecyclerView.ViewHolder {
        ImageView logoView;
        TextView nameView;
        Switch switchView;

        public ClubViewHolder(@NonNull View itemView) {
            super(itemView);

            logoView = itemView.findViewById(R.id.club_logo);
            nameView = itemView.findViewById(R.id.club_name);
            switchView = itemView.findViewById(R.id.club_switch);
        }
    }
}
