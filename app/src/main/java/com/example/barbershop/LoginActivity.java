package com.example.barbershop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.barbershop.models.User;
import com.example.barbershop.ui.FirstPage.FirstPage;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

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
    private String TAG = "Link Tag";
    private String _email_user;
    private LinearLayout linearLayout;


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
        linearLayout = findViewById(R.id.login_activity_layout);
        firebaseAuth = FirebaseAuth.getInstance();
        g_sign_in = findViewById(R.id.google_sign_in_button);
        otp_sign_in =findViewById(R.id.otp_sign_in_button);

        firebaseFirestore= FirebaseFirestore.getInstance();

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

    private void makeToast(String message, Context context)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    //-------------------------------------------- FIREBASE LOGIN START ---------------------------------------------
    private void loginForm() {
        usernameLayout.setErrorEnabled(false);
        passwordInputLayout.setErrorEnabled(false);
        final String userName = userNameEditText.getText().toString().trim();
        final String passWord = passwordEditText.getText().toString().trim();

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


        checkAccountExistAlready(userName, new OnCheckAccountExists() {
            @Override
            public void checkAccountExists(boolean accountExists,String loginType) {
                if (accountExists && loginType.equals("EmailPassword")) {
                    progressBar.setVisibility(View.INVISIBLE);
                    makeToast("First",LoginActivity.this);
                    signInAccountWithFirebase(getEmailCredentials(userName,passWord),null,false);
                }
                else if(accountExists) {
                    Snackbar.make(linearLayout, "User already signed in using " + loginType, Snackbar.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    makeToast("Second",LoginActivity.this);
                }
                else
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    Snackbar.make(linearLayout, "Invalid Login Credentials", Snackbar.LENGTH_LONG).show();
                    makeToast("Third",LoginActivity.this);
                }
            }
        });

    }



    private void getFirestoreUser(final String email, final OnGetFirestoreUser onGetFirestoreUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection(USER_COLLECTION_PATH);
        users.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    boolean flag = false;
                    for(QueryDocumentSnapshot snapshot : task.getResult())
                    {
                        User user = snapshot.toObject(User.class);
                        if(user.getEmail().equals(email))
                        {
                            flag = true;
                            onGetFirestoreUser.getFirestoreUser(user);
                            break;
                        }
                    }
                    if(!flag)
                    {
                        onGetFirestoreUser.getFirestoreUser(null);
                    }
                }
            }
        });

    }

    private interface OnGetFirestoreUser{
        void getFirestoreUser(User user);
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
                final GoogleSignInAccount account = task.getResult(ApiException.class);

                Log.println(Log.DEBUG,TAG,"Google SignIn");


                if(account != null) {

                    checkAccountExistAlready(account.getEmail(), new OnCheckAccountExists() {
                        @Override
                        public void checkAccountExists(boolean accountExists,String loginType) {
                            if (accountExists && loginType.equals("Google")) {
                                progressBar.setVisibility(View.INVISIBLE);
                                signInAccountWithFirebase(getGoogleCredentials(account.getIdToken()),account,false);
                            }
                            else if(accountExists) {
                                Snackbar.make(linearLayout, "User already signed in using " + loginType, Snackbar.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }else
                            {
                                signInAccountWithFirebase(getGoogleCredentials(account.getIdToken()),account,true);
                            }
                        }
                    });
                }

            } catch (ApiException e) {
                Toast.makeText(this,"Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void addAccountToFirestore(User user,String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection(USER_COLLECTION_PATH);
        Log.println(Log.INFO,"addUser","add User function running....");
        users.document(userID).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Log.println(Log.INFO,"addUser","User added....");
                    Toast.makeText(LoginActivity.this, "User Added successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, FirstPage.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Log.println(Log.INFO,"addUser","Error: " + task.getException());
                }
            }
        });


    }




    public AuthCredential getGoogleCredentials(String googleIdToken) {
        return GoogleAuthProvider.getCredential(googleIdToken, null);
    }

    // --------------------------------------- GOOGLE SIGN IN END -----------------------------------------


    // ---------------------------------------  SIGN IN WITH CREDENTIAL START ----------------------------------------

    public void signInAccountWithFirebase(final AuthCredential credential, final GoogleSignInAccount account, final boolean toBeAdded)
    {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.println(Log.INFO,TAG,"Signed in successfully!");
                            if(toBeAdded) {
                                User user = new User(account.getDisplayName(),account.getEmail(),"","",null,"Google");
                                if(account.getPhotoUrl() != null)
                                    user.setUser_profile_pic(account.getPhotoUrl().toString());
                                String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                                addAccountToFirestore(user,userId);
                            }
                            else
                            {
                                Intent intent = new Intent(LoginActivity.this, FirstPage.class);
                                startActivity(intent);
                                finish();
                            }
                        } else{

                            //handleUserCollisionException();
                            Log.println(Log.ERROR,TAG,"error:438: " + task.getException());
                        }


                    }
                });
    }

    private void checkAccountExistAlready(final String email, final OnCheckAccountExists onCheckAccountExists) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection(USER_COLLECTION_PATH);
        users.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    boolean flag = false;
                    for(QueryDocumentSnapshot snapshot : task.getResult())
                    {
                        User user = snapshot.toObject(User.class);
                        if(user.getEmail().equals(email))
                        {
                            flag = true;
                            onCheckAccountExists.checkAccountExists(true,user.getLoginType());
                            break;
                        }
                    }
                    if(!flag)
                    {
                        onCheckAccountExists.checkAccountExists(false,"");
                    }
                }
            }
        });

    }

    private interface OnCheckAccountExists{
        void checkAccountExists(boolean accountExists,String loginType);
    }


    // ---------------------------------------  SIGN IN WITH CREDENTIAL STOP ----------------------------------------


    // -------------------------------------- FORGOT PASSWORD START --------------------------------------------

    public void forgotPassword(View view) {
        startActivity(new Intent(LoginActivity.this,ForgotPassword.class));
    }

    // --------------------------------------- FORGOT PASSWORD END -------------------------------------------
}
