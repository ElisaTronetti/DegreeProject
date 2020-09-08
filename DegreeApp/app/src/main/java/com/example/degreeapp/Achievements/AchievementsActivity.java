package com.example.degreeapp.Achievements;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

        setUI();
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

    private void setButtonsListeners(){
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AchievementsActivity.this, MainActivity.class);
                AchievementsActivity.this.startActivity(intent);
            }
        });
    }

    private void setUI(){
        ((TextView) findViewById(R.id.main_title)).setText(R.string.i_miei_trofei);
    }

}
