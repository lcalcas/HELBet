package com.example.requestsapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class LeagueAdapter extends RecyclerView.Adapter<LeagueAdapter.LeagueViewHolder> {
    private Context context;
    private List<League> listLeagues;

    LeagueAdapter(Context context, List<League> listLeagues) {
        this.context = context;
        this.listLeagues = listLeagues;
    }

    @NonNull
    @Override
    public LeagueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View leagueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_league, parent, false);
        return new LeagueViewHolder(leagueView);
    }

    @Override
    public void onBindViewHolder(@NonNull LeagueViewHolder holder, int position) {
        League league = listLeagues.get(position);

        holder.nameView.setText(league.getName());
        Glide.with(holder.itemView.getContext())
                .load(league.getLogo())
                .error(R.drawable.error_image)
                .into(holder.logoView);
    }

    @Override
    public int getItemCount() {
        return listLeagues.size();
    }

    public class LeagueViewHolder extends RecyclerView.ViewHolder {
        ImageView logoView;
        TextView nameView;

        public LeagueViewHolder(@NonNull View itemView) {
            super(itemView);
            logoView = itemView.findViewById(R.id.league_logo);
            nameView = itemView.findViewById(R.id.league_name);
        }
    }
}
