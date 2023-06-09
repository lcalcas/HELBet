package com.example.helbet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class League extends DBModel {
    private String name;
    private String logoUrl;
    private int seasonYear;

    public League() {
    }

    public League(String name, String logoUrl, int seasonYear) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.seasonYear = seasonYear;
    }

    public League(@NonNull League l) {
        this.name = l.getName();
        this.logoUrl = l.getLogoUrl();
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

    public int getSeasonYear() {
        return seasonYear;
    }

    public void setSeasonYear(int seasonYear) {
        this.seasonYear = seasonYear;
    }

    @Override
    public String toString() {
        return "League{" +
                "name='" + name + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", seasonYear=" + seasonYear +
                "} " + super.toString();
    }
}

class LeagueCollectionDataModel extends League {
    private Context context;
    private ArrayList<ClubItemDataModel> clubItems;
    private boolean isExpandable;

    LeagueCollectionDataModel(League league, ArrayList<ClubItemDataModel> clubItems) {
        super(league);
        this.clubItems = clubItems;
        this.isExpandable = false;
    }

    public ArrayList<ClubItemDataModel> getClubItems() {
        return clubItems;
    }

    public void setClubItems(ArrayList<ClubItemDataModel> clubItems) {
        this.clubItems = clubItems;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }
}

class LeagueCollectionAdapter extends RecyclerView.Adapter<LeagueCollectionAdapter.LeagueCollectionViewHolder> {
    Context context;
    ArrayList<LeagueCollectionDataModel> leagues;
    User user;

    public LeagueCollectionAdapter(Context context, ArrayList<LeagueCollectionDataModel> leagues, User user) {
        this.context = context;
        this.leagues = leagues;
        this.user = user;
    }

    @NonNull
    @Override
    public LeagueCollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_league, parent, false);
        return new LeagueCollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeagueCollectionViewHolder holder, int position) {
        LeagueCollectionDataModel model = leagues.get(position);

        Glide.with(context)
            .load(model.getLogoUrl())
            .error(R.drawable.error_image)
            .into(holder.leagueLogoView);

        holder.leagueNameView.setText(model.getName());

        boolean isExpandable = model.isExpandable();

        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE: View.GONE);

        if (isExpandable) {
            holder.leagueExpanderView.setRotation(90);
        } else {
            holder.leagueExpanderView.setRotation(0);
        }

        ClubItemAdapter adapter = new ClubItemAdapter(model.getClubItems(), user);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setAdapter(adapter);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setExpandable(!model.isExpandable());
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.leagues.size();
    }

    public class LeagueCollectionViewHolder extends RecyclerView.ViewHolder {
        private ImageView leagueLogoView, leagueExpanderView;
        private TextView leagueNameView;
        private LinearLayout linearLayout;
        private RelativeLayout expandableLayout;
        private RecyclerView recyclerView;


        public LeagueCollectionViewHolder(@NonNull View itemView) {
            super(itemView);

            this.leagueLogoView = itemView.findViewById(R.id.league_logo);
            this.leagueNameView = itemView.findViewById(R.id.league_name);
            this.leagueExpanderView = itemView.findViewById(R.id.league_expander);

            this.linearLayout = itemView.findViewById(R.id.league_linear_layout);
            this.expandableLayout = itemView.findViewById(R.id.league_expandable_layout);
            this.recyclerView = itemView.findViewById(R.id.clubs_recycler_view);
        }
    }
}
