package com.example.degreeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.degreeapp.Collection.CollectionActivity;
import com.example.degreeapp.Database.Item.Item;
import com.example.degreeapp.Database.Item.ItemViewModel;

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

public class GardenActivity extends AppCompatActivity {
    private static final String FILE_NAME = "garden.txt";

    private ItemViewModel itemViewModel;

    private Map<Integer, Pair<Integer, Integer>> itemsCoordinates = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden);
        itemViewModel =  new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ItemViewModel.class);
        setButtonsListeners();

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int itemId = bundle.getInt("id");
                Item item = itemViewModel.getItemById(itemId);
                if(item != null){
                    loadGardenState();
                    itemsCoordinates.put(item.getId(), new Pair<>(0,0));
                    saveGardenState();
                }
            }
            else {
                //do stuff if you have to display the current status (not a new item to display)
                loadGardenState();
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

    //load the information about items and coordinates from file into a map
    private void loadGardenState(){
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
            for (int i = 0; i < jsonArray.length(); i++) {
                //fill the map
                JSONObject o = jsonArray.getJSONObject(i);
                itemsCoordinates.put(o.getInt("id"), new Pair<>(o.getInt("x"), o.getInt("y")));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

}
