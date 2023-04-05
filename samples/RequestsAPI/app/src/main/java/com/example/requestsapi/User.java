package com.example.requestsapi;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class User implements DBData{
    @Exclude
    private String id;

    private String email;
    private List<Club> favoriteClubs;

    public User() {
    }

    public User(String email) {
        this.email = email;
        this.favoriteClubs = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return (favoriteClubs.contains(club)) ? true: false;
    }
}
