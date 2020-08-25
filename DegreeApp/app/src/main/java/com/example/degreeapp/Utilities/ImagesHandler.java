package com.example.degreeapp.Utilities;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.room.Database;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.degreeapp.Database.AppRoomDatabase;
import com.example.degreeapp.Database.Item.Item;
import com.example.degreeapp.Volley.NetworkSingleton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImagesHandler {
    //request the image from the server and save it in the internal storage
    public static Item saveImage(final Item item, final Context context){
        final String url = item.getImage_url();
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                String path = saveImageToInternalStorage(url, response, context);
                item.setImage_url(path);
            }
        }, 0, 0, null, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("SERV", "Error downloading the image from the server");
                    }
                }
        );
        NetworkSingleton.getInstance(context).addRequestQueue(request);
        return item;
    }

    //save the image in the internal storage
    private static String saveImageToInternalStorage(final String title, final Bitmap bitmap, final Context context){
        ContextWrapper cw = new ContextWrapper(context);
        Log.e("TEST", "PRE FILE LIST");
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        Log.e("TEST", "POST FILE LIST");
        File myPath = new File(directory, title);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("ELISA", myPath.getAbsolutePath());
        return myPath.getAbsolutePath();
    }

}
