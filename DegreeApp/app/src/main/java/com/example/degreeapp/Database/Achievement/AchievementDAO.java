package com.example.degreeapp.Database.Achievement;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AchievementDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertAchievement(final Achievement achievement);

    @Query("SELECT * FROM achievements ORDER BY achievement_id")
    LiveData<List<Achievement>> getAllAchievements();

    @Query("SELECT * FROM achievements WHERE unlocked IS 1 " +
            "ORDER BY achievement_id")
    LiveData<List<Achievement>> getAllUnlockedAchievements();

    @Query("SELECT * FROM achievements WHERE unlocked IS 0 " +
            "ORDER BY achievement_id")
    LiveData<List<Achievement>> getAllLockedAchievements();

    @Query("SELECT * FROM achievements WHERE achievement_id = :id")
    Achievement getAchievementById(final int id);

    @Query("SELECT * FROM achievements WHERE uuid = :uuid")
    Achievement getAchievementByUuid(final String uuid);

    @Query("SELECT COUNT(*) > 0 FROM achievements WHERE unlocked = 1 AND uuid = :uuid")
    boolean isAchievementUnlocked(final String uuid);

    @Update
    void updateAchievement(final Achievement achievement);
}
