package com.example.barbershop;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.barbershop.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 999;
    private static final String USER_COLLECTION_PATH = "Users";
    private static final String EMAIL_FIELD = "email";

    TextInputLayout usernameLayout,passwordInputLayout;
    TextInputEditText userNameEditText,passwordEditText;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    MaterialButton loginButton;
    TextView signUp;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton google;
    FirebaseFirestore firebaseFirestore;
    //private SharedPreferences mPreferences;
    //private String sharedPrefFile = "user_name_login";
    //private static boolean email_flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //startActivity(new Intent(this,UploadProfile.class));

        //mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        initializeInstances();

    }



    private void initializeInstances() {

        usernameLayout = findViewById(R.id.userNameInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        userNameEditText = findViewById(R.id.userNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        progressBar = findViewById(R.id.progressBarLogin);
        signUp = findViewById(R.id.SignUp);
        loginButton = findViewById(R.id.login);
        firebaseAuth = FirebaseAuth.getInstance();
        google = findViewById(R.id.google);


        firebaseFirestore= FirebaseFirestore.getInstance();



        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }





    void addToFirestoreUserInfo(String userId,String name,String email){

        User user = new User(name,email,"","");
        CollectionReference users = firebaseFirestore.collection(USER_COLLECTION_PATH);

        users.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(LoginActivity.this, "User Added successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "User not added, error: " + e, Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        inputTextValidation();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginForm();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                googleSignIn();
            }
        });

    }

    private void googleSignIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this,"Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //Toast.makeText(LoginActivity.this, "GoogleSignIn", Toast.LENGTH_SHORT).show();
                String currentUserId = firebaseAuth.getCurrentUser().getUid();
                String name = account.getDisplayName();
                String uri = account.getPhotoUrl().toString();
                String email = account.getEmail();

                /*
                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                preferencesEditor.putString("UserName", name);
                preferencesEditor.apply();
                 */

                //Toast.makeText(LoginActivity.this, "Name: " + name, Toast.LENGTH_LONG).show();
                //addToFirestoreUserInfo(currentUserId,name,email);
                presentInFireStore(currentUserId,name,email);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,"Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }

    private void presentInFireStore(final String currentUserId, final String name, final String email) {
        firebaseFirestore.collection(USER_COLLECTION_PATH).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean flag = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String _email = document.get(EMAIL_FIELD)+"";
                                if(_email.equals(email))
                                {
                                    Toast.makeText(LoginActivity.this, "User already present!", Toast.LENGTH_SHORT).show();
                                    flag = true;
                                }
                            }
                            if(!flag)
                            {
                                addToFirestoreUserInfo(currentUserId,name,email);
                            }
                            startActivity(new Intent(LoginActivity.this,FirstPage.class));
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {                     //User not present in db

                Log.println(Log.ERROR,"Errors",""+e);

            }
        });

    }

    private void loginForm() {
        usernameLayout.setErrorEnabled(false);
        passwordInputLayout.setErrorEnabled(false);
        String userName = userNameEditText.getText().toString().trim();
        String passWord = passwordEditText.getText().toString().trim();
        final String user_name = userName;
        progressBar.setVisibility(View.VISIBLE);
        if(userName.equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            usernameLayout.setErrorEnabled(true);
            usernameLayout.setError("Please enter your email");
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(userName).matches()){
            progressBar.setVisibility(View.INVISIBLE);
            usernameLayout.setErrorEnabled(true);
            usernameLayout.setError("Invalid Email");
            return;
        }
        if(passWord.equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            passwordInputLayout.setErrorEnabled(true);
            passwordInputLayout.setError("Please enter your password");
            return;
        }
        if(passWord.length()<6){
            progressBar.setVisibility(View.INVISIBLE);
            passwordInputLayout.setErrorEnabled(true);
            passwordInputLayout.setError("Password length should be atleast 6");
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(userName,passWord).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                progressBar.setVisibility(View.INVISIBLE);

                startActivity(new Intent(LoginActivity.this, FirstPage.class));
                Toast.makeText(LoginActivity.this,"LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, "firebaseauth login"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inputTextValidation() {
        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String currentUserName = userNameEditText.getText().toString().trim();
                usernameLayout.setEndIconVisible(true);
                if(currentUserName.isEmpty()){
                    usernameLayout.setErrorEnabled(true);
                    usernameLayout.setError("Please enter your UserName");
                }else{
                    usernameLayout.setErrorEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String currentPassword = passwordEditText.getText().toString().trim();

                if(currentPassword.isEmpty()){
                    passwordInputLayout.setErrorEnabled(true);
                    passwordInputLayout.setError("Please enter your Password");
                }else{
                    passwordInputLayout.setErrorEnabled(false);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        userNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(userNameEditText.getText().toString().trim().isEmpty()){
                    usernameLayout.setErrorEnabled(true);
                    usernameLayout.setError("Please enter your UserName");
                }else{
                    usernameLayout.setErrorEnabled(false);
                }
            }
        });
    }


    public void forgotPassword(View view) {
        startActivity(new Intent(LoginActivity.this,ForgotPassword.class));
    }
}
