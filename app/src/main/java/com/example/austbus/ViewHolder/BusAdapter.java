package com.example.austbus.ViewHolder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.austbus.Model.Bus;
import com.example.austbus.R;

import java.util.ArrayList;

public class BusAdapter extends RecyclerView.Adapter<BusViewHolder> {

    Context c;
    ArrayList<Bus> models;

    public BusAdapter(Context c, ArrayList<Bus> models) {
        this.c = c;
        this.models = models;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_bus_list_card, parent, false);
        return new BusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder busViewHolder, int position) {
        busViewHolder.busName.setText(models.get(position).getBusName());
        busViewHolder.busRoute.setText(models.get(position).getBusRoute());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
