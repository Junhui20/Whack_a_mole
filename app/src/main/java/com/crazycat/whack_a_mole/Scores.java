package com.crazycat.whack_a_mole;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.crazycat.whack_a_mole.models.HighScore;
import java.util.List;

public class Scores extends AppCompatActivity {
    private ScoreManager scoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        
        scoreManager = new ScoreManager(this);
        
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                returnToMainMenu();
            }
        });

        MaterialButton backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(v -> returnToMainMenu());
        }

        showHighScores();
        
        int score = getIntent().getIntExtra(getString(R.string.extra_score), -1);
        if (score != -1 && scoreManager.isHighScore(score)) {
            showHighScoreDialog(score);
        }
    }

    private void showHighScores() {
        List<HighScore> highScores = scoreManager.getHighScores();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new HighScoreAdapter(highScores));
        }
    }

    private void showHighScoreDialog(int score) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_new_high_score);
        dialog.setCancelable(false);
        
        TextInputEditText nameInput = dialog.findViewById(R.id.nameInput);
        MaterialButton saveButton = dialog.findViewById(R.id.btn_save);
        
        if (saveButton != null && nameInput != null) {
            saveButton.setOnClickListener(v -> {
                Editable editable = nameInput.getText();
                String playerName = editable != null ? editable.toString().trim() : "";
                
                if (playerName.isEmpty()) {
                    playerName = getString(R.string.anonymous);
                }
                scoreManager.addHighScore(playerName, score);
                dialog.dismiss();
                showHighScores();
            });
        }
        
        dialog.show();
    }

    private void returnToMainMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}