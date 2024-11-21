package com.crazycat.whack_a_mole;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.crazycat.whack_a_mole.models.HighScore;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScoreManager {
    private final SharedPreferences preferences;
    private final Gson gson;
    private final String keyScores;

    public ScoreManager(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        
        String prefName = context.getString(R.string.pref_high_scores);
        this.keyScores = context.getString(R.string.key_scores);
        this.preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public List<HighScore> getHighScores() {
        String json = preferences.getString(keyScores, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<HighScore>>(){}.getType();
        List<HighScore> scores = gson.fromJson(json, type);
        scores.sort((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()));
        return scores;
    }

    public boolean isHighScore(int score) {
        List<HighScore> scores = getHighScores();
        return scores.size() < 25 || score > scores.get(scores.size() - 1).getScore();
    }

    public void addHighScore(String name, int score) {
        List<HighScore> scores = getHighScores();
        scores.add(new HighScore(name, score));
        scores.sort((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()));
        
        if (scores.size() > 25) {
            scores = scores.subList(0, 25);
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(keyScores, gson.toJson(scores));
        editor.apply();
    }
}