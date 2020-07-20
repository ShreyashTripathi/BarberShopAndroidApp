package com.example.barbershop.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.models.BarberService;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder> {
    Context context;
    ArrayList<BarberService> serviceArrayList;

    public ServicesAdapter(Context context, ArrayList<BarberService> serviceArrayList) {
        this.context = context;
        this.serviceArrayList = serviceArrayList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.service_card,null,false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        BarberService service = serviceArrayList.get(position);
        holder.service_img.setImageURI(Uri.parse(service.getService_img()));
        holder.service_name.setText(service.getService_name());
        holder.service_price.setText(service.getService_price());
        holder.book_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Book Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceArrayList.size();
    }

    public class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView service_img;
        TextView service_name;
        TextView service_price;
        MaterialButton book_button;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            service_img = itemView.findViewById(R.id.service_img);
            service_name = itemView.findViewById(R.id.service_name);
            service_price = itemView.findViewById(R.id.service_price);
            book_button = itemView.findViewById(R.id.book_service);
        }
    }
}
