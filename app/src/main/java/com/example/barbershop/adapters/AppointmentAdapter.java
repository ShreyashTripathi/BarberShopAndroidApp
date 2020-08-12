package com.example.barbershop.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.models.AppointmentData;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private Context context;
    private ArrayList<AppointmentData> appointmentDataArrayList;
    private SetIsBookingCancelled setIsBookingCancelledGlobal;

    public AppointmentAdapter(Context context, ArrayList<AppointmentData> appointmentDataArrayList,SetIsBookingCancelled setIsBookingCancelled) {
        this.context = context;
        this.appointmentDataArrayList = appointmentDataArrayList;
        this.setIsBookingCancelledGlobal = setIsBookingCancelled;
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
    public void onBindViewHolder(@NonNull final AppointmentViewHolder holder, final int position) {
        final AppointmentData ad = appointmentDataArrayList.get(position);
        String shop_name_text = "Shop Not Selected!",worker_name_text = "Worker not selected";
        if(ad.getShopId()!=null)
            shop_name_text = ad.getShopId().split("_", 0)[0];
        holder.shop_name.setText(shop_name_text);
        holder.service.setText(ad.getService_opted());
        holder.date.setText(ad.getDate());
        holder.time_slot.setText(ad.getTimeSlot());
        if(ad.getWorkerId() != null)
            worker_name_text = ad.getWorkerId().split("_",0)[0];
        holder.worker_name.setText(String.format("Worker Chosen: %s", worker_name_text));
        holder.active_status_switch.setChecked(ad.isActive_status());
        if(!ad.isActive_status())
            holder.active_status_switch.setClickable(false);
        else
            holder.active_status_switch.setClickable(true);
        if(ad.isActive_status()) {
            holder.active_status_switch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createAskUserDialog(holder.active_status_switch,ad,position);
                }
            });
        }
    }



    private void createAskUserDialog(final CompoundButton button, final AppointmentData ad,final int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setMessage("Do you want to cancel the booking?\nYou won't be able to alter the booking!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        button.setChecked(false);
                        button.setClickable(false);
                        setIsBookingCancelledGlobalStatus(true, ad);
                        appointmentDataArrayList.get(position).setActive_status(false);
                        notifyItemChanged(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        button.setChecked(true);
                        button.setClickable(true);
                        setIsBookingCancelledGlobalStatus(false,ad);
                        appointmentDataArrayList.get(position).setActive_status(true);
                        notifyItemChanged(position);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Booking Status");
        alertDialog.show();
    }

    public interface SetIsBookingCancelled{
        void isBookingCancelled(boolean bookingStatus,AppointmentData appointmentData);
    }

    private void setIsBookingCancelledGlobalStatus(boolean b,AppointmentData appointmentData) {
        setIsBookingCancelledGlobal.isBookingCancelled(b,appointmentData);

    }

    @Override
    public int getItemCount() {
        return appointmentDataArrayList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView shop_name,date,time_slot,worker_name,service;
        Switch active_status_switch;
        
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            shop_name = itemView.findViewById(R.id.shop_name_avc);
            date = itemView.findViewById(R.id.appt_date_avc);
            time_slot = itemView.findViewById(R.id.appt_time_avc);
            worker_name = itemView.findViewById(R.id.worker_name_avc);
            active_status_switch = itemView.findViewById(R.id.appt_active_avc);
            service = itemView.findViewById(R.id.service_opted_avc);
        }
    }
}
