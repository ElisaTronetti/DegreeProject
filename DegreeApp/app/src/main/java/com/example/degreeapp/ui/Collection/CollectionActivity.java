package com.example.degreeapp.ui.Collection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.degreeapp.Database.Item.Item;
import com.example.degreeapp.Database.Item.ItemViewModel;
import com.example.degreeapp.ui.GardenActivity;
import com.example.degreeapp.ui.ItemActivity;
import com.example.degreeapp.ui.MainActivity;
import com.example.degreeapp.R;

import java.util.List;

public class CollectionActivity extends AppCompatActivity {
    private ItemViewModel itemViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        itemViewModel =  new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ItemViewModel.class);

        //create grid layout manager to actually organize elements in a grid layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        final RecyclerView recyclerView = findViewById(R.id.collection_recycler);
        recyclerView.setLayoutManager(gridLayoutManager) ;
        final CollectionAdapter adapter = new CollectionAdapter();
        recyclerView.setAdapter(adapter);

        itemViewModel.getAllItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                if(items.size() == 0){
                    //make a toast if any item has been collected
                    Toast.makeText(CollectionActivity.this, "Non hai ancora trovato nessun oggetto...", Toast.LENGTH_LONG).show();
                } else {
                    adapter.setItems(items);
                }
            }
        });

        setUI();
        setBackButtonListener();

        Intent intent = getIntent();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                int mode = bundle.getInt("only_show");
                if (mode == 0) {
                    //coming from Garden Activity, to add an item in the garden
                    findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CollectionActivity.this, GardenActivity.class);
                            CollectionActivity.this.startActivity(intent);
                        }
                    });

                    //listener to capture the click on a item's card view
                    adapter.setOnCollectionItemClickListener(new CollectionAdapter.OnCollectionItemClickListener() {
                        @Override
                        public void onCollectionItemClickListener(Item item) {
                            Intent intent = new Intent(CollectionActivity.this, GardenActivity.class);
                            intent.putExtra("id", item.getId());
                            CollectionActivity.this.startActivity(intent);
                        }
                    });

                } else if (mode == 1){
                    //coming from Main Activity, to show the collection
                    setBackButtonListener();

                    //listener to capture the click on a item's card view
                    adapter.setOnCollectionItemClickListener(new CollectionAdapter.OnCollectionItemClickListener() {
                        @Override
                        public void onCollectionItemClickListener(Item item) {
                            Intent intent = new Intent(CollectionActivity.this, ItemActivity.class);
                            //item id that is needed to initialize the itemActivity
                            intent.putExtra("id", item.getId());
                            CollectionActivity.this.startActivity(intent);
                        }
                    });
                }
            }
        }
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

    private void setUI(){
        ((TextView) findViewById(R.id.main_title)).setText(R.string.la_mia_collezione);
    }

    private void setBackButtonListener(){
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CollectionActivity.this, MainActivity.class);
                CollectionActivity.this.startActivity(intent);
            }
        });
    }
}
