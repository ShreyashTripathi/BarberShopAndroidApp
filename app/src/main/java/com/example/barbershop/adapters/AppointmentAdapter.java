package com.example.barbershop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.models.AppointmentData;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private Context context;
    private ArrayList<AppointmentData> appointmentDataArrayList;

    public AppointmentAdapter(Context context, ArrayList<AppointmentData> appointmentDataArrayList) {
        this.context = context;
        this.appointmentDataArrayList = appointmentDataArrayList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.appointment_view_card,null,false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentData ad = appointmentDataArrayList.get(position);
        holder.shop_name.setText(ad.getShopName());
        holder.date.setText(ad.getDate());
        holder.time_slot.setText(ad.getTimeSlot());
        holder.hairstylist_name.setText(String.format("Hairstylist Chosen: %s", ad.getHairstylistName()));
        holder.active_status_switch.setChecked(ad.isActive());

    }

    @Override
    public int getItemCount() {
        return appointmentDataArrayList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView shop_name,date,time_slot,hairstylist_name;
        Switch active_status_switch;
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            shop_name = itemView.findViewById(R.id.shop_name_avc);
            date = itemView.findViewById(R.id.appt_date_avc);
            time_slot = itemView.findViewById(R.id.appt_time_avc);
            hairstylist_name = itemView.findViewById(R.id.hs_name_avc);
            active_status_switch = itemView.findViewById(R.id.appt_active_avc);
        }
    }
}
