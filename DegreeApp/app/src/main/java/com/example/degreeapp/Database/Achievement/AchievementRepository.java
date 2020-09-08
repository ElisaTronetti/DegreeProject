package com.example.degreeapp.Database.Achievement;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.degreeapp.Database.AppRoomDatabase;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

class AchievementRepository {
    private AchievementDAO achievementDAO;
    private LiveData<List<Achievement>> achievements;
    private LiveData<List<Achievement>> unlocked;
    private LiveData<List<Achievement>> locked;

    AchievementRepository(Application application){
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        achievementDAO = db.achievementDAO();
        achievements = achievementDAO.getAllAchievements();
        unlocked = achievementDAO.getAllUnlockedAchievements();
        locked = achievementDAO.getAllLockedAchievements();
    }

    LiveData<List<Achievement>> getAllAchievements(){
        return achievements;
    }

    LiveData<List<Achievement>> getUnlockedAchievements(){
        return unlocked;
    }

    LiveData<List<Achievement>> getLockedAchievements(){
        return locked;
    }

    long insertAchievement(final Achievement achievement){
        Future<Long> future;
        long achievementId = 0;
        try{
            future = AppRoomDatabase.databaseWriteExecutor.submit(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    return achievementDAO.insertAchievement(achievement);
                }
            });
            achievementId = future.get();
        } catch (Exception e){
            Log.e("DB", "Insert achievement error");
        }
        return achievementId;
    }

    Achievement getAchievementById(final int id){
        Future<Achievement> future;
        Achievement achievementSelected = null;
        try{
            future = AppRoomDatabase.databaseWriteExecutor.submit(new Callable<Achievement>() {
                @Override
                public Achievement call() throws Exception {
                    return achievementDAO.getAchievementById(id);
                }
            });
            achievementSelected = future.get();
        } catch (Exception e){
            Log.e("DB", "Get achievement by id error");
        }
        return achievementSelected;
    }

    Achievement getAchievementByUuid(final String uuid){
        Future<Achievement> future;
        Achievement achievementSelected = null;
        try{
            future = AppRoomDatabase.databaseWriteExecutor.submit(new Callable<Achievement>() {
                @Override
                public Achievement call() throws Exception {
                    return achievementDAO.getAchievementByUuid(uuid);
                }
            });
            achievementSelected = future.get();
        } catch (Exception e){
            Log.e("DB", "Get achievement by uuid error");
        }
        return achievementSelected;
    }

    boolean isAchievementUnlocked(final String uuid){
        Future<Boolean> future;
        Boolean isUnlocked = null;
        try{
            future = AppRoomDatabase.databaseWriteExecutor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return achievementDAO.isAchievementUnlocked(uuid);
                }
            });
            isUnlocked = future.get();
        } catch (Exception e){
            Log.e("DB", "Check if an achievement is unlocked error");
        }
        return isUnlocked;
    }

    void updateAchievement(final Achievement achievement){
        AppRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                achievementDAO.updateAchievement(achievement);
            }
        });
    }
}
