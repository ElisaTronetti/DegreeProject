package com.example.degreeapp.Volley;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class ServerRequester {
    private static final String URL = "https://degreeproject.nixo.la/api/";

    private static void addRequest(final JsonObjectRequest request){
        NetworkSingleton.getInstance(null).addRequestQueue(request);
    }

    /**
     * Query the server for the list of all possible achievements.
     * @param res Response listener which receives data on success.
     *            The returned JSON object contains 'achievements', an array containing objects containing id (UUID), title (String),
     *            requirement (String), image_url (String).
     * @param err Error listener which receives error data, if any.
     */
    public static void getAchievements(final Response.Listener<JSONObject> res, final Response.ErrorListener err){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL + "/achievements", null, res, err);
        addRequest(request);
    }

    /**
     * Query the server for an item.
     * @param res Response listener which received data on success.
     *            The returned JSON object contains:
     *            id (UUID), title (String), description (String), url (String)
     * @param err Error listener which receives error data, if any.
     * @param weatherCondition actual weather condition, to make sure the server returns the correct image url.
     */
    //TODO change weather condition, it will be a enum
    public static void getItem(final Response.Listener<JSONObject> res, final Response.ErrorListener err, final String weatherCondition){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL + "/item/" + weatherCondition, null, res, err);
    }
}
