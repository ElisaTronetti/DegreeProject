package com.example.degreeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.degreeapp.Achievements.AchievementsActivity;
import com.example.degreeapp.Collection.CollectionActivity;
import com.example.degreeapp.Database.Achievement.Achievement;
import com.example.degreeapp.Database.Achievement.AchievementViewModel;
import com.example.degreeapp.Database.AppRoomDatabase;
import com.example.degreeapp.Database.Item.Item;
import com.example.degreeapp.Database.Item.ItemViewModel;
import com.example.degreeapp.Utilities.ImagesHandler;
import com.example.degreeapp.Volley.JsonUnpacker;
import com.example.degreeapp.Volley.NetworkSingleton;
import com.example.degreeapp.Volley.ServerRequester;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public AppRoomDatabase db;
    private AchievementViewModel achievementViewModel;
    private ItemViewModel itemViewModel;

    private int itemUnlocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtonsListeners();
        db = AppRoomDatabase.getDatabase(this);

        achievementViewModel =  new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AchievementViewModel.class);
        itemViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ItemViewModel.class);

        NetworkSingleton.getInstance(getApplicationContext());
        getAchievements();
        checkAchievements();
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
        findViewById(R.id.home_options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OptionsActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        findViewById(R.id.home_achievements).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AchievementsActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        findViewById(R.id.home_collection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
                intent.putExtra("only_show", 1);
                MainActivity.this.startActivity(intent);
            }
        });
        findViewById(R.id.home_garden).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GardenActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        findViewById(R.id.home_information).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InformationActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        findViewById(R.id.home_qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void getAchievements(){
        ServerRequester.getAchievements(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<Achievement> achievements = JsonUnpacker.getAchievements(response);
                for(Achievement achievement : achievements){
                    //save the image only if the achievement is new
                    if(achievementViewModel.getAchievementByUuid(achievement.getUuid()) == null) {
                        saveImage(achievement);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("SERV", "Error getting achievements");
            }
        });
    }

    private void checkAchievements(){
        itemViewModel.getItemCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != null){
                    itemUnlocked = integer;
                }
            }
        });
        //todo doesn't work!
        Log.e("TEST", String.valueOf(itemUnlocked));
        achievementViewModel.getAllAchievements().observe(this, new Observer<List<Achievement>>() {
            @Override
            public void onChanged(List<Achievement> achievements) {
                for(Achievement achievement : achievements){
                    //unlock item if the requirement is reached
                    if (!achievement.isUnlocked() &&  itemUnlocked >= Integer.parseInt(achievement.getRequirements())){
                        //todo update achievement in db!
                        achievement.setUnlocked(true);
                    }
                }
            }
        });
    }

    public void saveImage(final Achievement achievement){
        final String url = achievement.getImage_url();
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                String title = url.substring(url.lastIndexOf("/") + 1);
                String path = saveImageToInternalStorage(title, response);
                achievement.setImage_url(path);
                achievementViewModel.insertAchievement(achievement);
            }
        }, 0, 0, null, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("SERV", "Error downloading the achievement image from the server");
                    }
                }
        );
        NetworkSingleton.getInstance(this).addRequestQueue(request);
    }


    //save the image in the internal storage
    private String saveImageToInternalStorage(final String title, final Bitmap bitmap){
        ContextWrapper cw = new ContextWrapper(this);
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        File myPath = new File(directory, title);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TEST", myPath.getAbsolutePath());
        return myPath.getAbsolutePath();
    }
}
