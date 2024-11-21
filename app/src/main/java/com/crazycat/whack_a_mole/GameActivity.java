package com.crazycat.whack_a_mole;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Random;

public class GameActivity extends BaseGameActivity {
    private static final int TIME_BONUS_CHANCE = 15; // 15% chance for time bonus
    private static final int SCORE_BONUS_CHANCE = 10; // 10% chance for score bonus
    private final Random random = new Random();
    private ScoreManager scoreManager;
    private boolean tutorialShown = false;
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        scoreManager = new ScoreManager(this);
        currentLevel = getIntent().getIntExtra(getString(R.string.extra_level), 1);
        configureLevelSettings();
        
        super.onCreate(savedInstanceState);
        score = getIntent().getIntExtra(getString(R.string.extra_score), 0);
        
        if (scoreText != null) {
            scoreText.setText(getString(R.string.score_display, score));
        }
        
        TextView levelText = findViewById(R.id.levelText);
        if (levelText != null) {
            levelText.setText(getString(R.string.level_format, currentLevel));
        }
        
        LinearLayout specialItemsInfo = findViewById(R.id.powerupInfo);
        if (currentLevel == 5 && specialItemsInfo != null) {
            specialItemsInfo.setVisibility(View.VISIBLE);
        }
        
        if (currentLevel == 5 && !tutorialShown) {
            showTutorial();
        } else {
            startGame();
        }
    }

    private void configureLevelSettings() {
        switch (currentLevel) {
            case 1:
                gridSize = 4;  // 2x2 grid
                timeRemaining = 5;
                break;
            case 2:
                gridSize = 9;  // 3x3 grid
                timeRemaining = 5;
                break;
            case 3:
                gridSize = 16; // 4x4 grid
                timeRemaining = 5;
                break;
            case 4:
                gridSize = 25; // 5x5 grid
                timeRemaining = 5;
                break;
            case 5:
                gridSize = 36; // 6x6 grid
                timeRemaining = 30;
                break;
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_game;
    }

    private void showTutorial() {
        Dialog dialog = new Dialog(this, R.style.LightDialogTheme);
        dialog.setContentView(R.layout.dialog_tutorial);
        dialog.setCancelable(false);
        
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                (int)(getResources().getDisplayMetrics().widthPixels * 0.9),
                WindowManager.LayoutParams.WRAP_CONTENT
            );
            window.setGravity(Gravity.CENTER);
        }
        
        TextView tutorialText = dialog.findViewById(R.id.tutorial_text);
        if (tutorialText != null) {
            tutorialText.setText(getString(R.string.tutorial_text_level5));
        }
        
        MaterialButton startButton = dialog.findViewById(R.id.btn_start);
        if (startButton != null) {
            startButton.setOnClickListener(v -> {
                dialog.dismiss();
                tutorialShown = true;
                startGame();
            });
        }
        
        dialog.show();
    }

    @Override
    protected void onTimeUp() {
        if (currentLevel == 5 || currentLevel == 4) {
            if (scoreManager.isHighScore(score)) {
                showNewHighScoreDialog();
            } else {
                showGameOverDialog();
            }
        } else {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(getString(R.string.extra_level), currentLevel + 1);
            intent.putExtra(getString(R.string.extra_score), score);
            startActivity(intent);
            finish();
        }
    }

    private void showNewHighScoreDialog() {
        playSuccessSound();
        Dialog dialog = new Dialog(this, R.style.LightDialogTheme);
        dialog.setContentView(R.layout.dialog_new_high_score);
        dialog.setCancelable(false);
        
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                (int)(getResources().getDisplayMetrics().widthPixels * 0.9),
                WindowManager.LayoutParams.WRAP_CONTENT
            );
            window.setGravity(Gravity.CENTER);
        }
        
        TextInputEditText nameInput = dialog.findViewById(R.id.nameInput);
        MaterialButton saveButton = dialog.findViewById(R.id.btn_save);
        
        if (saveButton != null && nameInput != null) {
            saveButton.setOnClickListener(v -> {
                CharSequence inputText = nameInput.getText();
                String playerName = inputText != null ? inputText.toString().trim() : "";
                
                if (!playerName.isEmpty()) {
                    scoreManager.addHighScore(playerName, score);
                    dialog.dismiss();
                    showGameOverDialog();
                }
            });
        }
        
        dialog.show();
    }

    private void showGameOverDialog() {
        stopBGM();
        playSuccessSound();
        Dialog dialog = new Dialog(this, R.style.LightDialogTheme);
        dialog.setContentView(R.layout.dialog_game_over);
        dialog.setCancelable(false);
        
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                (int)(getResources().getDisplayMetrics().widthPixels * 0.9),
                WindowManager.LayoutParams.WRAP_CONTENT
            );
            window.setGravity(Gravity.CENTER);
        }
        
        TextView finalScoreText = dialog.findViewById(R.id.final_score_text);
        finalScoreText.setText(getString(R.string.final_score, score));
        
        MaterialButton viewHighScoresButton = dialog.findViewById(R.id.btn_view_high_scores);
        MaterialButton mainMenuButton = dialog.findViewById(R.id.btn_main_menu);
        
        viewHighScoresButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, Scores.class);
            startActivity(intent);
            finish();
        });
        
        mainMenuButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        
        dialog.show();
    }

    @Override
    protected void showNextMole() {
        // Reset all holes
        for (ImageView view : targetViews) {
            if (view != null) {
                view.setImageResource(R.drawable.hole_empty);
                view.setTag(null);
                view.setVisibility(View.INVISIBLE);
                View parent = view.getParent() instanceof View ? (View) view.getParent() : null;
                if (parent != null) {
                    parent.setBackgroundResource(R.drawable.grid_cell_background);
                }
            }
        }

        int randomIndex = random.nextInt(targetViews.size());
        lastHighlightedView = targetViews.get(randomIndex);
        lastHighlightedView.setVisibility(View.VISIBLE);
        lastHighlightedView.setImageResource(R.drawable.mole_normal);

        if (currentLevel == 5) {
            int randomValue = random.nextInt(100);
            View parent = lastHighlightedView.getParent() instanceof View ? 
                (View) lastHighlightedView.getParent() : null;
            if (parent == null) return;
            
            if (randomValue < TIME_BONUS_CHANCE) {
                parent.setBackgroundResource(R.drawable.powerup_purple);
                lastHighlightedView.setTag(getString(R.string.tag_purple));
            } else if (randomValue < TIME_BONUS_CHANCE + SCORE_BONUS_CHANCE) {
                parent.setBackgroundResource(R.drawable.powerup_golden);
                lastHighlightedView.setTag(getString(R.string.tag_golden));
            } else {
                parent.setBackgroundResource(R.drawable.grid_cell_active);
                lastHighlightedView.setTag(getString(R.string.tag_mole));
            }
        } else {
            View parent = lastHighlightedView.getParent() instanceof View ? 
                (View) lastHighlightedView.getParent() : null;
            if (parent != null) {
                parent.setBackgroundResource(R.drawable.grid_cell_active);
                lastHighlightedView.setTag(getString(R.string.tag_mole));
            }
        }
    }

    @Override
    protected void onTargetClick(ImageView target) {
        if (!isGameActive || target == null || target.getTag() == null) {
            onMissedTarget();
            return;
        }

        Object tagObj = target.getTag();
        if (!(tagObj instanceof String)) {
            return;
        }
        String type = (String) tagObj;
        long currentTime = System.currentTimeMillis();

        if (getString(R.string.tag_purple).equals(type)) {
            specialItemsCollected++;
            achievementManager.checkPowerupCollection(specialItemsCollected);
            timeRemaining++;
            if (timerText != null) {
                timerText.setText(getString(R.string.time_format, timeRemaining));
            }
            score += 2;
            consecutiveHits++;
            playTimeBuffSound();
            target.setImageResource(R.drawable.mole_hit);
        } else if (getString(R.string.tag_golden).equals(type)) {
            specialItemsCollected++;
            achievementManager.checkPowerupCollection(specialItemsCollected);
            score += 2;
            consecutiveHits++;
            playScoreBuffSound();
            target.setImageResource(R.drawable.mole_hit);
        } else if (getString(R.string.tag_mole).equals(type)) {
            score++;
            consecutiveHits++;
            playClickSound();
            target.setImageResource(R.drawable.mole_hit);
        }

        if (scoreText != null) {
            scoreText.setText(getString(R.string.score_display, score));
        }

        // Check speed achievement
        long timeWindow = currentTime - speedRunStartTime;
        if (consecutiveHits >= 10 && timeWindow <= 10000) {
            achievementManager.checkSpeedAchievement(consecutiveHits, timeWindow);
        }

        lastHitTime = currentTime;

        // Hide the hit mole after a short delay
        new Handler().postDelayed(() -> {
            target.setVisibility(View.INVISIBLE);
            View parent = target.getParent() instanceof View ? (View) target.getParent() : null;
            if (parent != null) {
                parent.setBackgroundResource(R.drawable.grid_cell_background);
            }
            showNextMole();
        }, 200);
    }

    @Override
    protected void onMissedTarget() {
        if (currentLevel == 5) {
            score = Math.max(0, score - 1);
            if (scoreText != null) {
                scoreText.setText(getString(R.string.score_display, score));
            }
        }
    }

    @Override
    protected Class<?> getNextLevelClass() {
        if (currentLevel >= 5) {
            return null;
        }
        return GameActivity.class;
    }

    @Override
    protected void goToNextLevel() {
        if (currentLevel < 5) {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(getString(R.string.extra_score), score);
            intent.putExtra(getString(R.string.extra_level), currentLevel + 1);  // Pass the next level number
            startActivity(intent);
            finish();
        }
    }
}
