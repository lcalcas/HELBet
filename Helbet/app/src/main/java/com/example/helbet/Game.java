package com.example.helbet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
    Context context;
    ArrayList<GameItemDataModel> games;
    TimeZone timezone;
    User user;

    public GameItemAdapter(Context context, ArrayList<GameItemDataModel> games, TimeZone timezone, User user) {
        this.context = context;
        this.games = games;
        this.timezone = timezone;
        this.user = user;
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

        AtomicInteger betResult = new AtomicInteger(Results.NONE);

        holder.betConfirm.setEnabled(false);

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
                        "" :
                        (difference < 2) ?
                                "Demain" :
                                dayFormat.format(g.getTimestamp())
        );

        holder.homeLabel.setText(home.getName());
        holder.awayLabel.setText(away.getName());

        holder.homeVoteBtn.setText(String.valueOf(odds.getHomeOdd()));
        holder.awayVoteBtn.setText(String.valueOf(odds.getAwayOdd()));
        holder.drawVoteBtn.setText(String.valueOf(odds.getDrawOdd()));

        View.OnClickListener betConfirmListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int betAmount = Integer.parseInt(holder.betAmount.getText().toString());
                Bet newBet = new Bet(betResult.get(), betAmount);
                newBet.setId(g.getId());
                user.debit(betAmount);
                user.addBet(newBet);

                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "helbet")
                        .setSmallIcon(R.drawable.baseline_h_mobiledata_24)
                        .setContentTitle("Test")
                        .setContentText("textContent")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                notificationManager.notify(123, builder.build());
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC, new Date().getTime() + 10000, pendingIntent);
                DBManager.getInstance().storeObject(user, "users");
            }
        };

        holder.betConfirm.setOnClickListener(betConfirmListener);

        holder.homeVoteBtn.setOnClickListener(v -> {
            show(holder);
            betResult.set(Results.HOME);
        });

        holder.drawVoteBtn.setOnClickListener(v -> {
            show(holder);
            betResult.set(Results.DRAW);
        });

        holder.awayVoteBtn.setOnClickListener(v -> {
            show(holder);
            betResult.set(Results.AWAY);
        });

        holder.betCancel.setOnClickListener(v -> {
            holder.betLayout.setVisibility(View.GONE);
        });

        holder.betAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int i1, int i2) {
                String betInput = charSequence.toString();
                if (!betInput.isEmpty() && betInput.matches("\\d+")) {
                    int betAmount = Integer.parseInt(betInput);
                    if (betAmount > 0) {
                        holder.betConfirm.setEnabled(true);
                    }
                    if (betAmount > user.getBalance()) {
                        holder.betAmount.setText(user.getStringBalance());
                        holder.betAmount.setSelection(holder.betAmount.length());
                    }
                } else {
                    holder.betConfirm.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.betLayout.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public void show(GameItemViewHolder holder) {
        holder.betLayout.setVisibility(View.VISIBLE);
    }


    public void setAlarm(long gameTimestamp) {

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
        private LinearLayout betLayout;
        private EditText betAmount;
        private Button betConfirm;
        private ImageView betCancel;

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
            betLayout = itemView.findViewById(R.id.bet_frame);
            betAmount = itemView.findViewById(R.id.bet_amount);
            betConfirm = itemView.findViewById(R.id.bet_confirm);
            betCancel = itemView.findViewById(R.id.bet_cancel);
        }
    }
}


class Results {
    public static int NONE = 000;
    public static int DRAW = 100;
    public static int HOME = 110;
    public static int AWAY = 101;
}
