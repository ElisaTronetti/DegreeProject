package com.example.degreeapp.Volley;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ServerRequester {
    private static final String URL = "https://degreeproject.nixo.la/api/";
    private static final String URL_AIR_CHECKR = "https://api.aircheckr.com/territory/location/";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.MTE5Y2FiNjAtZGZiYi0xMWVhLTlmNTMtM2Q0OWNjOWNlOWY2.6Fvgw_osqIRroDmWtkkQrHNr49lljP4fm1NJGtM512s";

    private static class JsonObjectRequest extends com.android.volley.toolbox.JsonObjectRequest {
        private Map<String, String> headers;

        JsonObjectRequest(int method, String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
            this.headers = new HashMap<>();
        }

        void setHeader(String key, String value) {
            headers.put(key, value);
        }

        @Override
        public Map<String,String> getHeaders() {
            return headers;
        }
    }

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
        addRequest(request);
    }

    public static void getWeatherConditions(final Response.Listener<JSONObject> res, final Response.ErrorListener err, final String longitude, final String latitude){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_AIR_CHECKR + longitude + "/" + latitude, null, res, err);
        request.setHeader("x-access-token", TOKEN);
        addRequest(request);
    }
}
