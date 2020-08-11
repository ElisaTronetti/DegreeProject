package com.example.degreeapp.Volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * This is a singleton that is used to get the instance of the volley's request queue.
 * It will exist only one request queue for the entire life of the application.
 */
public class NetworkSingleton {
    private static NetworkSingleton instance = null;
    private RequestQueue requestQueue;
    private Context ctx;

    private NetworkSingleton(Context context){
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized NetworkSingleton getInstance(Context context){
        if(instance == null){
            instance = new NetworkSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            //we use the application context to be able to have the same request queue for all the
            //life of the application
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }
}
