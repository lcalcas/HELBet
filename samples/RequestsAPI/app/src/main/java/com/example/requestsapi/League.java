package com.example.requestsapi;

import java.util.List;

public class League implements DBData{
    private String id;
    private String name;
    private String logo;

    public League() {}
    public League(String name, String logo) {
        this.name = name;
        this.logo = logo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
