package com.example.degreeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.degreeapp.Collection.CollectionActivity;
import com.example.degreeapp.Database.Item.Item;
import com.example.degreeapp.Database.Item.ItemViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GardenActivity extends AppCompatActivity {
    private static final String FILE_NAME = "garden_conf.txt";

    private ItemViewModel itemViewModel;
    private ConstraintLayout layout;

    private int xDelta;
    private int yDelta;
    private int xCoordinate;
    private int yCoordinate;

    public Map<Integer, Pair<Integer, Integer>> itemsCoordinates = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden);
        itemViewModel =  new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ItemViewModel.class);
        layout = findViewById(R.id.garden_layout);

        //get the default position where a new item will spawn
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int defaultX = size.x / 2;
        int defaultY = size.y / 2;

        setButtonsListeners();

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int itemId = bundle.getInt("id");
                Item item = itemViewModel.getItemById(itemId);
                if(item != null){
                    //if a new item has to be added, first I load the past data from file
                    loadDataFromFile();
                    if(itemsCoordinates.get(item.getId()) == null){
                        //add the element in the current map (if it is a new element)
                        itemsCoordinates.put(item.getId(), new Pair<>(defaultX, defaultY));
                    } else {
                        //se l'elemento era già presente fa in modo di renderlo noto al bambino!
                        Toast.makeText(this, "Item già presente", Toast.LENGTH_LONG).show();
                    }
                    //saving in file the current status
                    saveGardenState();
                }
            }
            //load data from file
            loadDataFromFile();
            //actually draw the garden
            loadGardenState();
        }

    }

    @Override
    protected void onDestroy() {
        saveGardenState();
        super.onDestroy();
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
        findViewById(R.id.garden_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GardenActivity.this, MainActivity.class);
                GardenActivity.this.startActivity(intent);
                saveGardenState();
            }
        });
        findViewById(R.id.garden_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GardenActivity.this, CollectionActivity.class);
                intent.putExtra("only_show", 0);
                GardenActivity.this.startActivity(intent);
            }
        });
    }

    //used to save garden state on file, in jsonArray format
    private void saveGardenState() {
        JSONArray jsonArray;
        String itemsString;
        try {
            jsonArray = new JSONArray();
            //create a jsonObject with all the information needed to recreate the garden
            for(Map.Entry<Integer, Pair<Integer, Integer>> entry : itemsCoordinates.entrySet()){
                Integer id = entry.getKey();
                Pair<Integer, Integer> coordinates = entry.getValue();
                JSONObject itemInformation = new JSONObject();
                itemInformation.put("id", id)
                        .put("x", coordinates.first)
                        .put("y", coordinates.second);
                jsonArray.put(itemInformation);
            }
            itemsString = jsonArray.toString();
            File file = new File(getFilesDir(), FILE_NAME);
            try {
                //write the jsonArray on file
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(itemsString);
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //read the file to get all the information to build the map
    private void loadDataFromFile(){
        File file = new File(getFilesDir(), FILE_NAME);
        FileReader fileReader = null;
        try {
            //read file
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null){
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            // This response will have Json Format String
            String response = stringBuilder.toString();
            JSONArray jsonArray = new JSONArray(response);
            Log.e("TEST", String.valueOf(jsonArray.length()));
            for (int i = 0; i < jsonArray.length(); i++) {
                //fill the map

                JSONObject o = jsonArray.getJSONObject(i);
                Log.e("TEST", o.toString());
                int itemId = o.getInt("id");
                itemsCoordinates.put(itemId, new Pair<>(o.getInt("x"), o.getInt("y")));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    //take data from the map to recreate the view
    @SuppressLint("ClickableViewAccessibility")
    private void loadGardenState(){
            for (Map.Entry<Integer, Pair<Integer, Integer>> entry : itemsCoordinates.entrySet()) {

                //create image view with the src of the actual item
                ImageView imageView = new ImageView(this);
                imageView.setId(entry.getKey());

                Item item = itemViewModel.getItemById(entry.getKey());
                Picasso.get()
                        .load(item.getImage_url())
                        .placeholder(R.drawable.baseline_lock_black_24dp)
                        .error(R.drawable.baseline_image_not_supported_black_24dp)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.e("PICASSO", "Image garden ok");
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("PICASSO", "Image garden error");
                            }
                        });

                imageView.setOnTouchListener(onTouchListener());

                imageView.setX(entry.getValue().first);
                imageView.setY(entry.getValue().second);

                layout.addView(imageView);
            }

    }

    private View.OnTouchListener onTouchListener(){
        return new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //TODO inserisci vincoli di movimento di modo che gli elementi non possano essere posizionati fuori dalla view effettiva
                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                //handle the movement of the image view
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        xDelta = (int)v.getX() - x;
                        yDelta = (int)v.getY() - y;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        xCoordinate = (int)event.getRawX() + xDelta;
                        yCoordinate =(int)event.getRawY() + yDelta;
                        v.animate()
                                .x(xCoordinate)
                                .y(yCoordinate)
                                .setDuration(0)
                                .start();
                        itemsCoordinates.remove(v.getId());
                        itemsCoordinates.put(v.getId(), new Pair<>(xCoordinate, yCoordinate));
                        break;
                }
                return true;

            }
        };
    }
}
