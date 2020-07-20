package com.example.barbershop.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.models.Shops;
import com.example.barbershop.ui.barber_shop.BarberShopActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SalonsAdapter extends RecyclerView.Adapter<SalonsAdapter.SalonsViewHolder> {
    Context context;
    ArrayList<Shops> shopsArrayList;

    public SalonsAdapter(Context context, ArrayList<Shops> shopsArrayList) {
        this.context = context;
        this.shopsArrayList = shopsArrayList;
    }

    @NonNull
    @Override
    public SalonsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.salon_card,null,false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new SalonsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalonsViewHolder holder, int position) {
        final Shops shop = shopsArrayList.get(position);
        holder.shop_name.setText(shop.getShopName());
        //holder.shop_img.setImageURI(Uri.parse(shop.getShop_img()));
        Picasso.with(context).load(shop.getShop_img()).into(holder.shop_img);
        holder.shop_type.setText(shop.getType());
        if(shop.getType().equals("Male Salon") || shop.getType().equals("Female Salon"))
        {
            //Drawable.createFromPath("drawable/ic_baseline_person_24.xml").setBounds(5,5,5,5);
            //holder.shop_type.setCompoundDrawables(Drawable.createFromPath("drawable/ic_baseline_person_24.xml"),null,null,null);
        }
        holder.book_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Book Button Clicked!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, BarberShopActivity.class);
                intent.putExtra("shop_name",shop.getShopName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopsArrayList.size();
    }



    public class SalonsViewHolder extends RecyclerView.ViewHolder {
        ImageView shop_img;
        TextView shop_name;
        MaterialTextView shop_type;
        MaterialButton book_button;
        RatingBar shop_rating_bar;
        public SalonsViewHolder(@NonNull View itemView) {
            super(itemView);
            shop_img = itemView.findViewById(R.id.shop_img_sc);
            shop_name = itemView.findViewById(R.id.shop_name_sc);
            book_button = itemView.findViewById(R.id.book_button_sc);
            shop_type = itemView.findViewById(R.id.shop_type_sc);
            shop_rating_bar = itemView.findViewById(R.id.shop_rating_bar);
        }
    }


    public void setModels(ArrayList<Shops> models) {
        shopsArrayList = models;
        notifyDataSetChanged();
    }


    public void removeItem(int position) {
        shopsArrayList.remove(position);
        notifyItemRemoved(position);

    }

    public void addItem(int position, Shops model) {
        shopsArrayList.add(position, model);
        notifyItemInserted(position);

    }

    public void moveItem(int fromPosition, int toPosition) {
        final Shops model = shopsArrayList.remove(fromPosition);
        shopsArrayList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);

    }

    public void animateTo(ArrayList<Shops> models) {
        //applyAndAnimateRemovals(models);
        //applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(ArrayList<Shops> newModels) {
        for (int i = shopsArrayList.size() - 1; i >= 0; i--) {
            final Shops model = shopsArrayList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(ArrayList<Shops> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Shops model = newModels.get(i);
            if (!newModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(ArrayList<Shops> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Shops model = newModels.get(toPosition);
            final int fromPosition = shopsArrayList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
}
