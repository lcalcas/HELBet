package com.example.requestsapi;

import java.util.List;

public class League extends DBModel {

    private String name;
    private String logo;

    public League() {}
    public League(String name, String logo) {
        this.name = name;
        this.logo = logo;
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

    @Override
    public String toString() {
        return "League{" +
                "name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                "} " + super.toString();
    }
}
