package com.example.degreeapp.Database.Item;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.degreeapp.Database.AppRoomDatabase;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class ItemRepository {
    private ItemDAO itemDAO;
    private LiveData<List<Item>> items;
    private LiveData<List<Item>> displayed;
    private int itemCount;

    ItemRepository(Application application){
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        itemDAO = db.itemDAO();
        items = itemDAO.getAllItems();
        displayed = itemDAO.getAllDisplayedItems();
    }

    LiveData<List<Item>> getAllItems(){
        return items;
    }

    LiveData<List<Item>> getDisplayedItems(){
        return displayed;
    }

    public LiveData<Integer> getItemCount() {
        return itemDAO.getItemCount();
    }

    long insertItem(final Item item){
        Future<Long> future;
        long itemId = 0;
        try{
            future = AppRoomDatabase.databaseWriteExecutor.submit(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    return itemDAO.insertItem(item);
                }
            });
            itemId = future.get();
        } catch (Exception e){
            Log.e("DB", "Insert item error");
        }
        return itemId;
    }

    Item getItemById(final int id){
        Future<Item> future;
        Item itemSelected = null;
        try{
            future = AppRoomDatabase.databaseWriteExecutor.submit(new Callable<Item>() {
                @Override
                public Item call() throws Exception {
                    return itemDAO.getItemById(id);
                }
            });
            itemSelected = future.get();
        } catch (Exception e){
            Log.e("DB", "Get item by id error");
        }
        return itemSelected;
    }

    Item getItemByUuid(final String uuid){
        Future<Item> future;
        Item itemSelected = null;
        try{
            future = AppRoomDatabase.databaseWriteExecutor.submit(new Callable<Item>() {
                @Override
                public Item call() throws Exception {
                    return itemDAO.getItemByUuid(uuid);
                }
            });
            itemSelected = future.get();
        } catch (Exception e){
            Log.e("DB", "Get item by uuid error");
        }
        return itemSelected;
    }

}
