package com.example.barbershop.ui.barber_shop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.barbershop.R;
import com.example.barbershop.models.BarberService;
import com.example.barbershop.models.Shops;
import com.example.barbershop.ui.book_appointment.BookAppointment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class BarberShopActivity extends AppCompatActivity {
    private Shops shop;
    private ShopViewModel shopViewModel;

    private ImageView shop_img;
    private TextView shop_name,shop_loc,shop_desc;
    private RatingBar shop_ratings;
    private MaterialButton call_button;
    private CheckBox favButton;

    private ArrayList<BarberService> services;
    private double latitude,longitude;
    private LinearLayout insertPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_shop);
        shopViewModel = new ViewModelProvider(this).get(ShopViewModel.class);
        initialize();
        Intent intent = getIntent();
        String shop_name_text = intent.getStringExtra("shop_name");
        shop = shopViewModel.getShopData(shop_name_text);
        shop_img.setImageResource(shop.getShop_img());
        shop_name.setText(shop.getShopName());
        shop_desc.setText(shop.getAbout());
        shop_ratings.setRating(shop.getRatings());
        shop_loc.setText(shop.getLocation());
        services = shop.getServices();
        call_button.setText(shop.getPhone());

        insertPoint = findViewById(R.id.service_list);

        // fill in any details dynamically here
        for(final BarberService _service : services)
        {
            LayoutInflater vi = LayoutInflater.from(BarberShopActivity.this);
            View v = vi.inflate(R.layout.service_card, null,false);
            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

            ImageView iv = v.findViewById(R.id.service_img);
            iv.setImageResource(_service.getService_img());

            TextView tv1 = v.findViewById(R.id.service_name);
            tv1.setText(_service.getService_name());

            TextView tv2 = v.findViewById(R.id.service_price);
            tv2.setText(String.format("₹%s", _service.getService_price()));
            MaterialButton button = v.findViewById(R.id.book_service);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(BarberShopActivity.this, "Book Service!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BarberShopActivity.this, BookAppointment.class);
                    intent.putExtra("shop_name",shop_name.getText().toString());
                    intent.putExtra("service_booked",_service.getService_name());
                    startActivity(intent);

                }
            });

            insertPoint.addView(v);
        }

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favButton.isChecked())
                {
                    Toast.makeText(BarberShopActivity.this, "Shop added to Favorite!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(BarberShopActivity.this, "Shop removed from Favorite!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initialize() {
        shop_img = findViewById(R.id.shopImg);
        shop_name = findViewById(R.id.shop_name);
        shop_loc = findViewById(R.id.shop_location);
        shop_desc = findViewById(R.id.about_shop);
        shop_ratings = findViewById(R.id.shop_ratings);
        call_button = findViewById(R.id.call_button);
        favButton = findViewById(R.id.fav_button);
    }


    public void goToShopLocation(View view) {
        Toast.makeText(this, "Go to Shop Location!", Toast.LENGTH_SHORT).show();
    }

    public void callButton(View view) {
        String phone = call_button.getText().toString();
        Intent call_intent = new Intent(Intent.ACTION_DIAL);
        call_intent.setData(Uri.parse("tel:" + phone));
        startActivity(call_intent);
    }
}