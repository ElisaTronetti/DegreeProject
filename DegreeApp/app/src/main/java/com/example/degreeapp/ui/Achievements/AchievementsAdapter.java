package com.example.degreeapp.ui.Achievements;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.degreeapp.Database.Achievement.Achievement;
import com.example.degreeapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.AchievementHolder>{
    List<Achievement> achievements = new ArrayList<>();
    OnAchievementClickListener onAchievementClickListener;

    @NonNull
    @Override
    public AchievementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_achievement, parent, false);
        return new AchievementHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementHolder holder, int position) {
        final Achievement currentAchievement = achievements.get(position);

        if(currentAchievement.isUnlocked()){
            File file = new File(currentAchievement.getImage_url());
            Picasso.get()
                    .load(file)
                    .placeholder(R.drawable.ic_lock_black)
                    .error(R.drawable.ic_image_not_supported_black)
                    .into(holder.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.e("PICASSO", "Image achievement ok");
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                            Log.e("PICASSO", "Image achievement error");
                        }
                    });
            holder.description.setText(currentAchievement.getTitle());
        }else{
            //handle if the achievement is currently locked
            holder.image.setImageResource(R.drawable.ic_lock_black);
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(onAchievementClickListener != null){
                        onAchievementClickListener.onCollectionItemClickListener(achievements.get(position));
                    }
                }
            });
        }
    }

    //used to have a reference to the item in the activity to implement the behavior when
    //an item card view is clicked
    public interface OnAchievementClickListener{
        void onCollectionItemClickListener(Achievement achievement);
    }

    public void setOnAchievementClickListener(OnAchievementClickListener listener){
        this.onAchievementClickListener = listener;
    }

}
