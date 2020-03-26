package com.example.austbus.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.austbus.MainActivity;
import com.example.austbus.Model.Bus;
import com.example.austbus.R;
import com.example.austbus.ShareLocationActivity;
import com.example.austbus.ViewBusActivity;

import java.util.ArrayList;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

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

        busViewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), Integer.toString(models.get(position).getBusID()), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), ShareLocationActivity.class);
                intent.putExtra("busID", models.get(position).getBusID());
                intent.putExtra("busName", models.get(position).getBusName());
                getApplicationContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
