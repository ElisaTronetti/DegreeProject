package com.example.degreeapp.Database.Quote;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.degreeapp.Database.AppRoomDatabase;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class QuoteRepository {
    private QuoteDAO quoteDAO;
    private List<Quote> quotes;

    QuoteRepository(Application application){
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        quoteDAO = db.quoteDAO();

    }

    Quote getRandomQuote(){
        Future<Quote> future;
        Quote quote = null;
        try{
            future = AppRoomDatabase.databaseWriteExecutor.submit(new Callable<Quote>() {
                @Override
                public Quote call() throws Exception {
                    List<Quote> quotes = quoteDAO.getRandomQuote();
                    Random randomGenerator = new Random();
                    int index = randomGenerator.nextInt(quotes.size());
                    return quotes.get(index);
                }
            });
            quote = future.get();
        }catch (Exception e){
            Log.e("DB", "Get random quote error");
        }
        return quote;
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
