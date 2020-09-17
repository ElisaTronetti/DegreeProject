package com.example.degreeapp.Database.Quote;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuoteDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertQuote(final Quote quote);

    @Query("SELECT * FROM quote")
    LiveData<List<Quote>> getAllQuotes();
}
