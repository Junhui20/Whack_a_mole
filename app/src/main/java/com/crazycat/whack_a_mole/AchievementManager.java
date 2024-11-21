package com.crazycat.whack_a_mole;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages game achievements, including their initialization, unlocking conditions,
 * and persistence using SharedPreferences.
 */
public class AchievementManager {
    private static final String PREFS_NAME = "achievements";
    private static final String ACHIEVEMENT_PREFIX = "achievement_";
    private static final String SPEED_ACHIEVEMENT = "speed_demon";
    private static final String PERFECT_ACHIEVEMENT = "perfect";
    private static final String HIGH_SCORER_ACHIEVEMENT = "high_scorer";
    private static final String POWERUP_ACHIEVEMENT = "powerup_collector";
    
    private final Context context;
    private final SharedPreferences prefs;
    private final List<Achievement> achievements = new ArrayList<>();

    /**
     * Creates a new AchievementManager and loads existing achievement states.
     * @param context Application context for accessing resources and preferences
     */
    public AchievementManager(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        initializeAchievements();
        loadAchievementStates();
    }

    /**
     * Initializes all available achievements with their default states.
     */
    private void initializeAchievements() {
        achievements.add(new Achievement(
            PERFECT_ACHIEVEMENT,
            context.getString(R.string.achievement_perfect),
            context.getString(R.string.achievement_perfect_desc),
            R.drawable.ic_achievement_perfect
        ));
        
        achievements.add(new Achievement(
            SPEED_ACHIEVEMENT,
            context.getString(R.string.achievement_speed),
            context.getString(R.string.achievement_speed_desc),
            R.drawable.ic_achievement_speed
        ));
        
        achievements.add(new Achievement(
            HIGH_SCORER_ACHIEVEMENT,
            context.getString(R.string.achievement_score),
            context.getString(R.string.achievement_high_scorer_desc),
            R.drawable.ic_achievement_score
        ));

        achievements.add(new Achievement(
            POWERUP_ACHIEVEMENT,
            context.getString(R.string.achievement_powerup),
            context.getString(R.string.achievement_powerup_desc),
            R.drawable.ic_achievement_score  // Using existing icon temporarily
        ));
    }

    /**
     * Loads the unlocked state of each achievement from SharedPreferences.
     */
    private void loadAchievementStates() {
        for (Achievement achievement : achievements) {
            boolean isUnlocked = prefs.getBoolean(ACHIEVEMENT_PREFIX + achievement.getId(), false);
            achievement.setUnlocked(isUnlocked);
        }
    }

    /**
     * Returns an unmodifiable list of all achievements.
     */
    @NonNull
    public List<Achievement> getAchievements() {
        return Collections.unmodifiableList(achievements);
    }

    /**
     * Marks an achievement as unlocked and persists the state.
     */
    private void unlockAchievement(Achievement achievement) {
        if (!achievement.isUnlocked()) {
            achievement.setUnlocked(true);
            prefs.edit()
                .putBoolean(ACHIEVEMENT_PREFIX + achievement.getId(), true)
                .apply();
        }
    }

    /**
     * Checks if the speed achievement should be unlocked based on hit count and time.
     * @param hits Number of consecutive hits
     * @param timeSpan Time window in milliseconds
     */
    public void checkSpeedAchievement(int hits, long timeSpan) {
        if (hits >= 10 && timeSpan <= 5000) {
            for (Achievement achievement : achievements) {
                if (achievement.getId().equals(SPEED_ACHIEVEMENT)) {
                    unlockAchievement(achievement);
                    break;
                }
            }
        }
    }

    /**
     * Checks if the high score achievement should be unlocked.
     * @param score Current game score
     */
    public void checkHighScore(int score) {
        if (score >= 50) {
            for (Achievement achievement : achievements) {
                if (achievement.getId().equals(HIGH_SCORER_ACHIEVEMENT)) {
                    unlockAchievement(achievement);
                    break;
                }
            }
        }
    }

    /**
     * Checks if the perfect level achievement should be unlocked.
     * @param misses Number of misses
     */
    public void checkPerfectLevel(int misses) {
        if (misses == 0) {
            for (Achievement achievement : achievements) {
                if (achievement.getId().equals(PERFECT_ACHIEVEMENT)) {
                    unlockAchievement(achievement);
                    break;
                }
            }
        }
    }

    /**
     * Checks if the powerup collection achievement should be unlocked.
     * @param powerupsCollected Number of powerups collected
     */
    public void checkPowerupCollection(int powerupsCollected) {
        if (powerupsCollected >= 5) {
            for (Achievement achievement : achievements) {
                if (achievement.getId().equals(POWERUP_ACHIEVEMENT)) {
                    unlockAchievement(achievement);
                    break;
                }
            }
        }
    }
}
