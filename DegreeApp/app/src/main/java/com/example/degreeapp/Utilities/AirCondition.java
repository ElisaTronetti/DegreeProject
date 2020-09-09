package com.example.degreeapp.Utilities;

/**
 * Possible weather conditions
 */
public enum WeatherCondition {
    GOOD("good"),
    MEDIUM("medium"),
    BAD("bad");

    private String name;

    private WeatherCondition(final String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
