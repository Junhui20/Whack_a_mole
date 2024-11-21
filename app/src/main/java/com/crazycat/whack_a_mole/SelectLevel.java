package com.crazycat.whack_a_mole;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

// Activity for selecting game difficulty level (1-5)
public class SelectLevel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);
    }

    // Handles level button clicks and starts game with selected level
    public void onLevelClick(View view) {
        int level = -1;
        int viewId = view.getId();
        
        if (viewId == R.id.level1_btn) {
            level = 1;
        } else if (viewId == R.id.level2_btn) {
            level = 2;
        } else if (viewId == R.id.level3_btn) {
            level = 3;
        } else if (viewId == R.id.level4_btn) {
            level = 4;
        } else if (viewId == R.id.level5_btn) {
            level = 5;
        }
        
        if (level > 0) {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("level", level);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    // Returns to previous screen with slide animation
    public void onBackClick(View view) {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}