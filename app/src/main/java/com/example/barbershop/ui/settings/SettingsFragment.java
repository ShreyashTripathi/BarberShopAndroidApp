package com.example.barbershop.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.barbershop.LoginActivity;
import com.example.barbershop.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    private SettingsViewModel mViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private Context context;
    private Button button;
    private View view;
    private FirebaseAuth user;
    private GoogleSignInOptions gso;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.settings_fragment, container, false);

        initialize();
        button = view.findViewById(R.id.log_out);
        //user = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.signOut();
                signOutGoogleAccount();
                Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        return view;
    }

    private void signOutGoogleAccount() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Log.println(Log.INFO,"user_name","Logged out from google!");
                        }
                    }
                });
    }

    private void initialize() {
        button = view.findViewById(R.id.log_out);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        context = requireActivity();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        user = mViewModel.getUser();
        // TODO: Use the ViewModel
    }


}