package com.example.models;

public class Stats extends BaseEntity {
    private int wins;
    private int losses;
    private int draws;

    public Stats() {
        this.wins = 0;
        this.losses = 0;
        this.draws = 0;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }
}