package com.example.degreeapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.degreeapp.R;
import com.example.degreeapp.ui.Collection.CollectionActivity;
import com.example.degreeapp.Database.Item.Item;
import com.example.degreeapp.Database.Item.ItemViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

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
                intent.putExtra("only_show", 1);
                ItemActivity.this.startActivity(intent);
            }
        });
    }

    private void setUI(final Item item){
        ((TextView) findViewById(R.id.main_title)).setText(item.getTitle());
        ((TextView) findViewById(R.id.item_title)).setText(item.getTitle());
        ((TextView) findViewById(R.id.item_description)).setText(item.getDescription());
        ((TextView) findViewById(R.id.item_air_data)).setText(item.getAir_condition());

        ImageView imageView = findViewById(R.id.item_image);
        File file = new File(item.getImage_url());
        Picasso.get()
                .load(file)
                .placeholder(R.drawable.ic_lock_black)
                .error(R.drawable.ic_image_not_supported_black)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("PICASSO", "Image item ok");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("PICASSO", "Image item error");
                    }
                });
    }
}
