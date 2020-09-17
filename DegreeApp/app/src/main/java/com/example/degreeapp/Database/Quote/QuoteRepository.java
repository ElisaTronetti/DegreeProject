package com.example.degreeapp.Database.Quote;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.degreeapp.Database.AppRoomDatabase;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class QuoteRepository {
    private QuoteDAO quoteDAO;
    private LiveData<List<Quote>> quotes;

    QuoteRepository(Application application){
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        quoteDAO = db.quoteDAO();
        quotes = quoteDAO.getAllQuotes();
    }

    LiveData<List<Quote>> getAllQuotes(){
        return quotes;
    }

    long insertQuote(final Quote quote){
        Future<Long> future;
        long quoteId = 0;
        try{
            future = AppRoomDatabase.databaseWriteExecutor.submit(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    return quoteDAO.insertQuote(quote);
                }
            });
            quoteId = future.get();
        }catch (Exception e){
            Log.e("DB", "Insert quote error");
        }
        return quoteId;
    }
}
