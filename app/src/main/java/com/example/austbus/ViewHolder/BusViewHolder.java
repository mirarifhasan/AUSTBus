package com.example.austbus.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.austbus.Model.Bus;
import com.example.austbus.R;

public class BusViewHolder extends RecyclerView.ViewHolder {

    TextView busName, busRoute;

    public BusViewHolder(@NonNull View itemView) {
        super(itemView);
        this.busName = itemView.findViewById(R.id.busName);
        this.busRoute = itemView.findViewById(R.id.busRoute);
    }

}
