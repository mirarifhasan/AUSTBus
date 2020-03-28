package com.example.austbus.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.austbus.Model.Bus;
import com.example.austbus.R;

import java.util.ArrayList;

public class BusDetailsAdapter extends RecyclerView.Adapter<BusDetailsViewHolder> {

    Context c;
    ArrayList<Bus> models;

    public BusDetailsAdapter(Context c, ArrayList<Bus> models) {
        this.c = c;
        this.models = models;
    }

    @NonNull
    @Override
    public BusDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_details_card, parent, false);
        return new BusDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusDetailsViewHolder busDetailsViewHolder, int position) {
        busDetailsViewHolder.busName.setText(models.get(position).getBusName());
        busDetailsViewHolder.busRoute.setText("Route: " + models.get(position).getRouteToAUST());
        busDetailsViewHolder.startingTime.setText("Starting Time: "+models.get(position).getStartingTime());
        busDetailsViewHolder.departureTime.setText("Departure Time: "+models.get(position).getDepartureTime());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
