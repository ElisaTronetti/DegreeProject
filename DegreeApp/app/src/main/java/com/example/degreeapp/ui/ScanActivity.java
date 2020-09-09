package com.example.degreeapp.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.degreeapp.Database.Item.Item;
import com.example.degreeapp.Database.Item.ItemViewModel;
import com.example.degreeapp.R;
import com.example.degreeapp.Utilities.Const;
import com.example.degreeapp.Utilities.AirCondition;
import com.example.degreeapp.Volley.JsonUnpacker;
import com.example.degreeapp.Volley.NetworkSingleton;
import com.example.degreeapp.Volley.ServerRequester;
import com.google.zxing.Result;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity {
    private ItemViewModel itemViewModel;
    private ZXingScannerView scannerView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        scannerView = findViewById(R.id.zxscan);
        setButtonListeners();
        itemViewModel =  new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ItemViewModel.class);

        //request camera permissions if necessary
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Const.CAMERA_REQUEST_CODE);
        } else {
            starScan();
        }

    }

    @Override
    protected void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }

    //This is used to hide/show 'Status Bar' & 'System Bar'. Swipe bar to get it as visible.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void setButtonListeners(){
        findViewById(R.id.scan_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                ScanActivity.this.startActivity(intent);
            }
        });
        //open dialog to help to understand how scan works
        findViewById(R.id.scan_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
                builder.setTitle(R.string.help_title);
                builder.setMessage(R.string.help_description);
                builder.setPositiveButton(R.string.capito, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
    }

    //scan the qr-code with the camera and get the text
    private void starScan() {
        scannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
            @Override
            public void handleResult(Result rawResult) {
                //data read from qrcode
                if (rawResult.getText().equals(Const.QRCODE_TEXT)) {
                    progressDialog = new ProgressDialog(ScanActivity.this);
                    progressDialog.setMessage("L'oggetto sta per arrivare, attendi...");
                    progressDialog.show();
                    getLocationData();
                } else {
                    //handle qr code error
                    buildDialog("Qualcosa non va...", "Il QR Code che hai scannerizzato non è corretto");
                }
            }
        });
        scannerView.startCamera();
    }

    //get location data from gps
    private void getLocationData() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isNetworkEnabled && isGPSEnabled) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            //check permissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Const.LOCATION_REQUEST_CODE);
            } else {
                locationManager.requestSingleUpdate(criteria, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        //get data about the pollution of the air, passing longitude and latitude
                        getAirData(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));
                    }
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {}
                    @Override
                    public void onProviderEnabled(String provider) {}
                    @Override
                    public void onProviderDisabled(String provider) {}
                }, null);
            }
        }
    }

    //actually send request to AirCheckr server to get air pollution data
    private void getAirData(final String longitude, final String latitude){
        ServerRequester.getWeatherConditions(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                AirCondition currentAirCondition = JsonUnpacker.getWeatherCondition(response);
                if(currentAirCondition != null){
                    //get item with the current air condition
                    getItem(currentAirCondition);
                } else {
                    Log.e("SERV", "Something wrong getting current weather conditions");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SERV", "AirCheckr server error response");
                error.printStackTrace();
            }
        }, longitude, latitude);
    }

    //request an item to server with the current AirCondition
    private void getItem(final AirCondition airCondition){
        ServerRequester.getItem(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Item item = JsonUnpacker.getItem(response);
                progressDialog.dismiss();
                item.setAir_condition(AirCondition.getDescription(airCondition));
                if(itemViewModel.getItemByUuid(item.getUuid()) == null){
                    saveImage(item);
                    buildDialog("Bravo!", "Hai trovato " + item.getTitle() + ", è stato aggiunto alla tua collezione.");
                } else {
                    Log.e("TEST", "Item già posseduto");
                    buildDialog("Oh no, che sfortuna!", "Hai trovato " + item.getTitle() + ", ma lo avevi già collezionato...");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("SERV", "Get item server error");
            }
        }, airCondition.getName());
    }

    //dialog displayed when an item is returned successfully
    private void buildDialog(final String title, final String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setNegativeButton("Conferma", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                ScanActivity.this.startActivity(intent);
            }
        });
        builder.show();
    }

    //request the image from the server and save it in the internal storage
    public void saveImage(final Item item){
        final String url = item.getImage_url();
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                String title = url.substring(url.lastIndexOf("/") + 1);
                String path = saveImageToInternalStorage(title, response);
                item.setImage_url(path);
                itemViewModel.insertItem(item);
            }
        }, 0, 0, null, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("SERV", "Error downloading the item image from the server");
                    }
                }
        );
        NetworkSingleton.getInstance(this).addRequestQueue(request);
    }

    //save the image in the internal storage
    private String saveImageToInternalStorage(final String title, final Bitmap bitmap){
        ContextWrapper cw = new ContextWrapper(this);
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        File myPath = new File(directory, title);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TEST", myPath.getAbsolutePath());
        return myPath.getAbsolutePath();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Const.CAMERA_REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    starScan();
                } else {
                    Toast.makeText(ScanActivity.this, "Impossibile continuare, accetta i permessi delle camera", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                    ScanActivity.this.startActivity(intent);
                }
                break;
            case Const.LOCATION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLocationData();
                } else {
                    Toast.makeText(ScanActivity.this, "Impossibile continuare, accetta i permessi della localizzazione", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                    ScanActivity.this.startActivity(intent);
                }
        }
    }
}
