package com.example.barbershop.ui.barber_shop;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.barbershop.R;
import com.example.barbershop.models.BarberService;
import com.example.barbershop.models.Shops;
import com.example.barbershop.ui.book_appointment.BookAppointment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BarberShopActivity extends AppCompatActivity {
    private MutableLiveData<Shops> shopData;
    private ShopViewModel shopViewModel;

    private ImageView shop_img;
    private TextView shop_name,shop_loc,shop_desc;
    private RatingBar shop_ratings;
    private MaterialButton call_button;
    private CheckBox favButton;

    private ArrayList<BarberService> services;
    private Pair<Double,Double> loc_coordinates;
    private String shop_name_for_map_label;
    private LinearLayout insertPoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_shop);
        shopViewModel = new ViewModelProvider(this).get(ShopViewModel.class);
        initialize();

        Intent intent = getIntent();
        final String shop_name_text = intent.getStringExtra("shop_name");
        shopData = shopViewModel.getShopData(shop_name_text);
        shopData.observe(BarberShopActivity.this, new Observer<Shops>() {
            @Override
            public void onChanged(final Shops shop) {
                Picasso.with(BarberShopActivity.this).load(shop.getShop_img()).into(shop_img);
                shop_name.setText(shop.getShopName());
                shop_name_for_map_label = shop.getShopName();
                shop_desc.setText(shop.getAbout());
                shop_ratings.setRating(shop.getRatings());
                shop_loc.setText(getAddress(new Pair<>(shop.getLatitude(),shop.getLongitude())));
                loc_coordinates = new Pair<>(shop.getLatitude(),shop.getLongitude());
                services = shop.getServices();
                call_button.setText(shop.getPhone());

                insertPoint = findViewById(R.id.service_list);

                for(final BarberService _service : services)
                {
                    LayoutInflater vi = LayoutInflater.from(BarberShopActivity.this);
                    View v = vi.inflate(R.layout.service_card, null,false);
                    v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

                    ImageView iv = v.findViewById(R.id.service_img);
                    Picasso.with(BarberShopActivity.this).load(_service.getService_img()).into(iv);

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
                            intent.putExtra("shop_name",shop.getShopName());
                            intent.putExtra("shop_document_id",shop.getShopName()+"_"+shop.getPhone());
                            intent.putExtra("service_booked",_service.getService_name());
                            startActivity(intent);

                        }
                    });

                    insertPoint.addView(v);
                }
                setShopData(shop);
            }
        });
    }

    private void setShopData(Shops shop) {
        final String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final String shopDocumentID = shop.getShopName()+"_"+shop.getPhone();
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favButton.isChecked())
                {
                    shopViewModel.setShopAsFavourite(shopDocumentID,userID);
                    Toast.makeText(BarberShopActivity.this, "Shop added to Favorite!", Toast.LENGTH_SHORT).show();
                }
                else{
                    shopViewModel.removeShopFromFavourite(shopDocumentID,userID);
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


    public String getAddress(Pair<Double, Double> coordinates) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(BarberShopActivity.this, Locale.getDefault());
        double latitude = coordinates.first;
        double longitude = coordinates.second;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address_ = "ADDRESS";
        if (addresses != null) {
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            address_ = String.format("%s,%s", address.split(",", 0)[1], address.split(",", 0)[2]);
        }
        return address_;
    }

    public void goToShopLocation(View view) {
        Toast.makeText(this, "Go to Shop Location!", Toast.LENGTH_SHORT).show();
        double latitude = loc_coordinates.first;
        double longitude = loc_coordinates.second;
        Uri gmmIntentUri = Uri.parse("geo:0,0?z=20&q="+latitude+","+longitude+"("+shop_name_for_map_label+")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
        else
        {
            Toast.makeText(this, "Can not show the location on map!", Toast.LENGTH_SHORT).show();
        }
    }

    public void callButton(View view) {
        String phone = call_button.getText().toString();
        Intent call_intent = new Intent(Intent.ACTION_DIAL);
        call_intent.setData(Uri.parse("tel:" + phone));
        startActivity(call_intent);
    }
}