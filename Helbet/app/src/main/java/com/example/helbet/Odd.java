package com.example.helbet;

public class Odd extends DBModel {
    double homeOdd;
    double awayOdd;
    double drawOdd;

    public Odd() {
    }

    public Odd(double homeOdd, double awayOdd, double drawOdd) {
        this.homeOdd = homeOdd;
        this.awayOdd = awayOdd;
        this.drawOdd = drawOdd;
    }

    public double getHomeOdd() {
        return homeOdd;
    }

    public void setHomeOdd(double homeOdd) {
        this.homeOdd = homeOdd;
    }

    public double getAwayOdd() {
        return awayOdd;
    }

    public void setAwayOdd(double awayOdd) {
        this.awayOdd = awayOdd;
    }

    public double getDrawOdd() {
        return drawOdd;
    }

    public void setDrawOdd(double drawOdd) {
        this.drawOdd = drawOdd;
    }

    @Override
    public String toString() {
        return "Odd{" +
                "homeOdd=" + homeOdd +
                ", awayOdd=" + awayOdd +
                ", drawOdd=" + drawOdd +
                "} " + super.toString();
    }
}
