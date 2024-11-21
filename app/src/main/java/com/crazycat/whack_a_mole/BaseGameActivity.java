package com.crazycat.whack_a_mole;

import android.app.Dialog;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.FrameLayout;

/**
 * Base class for game activities.
 * Provides common functionality for game logic, sound management, and UI setup.
 */
public abstract class BaseGameActivity extends AppCompatActivity {
    protected GridLayout targetGrid;
    protected TextView scoreText, timerText;
    protected int score = 0;
    protected int gridSize = 9;
    protected List<ImageView> targetViews = new ArrayList<>();
    protected int timeRemaining = 30;
    protected CountDownTimer timer;
    protected ImageView lastHighlightedView;
    protected AchievementManager achievementManager;
    protected boolean isGameActive = false;
    protected int specialItemsCollected = 0;
    protected long lastHitTime = 0;
    protected int consecutiveHits = 0;
    protected long speedRunStartTime = 0;

    // Sound related variables
    protected MediaPlayer bgmPlayer;
    protected SoundPool soundPool;
    protected int clickSoundId;
    protected int successSoundId;
    protected int scoreBuffSoundId;
    protected int timeBuffSoundId;
    protected float soundVolume = 1.0f;

    /**
     * Called when the activity is created.
     * Initializes the game components, sets up the UI, and starts the game.
     * @param savedInstanceState Saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        // Initialize back press callback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                returnToMainMenu();
            }
        });

        initializeViews();
        initializeGame();
        initializeSound();
        setupGrid();
        startGame();

        score = getIntent().getIntExtra(getString(R.string.extra_score), 0);
        scoreText.setText(getString(R.string.score_display, score));
    }

    /**
     * Initializes the game views, including the score display and timer.
     */
    private void initializeViews() {
        targetGrid = findViewById(R.id.targetGrid);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);

