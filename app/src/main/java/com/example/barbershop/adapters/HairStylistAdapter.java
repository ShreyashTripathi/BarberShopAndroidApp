package com.example.barbershop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.models.HairStylist;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HairStylistAdapter extends RecyclerView.Adapter<HairStylistAdapter.HairStylistViewHolder> {

    ArrayList<HairStylist> hairStylists;
    Context context;

    public HairStylistAdapter(ArrayList<HairStylist> hairStylists, Context context) {
        this.hairStylists = hairStylists;
        this.context = context;
    }

    @NonNull
    @Override
    public HairStylistAdapter.HairStylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.h_s_card,null,false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new HairStylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HairStylistAdapter.HairStylistViewHolder holder, int position) {
        HairStylist hairStylist = hairStylists.get(position);
        //holder.h_s_img.setImageResource(hairStylist.getProfile_pic());
        Picasso.with(context).load(hairStylist.getProfile_pic_url()).into(holder.h_s_img);
        holder.phone_no.setText(hairStylist.getPhone_no());
        holder.name.setText(hairStylist.getName());
        holder.ratingBar.setRating(hairStylist.getRating());
    }

    @Override
    public int getItemCount() {
        return hairStylists.size();
    }

    public class HairStylistViewHolder extends RecyclerView.ViewHolder{
        ImageView h_s_img;
        TextView name;
        RatingBar ratingBar;
        TextView phone_no;
        public HairStylistViewHolder(@NonNull View itemView) {
            super(itemView);
            h_s_img = itemView.findViewById(R.id.h_s_img);
            name = itemView.findViewById(R.id.h_s_name);
            ratingBar = itemView.findViewById(R.id.h_s_rating);
            phone_no = itemView.findViewById(R.id.h_s_phone);
        }
    }
}
