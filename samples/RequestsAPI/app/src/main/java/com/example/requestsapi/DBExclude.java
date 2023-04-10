package com.example.requestsapi;

import java.util.HashMap;
import java.util.Map;

public interface DBExclude {
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
