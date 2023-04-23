package com.example.requestsapi;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class User extends DBModel{

    private String email;
    private ArrayList<String> favoriteClubs;

    public User() {
    }

    public User(String email) {
        this(email, new ArrayList<String>());
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

    public List<String> getFavoriteClubs() {
        return favoriteClubs;
    }

    public void setFavoriteClubs(ArrayList<String> favoriteClubs) {
        this.favoriteClubs = favoriteClubs;
    }

    public void addFavoriteClub(Club c) {
        if (favoriteClubs != null) {
            favoriteClubs.add(c.getId());
        } else {
            setFavoriteClubs(new ArrayList<>() {{
                add(c.getId());
            }});
        }
    }

    public void removeFavoriteClub(Club club) {
        favoriteClubs.remove(club.getId());
    }

    public boolean isFavorite(Club club) {
        return favoriteClubs != null && favoriteClubs.contains(club.getId());
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", favoriteClubs=" + favoriteClubs +
                "} " + super.toString();
    }
}
