package com.crazycat.whack_a_mole;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.crazycat.whack_a_mole.models.HighScore;
import java.util.List;

// Displays high scores in a RecyclerView with special styling for top 3 ranks
public class HighScoreAdapter extends RecyclerView.Adapter<HighScoreAdapter.ViewHolder> {
    private final List<HighScore> highScores;

    public HighScoreAdapter(List<HighScore> highScores) {
        this.highScores = highScores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_high_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        HighScore highScore = highScores.get(position);
        
        holder.rankText.setText(context.getString(R.string.rank_number, position + 1));
        holder.nameText.setText(highScore.getName());
        holder.scoreText.setText(context.getString(R.string.score_number, highScore.getScore()));

        // Special background for top 3 scores
        if (position < 3) {
            holder.itemView.setBackgroundResource(getBackgroundForRank(position));
        }
    }

    // Returns background resource based on rank (gold, silver, bronze)
    private int getBackgroundForRank(int position) {
        switch (position) {
            case 0: return R.drawable.gold_background;
            case 1: return R.drawable.silver_background;
            case 2: return R.drawable.bronze_background;
            default: return android.R.color.transparent;
        }
    }

    @Override
    public int getItemCount() {
        return highScores.size();
    }

    // Holds references to the views for each high score item
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankText;
        TextView nameText;
        TextView scoreText;

        ViewHolder(View itemView) {
            super(itemView);
            rankText = itemView.findViewById(R.id.rankText);
            nameText = itemView.findViewById(R.id.nameText);
            scoreText = itemView.findViewById(R.id.scoreText);
        }
    }
}