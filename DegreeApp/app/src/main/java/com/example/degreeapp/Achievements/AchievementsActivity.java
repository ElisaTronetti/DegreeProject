package com.example.degreeapp.Achievements;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.degreeapp.Database.Achievement.Achievement;
import com.example.degreeapp.Database.Achievement.AchievementViewModel;
import com.example.degreeapp.R;

import java.util.List;

public class AchievementsActivity extends AppCompatActivity {
    private AchievementViewModel achievementViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);
        //setting view model, to be able to take data from database
       achievementViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AchievementViewModel.class);

       initializeDB();

       RecyclerView recyclerView = findViewById(R.id.achievement_recycler);
       recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
       final AchievementsAdapter adapter = new AchievementsAdapter();
       recyclerView.setAdapter(adapter);

        achievementViewModel.getAllAchievements().observe(this, new Observer<List<Achievement>>() {
            @Override
            public void onChanged(List<Achievement> achievements) {
                adapter.setAchievements(achievements);
            }
        });
    }


    //This is used to hide/show 'Status Bar' & 'System Bar'. Swipe bar to get it as visible.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void initializeDB(){
        achievementViewModel.insertAchievement(new Achievement("1", "Achievement1", "no", "no"));
        achievementViewModel.insertAchievement(new Achievement("2", "Achievement2", "no", "no"));
        achievementViewModel.insertAchievement(new Achievement("3", "Achievement3", "no", "no"));
        achievementViewModel.insertAchievement(new Achievement("4", "Achievement4", "no", "no"));
        achievementViewModel.insertAchievement(new Achievement("5", "Achievement5", "no", "no"));
        achievementViewModel.insertAchievement(new Achievement("6", "Achievement6", "no", "no"));
        achievementViewModel.insertAchievement(new Achievement("7", "Achievement7", "no", "no"));
        achievementViewModel.insertAchievement(new Achievement("8", "Achievement8", "no", "no"));
        achievementViewModel.insertAchievement(new Achievement("9", "Achievement9", "no", "no"));

    }

}