        // Set initial text
        scoreText.setText(getString(R.string.score_display, 0));
        timerText.setText(getString(R.string.time_format, timeRemaining));
    }

    /**
     * Initializes the game logic and variables.
     */
    private void initializeGame() {
        // Initialize game variables
    }

    /**
     * Initializes the sound effects and background music.
     */
    private void initializeSound() {
        // Initialize background music
        bgmPlayer = MediaPlayer.create(this, R.raw.bgm);
        bgmPlayer.setLooping(true);

        // Initialize sound effects
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build();

        soundPool = new SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build();

        // Load sound effects
        clickSoundId = soundPool.load(this, R.raw.click, 1);
        successSoundId = soundPool.load(this, R.raw.success, 1);
        scoreBuffSoundId = soundPool.load(this, R.raw.magic_spell, 1);
        timeBuffSoundId = soundPool.load(this, R.raw.magic_spell_2, 1);

        // Set up sound loading completion listener
        soundPool.setOnLoadCompleteListener((pool, sampleId, status) -> {
            // Sound loading is handled automatically by SoundPool
            // No additional action needed here as sounds are played only after being loaded
        });
    }

    /**
     * Plays the click sound effect.
     */
    protected void playClickSound() {
        if (soundPool != null) {
            soundPool.play(clickSoundId, soundVolume, soundVolume, 1, 0, 1.0f);
        }
    }

    /**
     * Plays the success sound effect.
     */
    protected void playSuccessSound() {
        if (soundPool != null) {
            soundPool.play(successSoundId, soundVolume, soundVolume, 1, 0, 1.0f);
        }
    }

    /**
     * Plays the score buff sound effect.
     */
    protected void playScoreBuffSound() {
        if (soundPool != null) {
            soundPool.play(scoreBuffSoundId, soundVolume, soundVolume, 1, 0, 1.0f);
        }
    }

    /**
     * Plays the time buff sound effect.
     */
    protected void playTimeBuffSound() {
        if (soundPool != null) {
            soundPool.play(timeBuffSoundId, soundVolume, soundVolume, 1, 0, 1.0f);
        }
    }

    /**
     * Sets up the grid layout and creates the grid cells.
     */
    protected void setupGrid() {
        // Calculate grid size
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        
        // Calculate available space - use slightly less width to ensure no cutoff
        int availableHeight = (int)(screenHeight * 0.8); // Use 70% of screen height
        int availableWidth = (int)(screenWidth * 0.90); // Use 90% of screen width
        
        // Use the smaller dimension to ensure grid fits
        int maxGridSize = Math.min(availableWidth, availableHeight);
        
        // Calculate cell size based on grid dimensions
        int columns = (int)Math.sqrt(this.gridSize);
        
        // Calculate total margins and padding per row
        int cellMargin = this.gridSize == 36 ? 2 : 4; // Margin between cells
        int totalMarginsPerRow = cellMargin * (columns + 1); // Include outer margins
        
        // Calculate cell size accounting for margins
        int cellSize = (maxGridSize - totalMarginsPerRow) / columns;
        
        // For Level 5 (6x6 grid), adjust if needed
        if (this.gridSize == 36) {
            cellSize = (int)(cellSize * 0.95); // Slightly reduce cell size
        }

        // Set grid properties
        targetGrid.setColumnCount(columns);
        targetGrid.setRowCount(columns);
        
        // Calculate total grid size including margins
        int totalGridSize = (cellSize * columns) + totalMarginsPerRow;
        
        // Center the grid
        int horizontalMargin = (screenWidth - totalGridSize) / 2;
        
        // Ensure minimum margin
        horizontalMargin = Math.max(horizontalMargin, 16); // Minimum 16dp margin
        
        ViewGroup.MarginLayoutParams gridParams = (ViewGroup.MarginLayoutParams) targetGrid.getLayoutParams();
        if (gridParams == null) {
            gridParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        gridParams.setMargins(horizontalMargin, 16, horizontalMargin, 16);
        gridParams.width = totalGridSize;
        gridParams.height = totalGridSize;
        targetGrid.setLayoutParams(gridParams);

        // Create grid cells
        createGridCells(cellSize, cellMargin);

        // Reset all cells to initial state
        resetAllCells();
    }

    /**
     * Creates the grid cells and sets up their layout.
     * @param cellSize The size of each cell.
     * @param cellMargin The margin between cells.
     */
    private void createGridCells(int cellSize, int cellMargin) {
        for (int i = 0; i < this.gridSize; i++) {
            FrameLayout cell = new FrameLayout(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cellSize;
            params.height = cellSize;
            
            // Set consistent margins
            params.setMargins(cellMargin, cellMargin, cellMargin, cellMargin);
            
            cell.setLayoutParams(params);
            cell.setBackgroundResource(R.drawable.grid_cell_background);

            ImageView moleView = new ImageView(this);
            FrameLayout.LayoutParams moleParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            );
            
            // Set consistent padding
            int padding = this.gridSize == 36 ? 4 : 8;
            moleView.setPadding(padding, padding, padding, padding);
            
            moleView.setLayoutParams(moleParams);
            moleView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            moleView.setVisibility(View.INVISIBLE);
            moleView.setImageResource(R.drawable.hole_empty);
            moleView.setTag(null);

            cell.addView(moleView);
            cell.setOnClickListener(v -> {
                if (isGameActive) {
                    ImageView clickedMole = (ImageView) ((FrameLayout) v).getChildAt(0);
                    if (clickedMole.getVisibility() == View.VISIBLE) {
                        onTargetClick(clickedMole);
                    }
                }
            });

            targetGrid.addView(cell);
            targetViews.add(moleView);
        }
    }

    /**
     * Resets all cells to their initial state.
     */
    private void resetAllCells() {
        for (ImageView view : targetViews) {
            view.setVisibility(View.INVISIBLE);
            view.setTag(null);
            view.setImageResource(R.drawable.hole_empty);
            ((View)view.getParent()).setBackgroundResource(R.drawable.grid_cell_background);
        }
        lastHighlightedView = null;
    }

    /**
     * Stops the background music.
     */
    protected void stopBGM() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.stop();
        }
    }

    /**
     * Starts the game.
     */
    protected void startGame() {
        score = getIntent().getIntExtra(getString(R.string.extra_score), 0);
        scoreText.setText(getString(R.string.score_display, score));
        achievementManager = new AchievementManager(this);
        isGameActive = true;
        startTimer();
        showNextMole();
    }

    /**
     * Shows the next mole or power-up.
     */
    protected void showNextMole() {
        // First reset all cells to normal state
        resetAllCells();

        // Show new mole or power-up
        int randomValue = new Random().nextInt(100);
        int randomIndex = new Random().nextInt(targetViews.size());
        lastHighlightedView = targetViews.get(randomIndex);

        // Show the mole/power-up
        lastHighlightedView.setVisibility(View.VISIBLE);

        // Determine type and set appearance
        if (randomValue < 15) { // 15% chance for purple power-up
            // Set the mole image
            lastHighlightedView.setImageResource(R.drawable.mole_normal);
            // Set the power-up background
            ((View)lastHighlightedView.getParent()).setBackgroundResource(R.drawable.powerup_purple);
            lastHighlightedView.setTag(getString(R.string.tag_purple));
        } else if (randomValue < 25) { // Additional 10% chance for golden power-up
            // Set the mole image
            lastHighlightedView.setImageResource(R.drawable.mole_normal);
            // Set the power-up background
            ((View)lastHighlightedView.getParent()).setBackgroundResource(R.drawable.powerup_golden);
            lastHighlightedView.setTag(getString(R.string.tag_golden));
        } else {
            // Regular mole
            lastHighlightedView.setImageResource(R.drawable.mole_normal);
            lastHighlightedView.setTag(getString(R.string.tag_mole));
            // Set normal active cell background
            ((View)lastHighlightedView.getParent()).setBackgroundResource(R.drawable.grid_cell_active);
        }
    }

    /**
     * Handles a click on a target.
     * @param target The target that was clicked.
     */
    protected void onTargetClick(ImageView target) {
        if (!isGameActive || target == null || target.getTag() == null) {
            onMissedTarget();
            return;
        }

        String type = target.getTag().toString();
        long currentTime = System.currentTimeMillis();

        if (type.equals(getString(R.string.tag_purple))) {
            specialItemsCollected++;
            achievementManager.checkPowerupCollection(specialItemsCollected);
            timeRemaining++;
            timerText.setText(getString(R.string.time_format, timeRemaining));
            score += 2;
            consecutiveHits++;
            playTimeBuffSound();
            target.setImageResource(R.drawable.mole_hit);
        } else if (type.equals(getString(R.string.tag_golden))) {
            specialItemsCollected++;
            achievementManager.checkPowerupCollection(specialItemsCollected);
            score += 2;
            consecutiveHits++;
            playScoreBuffSound();
            target.setImageResource(R.drawable.mole_hit);
        } else if (type.equals(getString(R.string.tag_mole))) {
            score++;
            consecutiveHits++;
            playClickSound();
            target.setImageResource(R.drawable.mole_hit);
        }

        // Update score display
        scoreText.setText(getString(R.string.score_display, score));

        // Check speed achievement
        long timeWindow = currentTime - speedRunStartTime;
        if (consecutiveHits >= 10 && timeWindow <= 10000) {
            achievementManager.checkSpeedAchievement(consecutiveHits, timeWindow);
        }

        // Update last hit time
        lastHitTime = currentTime;

        // Hide the hit mole/power-up after a short delay
        new Handler().postDelayed(() -> {
            target.setVisibility(View.INVISIBLE);
            target.setTag(null);
            ((View)target.getParent()).setBackgroundResource(R.drawable.grid_cell_background);
        }, 50);

        // Show next mole after a delay
        new Handler().postDelayed(this::showNextMole, 50);
    }

    /**
     * Starts the game timer.
     */
    protected void startTimer() {
        timer = new CountDownTimer(timeRemaining * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = (int) (millisUntilFinished / 1000);
                timerText.setText(getString(R.string.time_format, timeRemaining));
            }

            @Override
            public void onFinish() {
                endLevel();
            }
        };
        timer.start();
    }

    /**
     * Called when the activity is resumed.
     * Starts the background music if it's not already playing.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (bgmPlayer != null && !bgmPlayer.isPlaying()) {
            bgmPlayer.start();
        }
    }

    /**
     * Called when the activity is paused.
     * Pauses the background music if it's playing.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }

    /**
     * Called when the activity is destroyed.
     * Releases all resources used by the game.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bgmPlayer != null) {
            bgmPlayer.release();
            bgmPlayer = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * Ends the current level.
     */
    protected void endLevel() {
        if (timer != null) {
            timer.cancel();
        }

        achievementManager.checkPerfectLevel(0);
        achievementManager.checkHighScore(score);

        if (getNextLevelClass() == null) {
            onTimeUp();
        } else {
            showLevelCompleteDialog();
        }
    }

    /**
     * Shows the level complete dialog.
     */
    protected void showLevelCompleteDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_level_complete);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                (int)(getResources().getDisplayMetrics().widthPixels * 0.9),
                WindowManager.LayoutParams.WRAP_CONTENT
            );
            window.setGravity(Gravity.CENTER);
        }

        TextView scoreText = dialog.findViewById(R.id.score_text);
        scoreText.setText(getString(R.string.score_display, score));

        MaterialButton endButton = dialog.findViewById(R.id.btn_end_game);
        MaterialButton continueButton = dialog.findViewById(R.id.btn_continue);

        endButton.setOnClickListener(v -> {
            dialog.dismiss();
            goToScores();
        });

        continueButton.setOnClickListener(v -> {
            dialog.dismiss();
            goToNextLevel();
        });

        dialog.show();
    }

    /**
     * Goes to the next level.
     */
    protected void goToNextLevel() {
        Class<?> nextLevel = getNextLevelClass();
        if (nextLevel != null) {
            Intent intent = new Intent(this, nextLevel);
            intent.putExtra(getString(R.string.extra_score), score);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Goes to the scores screen.
     */
    protected void goToScores() {
        Intent intent = new Intent(this, Scores.class);
        intent.putExtra(getString(R.string.extra_score), score);
        startActivity(intent);
        finish();
    }

    /**
     * Returns to the main menu.
     */
    protected void returnToMainMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Gets the layout resource ID for the game activity.
     * @return The layout resource ID.
     */
    protected abstract int getLayoutResourceId();

    /**
     * Gets the next level class.
     * @return The next level class, or null if there is no next level.
     */
    protected abstract Class<?> getNextLevelClass();

    /**
     * Called when the time is up.
     */
    protected abstract void onTimeUp();

    /**
     * Handles when the player misses a target.
     * Subclasses can override this to implement specific miss penalties.
     */
    protected void onMissedTarget() {
        // Base implementation does nothing
        // Subclasses can override to add penalties
    }
}