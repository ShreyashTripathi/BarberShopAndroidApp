package com.example.barbershop.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.adapters.WorkerAdapter;
import com.example.barbershop.adapters.SalonAdAdapter;
import com.example.barbershop.location.FetchAddressTask;
import com.example.barbershop.models.Worker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import global_class.MyGlobalClass;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeFragment extends Fragment implements
        FetchAddressTask.OnTaskCompleted {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TRACKING_LOCATION_KEY = "tracking_location";
    private Context context;
    private TextView street_name;

    private FloatingActionButton address_button;
    private ProgressBar progressBar;
    private RecyclerView h_s_rv,salon_ad_rv;
    private ScrollView sv_layout;
    private final String TAG = "home_fragment";
    private View _root;
    private boolean mTrackingLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {


        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        _root = root;


        initialize();
        //street_name.setText(savedInstanceState.getString("user_location"));

        MyGlobalClass myGlobalClass = (MyGlobalClass) getApplicationContext();
        if(myGlobalClass.getUser_loc_string() != null)
        {
            street_name.setText(myGlobalClass.getUser_loc_string());
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


        if (savedInstanceState != null) {
            mTrackingLocation = savedInstanceState.getBoolean(
                    TRACKING_LOCATION_KEY);
        }

        mLocationCallback = new LocationCallback() {
            /**
             * This is the callback that is triggered when the
             * FusedLocationClient updates your location.
             * @param locationResult The result containing the device location.
             */
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // If tracking is turned on, reverse geocode into an address
                if (mTrackingLocation && isAdded()) {
                    /*double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    MyGlobalClass myGlobalClass = (MyGlobalClass)getApplicationContext();
                    myGlobalClass.setUser_location_coordinates(new Pair<Double, Double>(latitude,longitude));*/
                    new FetchAddressTask(requireActivity(), HomeFragment.this)
                            .execute(locationResult.getLastLocation());
                }
            }
        };

        address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Setting up location!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                if (!mTrackingLocation) {
                    startTrackingLocation();
                } else {
                    stopTrackingLocation();
                }
            }

        });

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        h_s_rv.setLayoutManager(llm);

        LinearLayoutManager llm2 = new LinearLayoutManager(context);
        llm2.setOrientation(LinearLayoutManager.HORIZONTAL);

        salon_ad_rv.setLayoutManager(llm2);


        final LiveData<ArrayList<Worker>> hairStylists = homeViewModel.getWorkersList();
        hairStylists.observe(requireActivity(), new Observer<ArrayList<Worker>>() {
            @Override
            public void onChanged(ArrayList<Worker> workers) {
                h_s_rv.setAdapter(new WorkerAdapter(workers, context, new WorkerAdapter.OnGetHairStylist() {
                    @Override
                    public void getHairStylist(Worker worker) {

                    }
                }));
                Log.println(Log.INFO,TAG,"Worker adapter set!");
            }
        });

        final LiveData<ArrayList<StorageReference>> advertisements = homeViewModel.getAdvertisementList();
        advertisements.observe(requireActivity(), new Observer<ArrayList<StorageReference>>() {
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

    }



    //----------------------------- Get Location START-------------------------

    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mTrackingLocation = true;
            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(),
                            mLocationCallback,
                            null /* Looper */);

            // Set a loading text while you wait for the address to be
            // returned
            street_name.setText(R.string.location_loading_text);
        }
    }


    /**
     * Stops tracking the device. Removes the location
     * updates, stops the animation, and resets the UI.
     */
    private void stopTrackingLocation() {
        if (mTrackingLocation) {
            mTrackingLocation = false;
            Toast.makeText(context, "Stopped tracking location!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * Sets up the location request.
     *
     * @return The LocationRequest object containing the desired parameters.
     */
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    /**
     * Saves the last location on configuration change
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(TRACKING_LOCATION_KEY, mTrackingLocation);
        super.onSaveInstanceState(outState);
    }

    /**
     * Callback that is invoked when the user responds to the permissions
     * dialog.
     *
     * @param requestCode  Request code representing the permission request
     *                     issued by the app.
     * @param permissions  An array that contains the permissions that were
     *                     requested.
     * @param grantResults An array with the results of the request for each
     *                     permission requested.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {// If the permission is granted, get the location, otherwise,
            // show a Toast
            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                startTrackingLocation();
            } else {
                Toast.makeText(context,
                        R.string.location_permission_denied,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onTaskCompleted(String result) {
        if (mTrackingLocation && isAdded()) {
            // Update the UI
            String resAddress = String.format("%s,%s", result.split(",", 0)[1], result.split(",", 0)[2]);
            street_name.setText(resAddress);
            progressBar.setVisibility(View.INVISIBLE);
            MyGlobalClass myGlobalClass = (MyGlobalClass)getApplicationContext();
            myGlobalClass.setUser_loc_string(resAddress);
        }
    }

    @Override
    public void onPause() {
        if (mTrackingLocation) {
            stopTrackingLocation();
            mTrackingLocation = true;
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mTrackingLocation) {
            startTrackingLocation();
        }
        super.onResume();
    }


    //----------------------------- Get Location END -------------------------



}