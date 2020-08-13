package com.example.barbershop.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.barbershop.LoginActivity;
import com.example.barbershop.R;
import com.example.barbershop.models.User;
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
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {


    private GoogleSignInClient mGoogleSignInClient;
    private Context context;
    private Button button;
    private GoogleSignInOptions gso;
    private LinearLayout linear_list;
    private Configuration configuration;
    private TextView user_name_tv;
    private CircleImageView user_pic_civ;
    private ImageView drop_down;
    private SettingsViewModel settingsViewModel;
    private View view;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.settings_fragment, container, false);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        initialize(view);
        final UpdateProfileDialogFragment updateProfileDialogFragment = UpdateProfileDialogFragment.newInstance();
        updateProfileDialogFragment.setUserView(view,getContext());
        updateUI();
        
        drop_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drop_down.setImageResource(R.drawable.ic_baseline_create_24);
                updateProfileDialogFragment.show(requireActivity().getSupportFragmentManager(),"update_profile_fragment");
            }
        });


        button = view.findViewById(R.id.log_out);
        linear_list = view.findViewById(R.id.linear_list_view);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                signOutGoogleAccount();
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

    private void initialize(View view) {
        button = view.findViewById(R.id.log_out);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        context = requireActivity();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        user_name_tv = view.findViewById(R.id.profile_name_setting);
        user_pic_civ = view.findViewById(R.id.profile_pic_setting);
        drop_down = view.findViewById(R.id.drop_down_icon_setting);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateUI();
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

        final ImageView arrow_img = v.findViewById(R.id.clickArrow);
        final UpdateProfileDialogFragment updateProfileDialogFragment = UpdateProfileDialogFragment.newInstance();
        updateProfileDialogFragment.setUserView(view,getContext());
        if(name.equals("Account"))
            arrow_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateProfileDialogFragment.show(requireActivity().getSupportFragmentManager(),"update_profile_fragment");
                }
            });

        linear_list.addView(v);
    }

    protected void updateUI() {
        drop_down.setImageResource(R.drawable.ic_outline_create_24);
        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        settingsViewModel.getUserData(userID, new SettingsViewModel.OnGetUserData() {
            @Override
            public void getUserData(User user) {
                String name = user.getName();
                String user_profile_pic = user.getUser_profile_pic();
                user_name_tv.setText(name);
                if(user.getUser_profile_pic() != null)
                    Picasso.with(getContext()).load(user_profile_pic).into(user_pic_civ);
                else
                    Picasso.with(getContext()).load(R.drawable.user).into(user_pic_civ);
            }
        });
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