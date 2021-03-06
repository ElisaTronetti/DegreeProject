package com.example.degreeapp.Database.Item;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertItem(final Item item);

    @Query("SELECT * FROM items ORDER BY item_id")
    LiveData<List<Item>> getAllItems();

    @Query("SELECT * FROM items WHERE item_id = :id")
    Item getItemById(final int id);

    @Query("SELECT * FROM items WHERE uuid = :uuid")
    Item getItemByUuid(final String uuid);

    @Query("SELECT COUNT(item_id) FROM items")
    LiveData<Integer> getItemCount();

    @Update
    void updateItem(final Item item);
}
