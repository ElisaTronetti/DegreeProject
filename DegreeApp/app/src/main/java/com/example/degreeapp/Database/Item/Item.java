package com.example.degreeapp.Database.Item;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "items", indices = {@Index(value = {"uuid"}, unique = true)})
public class Item {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id")
    private int id;

    private String uuid;
    private String title;
    private String description;
    private String image_url;
    private String unlocked_time;
    private String air_condition;


    public Item(final String uuid, final String title, final String description, final String image_url, final String unlocked_time){
        this.uuid = uuid;
        this.title = title;
        this.description = description;
        this.image_url = image_url;
        this.unlocked_time = unlocked_time;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(final String image_url) {
        this.image_url = image_url;
    }

    public String getUnlocked_time(){
        return unlocked_time;
    }

    public void setUnlocked_time(final String unlocked_time) {
        this.unlocked_time = unlocked_time;
    }

    public String getAir_condition() {
        return air_condition;
    }

    public void setAir_condition(String air_condition) {
        this.air_condition = air_condition;
    }
}
