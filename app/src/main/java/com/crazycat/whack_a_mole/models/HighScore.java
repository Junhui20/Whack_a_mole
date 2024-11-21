package com.crazycat.whack_a_mole.models;

public class HighScore {
    private final String playerName;
    private final int score;

    public HighScore(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    public String getName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }
} 