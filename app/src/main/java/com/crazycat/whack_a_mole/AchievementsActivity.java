package com.crazycat.whack_a_mole;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        AchievementManager achievementManager = new AchievementManager(this);
        List<Achievement> achievements = achievementManager.getAchievements();

        RecyclerView recyclerView = findViewById(R.id.achievementsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new AchievementAdapter(achievements));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                returnToMainMenu();
            }
        });
    }

    private void returnToMainMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void onBackClick(View view) {
        returnToMainMenu();
    }

    private static class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {
        private final List<Achievement> achievements;

        AchievementAdapter(List<Achievement> achievements) {
            this.achievements = achievements;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(achievements.get(position));
        }

        @Override
        public int getItemCount() {
            return achievements.size();
        }

        private static class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView icon;
            private final TextView title;
            private final TextView description;
            private final ImageView status;

            ViewHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.item_achievement, parent, false));
                icon = itemView.findViewById(R.id.achievementIcon);
                title = itemView.findViewById(R.id.achievementTitle);
                description = itemView.findViewById(R.id.achievementDescription);
                status = itemView.findViewById(R.id.achievementStatus);
            }

            void bind(Achievement achievement) {
                icon.setImageResource(achievement.getIconResId());
                title.setText(achievement.getTitle());
                description.setText(achievement.getDescription());
                status.setImageResource(achievement.isUnlocked() ? 
                    R.drawable.ic_achievement_unlocked : 
                    R.drawable.ic_achievement_locked);
                itemView.setAlpha(achievement.isUnlocked() ? 1.0f : 0.5f);
            }
        }
    }
}
