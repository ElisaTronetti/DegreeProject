package com.example.degreeapp.Volley;

import android.util.Log;

import com.example.degreeapp.Database.Achievement.Achievement;
import com.example.degreeapp.Database.Item.Item;
import com.example.degreeapp.Utilities.AirCondition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
                int requirement = Integer.parseInt(achievementJson.getString("requirement"));
                Achievement achievement = new Achievement(
                        achievementJson.getString("id"),
                        achievementJson.getString("title"),
                        requirement,
                        "https://degreeproject.nixo.la/storage/" + achievementJson.getString("image_url"));
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
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.ITALY);
        try{
            item = new Item(
                    jsonObject.getString("id"),
                    jsonObject.getString("title"),
                    jsonObject.getString("description"),
                    jsonObject.getString("url"),
                    df.format(new Date()));
        } catch (JSONException e){
            e.printStackTrace();
            Log.e("VOLLEY", "Error unpacking item");
        }
        return item;
    }

    public static AirCondition getWeatherCondition(final JSONObject jsonObject){
        try {
            JSONArray data = jsonObject.getJSONArray("data");
            JSONObject specificData = data.getJSONObject(0);
            JSONObject aqi11 = specificData.getJSONObject("aqi_11");
            String aqi11Value = aqi11.getString("class");

            if(Integer.parseInt(aqi11Value) <= 3){
                return AirCondition.BAD;
            } else if(Integer.parseInt(aqi11Value) >= 7){
                return  AirCondition.GOOD;
            } else {
                return AirCondition.MEDIUM;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
