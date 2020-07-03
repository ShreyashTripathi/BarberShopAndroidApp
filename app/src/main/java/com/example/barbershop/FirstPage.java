package com.example.barbershop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FirstPage extends AppCompatActivity {

    private static final String USER_COLLECTION_PATH = "Users";
    private static final String EMAIL_FIELD = "email";
    private static final String NAME_FIELD = "name";
    private static final String SAMPLE_VIDEO_URL = "https://file-examples.com/wp-content/uploads/2017/04/file_example_MP4_480_1_5MG.mp4";
    private RadioGroup rg;
    private TextView heading;
    private FirebaseFirestore firestore;
    private static String user_name = "UserName.....";
    private FirebaseAuth firebaseAuth;
    private VideoView videoView;
    private MediaController mediaController;
    ImageView iv;
    TextView tv,about_text;
    FloatingActionButton fab,profile_pic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        initialize();
        setVideoPlayer();
        heading.setText(String.format("Hi %s", getUserName()));
        final Intent intent = new Intent(FirstPage.this,FirstPage_2.class);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.services_men)
                {
                    Toast.makeText(FirstPage.this, "Men", Toast.LENGTH_SHORT).show();
                    intent.putExtra("Gender","Male");
                }
                else if(checkedId == R.id.services_women)
                {
                    Toast.makeText(FirstPage.this, "Women", Toast.LENGTH_SHORT).show();
                    intent.putExtra("Gender","Female");
                }

                startActivity(intent);
            }
        });

        about_text.setText("Good population in human society is the basic principle for peace,\n" +
                "prosperity and spiritual progress in life. The varriisrama religion's principles" +
                "were so designed that the good population would prevail in society.");
    }

    private void setVideoPlayer() {
        //Creating MediaController
        mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);

        //specify the location of media file

        Uri uri = Uri.parse(SAMPLE_VIDEO_URL);
        //Setting MediaController and URI, then starting the videoView
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

        mediaController.setSoundEffectsEnabled(false);

    }

    private void initialize() {
        rg = findViewById(R.id.services);
        videoView = findViewById(R.id.intro_vid);

        firestore = FirebaseFirestore.getInstance();
        heading = findViewById(R.id.first_page_heading);
        firebaseAuth = FirebaseAuth.getInstance();
        iv = findViewById(R.id.video_img);
        tv = findViewById(R.id.video_text);
        fab = findViewById(R.id.video_button);
        about_text = findViewById(R.id.about_text);
        profile_pic = findViewById(R.id.profile_pic);

    }

    private String getUserName() {

        Log.println(Log.INFO,"user_name","getUserName() invoked!");
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(FirstPage.this);
        if (acct != null) {
            Log.println(Log.INFO,"user_name","acc is not null!");
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            user_name = personName;
            profile_pic.setImageURI(personPhoto);
            
        }
        else {
            user_name = getFirebaseUserName();
            //user_name = "Hahahahaha!";
        }
        return user_name;
    }

    private String getFirebaseUserName() {
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
                                }
                            }
                        }
                    }
                });
        return user_name;
    }


    public void playVideo(View view) {
        iv.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
    }
}