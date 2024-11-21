package com.crazycat.whack_a_mole;

public class Achievement {
    private final String id;
    private final String title;
    private final String description;
    private final int iconResId;
    private boolean unlocked;

    public Achievement(String id, String title, String description, int iconResId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.unlocked = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getIconResId() { return iconResId; }
    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
}
