package com.example.helbet;

import android.content.Context;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Game extends DBModel implements Comparable<Game> {
    public String leagueId;
    public long timestamp;
    public String homeClubId;
    public String awayClubId;
    public int result;

    public String approximateAddress;

    public Game() {
    }

    public Game(Game other) {
        this(
                other.getLeagueId(),
                other.getTimestamp(),
                other.getHomeClubId(),
                other.getAwayClubId(),
                other.getResult(),
                other.getApproximateAddress()
        );
    }

    public Game(String leagueId, long timestamp, String homeClubId, String awayClubId, String approximateAddress) {
        this(leagueId, timestamp, homeClubId, awayClubId, Results.NONE, approximateAddress);
    }

    public Game(String leagueId, long timestamp, String homeClubId, String awayClubId, int homeResult, int awayResult, String approximateAddress) {
        this(leagueId, timestamp, homeClubId, awayClubId, calculateResult(homeResult, awayResult), approximateAddress);
    }

    public Game(String leagueId, long timestamp, String homeClubId, String awayClubId, int result, String approximateAddress) {
        this.leagueId = leagueId;
        this.timestamp = timestamp;
        this.homeClubId = homeClubId;
        this.awayClubId = awayClubId;
        this.result = result;
        this.approximateAddress = approximateAddress;
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

    public String getApproximateAddress() {
        return approximateAddress;
    }

    public void setApproximateAddress(String approximateAddress) {
        this.approximateAddress = approximateAddress;
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
    private double latitude;
    private double longitude;

    public GameItemDataModel(Game g, Club home, Club away, Odd odds, double latitude, double longitude) {
        super(g);
        this.home = home;
        this.away = away;
        this.odds = odds;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
                ", latitude=" + latitude +
                ", longitude=" + longitude +
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
    MutableLiveData<User> userLiveData;

    public GameItemAdapter(Context context, ArrayList<GameItemDataModel> games, TimeZone timezone, User user, MutableLiveData<User> userLiveData) {
        this.context = context;
        this.games = games;
        this.timezone = timezone;
        this.user = user;
        this.userLiveData = userLiveData;
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
        AtomicReference<Double> oddValue = new AtomicReference<>(1.5);

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
                holder.betLayout.setVisibility(View.GONE);

                int betAmount = Integer.parseInt(holder.betAmount.getText().toString());
                Bet newBet = new Bet(betResult.get(), betAmount, oddValue.get());
                newBet.setId(g.getId());

                System.out.println(user);

                user.retireMoney(betAmount);
                user.addBet(newBet);

                userLiveData.setValue(user);

                //TODO
                WorkManager.getInstance(context).enqueue(
                        new OneTimeWorkRequest.Builder(NotificationWorker.class).setInitialDelay(300, TimeUnit.SECONDS).build()
                );

                DBManager.getInstance().storeObject(user, "users");
            }
        };

        holder.betConfirm.setOnClickListener(betConfirmListener);

        holder.homeVoteBtn.setOnClickListener(v -> {
            show(holder);
            betResult.set(Results.HOME);
            oddValue.set(odds.getHomeOdd());
        });

        holder.drawVoteBtn.setOnClickListener(v -> {
            show(holder);
            betResult.set(Results.DRAW);
            oddValue.set(odds.getDrawOdd());
        });

        holder.awayVoteBtn.setOnClickListener(v -> {
            show(holder);
            betResult.set(Results.AWAY);
            oddValue.set(odds.getAwayOdd());
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

        holder.venueContainer.setVisibility(View.GONE);

        holder.openMap.setOnClickListener(v -> {
            holder.gameContainer.setVisibility(View.GONE);
            holder.venueContainer.setVisibility(View.VISIBLE);

            holder.mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
            GeoPoint location = new GeoPoint(g.getLatitude(), g.getLongitude());
            Marker marker = new Marker(holder.mapView);
            marker.setPosition(location);
            holder.mapView.getOverlays().add(marker);
            holder.mapView.getController().setCenter(location);
            holder.mapView.setMultiTouchControls(false);
            holder.mapView.getController().setZoom(12.0);
        });

        holder.closeMap.setOnClickListener(v -> {
            holder.venueContainer.setVisibility(View.GONE);
            holder.gameContainer.setVisibility(View.VISIBLE);
        });
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
        private MapView mapView;
        private ImageView closeMap;
        private ImageView openMap;
        private ConstraintLayout venueContainer;
        private ConstraintLayout gameContainer;

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
            mapView = itemView.findViewById(R.id.venue_map);
            openMap = itemView.findViewById(R.id.open_map);
            closeMap = itemView.findViewById(R.id.close_map);
            venueContainer = itemView.findViewById(R.id.venue_container);
            gameContainer = itemView.findViewById(R.id.gameData_container);
        }
    }
}
class Results {
    public static int NONE = 000;
    public static int DRAW = 100;
    public static int HOME = 110;
    public static int AWAY = 101;
}
