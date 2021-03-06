package com.example.degreeapp.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.degreeapp.Database.Item.Item;
import com.example.degreeapp.Database.Quote.Quote;
import com.example.degreeapp.Database.Quote.QuoteViewModel;
import com.example.degreeapp.R;
import com.example.degreeapp.Utilities.AirCondition;
import com.example.degreeapp.ui.Achievements.AchievementsActivity;
import com.example.degreeapp.ui.Collection.CollectionActivity;
import com.example.degreeapp.Database.Achievement.Achievement;
import com.example.degreeapp.Database.Achievement.AchievementViewModel;
import com.example.degreeapp.Database.AppRoomDatabase;
import com.example.degreeapp.Database.Item.ItemViewModel;
import com.example.degreeapp.Volley.JsonUnpacker;
import com.example.degreeapp.Volley.NetworkSingleton;
import com.example.degreeapp.Volley.ServerRequester;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public AppRoomDatabase db;
    private AchievementViewModel achievementViewModel;
    private ItemViewModel itemViewModel;
    private QuoteViewModel quoteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppRoomDatabase.getDatabase(this);

        achievementViewModel =  new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AchievementViewModel.class);
        itemViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ItemViewModel.class);
        quoteViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(QuoteViewModel.class);

        checkFirstStart();

        setContentView(R.layout.activity_main);
        setButtonsListeners();

        NetworkSingleton.getInstance(getApplicationContext());
        getQuotes();
        getAchievements();
        checkAchievements();

        createNotificationChannel();
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

    private void getQuotes(){
        ServerRequester.getQuotes(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<Quote> quotes = JsonUnpacker.getQuotes(response);
                for(Quote quote : quotes){
                    quoteViewModel.insertQuote(quote);
                }
                setAlarm();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("SERV", "Error getting quotes");
            }
        });
    }

    private void checkAchievements(){
        itemViewModel.getItemCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(final Integer integer) {
                achievementViewModel.getAllAchievements().observe(MainActivity.this, new Observer<List<Achievement>>() {
                    @Override
                    public void onChanged(List<Achievement> achievements) {
                        for(Achievement achievement : achievements){
                            //unlock item if the requirement is reached
                            if (!achievement.isUnlocked() && integer >= achievement.getRequirements()){
                                //setting an achievement unlocked if the conditions are verified
                                achievement.setUnlocked(true);
                                achievementViewModel.updateAchievement(achievement);
                                buildDialog();
                            }
                        }
                    }
                });
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

    //dialog displayed when an item is returned successfully
    private void buildDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Grande!");
        builder.setMessage("Hai sbloccato un nuovo trofeo, vai a controllare!");
        builder.setNegativeButton("Conferma", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    //sets an alarm that runs daily
    private void setAlarm(){
        String description = quoteViewModel.getRandomQuote().getDescription();
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        intent.putExtra("text", description);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    //create the notification channel to display the notification after oreo sdk version
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "notify";
            String description = "notify_message";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notify", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //checks if the app has been started for the first time
    private void checkFirstStart(){
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //save in internal storage the avatar images
            Bitmap boyImage = BitmapFactory.decodeResource(getResources(),R.drawable.boy);
            String url = saveImageToInternalStorage("boy", boyImage);

            Bitmap girlImage = BitmapFactory.decodeResource(getResources(),R.drawable.girl);
            saveImageToInternalStorage("girl", girlImage);

            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.ITALY);

            Item item = new Item("default", "Io",
                    "Questo sei tu! Puoi cambiare il tuo personaggio dalla schermata delle informazioni",
                    url,
                    df.format(new Date()));
            item.setAir_condition("La qualità dell'aria in questa zona è buona!");
            //add the default item in database
            itemViewModel.insertItem(item);

            //starts information activity
            startActivity(new Intent(MainActivity.this, InformationActivity.class));
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();
    }
}
