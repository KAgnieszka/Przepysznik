package com.example.przepysznik;

public class UserComment {
    private String userId;
    private String nick;
    private String comment;
    private String time;

    public UserComment() {
        // Pusty konstruktor wymagany przez Firebase
    }

    public UserComment(String userId, String nick, String comment, String time) {
        this.userId = userId;
        this.nick = nick;
        this.comment = comment;
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public String getNick() {
        return nick;
    }

    public String getComment() {
        return comment;
    }

    public String getTime() {
        return time;
    }
}
