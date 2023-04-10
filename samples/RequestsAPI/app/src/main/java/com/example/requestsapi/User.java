package com.example.requestsapi;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class User extends DBModel{

    private String email;
    private List<Club> favoriteClubs;

    public User() {
    }

    public User(String email) {
        this.email = email;
        this.favoriteClubs = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Club> getFavoriteClubs() {
        return favoriteClubs;
    }

    public void setFavoriteClubs(List<Club> favoriteClubs) {
        this.favoriteClubs = favoriteClubs;
    }

    public boolean isFavorite(Club club) {
        return (favoriteClubs == null) ? false : (favoriteClubs.contains(club)) ? true: false;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", favoriteClubs=" + favoriteClubs +
                "} " + super.toString();
    }
}
