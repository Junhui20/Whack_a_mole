package com.crazycat.whack_a_mole;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import android.view.animation.Animation;
import android.widget.TextView;
import android.view.animation.ScaleAnimation;

public class MainActivity extends AppCompatActivity {
    private ScoreManager scoreManager;
    private MediaPlayer homepageBGM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        scoreManager = new ScoreManager(this);
        
        // Initialize and start homepage BGM
        setupHomepageBGM();
        
        // Initialize buttons
        MaterialButton startButton = findViewById(R.id.start);
        MaterialButton scoresButton = findViewById(R.id.scores);
        MaterialButton achievementsButton = findViewById(R.id.achievements);
        MaterialButton aboutButton = findViewById(R.id.About);
        MaterialButton quitButton = findViewById(R.id.quit);
        
        // Set click listeners
        startButton.setOnClickListener(this::StartGame);
        scoresButton.setOnClickListener(this::SeeScores);
        achievementsButton.setOnClickListener(this::ShowAchievements);
        aboutButton.setOnClickListener(this::HowtoPlay);
        quitButton.setOnClickListener(this::QuitGame);

        TextView titleText = findViewById(R.id.textView8);
        
        // Create scale animation
        ScaleAnimation scaleAnimation = new ScaleAnimation(
            0.8f, 1.0f, // Start and end values for X axis scaling
            0.8f, 1.0f, // Start and end values for Y axis scaling
            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point X
            Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point Y
        
        scaleAnimation.setDuration(1000);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        
        titleText.startAnimation(scaleAnimation);
    }

    private void setupHomepageBGM() {
        homepageBGM = MediaPlayer.create(this, R.raw.homepage_bgm);
        homepageBGM.setLooping(true);
        // Set volume to maximum (1.0) for both left and right channels
        homepageBGM.setVolume(1.0f, 1.0f);
        homepageBGM.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (homepageBGM != null && !homepageBGM.isPlaying()) {
            homepageBGM.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (homepageBGM != null && homepageBGM.isPlaying()) {
            homepageBGM.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (homepageBGM != null) {
            homepageBGM.stop();
            homepageBGM.release();
            homepageBGM = null;
        }
    }

    public void StartGame(View view) {
        // Stop homepage BGM before starting the game
        if (homepageBGM != null && homepageBGM.isPlaying()) {
            homepageBGM.pause();
        }
        Intent intent = new Intent(this, SelectLevel.class);
        startActivity(intent);
    }

    public void SeeScores(View view) {
        showHighScores();
    }

    public void HowtoPlay(View view) {
        showHowToPlay();
    }

    public void QuitGame(View view) {
        finish();
    }

    public void ShowAchievements(View view) {
        Intent intent = new Intent(this, AchievementsActivity.class);
        startActivity(intent);
    }

    private void showHighScores() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_high_scores);
        
        // 设置对话框窗口参数
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                (int)(getResources().getDisplayMetrics().widthPixels * 0.9),  // 宽度为屏幕的90%
                WindowManager.LayoutParams.WRAP_CONTENT
            );
            window.setGravity(Gravity.CENTER);  // 居中显示
            
            // 设置动画
            window.setWindowAnimations(R.style.DialogAnimation);
        }
        
        RecyclerView recyclerView = dialog.findViewById(R.id.highScoresList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new HighScoreAdapter(scoreManager.getHighScores()));
        
        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showHowToPlay() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_how_to_play);
        
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                (int)(getResources().getDisplayMetrics().widthPixels * 0.9),
                WindowManager.LayoutParams.WRAP_CONTENT
            );
            window.setGravity(Gravity.CENTER);
            window.setWindowAnimations(R.style.DialogAnimation);
        }
        
        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}