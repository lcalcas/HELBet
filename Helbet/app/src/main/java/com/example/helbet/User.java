package com.example.helbet;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class User extends DBModel{
    private String email;
    private ArrayList<String> favoriteClubs;

    public User() {}

    public User(String email) {
        this(email, new ArrayList<>());
    }

    public User(String email, ArrayList<String> favoriteClubs) {
        this.email = email;
        this.favoriteClubs = favoriteClubs;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addFavoriteClub(String clubId) {
        this.favoriteClubs.add(clubId);
    }

    public void removeFavoriteClub(String clubId) {
        this.favoriteClubs.remove(clubId);
    }

    public boolean isClubFavorite(String clubId) {
        return this.favoriteClubs != null && this.favoriteClubs.contains(clubId);
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", favoriteClubs=" + favoriteClubs +
                "} " + super.toString();
    }
}
