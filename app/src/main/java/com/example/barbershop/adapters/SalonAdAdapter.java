package com.example.barbershop.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SalonAdAdapter extends RecyclerView.Adapter<SalonAdAdapter.SalonAdViewHolder> {
    private static final String TAG = "SalonAdAdapter";
    ArrayList<StorageReference> imageList;
    Context context;

    public SalonAdAdapter(ArrayList<StorageReference> imageList, Context context) {
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
    public void onBindViewHolder(@NonNull final SalonAdAdapter.SalonAdViewHolder holder, int position) {
        final StorageReference img_ref = imageList.get(position);
        img_ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context).load(uri).into(holder.ad_img);
                Log.println(Log.INFO,TAG,"Img uri: " + uri);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.println(Log.ERROR,TAG,"Img not downloaded: " + img_ref.getName());
            }
        });


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
