package com.crazycat.whack_a_mole;

/**
 * Represents a game achievement that can be unlocked by the player.
 * Each achievement has a unique ID, title, description, and icon.
 */
public class Achievement {
    private final String id;
    private final String title;
    private final String description;
    private final int iconResId;
    private boolean unlocked;

    /**
     * Creates a new Achievement with the specified details.
     * @param id Unique identifier for the achievement
     * @param title Display title of the achievement
     * @param description Detailed description of how to unlock the achievement
     * @param iconResId Resource ID for the achievement's icon
     */
    public Achievement(String id, String title, String description, int iconResId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.unlocked = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getIconResId() { return iconResId; }
    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
}
