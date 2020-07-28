package com.example.barbershop.ui.appointment;

import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class AppointmentFragment extends Fragment {

    private AppointmentViewModel appointmentViewModel;
    private RecyclerView appointment_rv;
    private FloatingActionButton addAppointment;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        appointmentViewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
        root = inflater.inflate(R.layout.fragment_appointment, container, false);
        initializeUI();
        String sharedPrefFile = "login";
        SharedPreferences mPreferences = requireActivity().getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String email = mPreferences.getString("user_email","");
        String phone = mPreferences.getString("user_phone","");
        String emailOrPhone;
        if(!email.equals(""))
        {
            emailOrPhone = email;
        }
        else
        {
            emailOrPhone = phone;
        }
        appointment_rv = root.findViewById(R.id.appointment_list_rv);
        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        appointment_rv.setLayoutManager(llm);

        MutableLiveData <ArrayList<AppointmentData>> appointmentData = appointmentViewModel.getAppointmentData(emailOrPhone);
        appointmentData.observe(requireActivity(), new Observer<ArrayList<AppointmentData>>() {
            @Override
            public void onChanged(ArrayList<AppointmentData> appointmentData) {
                appointment_rv.setAdapter(new AppointmentAdapter(requireActivity(),appointmentData));
            }
        });



        return root;
    }

    private void initializeUI() {


    }
}