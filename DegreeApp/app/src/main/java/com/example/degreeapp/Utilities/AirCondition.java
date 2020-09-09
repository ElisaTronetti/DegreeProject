package com.example.degreeapp.Utilities;

/**
 * Possible weather conditions
 */
public enum AirCondition {
    GOOD("good"),
    MEDIUM("medium"),
    BAD("bad");

    private String name;

    AirCondition(final String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static String getDescription(final AirCondition airCondition){
        switch (airCondition){
            case GOOD: return "La qualità dell'aria in questa zona è buona!";
            case MEDIUM: return  "La qualità dell'aria in questa zona non è male";
            case BAD: return  "La qualità dell'aria in questa zona è pessima";
        }
        return null;
    }
}
