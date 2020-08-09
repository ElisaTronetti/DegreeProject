package com.example.degreeapp.Database.Item;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ItemViewModel extends AndroidViewModel {
    private ItemRepository repository;
    private LiveData<List<Item>> items;

    public ItemViewModel(@NonNull Application application) {
        super(application);
        repository = new ItemRepository(application);
        items = repository.getAllItems();
    }

    public long insertItem(final Item item){
        return repository.insertItem(item);
    }

    public LiveData<List<Item>> getAllItems(){
        return items;
    }

    public Item getItemById(final int id){
        return repository.getItemById(id);
    }

    public Item getItemByUuid(final String uuid){
        return repository.getItemByUuid(uuid);
    }
}
