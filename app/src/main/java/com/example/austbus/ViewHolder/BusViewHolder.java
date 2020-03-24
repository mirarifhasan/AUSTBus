package com.example.austbus.ViewHolder;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.austbus.Model.Bus;
import com.example.austbus.R;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class BusViewHolder extends RecyclerView.ViewHolder {

    TextView busName, busRoute;
    ConstraintLayout constraintLayout;

    public BusViewHolder(@NonNull View itemView) {
        super(itemView);
        this.busName = itemView.findViewById(R.id.busName);
        this.busRoute = itemView.findViewById(R.id.busRoute);

        constraintLayout = itemView.findViewById(R.id.constrainLayoutBusListCard);
    }

}
