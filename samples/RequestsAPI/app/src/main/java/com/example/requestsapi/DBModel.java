package com.example.requestsapi;

import java.util.Map;

public abstract class DBModel implements DBExclude {
    @DBExcludeField
    private String id;

    public DBModel() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DBModel{" +
                "id='" + id + '\'' +
                '}';
    }
}
