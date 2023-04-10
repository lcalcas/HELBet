package com.example.requestsapi;

import java.util.List;

public class Club extends DBModel {

    private String name;
    private String logo;
    private String leagueId;

    public Club() {}
    public Club(String name, String logo, String leagueId) {
        this.name = name;
        this.logo = logo;
        this.leagueId = leagueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLeagueId() {
        return this.leagueId;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    @Override
    public String toString() {
        return "Club{" +
                "name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                "} " + super.toString();
    }
}
