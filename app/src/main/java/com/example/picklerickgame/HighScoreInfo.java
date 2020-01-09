package com.example.picklerickgame;

public class HighScoreInfo implements Comparable<HighScoreInfo> {
    private int id;
    private String initials;
    private int score;

    HighScoreInfo(String initials, int score) {
        this.id = id;
        this.initials = initials;
        this.score = score;
    }

    HighScoreInfo() {
        this.id = id;
        this.initials = initials;
        this.score = score;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInitials() {
        return this.initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(HighScoreInfo info) {
        return this.getScore() - info.getScore();
    }
}
