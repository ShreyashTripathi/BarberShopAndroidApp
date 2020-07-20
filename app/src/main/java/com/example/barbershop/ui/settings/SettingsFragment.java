package com.example.barbershop.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.barbershop.LoginActivity;
import com.example.barbershop.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {

    private SettingsViewModel mViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private Context context;
    private Button button;
    private View view;
    private FirebaseAuth user;
    private GoogleSignInOptions gso;
    private LinearLayout linear_list;
    private Configuration configuration;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "login";

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
        linear_list = view.findViewById(R.id.linear_list_view);

        final String login_type = mPreferences.getString("login_type", "FirebaseSignIn");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(login_type.equals("FacebookSignIn"))
                {
                    disconnectFromFacebook();
                    user.signOut();
                }
                else {
                    user.signOut();
                    signOutGoogleAccount();
                }
                Toast.makeText(requireActivity(), "Logged Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        inflateView(R.drawable.user,"Account");
        inflateView(R.drawable.user,"Notification");
        inflateView(R.drawable.user,"Location");
        inflateView(R.drawable.user,"Support");
        inflateView(R.drawable.user,"Share");


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
        mPreferences = requireContext().getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        user = mViewModel.getUser();
        // TODO: Use the ViewModel
    }

    private void inflateView(int img,String name)
    {
        LayoutInflater vi = LayoutInflater.from(requireActivity());
        View v = vi.inflate(R.layout.setting_item_card, null,false);
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView tv = v.findViewById(R.id.setting_name_setting_card);
        tv.setText(name);

        CircleImageView c_img = v.findViewById(R.id.user_img_setting_card);
        c_img.setImageResource(img);


        linear_list.addView(v);
    }

    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }


}