package com.crazycat.whack_a_mole;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {
    private final Context context;
    private final OnLevelClickListener listener;
    private final int[] levelColors = {
        Color.parseColor("#4CAF50"), // Green
        Color.parseColor("#2196F3"), // Blue
        Color.parseColor("#FF9800"), // Orange
        Color.parseColor("#9C27B0"), // Purple
        Color.parseColor("#E91E63")  // Pink
    };
    private final String[] levelDescriptions = {
        "4 Blocks",
        "9 Blocks",
        "16 Blocks",
        "25 Blocks",
        "Extra ???"
    };

    public interface OnLevelClickListener {
        void onLevelClick(int level);
    }

    public LevelAdapter(Context context, OnLevelClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.level_item, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
        int level = position + 1;
        holder.levelNumber.setText("LEVEL " + level);
        holder.levelDescription.setText(levelDescriptions[position]);
        holder.card.setCardBackgroundColor(levelColors[position]);
        
        // Apply animation
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.item_animation));
        
        holder.itemView.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.button_click));
            listener.onLevelClick(level);
        });
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    static class LevelViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView levelNumber;
        TextView levelDescription;

        LevelViewHolder(View itemView) {
            super(itemView);
            card = (MaterialCardView) itemView;
            levelNumber = itemView.findViewById(R.id.levelNumber);
            levelDescription = itemView.findViewById(R.id.levelDescription);
        }
    }
}
