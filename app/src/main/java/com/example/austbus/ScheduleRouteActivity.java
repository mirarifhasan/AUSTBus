package com.example.austbus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.austbus.Model.Bus;
import com.example.austbus.Remote.ServerAPI;
import com.example.austbus.ViewHolder.BusDetailsAdapter;
import com.example.austbus.ViewHolder.BusListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScheduleRouteActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    BusDetailsAdapter busDetailsAdapter;
    LinearLayout linearLayout;

    SwipeRefreshLayout swipeRefreshLayout;

    RequestQueue requestQueue;
    String showUrl = new ServerAPI().baseUrl + "showBusList.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_route);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        linearLayout = findViewById(R.id.backLinearLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getBusListOnRecyclerView();

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleRouteActivity.this, ViewBusActivity.class);
                startActivity(intent);
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBusListOnRecyclerView();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    private void getBusListOnRecyclerView() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, showUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("Response", response.toString());
                    JSONArray buses = response.getJSONArray("buses");
                    ArrayList<Bus> models = new ArrayList<>();

                    for (int i = 0; i < buses.length(); i++) {
                        JSONObject bus = buses.getJSONObject(i);

                        Bus busObj = new Bus(bus.getString("BusName"), bus.getString("RouteToAUST"), bus.getInt("BusID"), bus.getString("StartingTime"), bus.getString("DepartureTime"));
                        models.add(busObj);
                    }
                    busDetailsAdapter = new BusDetailsAdapter(getApplicationContext(), models);
                    recyclerView.setAdapter(busDetailsAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ScheduleRouteActivity.this, "Response Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
