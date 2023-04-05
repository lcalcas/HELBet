package com.example.requestsapi;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ClubViewHolder> {

    public Context context;
    public List<Club> listClubs;

    public ClubAdapter(Context context, List<Club> listClubs) {
        this.context = context;
        this.listClubs = listClubs;
    }

    @NonNull
    @Override
    public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ClubViewHolder holder, int position) {
        Club club = listClubs.get(position);

        Glide.with(holder.itemView.getContext())
                .load(club.getLogo())
                .error(R.drawable.error_image)
                .into(holder.logoView);
        holder.nameView.setText(club.getName());


    }

    @Override
    public int getItemCount() {
        return 0;
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
