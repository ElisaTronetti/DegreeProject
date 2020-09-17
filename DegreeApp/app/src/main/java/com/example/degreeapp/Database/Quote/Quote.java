package com.example.degreeapp.Database.Quote;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Quote {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "quote_id")
    private int id;

    private String uuid;
    private String description;

    public Quote(final String uuid, final String description){
        this.uuid = uuid;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
