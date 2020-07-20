package com.example.barbershop;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 999;
    private static final String USER_COLLECTION_PATH = "Users";
    private static final String EMAIL_FIELD = "email";
    private static boolean EMAIL_EXISTS_FLAG = false;

    TextInputLayout usernameLayout,passwordInputLayout;
    TextInputEditText userNameEditText,passwordEditText;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    MaterialButton loginButton;
    TextView signUp;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    FirebaseFirestore firebaseFirestore;

    FloatingActionButton g_sign_in,otp_sign_in,fb_sign_in_;
    private CallbackManager callbackManager;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "login";
    private String TAG = "Link Tag";
    private String _email_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeInstances();
        callbackManager = CallbackManager.Factory.create();
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

        g_sign_in = findViewById(R.id.google_sign_in_button);
        otp_sign_in =findViewById(R.id.otp_sign_in_button);

        firebaseFirestore= FirebaseFirestore.getInstance();
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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


        g_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                googleSignIn();

            }
        });

        otp_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,PhoneAuthentication.class));
            }
        });

    }

    //-------------------------------------------- FIREBASE LOGIN START ---------------------------------------------



    private void loginForm() {
        usernameLayout.setErrorEnabled(false);
        passwordInputLayout.setErrorEnabled(false);
        String userName = userNameEditText.getText().toString().trim();
        String passWord = passwordEditText.getText().toString().trim();

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
        }

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString("login_type", "EmailSignIn");
        preferencesEditor.apply();

        signInAccountWithFirebase(getEmailCredentials(userName,passWord));

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

    public AuthCredential getEmailCredentials(String email, String password) {
        return EmailAuthProvider.getCredential(email, password);
    }


    //-------------------------------------------- FIREBASE LOGIN END ------------------------------------------------


    // ------------------------------------------ GOOGLE SIGN IN START ------------------------------------------

    private void googleSignIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                Log.println(Log.DEBUG,TAG,"Google SignIn");

                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                preferencesEditor.putString("login_type", "GoogleSignIn");
                preferencesEditor.apply();

                if(account != null)
                    signInAccountWithFirebase(getGoogleCredentials(account.getIdToken()));


            } catch (ApiException e) {
                Toast.makeText(this,"Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public AuthCredential getGoogleCredentials(String googleIdToken) {
        return GoogleAuthProvider.getCredential(googleIdToken, null);
    }

    // --------------------------------------- GOOGLE SIGN IN END -----------------------------------------


    // --------------------------------------- LINK,MERGE AND SIGN IN WITH CREDENTIAL START ----------------------------------------

    public void signInAccountWithFirebase(final AuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(LoginActivity.this,FirstPage.class);
                            startActivity(intent);
                            progressBar.setVisibility(View.INVISIBLE);
                        } else if (!task.isSuccessful() && task.getException() instanceof FirebaseAuthUserCollisionException){

                            //handleUserCollisionException();
                            Toast.makeText(LoginActivity.this, "User Collision Exception", Toast.LENGTH_SHORT).show();
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


    // -------------------------------------- FORGOT PASSWORD START --------------------------------------------

    public void forgotPassword(View view) {
        startActivity(new Intent(LoginActivity.this,ForgotPassword.class));
    }

    // --------------------------------------- FORGOT PASSWORD END -------------------------------------------
}
