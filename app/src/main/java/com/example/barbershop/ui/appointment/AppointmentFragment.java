package com.example.barbershop.ui.appointment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.barbershop.R;

public class AppointmentFragment extends Fragment {

    private AppointmentViewModel appointmentViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        appointmentViewModel =
                ViewModelProviders.of(this).get(AppointmentViewModel.class);
        View root = inflater.inflate(R.layout.fragment_appointment, container, false);
        //final TextView textView = root.findViewById(R.id.text_notifications);
        appointmentViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }
}