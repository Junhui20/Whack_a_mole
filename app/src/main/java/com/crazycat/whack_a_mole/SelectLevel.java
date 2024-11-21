package com.crazycat.whack_a_mole;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

// Activity for selecting game difficulty level (1-5)
public class SelectLevel extends AppCompatActivity implements LevelAdapter.OnLevelClickListener {
    private RecyclerView levelRecyclerView;
    private LevelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);

        levelRecyclerView = findViewById(R.id.levelRecyclerView);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        adapter = new LevelAdapter(this, this);
        levelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        levelRecyclerView.setAdapter(adapter);
    }

    // Handles level selection and starts game with selected level
    @Override
    public void onLevelClick(int level) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("level", level);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // Returns to previous screen with slide animation
    public void onBackClick(View view) {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}