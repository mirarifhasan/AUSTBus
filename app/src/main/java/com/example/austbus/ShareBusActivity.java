package com.example.austbus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.austbus.Model.Bus;
import com.example.austbus.Remote.ServerAPI;
import com.example.austbus.ViewHolder.BusAdapter;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShareBusActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    BusAdapter busAdapter;

    RequestQueue requestQueue;
    String showUrl = new ServerAPI().baseUrl + "showBusList.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_bus);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        busAdapter = new BusAdapter(this, getMyList());
        recyclerView.setAdapter(busAdapter);
    }

    private ArrayList<Bus> getMyList() {

        ArrayList<Bus> models = new ArrayList<>();
        Bus busObj = new Bus("Arif", "Dhaka");
        models.add(busObj);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, showUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray buses = response.getJSONArray("buses");

                    for (int i = 0; i < buses.length(); i++) {
                        JSONObject bus = buses.getJSONObject(i);

                        Bus busObj = new Bus(bus.getString("BusName"), bus.getString("RouteToAUST"));
                        models.add(busObj);
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

        return models;
    }
}
