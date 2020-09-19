package com.example.degreeapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.degreeapp.Database.Item.Item;
import com.example.degreeapp.Database.Item.ItemViewModel;
import com.example.degreeapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

public class InformationActivity extends AppCompatActivity {
    private ItemViewModel itemViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        itemViewModel =  new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ItemViewModel.class);

        setButtonsListeners();
        setUI();

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
                Intent intent = new Intent(InformationActivity.this, MainActivity.class);
                InformationActivity.this.startActivity(intent);
            }
        });
        findViewById(R.id.information_choose_female).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item = itemViewModel.getItemByUuid("default");
                item.setImage_url(item.getImage_url().replace("boy", "girl"));
                itemViewModel.updateItem(item);
                loadImage();
            }
        });
        findViewById(R.id.information_choose_male).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item = itemViewModel.getItemByUuid("default");
                item.setImage_url(item.getImage_url().replace("girl", "boy"));
                itemViewModel.updateItem(item);
                loadImage();
            }
        });
    }

    private void setUI(){
        ((TextView)findViewById(R.id.main_title)).setText(R.string.informazioni);
        loadImage();
    }

    private void loadImage(){
        ImageView imageView = findViewById(R.id.information_image);

        Item item = itemViewModel.getItemByUuid("default");
        File file = new File(item.getImage_url());
        Log.e("TEST", file.getAbsolutePath());
        Picasso.get()
                .load(file)
                .resize(350,350)
                .error(R.drawable.ic_image_not_supported_black)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("PICASSO", "Image information ok");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("PICASSO", "Image information error");
                    }
                });
    }
}
