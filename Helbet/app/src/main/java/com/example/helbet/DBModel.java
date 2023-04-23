package com.example.helbet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class DBModel implements DBExclude, Serializable {
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

interface DBExclude {
    @interface DBExcludeField {}
    default Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        for (java.lang.reflect.Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(DBExcludeField.class)) {
                continue;
            }
            try {
                field.setAccessible(true);
                Object value = field.get(this);
                map.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
