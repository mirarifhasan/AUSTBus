package com.example.austbus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.austbus.Common.Common;
import com.example.austbus.Remote.ServerAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ViewBusActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, NavigationView.OnNavigationItemSelectedListener {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;

    private Handler handler = new Handler();
    private boolean isThreadRunning = false;

    String showUrl = new ServerAPI().baseUrl + "showBusLocation.php";

    RequestQueue requestQueue;

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    FloatingActionButton myLocationFloatingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_view_bus);

        try{
            Common.gpsChoice = getIntent().getBooleanExtra("common.gpsChoice", Common.gpsChoice);
        }catch (Exception e){}

        //Navigation Icon
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        mapView.getMapAsync(this);

        myLocationFloatingButton = findViewById(R.id.myLocationFloatingButton);
        myLocationFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    locationComponent = mapboxMap.getLocationComponent();
                    CameraPosition position = new CameraPosition.Builder()
                            .target(new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude()))
                            .zoom(15)
                            .build();

                    mapboxMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(position), 1000);
                } catch (Exception e) {
                    Common.gpsChoice = false;
                    onMapReady(mapboxMap);
                }

            }
        });
    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if(!Common.gpsChoice){
                        buildAlertMessageNoGps();
                        Common.gpsChoice = true;
                    }
                } else {
                    // enableLocationComponent(style);
                    if (Common.isConnectedToInternet(getBaseContext())) {
                        if (!isThreadRunning) loadBus.run();
                    } else {
                        Common common = new Common();
                        common.show(getSupportFragmentManager(), "Seeking internet");
                    }
                }
            }
        });

    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Want to see your own location with buses?")
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        if (!isThreadRunning) loadBus.run();
                        Common.gpsChoice = true;
                    }
                })
                .setNegativeButton("No, Thanks", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        Common.gpsChoice = true;
                        if (Common.isConnectedToInternet(getBaseContext())) {
                            if (!isThreadRunning) loadBus.run();
                        } else {
                            Common common = new Common();
                            common.show(getSupportFragmentManager(), "Seeking internet");
                        }

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    private Runnable loadBus = new Runnable() {
        @Override
        public void run() {
            isThreadRunning = true;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, showUrl, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d("Show Bus response: ", response.toString());
                        JSONArray buses = response.getJSONArray("buses");

                        try {
                            mapboxMap.clear();
                            for (int i = 0; i < buses.length(); i++) {
                                JSONObject bus = buses.getJSONObject(i);

                                double lat = Double.valueOf(bus.getString("Lat"));
                                double lon = Double.valueOf(bus.getString("Lon"));

                                Icon icon = IconFactory.getInstance(ViewBusActivity.this).fromResource(R.drawable.buslocation);
                                mapboxMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon))
                                        .icon(icon)
                                        .title(bus.getString("BusName")));
                            }
                        } catch (Exception e) {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            requestQueue.add(jsonObjectRequest);
            handler.postDelayed(this, 3000);
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        if (!isThreadRunning) loadBus.run();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (!isThreadRunning) loadBus.run();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        handler.removeCallbacks(loadBus);
        isThreadRunning = false;
    }


    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        handler.removeCallbacks(loadBus);
        isThreadRunning = false;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        handler.removeCallbacks(loadBus);
        isThreadRunning = false;
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        // Toast.makeText(this, "user_location_permission_explanation", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            // Toast.makeText(this, "Location_permission_not_granted", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.viewBuses:
                drawerLayout.closeDrawers();
                break;

            case R.id.sharePosition:
                handler.removeCallbacks(loadBus);
                isThreadRunning = false;
                Intent intent = new Intent(ViewBusActivity.this, BusListActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.routeSchedule:
                handler.removeCallbacks(loadBus);
                isThreadRunning = false;
                Intent intent2 = new Intent(ViewBusActivity.this, ScheduleRouteActivity.class);
                startActivity(intent2);
                finish();
                break;

            case R.id.complain:
                handler.removeCallbacks(loadBus);
                isThreadRunning = false;
                Intent intent1 = new Intent(ViewBusActivity.this, ReportActivity.class);
                startActivity(intent1);
                finish();
                break;

            default:
                break;
        }
        return false;
    }
}
