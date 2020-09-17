package com.example.degreeapp.Database.Quote;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class QuoteViewModel extends AndroidViewModel {
    private QuoteRepository repository;
    private LiveData<List<Quote>> quotes;

    public QuoteViewModel(@NonNull Application application) {
        super(application);
        repository = new QuoteRepository(application);
    }

    public void insertQuote(final Quote quote){
        repository.insertQuote(quote);
    }

    public Quote getRandomQuote() {
        return repository.getRandomQuote();
    }
}
