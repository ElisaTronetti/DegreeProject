package com.example.degreeapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.degreeapp.Database.Achievement.Achievement;
import com.example.degreeapp.Database.Achievement.AchievementDAO;
import com.example.degreeapp.Database.Item.Item;
import com.example.degreeapp.Database.Item.ItemDAO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Achievement.class, Item.class}, version = 24, exportSchema = false)
public abstract class AppRoomDatabase extends RoomDatabase {

    //get dao reference
    public abstract AchievementDAO achievementDAO();
    public abstract ItemDAO itemDAO();

    //Singleton instance
    private static volatile AppRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    //ExecutorService with a fixed thread pool that you will use to run database operations
    // asynchronously on a background thread.
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * It'll create the database the first time it's accessed, using Room's database
     * @param context the context of the Application
     * @return the singleton
     */
    public static AppRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppRoomDatabase.class, "database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
