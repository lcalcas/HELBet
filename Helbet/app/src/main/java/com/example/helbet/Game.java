package com.example.helbet;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class Game extends DBModel{
    public String leagueId;
    public String formattedDate;
    public String formattedTime;
    public String homeClubId;
    public String awayClubId;
//    public String homeClubLogo;
//    public String awayClubLogo;
    public int result;

    public Game() {
    }

    public Game(Game other) {
       this.leagueId = other.getLeagueId();
       this.formattedDate = other.getFormattedDate();
       this.formattedTime = other.getFormattedTime();
       this.homeClubId = other.getHomeClubId();
       this.awayClubId = other.getAwayClubId();
//       this.homeClubId = other.getHomeClubLogo();
//       this.awayClubLogo = other.getAwayClubLogo();
       this.result = other.getResult();
    }

    public Game(String leagueId, String formattedDate, String formattedTime, String homeClubId, String awayClubId) {
        this(leagueId, formattedDate, formattedTime, homeClubId, awayClubId, Results.NONE);
    }

    public Game(String leagueId, String formattedDate, String formattedTime, String homeClubId, String awayClubId, int homeResult, int awayResult) {
        this(leagueId, formattedDate, formattedTime, homeClubId, awayClubId, calculateResult(homeResult, awayResult));
    }

    public Game(String leagueId, String formattedDate, String formattedTime, String homeClubId, String awayClubId, int result) {
        this.leagueId = leagueId;
        this.formattedDate = formattedDate;
        this.formattedTime = formattedTime;
        this.homeClubId = homeClubId;
        this.awayClubId = awayClubId;
//        this.homeClubLogo = homeClubLogo;
//        this.awayClubLogo = awayClubLogo;
        this.result = result;
    }

//    public Game(String leagueId, String formattedDate, String formattedTime, String homeClubId, String awayClubId, String homeClubLogo, String awayClubLogo) {
//        this(leagueId, formattedDate, formattedTime, homeClubId, awayClubId, homeClubLogo, awayClubLogo, Results.NONE);
//    }
//
//    public Game(String leagueId, String formattedDate, String formattedTime, String homeClubId, String awayClubId, String homeCLubLogo, String awayClubLogo, int homeResult, int awayResult) {
//        this(leagueId, formattedDate, formattedTime, homeClubId, awayClubId, homeCLubLogo, awayClubLogo, calculateResult(homeResult, awayResult));
//    }
//
//    public Game(String leagueId, String formattedDate, String formattedTime, String homeClubId, String awayClubId, String homeClubLogo, String awayClubLogo, int result) {
//        this.leagueId = leagueId;
//        this.formattedDate = formattedDate;
//        this.formattedTime = formattedTime;
//        this.homeClubId = homeClubId;
//        this.awayClubId = awayClubId;
//        this.homeClubLogo = homeClubLogo;
//        this.awayClubLogo = awayClubLogo;
//        this.result = result;
//    }

    public String getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public String getHomeClubId() {
        return homeClubId;
    }

    public void setHomeClubId(String homeClubId) {
        this.homeClubId = homeClubId;
    }

    public String getAwayClubId() {
        return awayClubId;
    }

    public void setAwayClubId(String awayClubId) {
        this.awayClubId = awayClubId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public static int calculateResult(int homeResult, int awayResult) {
        if (homeResult == awayResult) return Results.DRAW;
        else if (homeResult > awayResult) return Results.HOME;
        else return Results.AWAY;
    }

    public HashMap<String, String> getClubIds() {
        HashMap<String, String> result = new HashMap<>();
        result.put("home", this.homeClubId);
        result.put("away", this.awayClubId);
        return result;
    }

    public Calendar getDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat resultFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        Date parsedDate = null;
        try {
            parsedDate = resultFormatter.parse(this.getFormattedDate() + " " + this.getFormattedTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        calendar.setTime(parsedDate);
        return calendar;
    }

    public long getTimeRemaining() {
        return Calendar.getInstance().getTimeInMillis() - this.getDateTime().getTimeInMillis();
    }

//    public String getHomeClubLogo() {
//        return homeClubLogo;
//    }
//
//    public void setHomeClubLogo(String homeClubLogo) {
//        this.homeClubLogo = homeClubLogo;
//    }
//
//    public String getAwayClubLogo() {
//        return awayClubLogo;
//    }
//
//    public void setAwayClubLogo(String awayClubLogo) {
//        this.awayClubLogo = awayClubLogo;
//    }

    @Override
    public String toString() {
        return "Game{" +
                "leagueId='" + leagueId + '\'' +
                ", formattedDate='" + formattedDate + '\'' +
                ", formattedTime='" + formattedTime + '\'' +
                ", homeClubId='" + homeClubId + '\'' +
                ", awayClubId='" + awayClubId + '\'' +
//                ", homeClubLogo='" + homeClubLogo + '\'' +
//                ", awayClubLogo='" + awayClubLogo + '\'' +
                ", result=" + result +
                "} " + super.toString();
    }
}

class GameItemDataModel extends Game {
    private Club homeClub;
    private Club awayClub;
    private String formattedTimeRemaining;

    GameItemDataModel(Game g) {
        this(g, null, null, null);
    }

    GameItemDataModel(Game g, Club homeClub, Club awayClub, String timeRemaining) {
        super(g);
        this.formattedTimeRemaining = timeRemaining;
        this.homeClub = homeClub;
        this.awayClub = awayClub;
    }

    boolean valid() {
        return !(this.homeClub == null || this.awayClub == null || this.formattedTimeRemaining == null);
    }

    public String getFormattedTimeRemaining() {
        return formattedTimeRemaining;
    }

    public void setFormattedTimeRemaining(String formattedTimeRemaining) {
        this.formattedTimeRemaining = formattedTimeRemaining;
    }

    public Club getHomeClub() {
        return homeClub;
    }

    public void setHomeClub(Club homeClub) {
        this.homeClub = homeClub;
    }

    public Club getAwayClub() {
        return awayClub;
    }

    public void setAwayClub(Club awayClub) {
        this.awayClub = awayClub;
    }
}

class GameItemDataModelFactory {
    private static GameItemDataModelFactory singleton;

    private GameItemDataModelFactory() {

    }

    public synchronized static GameItemDataModelFactory getInstance() {
        if (singleton == null) {
            singleton = new GameItemDataModelFactory();
        }

        return singleton;
    }

    public <T extends Game> GameItemDataModel makeGameItemDataModel(T gameObject, HashMap<String, Club> homeNAwayClubs) {
        Club home = homeNAwayClubs.get("home");
        Club away = homeNAwayClubs.get("away");
        Game g = (Game) gameObject;
        GameItemDataModel result = new GameItemDataModel(g);
        Calendar currentDateTime = Calendar.getInstance(TimeZone.getDefault());
        long timeRemaining = (currentDateTime.getTimeInMillis() - g.getDateTime().getTimeInMillis()) / (1000 * 60 * 60);
        result.setFormattedTimeRemaining((timeRemaining < 0)? String.valueOf(timeRemaining): "0");

        result.setHomeClub(home);
        result.setAwayClub(away);

        return result;
    }
}


class GameItemAdapter extends RecyclerView.Adapter<GameItemAdapter.GameItemViewHolder> {
    ArrayList<GameItemDataModel> games;
    public GameItemAdapter(ArrayList<GameItemDataModel> games) {
        this.games = games;
    }

    @NonNull
    @Override
    public GameItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View gameView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_game, parent, false);
        return new GameItemViewHolder(gameView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameItemViewHolder holder, int position) {
        GameItemDataModel g = games.get(position);

        Club home = g.getHomeClub();
        Club away = g.getAwayClub();

        Glide.with(holder.itemView.getContext())
                .load(home.getLogoUrl())
                .error(R.drawable.error_image)
                .into(holder.homeLogo);

        Glide.with(holder.itemView.getContext())
                .load(away.getLogoUrl())
                .error(R.drawable.error_image)
                .into(holder.awayLogo);

        Handler mHandler = new Handler();

        Runnable mUpdateTimeTask = new Runnable() {
            public void run() {
                holder.timeRemaining.setText(g.getFormattedTimeRemaining());
                mHandler.postDelayed(this, 60000);
            }
        };

        mHandler.post(mUpdateTimeTask);

        if (g.getFormattedTimeRemaining() == "0") {
            holder.homeVoteBtn.setVisibility(View.GONE);
            holder.awayVoteBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class GameItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView homeLogo;
        private ImageView awayLogo;
        private Button homeVoteBtn;
        private Button awayVoteBtn;
        private TextView timeRemaining;

        GameItemViewHolder(@NonNull View itemView) {
            super(itemView);
            homeLogo = itemView.findViewById(R.id.home_logo);
            awayLogo = itemView.findViewById(R.id.away_logo);
            homeVoteBtn = itemView.findViewById(R.id.home_vote_button);
            awayVoteBtn = itemView.findViewById(R.id.away_vote_button);
            timeRemaining = itemView.findViewById(R.id.time_remaining);
        }
    }
}


class Results {
    public static int NONE = 000;
    public static int DRAW = 100;
    public static int HOME = 110;
    public static int AWAY = 101;
}
