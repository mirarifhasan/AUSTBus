package com.example.austbus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.austbus.Common.Common;
import com.example.austbus.Model.Bus;
import com.example.austbus.Remote.ServerAPI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareLocationActivity extends AppCompatActivity {

    private FusedLocationProviderClient client;
    LinearLayout linearLayout;
    LinearLayout linearLayoutLoadingAnimation;

    private Handler handler = new Handler();
    private boolean isThreadAlive = false;

    Bus bus;
    TextView busNameTV, loadingTV;
    Button stopSharingBtn;

    double lat, lon;
    final boolean[] b = {false};

    RequestQueue requestQueue;
    String updateUrl = new ServerAPI().baseUrl + "updatebus.php";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);

        requestQueue = Volley.newRequestQueue(this);

        client = LocationServices.getFusedLocationProviderClient(this);
        bus = new Bus(getIntent().getIntExtra("busID", 0), getIntent().getStringExtra("busName"));

        busNameTV = (TextView) findViewById(R.id.busNameTextView);
        busNameTV.setText(bus.getBusName() + " getting your location");
        loadingTV = findViewById(R.id.loadingTextView);

        linearLayout = findViewById(R.id.successLiniarLayout);
        linearLayout.setVisibility(View.INVISIBLE);
        linearLayoutLoadingAnimation = findViewById(R.id.loadingAnimation);

        sendLocationToServer.run(); //There is another function for send location to server

        stopSharingBtn = findViewById(R.id.stopSharingButton);
        stopSharingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isThreadAlive) {
                        handler.removeCallbacks(sendLocationToServer);
                        isThreadAlive = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent1 = new Intent(ShareLocationActivity.this, ViewBusActivity.class);
                startActivity(intent1);
                finish();
            }
        });
    }

    @SuppressWarnings({"MissingPermission"})
    private void getLocation() {
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            Toast.makeText(ShareLocationActivity.this, "Location permission not provided", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(ShareLocationActivity.this, ViewBusActivity.class);
            startActivity(intent1);
            finish();
        }
        client.getLastLocation().addOnSuccessListener(ShareLocationActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    loadingTV.setText("Almost done...");
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                } else {
                    handler.removeCallbacks(sendLocationToServer);
                    isThreadAlive = false;

                    final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();
                    }
                    else if (!Common.isConnectedToInternet(getBaseContext())) {
                        Common common = new Common();
                        common.show(getSupportFragmentManager(), "Seeking internet");
                    }
                    else{
                        try {
                            Toast.makeText(ShareLocationActivity.this, String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            Toast.makeText(ShareLocationActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        if(!isThreadAlive) sendLocationToServer.run();return;
                    }

                    Toast.makeText(ShareLocationActivity.this, "Problem", Toast.LENGTH_SHORT).show();
                    if (b[0]) {
                        linearLayout.setVisibility(View.INVISIBLE);
                        linearLayoutLoadingAnimation.setVisibility(View.VISIBLE);
                        b[0] = false;
                    }
                }
            }
        });
    }


    private Runnable sendLocationToServer = new Runnable() {
        @Override
        public void run() {
            isThreadAlive = true;
            getLocation();

            if (lat != 0.0 && lon != 0.0) {
                StringRequest request = new StringRequest(Request.Method.POST, updateUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response Data", response);

                        if (!b[0]) {
                            linearLayout.setVisibility(View.VISIBLE);
                            linearLayoutLoadingAnimation.setVisibility(View.INVISIBLE);
                            b[0] = true;
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("busID", String.valueOf(bus.getBusID()));
                        parameters.put("Lat", String.valueOf(lat));
                        parameters.put("Lon", String.valueOf(lon));
                        Log.d("GPS Position", parameters.toString());

                        return parameters;
                    }
                };
                requestQueue.add(request);
            }
            handler.postDelayed(this, 3000);
        }
    };

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please turn on your GPS")
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.dismiss();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No, Thanks", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        if (isThreadAlive) {
                            handler.removeCallbacks(sendLocationToServer);
                            isThreadAlive = false;
                        }
                        Intent intent1 = new Intent(ShareLocationActivity.this, ViewBusActivity.class);
                        intent1.putExtra("common.gpsChoice", true);
                        startActivity(intent1);
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isThreadAlive) sendLocationToServer.run();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (isThreadAlive) {
                handler.removeCallbacks(sendLocationToServer);
                isThreadAlive = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent1 = new Intent(ShareLocationActivity.this, ViewBusActivity.class);
        startActivity(intent1);
        finish();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(!isThreadAlive) sendLocationToServer.run();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isThreadAlive) handler.removeCallbacks(sendLocationToServer);
    }
}
