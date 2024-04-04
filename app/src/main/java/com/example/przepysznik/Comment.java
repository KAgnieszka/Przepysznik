package com.example.przepysznik;

import java.util.HashMap;
import java.util.Map;

public class Comment {
    private String comment;
    private String time;

    public Comment() {
        // Pusty konstruktor wymagany dla Firebase
    }

    public Comment(String comment, String time) {
        this.comment = comment;
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    // Metoda do mapowania obiektu
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("comment", comment);
        map.put("commentTime", time);
        return map;
    }
}
