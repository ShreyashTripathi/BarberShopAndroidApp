package com.example.barbershop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;

import java.util.ArrayList;

public class SalonAdAdapter extends RecyclerView.Adapter<SalonAdAdapter.SalonAdViewHolder> {
    ArrayList<Integer> imageList;
    Context context;

    public SalonAdAdapter(ArrayList<Integer> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
    }

    @NonNull
    @Override
    public SalonAdAdapter.SalonAdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.salon_ad_card,null,false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new SalonAdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalonAdAdapter.SalonAdViewHolder holder, int position) {
        int _img = imageList.get(position);
        holder.ad_img.setImageResource(_img);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class SalonAdViewHolder extends RecyclerView.ViewHolder{
        ImageView ad_img;
        public SalonAdViewHolder(@NonNull View itemView) {
            super(itemView);
            ad_img = itemView.findViewById(R.id.ad_img);
        }
    }
}
