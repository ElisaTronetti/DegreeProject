package com.example.degreeapp.Achievements;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.degreeapp.Database.Achievement.Achievement;
import com.example.degreeapp.Database.Achievement.AchievementViewModel;
import com.example.degreeapp.MainActivity;
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
        setButtonsListeners();

        //create grid layout manager to actually organize elements in a grid layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        RecyclerView recyclerView = findViewById(R.id.achievement_recycler);
        recyclerView.setLayoutManager(gridLayoutManager) ;
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

    //TODO only for debug purposes (delete it when you take data from server!)
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

    private void setButtonsListeners(){
        findViewById(R.id.achievement_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AchievementsActivity.this, MainActivity.class);
                AchievementsActivity.this.startActivity(intent);
            }
        });
    }

}
