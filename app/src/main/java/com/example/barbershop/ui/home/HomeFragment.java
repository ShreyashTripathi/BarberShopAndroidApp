package com.example.barbershop.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.adapters.HairStylistAdapter;
import com.example.barbershop.adapters.SalonAdAdapter;
import com.example.barbershop.models.HairStylist;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import global_class.MyGlobalClass;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private HomeViewModel homeViewModel;
    private Context context;
    private TextView street_name;

    private FloatingActionButton address_button;
    private ProgressBar progressBar;
    private RecyclerView h_s_rv,salon_ad_rv;
    private ScrollView sv_layout;
    private final String TAG = "home_fragment";
    private View _root;
    private MyGlobalClass myGlobalClass;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {



        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        _root = root;


        initialize();
        //street_name.setText(savedInstanceState.getString("user_location"));
        myGlobalClass = (MyGlobalClass) getApplicationContext();
        if(myGlobalClass.getUser_location_coordinates() != null)
        {
            street_name.setText(getAddress(myGlobalClass.getUser_location_coordinates()));
            progressBar.setVisibility(View.INVISIBLE);
        }
        String gender = myGlobalClass.getGender();
        if(gender.equals("Male"))
        {
            //sv_layout.setBackgroundResource(R.drawable.back_pic);
            Toast.makeText(requireActivity(), "Men_Salons", Toast.LENGTH_SHORT).show();
        }
        else if(gender.equals("Female"))
        {
            //sv_layout.setBackgroundResource(R.drawable.cake_back);
            Toast.makeText(requireActivity(), "Women_Salons", Toast.LENGTH_SHORT).show();
        }

        address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Setting up location!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                if (checkPermission()) {

                    //Snackbar.make(container, "Permission already granted.", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(context, "Permission already granted!", Toast.LENGTH_SHORT).show();
                    getMyLocation();

                } else {

                    //Snackbar.make(container, "Please request permission.", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(context, "Request permission!", Toast.LENGTH_SHORT).show();
                    //progressBar.setVisibility(View.INVISIBLE);
                    requestPermission();
                }

            }

        });

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        h_s_rv.setLayoutManager(llm);

        LinearLayoutManager llm2 = new LinearLayoutManager(context);
        llm2.setOrientation(LinearLayoutManager.HORIZONTAL);

        salon_ad_rv.setLayoutManager(llm2);


        final LiveData<ArrayList<HairStylist>> hairStylists = homeViewModel.getHairStylistsList();
        hairStylists.observe(requireActivity(), new Observer<ArrayList<HairStylist>>() {
            @Override
            public void onChanged(ArrayList<HairStylist> hairStylists) {
                h_s_rv.setAdapter(new HairStylistAdapter(hairStylists,context));
                Log.println(Log.INFO,TAG,"HairStylist adapter set!");
            }
        });

        final LiveData<ArrayList<StorageReference>> advertisements = homeViewModel.getAdvertisementList();
        advertisements.observe(getActivity(), new Observer<ArrayList<StorageReference>>() {
            @Override
            public void onChanged(ArrayList<StorageReference> images) {
                salon_ad_rv.setAdapter(new SalonAdAdapter(images,context));
                Log.println(Log.INFO,TAG,"Advertisement adapter set!");
            }
        });

        return root;
    }

    private void initialize() {
        street_name = _root.findViewById(R.id.location_text);
        context = requireActivity();
        progressBar = _root.findViewById(R.id.progressBarLocation);
        address_button = _root.findViewById(R.id.address_bt);
        h_s_rv = _root.findViewById(R.id.h_s_rv);
        salon_ad_rv= _root.findViewById(R.id.home_ad_rv);
        sv_layout = _root.findViewById(R.id.home_main_view);

    }


    private void getMyLocation() {
        getLocation(requireActivity());
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

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            Log.println(Log.INFO,"LocationTag","Lat: "+latitude+" Long: " + longitude);

            myGlobalClass.setUser_location_coordinates(new Pair<>(latitude, longitude));

            street_name.setText(getAddress(new Pair<>(latitude,longitude)));
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public String getAddress(Pair<Double, Double> coordinates) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
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


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        requestPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //Toast.makeText(context, "onRequestPermissionResult invoked!", Toast.LENGTH_LONG).show();
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        Log.println(Log.INFO,"permission","onRequestPermissionResult invoked!");
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (locationAccepted){
                    //Snackbar.make(view, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(context, "Permission granted for location!", Toast.LENGTH_LONG).show();
                    getMyLocation();
                }
                else {

                    Toast.makeText(context, "Permission Denied, You cannot access location data!", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                            showMessageOKCancel("You need to allow access to location for smooth functioning!",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                                                    PERMISSION_REQUEST_CODE);
                                        }
                                    });
                        }
                    }

                }
            }
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



}