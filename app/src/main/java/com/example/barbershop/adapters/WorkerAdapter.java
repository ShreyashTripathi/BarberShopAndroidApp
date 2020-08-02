package com.example.barbershop.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.models.Worker;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.HairStylistViewHolder> {

    ArrayList<Worker> workers;
    Context context;
    OnGetHairStylist onGetHairStylist;

    public WorkerAdapter(ArrayList<Worker> workers, Context context, OnGetHairStylist onGetHairStylist) {
        this.workers = workers;
        this.context = context;
        this.onGetHairStylist = onGetHairStylist;
    }

    @NonNull
    @Override
    public WorkerAdapter.HairStylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.h_s_card,null,false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new HairStylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerAdapter.HairStylistViewHolder holder, int position) {
        final Worker worker = workers.get(position);
        //holder.h_s_img.setImageResource(worker.getProfile_pic());
        Picasso.with(context).load(worker.getProfile_pic_url()).into(holder.h_s_img);
        holder.phone_no.setText(worker.getPhone_no());
        holder.phone_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = worker.getPhone_no();
                Intent call_intent = new Intent(Intent.ACTION_DIAL);
                call_intent.setData(Uri.parse("tel:" + phone));
                context.startActivity(call_intent);
            }
        });
        holder.name.setText(worker.getName());
        holder.ratingBar.setRating(worker.getRating());
        holder.h_s_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGetHairStylist.getHairStylist(worker);
                Toast.makeText(context, "Worker: "+ worker.getName()+" chosen!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnGetHairStylist{
        void getHairStylist(Worker worker);
    }

    @Override
    public int getItemCount() {
        return workers.size();
    }

    public static class HairStylistViewHolder extends RecyclerView.ViewHolder{
        ImageView h_s_img;
        TextView name;
        RatingBar ratingBar;
        TextView phone_no;
        MaterialCardView h_s_card;
        public HairStylistViewHolder(@NonNull View itemView) {
            super(itemView);
            h_s_img = itemView.findViewById(R.id.h_s_img);
            name = itemView.findViewById(R.id.h_s_name);
            ratingBar = itemView.findViewById(R.id.h_s_rating);
            phone_no = itemView.findViewById(R.id.h_s_phone);
            h_s_card = itemView.findViewById(R.id.hair_stylist_card);
        }
    }
}

