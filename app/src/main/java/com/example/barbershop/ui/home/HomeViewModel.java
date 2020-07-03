package com.example.barbershop.ui.home;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.barbershop.R;
import com.example.barbershop.models.HairStylist;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeViewModel extends ViewModel {

    private MutableLiveData< ArrayList<HairStylist> > hair_stylists_list;
    private MutableLiveData< ArrayList<Integer> > advertisement_list;
    private String myAddress;

    private final String TAG = "home_view_model";

    public HomeViewModel() {
        Log.println(Log.INFO,TAG,"Home View Model Running!");
        hair_stylists_list = new MutableLiveData<>();
        advertisement_list = new MutableLiveData<>();
        setHairStylistsList();
        setAdvertisementList();


    }

    private void setAdvertisementList() {
        ArrayList<Integer> image_list = new ArrayList<>();
        image_list.add(R.drawable.model7);
        image_list.add(R.drawable.model3);
        image_list.add(R.drawable.model5);
        image_list.add(R.drawable.model6);
        image_list.add(R.drawable.model7);

        advertisement_list.setValue(image_list);
    }

    private void setHairStylistsList()
    {
        ArrayList<HairStylist> hairStylists = new ArrayList<>();
        hairStylists.add(new HairStylist(R.drawable.model1,2,"+91-1111111111","HairStylist1"));
        hairStylists.add(new HairStylist(R.drawable.model2,3,"+91-2222222222","HairStylist2"));
        hairStylists.add(new HairStylist(R.drawable.model3,2,"+91-1111111111","HairStylist3"));
        hairStylists.add(new HairStylist(R.drawable.model4,3,"+91-1111111111","HairStylist4"));
        hairStylists.add(new HairStylist(R.drawable.model5,3,"+91-1111111111","HairStylist5"));
        hairStylists.add(new HairStylist(R.drawable.model6,4,"+91-1111111111","HairStylist6"));
        hairStylists.add(new HairStylist(R.drawable.model7,3,"+91-1111111111","HairStylist7"));

        hair_stylists_list.setValue(hairStylists);
    }

    public MutableLiveData<ArrayList<HairStylist>> getHairStylistsList()
    {
        return hair_stylists_list;
    }

    public MutableLiveData<ArrayList<Integer>> getAdvertisementList()
    {
        return advertisement_list;
    }

    public String getMyLocation(Context context)
    {
        getLocation(context);
        return myAddress;
    }

    public void getLocation(final Context context)
    {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);

        int permission = ContextCompat.checkSelfPermission(context,
                ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    Location location = locationResult.getLastLocation();
                    if (location != null) {

                        Log.println(Log.INFO,"location_text","onLocationResult func running.....");
                        setFullPath(location,context);
                    }

                }
            }, null);
        }
    }

    private void setFullPath(Location location, Context context) {
        if (location != null) {

            Log.println(Log.INFO,"location_text","setFullPath func running.....");
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(context, Locale.getDefault());

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (addresses != null) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                myAddress = String.format("%s,%s", address.split(",", 0)[1], address.split(",", 0)[2]);
            }
        }
    }

}