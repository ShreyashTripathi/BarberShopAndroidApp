package com.example.barbershop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import global_class.MyGlobalClass;

public class FirstPage extends AppCompatActivity {

    private static final String USER_COLLECTION_PATH = "Users";
    private static final String EMAIL_FIELD = "email";
    private static final String NAME_FIELD = "name";
    private static final String SAMPLE_VIDEO_URL = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4";
    private RadioGroup rg;
    private TextView user_name_tv;
    private FirebaseFirestore firestore;
    private static String user_name = "User";
    private FirebaseAuth firebaseAuth;
    private VideoView videoView;
    private MediaController mediaController;
    ImageView iv;
    TextView tv,about_text;
    FloatingActionButton fab;
    private Uri photo_uri;
    CircleImageView profile_pic;
    private String TAG = "Pic";
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    private SharedPreferences mPreferences;
    private String sharedPrefFile = "login";
    private CallbackManager callbackManager;
    //LoginButton fb_loginButton;
    private RadioButton rb_men,rb_women;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        initialize();
        setVideoPlayer();
        //fb_loginButton.setPermissions(Arrays.asList("email", "public_profile"));
        callbackManager = CallbackManager.Factory.create();
        String login_type = mPreferences.getString("login_type", "LoggedOut");

        switch (login_type) {
            case "EmailSignIn":
                getUserProfileFireStore();
                break;
            case "GoogleSignIn":
                getUserProfileGoogle();
                break;
            case "FacebookSignIn":
                getUserProfileFacebook(AccessToken.getCurrentAccessToken());
                break;
        }

        final MyGlobalClass myGlobalClass = (MyGlobalClass) getApplicationContext();
        final Intent intent = new Intent(FirstPage.this,FirstPage_2.class);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.services_men)
                {
                    //Toast.makeText(FirstPage.this, "Men", Toast.LENGTH_SHORT).show();
                    //intent.putExtra("Gender","Male");
                    myGlobalClass.setGender("Male");
                    rb_men.setChecked(false);
                }
                else if(checkedId == R.id.services_women)
                {
                    //Toast.makeText(FirstPage.this, "Women", Toast.LENGTH_SHORT).show();
                    //intent.putExtra("Gender","Female");
                    myGlobalClass.setGender("Female");
                    rb_women.setChecked(false);
                }

                startActivity(intent);
            }
        });

        about_text.setText(R.string.about_text);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        checkCurrentUser();
        //fbLoginButtonManager();

    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.stopPlayback();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void checkCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User already logged out!", Toast.LENGTH_SHORT).show();
            finishAfterTransition();
            startActivity(new Intent(FirstPage.this,LoginActivity.class));
        }

    }

    private void initialize() {
        rg = findViewById(R.id.services);
        videoView = findViewById(R.id.intro_vid);

        firestore = FirebaseFirestore.getInstance();
        user_name_tv = findViewById(R.id.user_name_text);
        firebaseAuth = FirebaseAuth.getInstance();
        iv = findViewById(R.id.video_img);
        tv = findViewById(R.id.video_text);
        fab = findViewById(R.id.video_button);
        about_text = findViewById(R.id.about_text);
        profile_pic = findViewById(R.id.profile_pic);
        rb_men = findViewById(R.id.services_men);
        rb_women = findViewById(R.id.services_women);

        //fb_loginButton = findViewById(R.id.fb_sign_in_button);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //MySetterClass.setShopData();
        //MySetterClass.setHairStylistData();
    }

    private void setVideoPlayer() {

        mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);

        Uri uri = Uri.parse(SAMPLE_VIDEO_URL);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });
        mediaController.setSoundEffectsEnabled(true);
    }

    public void playVideo(View view) {
        iv.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
    }

    private void getUserProfileFacebook(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", object.toString());
                        try {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            String id = object.getString("id");
                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                            String fb_user_name = first_name + " " + last_name;
                            user_name_tv.setText(fb_user_name);
                            Picasso.with(FirstPage.this).load(Profile.getCurrentProfile().getProfilePictureUri(50, 50)).into(profile_pic);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });



    }


    private void getUserProfileGoogle() {

        Log.println(Log.INFO,"user_name","getUserName() invoked!");
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(FirstPage.this);
        if (acct != null) {
            Log.println(Log.INFO,"user_name","acc is not null!");
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            user_name = personName;
            user_name_tv.setText(user_name);
            Picasso.with(FirstPage.this).load(personPhoto).into(profile_pic);
        }

    }

    private void getUserProfileFireStore() {
        final String email = firebaseAuth.getCurrentUser().getEmail();
        firestore.collection(USER_COLLECTION_PATH).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot document : task.getResult() )
                            {
                                if(document.get(EMAIL_FIELD).equals(email))
                                {
                                    user_name = document.get(NAME_FIELD).toString();
                                    user_name_tv.setText(user_name);
                                }
                            }
                        }
                    }
                });

    }

    /*
    // ------------------------- FACEBOOK LOGIN START ---------------------------------------------------------------


    private void fbLoginButtonManager() {
        fb_loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                AccessToken.setCurrentAccessToken(loginResult.getAccessToken());

                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                preferencesEditor.putString("login_type", "FacebookSignIn");
                preferencesEditor.apply();

                getUserProfileFacebook(loginResult.getAccessToken());
                signInAccountWithFirebase(getFbCredentials(loginResult.getAccessToken()));

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    public AuthCredential getFbCredentials(AccessToken token) {
        return FacebookAuthProvider.getCredential(token.getToken());
    }




    //--------------------------------------------- FB LOGIN END --------------------------------------------------


    // --------------------------------------- LINK,MERGE AND SIGN IN WITH CREDENTIAL START ----------------------------------------

    public void signInAccountWithFirebase(final AuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(FirstPage.this, "Fb Sign In Success!", Toast.LENGTH_SHORT).show();
                            //progressBar.setVisibility(View.INVISIBLE);
                        } else if (!task.isSuccessful() && task.getException() instanceof FirebaseAuthUserCollisionException){

                            //handleUserCollisionException();
                            Toast.makeText(FirstPage.this, "User Collision Exception", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }


    private void handleUserCollisionException() {

        firebaseAuth.fetchSignInMethodsForEmail(_email_user)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getSignInMethods().contains(GoogleAuthProvider.PROVIDER_ID)) {
                                // Password account already exists with the same email.
                                // Ask user to provide password associated with that account.

                                // Sign in with email and the provided password.
                                // If this was a Google account, call signInWithCredential instead.

                            }
                        }
                    }
                });
    }


    public void linkAccountsWithCredential(final AuthCredential credential)
    {
        firebaseAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed !! " + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            linkAndMerge(credential);
                        }

                    }
                });
    }


    public void linkAndMerge(AuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        FirebaseUser prevUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser currentUser = task.getResult().getUser();
                        // Merge prevUser and currentUser accounts and data
                        // ...
                    }
                });

    }



    // --------------------------------------- LINK,MERGE AND SIGN IN WITH CREDENTIAL STOP ----------------------------------------
   */

}