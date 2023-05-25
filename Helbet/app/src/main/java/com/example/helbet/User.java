//package com.example.helbet;
//
//import com.google.firebase.database.IgnoreExtraProperties;
//
//import java.lang.reflect.Array;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@IgnoreExtraProperties
//public class User extends DBModel{
//    private String email;
//    private ArrayList<String> favoriteClubs;
//
//    public User() {}
//
//    public User(String email) {
//        this(email, new ArrayList<String>());
//    }
//
//    public User(String email, ArrayList<String> favoriteClubs) {
//        this.email = email;
//        this.favoriteClubs = favoriteClubs;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public void addFavoriteClub(String clubId) {
//        if (this.favoriteClubs == null) {
//            this.favoriteClubs = new ArrayList<>();
//        }
//
//        favoriteClubs.add(clubId);
//    }
//
//    public void removeFavoriteClub(String clubId) {
//        if (this.favoriteClubs != null) {
//            this.favoriteClubs.remove(clubId);
//        }
//    }
//
//    public boolean isClubFavorite(String clubId) {
//        return this.favoriteClubs != null && this.favoriteClubs.contains(clubId);
//    }
//
//    @Override
//    public String toString() {
//        return "User{" +
//                "email='" + email + '\'' +
//                ", favoriteClubs=" + favoriteClubs +
//                "} " + super.toString();
//    }
//}

package com.example.helbet;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class User extends DBModel{

    private String email;
    private ArrayList<String> favoriteClubs;
    private int balance;
    private ArrayList<Bet> bets;
    public User() {
    }

    public User(String email) {
        this(email, new ArrayList<String>(), 500);
    }

    public User(String email, ArrayList<String> favoriteClubs, int balance) {
        this.email = email;
        this.favoriteClubs = favoriteClubs;
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFavoriteClubs() {
        return favoriteClubs;
    }

    public void setFavoriteClubs(ArrayList<String> favoriteClubs) {
        this.favoriteClubs = favoriteClubs;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public ArrayList<Bet> getBets() {
        return bets;
    }

    public void setBets(ArrayList<Bet> bets) {
        this.bets = bets;
    }

    public void addFavoriteClub(String clubId) {
        if (favoriteClubs == null) {
            setFavoriteClubs(new ArrayList<>());
        }

        favoriteClubs.add(clubId);
    }

    public void removeFavoriteClub(String clubId) {
        if (isClubFavorite(clubId)) {
            favoriteClubs.remove(clubId);
        }
    }

    public boolean isClubFavorite(String clubId) {
        return favoriteClubs != null && favoriteClubs.contains(clubId);
    }

    public String getStringBalance() {
        return String.valueOf(balance);
    }

    public void addBet(Bet bet) {
        if (bets == null) {
            setBets(new ArrayList<>());
        }

        bets.add(bet);
    }

    public void debit(int amount) {
        this.balance -= amount;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", favoriteClubs=" + favoriteClubs +
                ", balance=" + balance +
                ", bets=" + bets +
                "} " + super.toString();
    }
}

