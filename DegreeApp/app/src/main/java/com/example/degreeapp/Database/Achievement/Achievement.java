package com.example.degreeapp.Database.Achievement;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "achievements", indices = {@Index(value = {"uuid"}, unique = true)})
public class Achievement {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "achievement_id")
    private int id;

    private String uuid;
    private String title;
    private String requirements;
    private String image_url;
    private boolean unlocked;

    public Achievement(final String uuid, final String title, final String requirements, final String image_url) {
        this.uuid = uuid;
        this.title = title;
        this.requirements = requirements;
        this.image_url = image_url;
        this.unlocked = false;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

}
