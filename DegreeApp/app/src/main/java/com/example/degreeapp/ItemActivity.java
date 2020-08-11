package com.example.degreeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.degreeapp.Collection.CollectionActivity;
import com.example.degreeapp.Database.Item.Item;
import com.example.degreeapp.Database.Item.ItemViewModel;

public class ItemActivity extends AppCompatActivity {
    private ItemViewModel itemViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        itemViewModel =  new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ItemViewModel.class);

        setButtonsListeners();
        Intent intent = getIntent();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                int itemId = bundle.getInt("id");
                Item selectedItem = itemViewModel.getItemById(itemId);
                if (selectedItem != null) {
                    //if the element clicked actually exists, initialize the ui
                    setUI(selectedItem);
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

    private void setButtonsListeners(){
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemActivity.this, CollectionActivity.class);
                ItemActivity.this.startActivity(intent);
            }
        });
    }

    private void setUI(final Item item){
        //TODO handle image
        ((TextView) findViewById(R.id.main_title)).setText(item.getTitle());
        ((TextView) findViewById(R.id.item_title)).setText(item.getTitle());
        ((TextView) findViewById(R.id.item_description)).setText(item.getDescription());
    }
}
