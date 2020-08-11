package com.example.degreeapp.Volley;

import android.util.Log;

import com.example.degreeapp.Database.Achievement.Achievement;
import com.example.degreeapp.Database.Item.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to unpack JsonObjects that the server returns
 */
public class JsonUnpacker {

    /**
     * Unpack the jsonObject in a actual list of achievements.
     * @param jsonObject sent by server, contains an jsonArray with all the achievements.
     * @return list of achievements.
     */
    public static List<Achievement> getAchievements(final JSONObject jsonObject){
        JSONArray jsonArray;
        List<Achievement> achievements = new ArrayList<>();
        try{
            jsonArray = jsonObject.getJSONArray("achievements");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject achievementJson = jsonArray.getJSONObject(i);

                Achievement achievement = new Achievement(
                        achievementJson.getString("id"),
                        achievementJson.getString("title"),
                        achievementJson.getString("requirement"),
                        achievementJson.getString("image_url"));
                achievements.add(achievement);
            }
        } catch (JSONException e){
            e.printStackTrace();
            Log.e("VOLLEY", "Error unpacking achievements");
        }
        return achievements;
    }

    /**
     * Unpack the jsonObject in a actual item.
     * @param jsonObject sent by server, contains data of an item.
     * @return an actual item.
     */
    public static Item getItem(final JSONObject jsonObject){
        Item item = null;
        try{
            item = new Item(
                    jsonObject.getString("id"),
                    jsonObject.getString("title"),
                    jsonObject.getString("description"),
                    jsonObject.getString("url"));
        } catch (JSONException e){
            e.printStackTrace();
            Log.e("VOLLEY", "Error unpacking item");
        }
        return item;
    }
}
