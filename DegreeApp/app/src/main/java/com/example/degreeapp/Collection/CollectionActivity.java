package com.example.degreeapp.Collection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.degreeapp.Achievements.AchievementsActivity;
import com.example.degreeapp.Database.Item.Item;
import com.example.degreeapp.Database.Item.ItemViewModel;
import com.example.degreeapp.MainActivity;
import com.example.degreeapp.R;

import java.util.List;

public class CollectionActivity extends AppCompatActivity {
    private ItemViewModel itemViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        itemViewModel =  new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ItemViewModel.class);

        initializeDB();
        setButtonsListeners();

        //create grid layout manager to actually organize elements in a grid layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        final RecyclerView recyclerView = findViewById(R.id.collection_recycler);
        recyclerView.setLayoutManager(gridLayoutManager) ;
        final CollectionAdapter adapter = new CollectionAdapter();
        recyclerView.setAdapter(adapter);

        itemViewModel.getAllItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                adapter.setItems(items);
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
        itemViewModel.insertItem(new Item("1", "Item1", "Sono l'item 1", "no"));
        itemViewModel.insertItem(new Item("2", "Item2", "Sono l'item 2", "no"));
        itemViewModel.insertItem(new Item("3", "Item3", "Sono l'item 3", "no"));
        itemViewModel.insertItem(new Item("4", "Item4", "Sono l'item 4", "no"));
        itemViewModel.insertItem(new Item("5", "Item5", "Sono l'item 5", "no"));
        itemViewModel.insertItem(new Item("6", "Item6", "Sono l'item 6", "no"));
        itemViewModel.insertItem(new Item("7", "Item7", "Sono l'item 7", "no"));
        itemViewModel.insertItem(new Item("8", "Item8", "Sono l'item 8", "no"));
    }

    private void setButtonsListeners(){
        findViewById(R.id.collection_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CollectionActivity.this, MainActivity.class);
                CollectionActivity.this.startActivity(intent);
            }
        });
    }
}
