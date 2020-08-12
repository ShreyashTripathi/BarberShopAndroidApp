package com.example.barbershop.ui.FirstPage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.barbershop.FirstPage_2;
import com.example.barbershop.LoginActivity;
import com.example.barbershop.MySetterClass;
import com.example.barbershop.R;
import com.example.barbershop.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import global_class.MyGlobalClass;

public class FirstPage extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final String USER_COLLECTION_PATH = "Users";
    private static final String EMAIL_FIELD = "email";
    private static final String NAME_FIELD = "name";
    //private static final String SAMPLE_VIDEO_URL = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4";
    private static final String YOUTUBE_VIDEO_URL = "xKzACBoKLhg"; //https://www.youtube.com/watch?v=
    private RadioGroup rg;
    private TextView user_name_tv;
    private FirebaseFirestore firestore;
    private static String user_name = "User";
    private FirebaseAuth firebaseAuth;
    TextView tv,about_text;
    FloatingActionButton fab;
    CircleImageView profile_pic;
    private String TAG = "Pic";
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    private SharedPreferences mPreferences;
    private String sharedPrefFile = "login";
    //private CallbackManager callbackManager;
    //LoginButton fb_loginButton;
    private RadioButton rb_men,rb_women;
    private YouTubePlayerView youTubePlayerView;
    //private YouTubeThumbnailView youTubeThumbnailView;
    private static final int RECOVERY_REQUEST = 1;
    private MyGlobalClass myGlobalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        initialize();
        //callbackManager = CallbackManager.Factory.create();

        setUserNameAndPic();
        final Intent intent = new Intent(FirstPage.this, FirstPage_2.class);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.services_men)
                {
                    myGlobalClass.setGender("Male");
                    rb_men.setChecked(false);
                }
                else if(checkedId == R.id.services_women)
                {
                    myGlobalClass.setGender("Female");
                    rb_women.setChecked(false);
                }

                startActivity(intent);
            }
        });
        about_text.setText(R.string.about_text);
        youTubePlayerView.initialize(getResources().getString(R.string.youtube_api_key), this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        checkCurrentUser();
        setUserNameAndPic();
        //fbLoginButtonManager();

    }
    @Override
    protected void onStop() {
        super.onStop();
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void checkCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User already logged out!", Toast.LENGTH_SHORT).show();
            finishAfterTransition();
            startActivity(new Intent(FirstPage.this, LoginActivity.class));
        }

    }

    private void initialize() {
        rg = findViewById(R.id.services);
        firestore = FirebaseFirestore.getInstance();
        user_name_tv = findViewById(R.id.user_name_text);
        firebaseAuth = FirebaseAuth.getInstance();
        about_text = findViewById(R.id.about_text);
        profile_pic = findViewById(R.id.profile_pic);
        rb_men = findViewById(R.id.services_men);
        rb_women = findViewById(R.id.services_women);
        myGlobalClass = (MyGlobalClass) getApplicationContext();
        //fb_loginButton = findViewById(R.id.fb_sign_in_button);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        youTubePlayerView = findViewById(R.id.youtube_player);

        MySetterClass.setShopData();
        MySetterClass.setWorkerData();
    }



    private void setUserNameAndPic() {

        SharedPreferences mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String user_email = mPreferences.getString("user_email","");
        String user_phone = mPreferences.getString("user_phone","");
        String emailOrPhone;
        if(!user_email.equals(""))
        {
            emailOrPhone = user_email;
        }
        else {
            emailOrPhone = user_phone;
        }

        getUserData(emailOrPhone, new OnGetUserData() {
            @Override
            public void getUserData(User user) {
                if(!user.getName().equals(""))
                    user_name_tv.setText(String.format("Hello %s",user.getName()));
                else
                    user_name_tv.setText(String.format("Hello User %s",user.getPhone()));

                if(user.getUser_profile_pic()!=null)
                    Picasso.with(FirstPage.this).load(user.getUser_profile_pic()).into(profile_pic);
                else
                    Picasso.with(FirstPage.this).load(R.drawable.user).into(profile_pic);
            }
        });

        /*if(user_email.equals(""))         //New OTP User
        {
            user_name_tv.setText(String.format("Hello User %s", user_phone));
            Picasso.with(FirstPage.this).load(R.drawable.user).into(profile_pic);
        }
        else
        {
            getUser_name(user_email, new OnGetUserName() {
                @Override
                void getUserName(String userName) {
                    user_name_tv.setText(String.format("Hello %s", userName));
                }
            });

            getUser_profile_pic(user_email, new OnGetUserProfilePicUrl() {
                @Override
                void getUserProfilePicUrl(String pic_url) {
                    Picasso.with(FirstPage.this).load(pic_url).into(profile_pic);
                }
            });
        }*/

    }


    // -------------------------- YOUTUBE Settings START ---------------------------

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubePlayerView;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(getResources().getString(R.string.youtube_api_key),this );
        }
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if(!wasRestored)
        {
            youTubePlayer.cueVideo(YOUTUBE_VIDEO_URL);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            Log.println(Log.ERROR,TAG," Youtube Error");
            errorReason.getErrorDialog(FirstPage.this, RECOVERY_REQUEST).show();
        } else {
            String error = "Video Player Error";
            Toast.makeText(FirstPage.this, error, Toast.LENGTH_SHORT).show();
        }
    }


    // -------------------------- YOUTUBE Settings END ---------------------------

    // -------------------------- Get User Profile Start -------------------------

    public void getUserData(final String user_doc_id, final OnGetUserData onGetUserData) {

        firestore.collection(USER_COLLECTION_PATH)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                            {
                                if(document.getId().equals(user_doc_id))
                                {
                                    User user = document.toObject(User.class);
                                    onGetUserData.getUserData(user);
                                    break;
                                }
                            }
                        }
                        else
                        {
                            Log.println(Log.ERROR,TAG,""+task.getException());
                        }
                    }
                });
    }



    abstract static class OnGetUserData{
        public abstract void getUserData(User user);
    }

    //____________________________________________________________________________________

    public void getUser_name(final String email, final OnGetUserName onGetUserName) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(USER_COLLECTION_PATH).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                            {
                                if(Objects.equals(document.get(EMAIL_FIELD), email))
                                {
                                    User user = document.toObject(User.class);
                                    onGetUserName.getUserName(user.getName());
                                }
                            }
                        }
                    }
                });
    }

    abstract static class OnGetUserName{
        abstract void getUserName(String userName);
    }

    public void getUser_profile_pic(final String email, final OnGetUserProfilePicUrl onGetUserProfilePicUrl) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(USER_COLLECTION_PATH).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                            {
                                if(Objects.equals(document.get(EMAIL_FIELD), email))
                                {
                                    User user = document.toObject(User.class);
                                    onGetUserProfilePicUrl.getUserProfilePicUrl(user.getUser_profile_pic());
                                }
                            }
                        }
                    }
                });
    }

    abstract static class OnGetUserProfilePicUrl{
        abstract void getUserProfilePicUrl(String pic_url);
    }


    // -------------------------- Get User Profile End -------------------------
}