package com.example.barbershop.ui.settings;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsViewModel extends ViewModel {

    private FirebaseAuth user;
    public SettingsViewModel()
    {
        user = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getUser()
    {
        return user;
    }
}