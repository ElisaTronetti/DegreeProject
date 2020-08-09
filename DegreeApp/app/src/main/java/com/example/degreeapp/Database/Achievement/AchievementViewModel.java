package com.example.degreeapp.Database.Achievement;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class AchievementViewModel extends AndroidViewModel {
    private AchievementRepository repository;
    private LiveData<List<Achievement>> achievements;
    private LiveData<List<Achievement>> unlocked;
    private LiveData<List<Achievement>> locked;

    public AchievementViewModel(@NonNull Application application) {
        super(application);
        repository = new AchievementRepository(application);
        achievements = repository.getAllAchievements();
        unlocked = repository.getUnlockedAchievements();
        locked = repository.getLockedAchievements();
    }

    public long insertAchievement(final Achievement achievement){
        return repository.insertAchievement(achievement);
    }

    public LiveData<List<Achievement>> getAllAchievements() {
        return achievements;
    }

    public LiveData<List<Achievement>> getUnlockedAchievements(){
        return unlocked;
    }

    public LiveData<List<Achievement>> getLockedAchievements(){
        return locked;
    }

    public Achievement getAchievementById(final int id){
        return repository.getAchievementById(id);
    }

    public Achievement getAchievementByUuid(final String uuid){
        return repository.getAchievementByUuid(uuid);
    }

    public boolean isAchievementUnlocked(final String uuid){
        return repository.isAchievementUnlocked(uuid);
    }
}
