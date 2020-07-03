package com.example.barbershop.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.adapters.HairStylistAdapter;
import com.example.barbershop.adapters.SalonAdAdapter;
import com.example.barbershop.models.HairStylist;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {


        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        _root = root;

        initialize();
        Intent intent = getActivity().getIntent();
        String gender = intent.getStringExtra("Gender");
        if(gender.equals("Male"))
        {
            //sv_layout.setBackgroundResource(R.drawable.back_pic);
        }
        else if(gender.equals("Female"))
        {
            //sv_layout.setBackgroundResource(R.drawable.cake_back);
        }

        address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Setting up location!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                if (checkPermission()) {

                    //Snackbar.make(container, "Permission already granted.", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(context, "Permission already granted!", Toast.LENGTH_LONG).show();
                    getMyLocation();

                } else {

                    //Snackbar.make(container, "Please request permission.", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(context, "Request permission!", Toast.LENGTH_LONG).show();
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
        hairStylists.observe(getActivity(), new Observer<ArrayList<HairStylist>>() {
            @Override
            public void onChanged(ArrayList<HairStylist> hairStylists) {
                h_s_rv.setAdapter(new HairStylistAdapter(hairStylists,context));
                Log.println(Log.INFO,TAG,"HairStylist adapter set!");
            }
        });

        final LiveData<ArrayList<Integer>> advertisements = homeViewModel.getAdvertisementList();
        advertisements.observe(getActivity(), new Observer<ArrayList<Integer>>() {
            @Override
            public void onChanged(ArrayList<Integer> integers) {
                salon_ad_rv.setAdapter(new SalonAdAdapter(integers,context));
                Log.println(Log.INFO,TAG,"Advertisement adapter set!");
            }
        });

        return root;
    }

    private void getMyLocation() {
        String my_loc = homeViewModel.getMyLocation(requireActivity());
        street_name.setText(my_loc);
        if(my_loc != null)
            progressBar.setVisibility(View.INVISIBLE);
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



    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        //ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
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