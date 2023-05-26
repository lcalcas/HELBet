package com.example.helbet;

public class Constants {

    public final static String[] LEAGUE_IDS = new String[] {"39", "140", "78", "135", "61", "144", "88", "203"};

    class InitialValues {
        public static final int USER_BALANCE = 500;
        public static final double ODD_VALUE = 1.5;
        public static final int NOTIFICATION_ID = 123;
        public static final String NOTIFICATION_CHANNEL_ID = "helbet_channel";
    }

    class DBPathRefs {
        public static final String USERS = "users";
        public static final String LEAGUES = "leagues";
        public static final String CLUBS = "clubs";
        public static final String GAMES = "games";
        public static final String ODDS = "odds";
        public static final String UPDATETIMER = "updateTimer";
    }
}
