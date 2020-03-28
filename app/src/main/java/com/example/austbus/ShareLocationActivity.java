package com.example.austbus;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.austbus.Model.Bus;
import com.example.austbus.Remote.ServerAPI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareLocationActivity extends AppCompatActivity {

    private FusedLocationProviderClient client;
    LinearLayout linearLayout;
    LinearLayout linearLayoutLoadingAnimation;

    private Handler handler = new Handler();

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

        requestQueue = Volley.newRequestQueue(getApplicationContext());

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
                    sendLocationToServer.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent1 = new Intent(ShareLocationActivity.this, BusListActivity.class);
                startActivity(intent1);
                finish();
            }
        });
    }

    private void getLocation() {
        client.getLastLocation().addOnSuccessListener(ShareLocationActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    loadingTV.setText("Almost done...");
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                }
                else {
                    if(b[0]){
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
            getLocation();

            if(lat!=0.0 && lon!=0.0){
                StringRequest request = new StringRequest(Request.Method.POST, updateUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response Data", response);

                        if(!b[0]){
                            linearLayout.setVisibility(View.VISIBLE);
                            linearLayoutLoadingAnimation.setVisibility(View.INVISIBLE);
                            b[0] = true;
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            sendLocationToServer.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
