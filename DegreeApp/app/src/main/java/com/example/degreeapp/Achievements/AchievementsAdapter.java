package com.example.degreeapp.Achievements;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.degreeapp.Database.Achievement.Achievement;
import com.example.degreeapp.R;

import java.util.ArrayList;
import java.util.List;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.AchievementHolder>{
    List<Achievement> achievements = new ArrayList<>();

    @NonNull
    @Override
    public AchievementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_achievement, parent, false);
        return new AchievementHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementHolder holder, int position) {
        final Achievement currentAchievement = achievements.get(position);

        //TODO handle image
        if(currentAchievement.isUnlocked()){
            holder.description.setText(currentAchievement.getTitle());
        }else{
            //handle if the current achievement is currently locked
            holder.image.setImageResource(R.drawable.baseline_lock_black_24dp);
            holder.description.setText(R.string.bloccato);
        }

    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public void setAchievements(final List<Achievement> achievements){
        this.achievements = achievements;
        notifyDataSetChanged();
    }

    class AchievementHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private TextView description;

        AchievementHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.achievement_image);
            description = itemView.findViewById(R.id.achievement_description);
        }
    }

}
