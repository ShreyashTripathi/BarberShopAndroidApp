package com.example.barbershop.ui.appointment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.adapters.AppointmentAdapter;
import com.example.barbershop.models.AppointmentData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

import global_class.MyGlobalClass;

import static android.content.Context.MODE_PRIVATE;

public class AppointmentFragment extends Fragment {

    private static final String TAG = "appt fragment";
    private AppointmentViewModel appointmentViewModel;
    private RecyclerView appointment_rv;
    private FloatingActionButton addAppointment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        appointmentViewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
        View root = inflater.inflate(R.layout.fragment_appointment, container, false);
        //initializeUI();
        String sharedPrefFile = "login";
        SharedPreferences mPreferences = requireActivity().getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        final String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        appointment_rv = root.findViewById(R.id.appointment_list_rv);
        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        appointment_rv.setLayoutManager(llm);

        MyGlobalClass myGlobalClass = (MyGlobalClass)requireActivity().getApplicationContext();
        String gender = myGlobalClass.getGender();
        MutableLiveData <ArrayList<AppointmentData>> appointmentData = appointmentViewModel.getAppointmentData(userID,gender);
        appointmentData.observe(requireActivity(), new Observer<ArrayList<AppointmentData>>() {
            @Override
            public void onChanged(ArrayList<AppointmentData> appointmentData) {
                AppointmentAdapter adapter = new AppointmentAdapter(requireActivity(), appointmentData, new AppointmentAdapter.SetIsBookingCancelled() {
                    @Override
                    public void isBookingCancelled(boolean bookingStatus,AppointmentData appointmentData) {
                        Log.println(Log.INFO,TAG,"appointment data: " + appointmentData.getService_opted() + " : " + bookingStatus);
                        if(bookingStatus)          //is cancelled
                            appointmentViewModel.setAppointmentStatus(appointmentData,userID, false);
                    }
                });
                appointment_rv.setAdapter(adapter);
            }
        });


        return root;
    }


}