package com.example.austbus.ViewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.austbus.Model.Bus;
import com.example.austbus.R;
import com.example.austbus.ShareLocationActivity;

import java.util.ArrayList;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class BusListAdapter extends RecyclerView.Adapter<BusListViewHolder> {

    Context c;
    ArrayList<Bus> models;

    public BusListAdapter(Context c, ArrayList<Bus> models) {
        this.c = c;
        this.models = models;
    }

    @NonNull
    @Override
    public BusListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_bus_list_card, parent, false);
        return new BusListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusListViewHolder busListViewHolder, int position) {
        busListViewHolder.busName.setText(models.get(position).getBusName());
        busListViewHolder.busRoute.setText(models.get(position).getBusRoute());

        busListViewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), Integer.toString(models.get(position).getBusID()), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), ShareLocationActivity.class);
                intent.putExtra("busID", models.get(position).getBusID());
                intent.putExtra("busName", models.get(position).getBusName());
                getApplicationContext().startActivity(intent);
                // ((Activity)getApplicationContext()).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
