package com.example.helbet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Game extends DBModel implements Comparable<Game> {
    public String leagueId;
    public long timestamp;
    public String homeClubId;
    public String awayClubId;
    public int result;

    public Game() {
    }

    public Game(Game other) {
       this(
               other.getLeagueId(),
               other.getTimestamp(),
               other.getHomeClubId(),
               other.getAwayClubId(),
               other.getResult()
       );
    }

    public Game(String leagueId, long timestamp, String homeClubId, String awayClubId) {
        this(leagueId, timestamp, homeClubId, awayClubId, Results.NONE);
    }

    public Game(String leagueId, long timestamp, String homeClubId, String awayClubId, int homeResult, int awayResult) {
        this(leagueId, timestamp, homeClubId, awayClubId, calculateResult(homeResult, awayResult));
    }

    public Game(String leagueId, long timestamp, String homeClubId, String awayClubId, int result) {
        this.leagueId = leagueId;
        this.timestamp = timestamp;
        this.homeClubId = homeClubId;
        this.awayClubId = awayClubId;
        this.result = result;
    }

    public String getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    @Override
    public String toString() {
        return "Game{" +
                "leagueId='" + leagueId + '\'' +
                ", timestamp=" + timestamp +
                ", homeClubId='" + homeClubId + '\'' +
                ", awayClubId='" + awayClubId + '\'' +
                ", result=" + result +
                "} " + super.toString();
    }

    @Override
    public int compareTo(Game other) {
        return Long.compare(this.timestamp, ((Game) other).getTimestamp());
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



}

class GameItemDataModel extends Game {
    private Club home;
    private Club away;
    private Odd odds;

    public GameItemDataModel(Game g, Club home, Club away, Odd odds) {
        super(g);
        this.home = home;
        this.away = away;
        this.odds = odds;
    }

    public Club getHome() {
        return home;
    }

    public void setHome(Club home) {
        this.home = home;
    }

    public Club getAway() {
        return away;
    }

    public void setAway(Club away) {
        this.away = away;
    }

    public Odd getOdds() {
        return odds;
    }

    public void setOdds(Odd odds) {
        this.odds = odds;
    }

    public String getFormattedTime(TimeZone timezone) {
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(timezone);
        Date date = new Date(this.getTimestamp());
        return formatter.format(date);
    }

    @Override
    public String toString() {
        return "GameItemDataModel{" +
                "home=" + home +
                ", away=" + away +
                ", odds=" + odds +
                "} " + super.toString();
    }
}

//class GameItemDataModelFactory {
//    private static GameItemDataModelFactory singleton;
//
//    private GameItemDataModelFactory() {
//
//    }
//
//    public synchronized static GameItemDataModelFactory getInstance() {
//        if (singleton == null) {
//            singleton = new GameItemDataModelFactory();
//        }
//
//        return singleton;
//    }
//
//    public <T extends Game> GameItemDataModel makeGameItemDataModel(T gameObject, HashMap<String, Club> homeNAwayClubs) {
//        Club home = homeNAwayClubs.get("home");
//        Club away = homeNAwayClubs.get("away");
//        Game g = (Game) gameObject;
//        GameItemDataModel result = new GameItemDataModel(g);
//        Calendar currentDateTime = Calendar.getInstance(TimeZone.getDefault());
//        long timeRemaining = (currentDateTime.getTimeInMillis() - g.getDateTime().getTimeInMillis()) / (1000 * 60 * 60);
//        result.setFormattedTimeRemaining((timeRemaining < 0)? String.valueOf(timeRemaining): "0");
//
//        result.setHomeClub(home);
//        result.setAwayClub(away);
//
//        return result;
//    }
//}


class GameItemAdapter extends RecyclerView.Adapter<GameItemAdapter.GameItemViewHolder> {
    ArrayList<GameItemDataModel> games;
    TimeZone timezone;
    public GameItemAdapter(ArrayList<GameItemDataModel> games, TimeZone timezone) {
        this.games = games;
        this.timezone = timezone;
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

        Club home = g.getHome();
        Club away = g.getAway();
        Odd odds = g.getOdds();

        Glide.with(holder.itemView.getContext())
                .load(home.getLogoUrl())
                .error(R.drawable.error_image)
                .into(holder.homeLogo);

        Glide.with(holder.itemView.getContext())
                .load(away.getLogoUrl())
                .error(R.drawable.error_image)
                .into(holder.awayLogo);

        holder.timeView.setText(g.getFormattedTime(timezone));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(timezone);
        calendar.setTime(new Date());
        int dayNow = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(new Date(g.getTimestamp()));
        int matchDay = calendar.get(Calendar.DAY_OF_YEAR);
        int difference = matchDay - dayNow;

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("fr", "FR"));
        holder.dayView.setText(
                (difference < 1) ?
                            "":
                            (difference < 2) ?
                                    "Demain":
                                    dayFormat.format(g.getTimestamp())
        );

        holder.homeLabel.setText(home.getName());
        holder.awayLabel.setText(away.getName());

        holder.homeVoteBtn.setText(String.valueOf(odds.getHomeOdd()));
        holder.awayVoteBtn.setText(String.valueOf(odds.getAwayOdd()));
        holder.drawVoteBtn.setText(String.valueOf(odds.getDrawOdd()));
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class GameItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView homeLogo;
        private ImageView awayLogo;
        private TextView homeLabel;
        private TextView awayLabel;
        private Button homeVoteBtn;
        private Button awayVoteBtn;
        private Button drawVoteBtn;
        private TextView dayView;
        private TextView timeView;

        GameItemViewHolder(@NonNull View itemView) {
            super(itemView);
            homeLogo = itemView.findViewById(R.id.home_logo);
            awayLogo = itemView.findViewById(R.id.away_logo);
            homeLabel = itemView.findViewById(R.id.home_club_name);
            awayLabel = itemView.findViewById(R.id.away_club_name);
            homeVoteBtn = itemView.findViewById(R.id.home_vote_button);
            awayVoteBtn = itemView.findViewById(R.id.away_vote_button);
            drawVoteBtn = itemView.findViewById(R.id.draw_vote_button);
            dayView = itemView.findViewById(R.id.day);
            timeView = itemView.findViewById(R.id.time);
        }
    }
}


class Results {
    public static int NONE = 000;
    public static int DRAW = 100;
    public static int HOME = 110;
    public static int AWAY = 101;
}
