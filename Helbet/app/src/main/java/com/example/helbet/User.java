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

    public void addFavoriteClub(String clubId) {
//        if (favoriteClubs != null) {
//            favoriteClubs.add(c.getId());
//        } else {
//            setFavoriteClubs(new ArrayList<String>() {{
//                add(c.getId());
//            }});
//        }
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

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", favoriteClubs=" + favoriteClubs +
                "} " + super.toString();
    }
}

