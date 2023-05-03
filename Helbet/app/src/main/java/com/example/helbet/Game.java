package com.example.helbet;

public class Game extends DBModel{
    public String leagueId;
    public String date;
    public String homeClubId;
    public String awayClubId;
    public Integer result;

    public Game() {
    }

    public Game(String leagueId, String homeClubId, String awayClubId, String date, Integer result) {
        this.leagueId = leagueId;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Game{" +
                "leagueId='" + leagueId + '\'' +
                ", date='" + date + '\'' +
                ", homeClubId='" + homeClubId + '\'' +
                ", awayClubId='" + awayClubId + '\'' +
                ", result=" + result +
                "} " + super.toString();
    }
}
