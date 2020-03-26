package com.example.austbus.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.austbus.R;

public class BusDetailsViewHolder extends RecyclerView.ViewHolder {

    TextView busName, busRoute, startingTime, departureTime;
    ConstraintLayout constraintLayout;

    public BusDetailsViewHolder(@NonNull View itemView) {
        super(itemView);
        this.busName = itemView.findViewById(R.id.busName);
        this.busRoute = itemView.findViewById(R.id.busRoute);
        this.startingTime = itemView.findViewById(R.id.startTimeTextView);
        this.departureTime = itemView.findViewById(R.id.departureTimeTextView);

        constraintLayout = itemView.findViewById(R.id.constrainLayoutBusListCard);
    }
}
