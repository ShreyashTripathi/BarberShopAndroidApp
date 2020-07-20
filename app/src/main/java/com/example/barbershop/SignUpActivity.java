package com.example.barbershop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.barbershop.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private static final String USER_COLLECTION_PATH = "Users";
    TextInputLayout nameRegisterLayout,emailRegisterLayout,passwordRegisterLayout,repeatPasswordRegisterLayout,phoneRegisterLayout;
    TextInputEditText nameRegisterEditText,emailRegisterEditText,passwordRegisterEditText,repeatPasswordRegisterEditText,phoneRegisterEdiText;
    MaterialButton submitRegister;
    ConstraintLayout constraintLayoutRegister;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "login";
    private FirebaseFirestore db;
    private String NAME_KEY = "name";
    private String EMAIL_KEY = "email";
    private String PASSWORD_KEY = "password";
    private String PHONE_KEY = "phone";
    private String TAG = "Link Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        inititalize();
        //setupFirestore();

    }

    private void inititalize() {

        nameRegisterLayout = findViewById(R.id.nameInputLayout);
        emailRegisterLayout = findViewById(R.id.emailRegisterLayout);
        passwordRegisterLayout = findViewById(R.id.PasswordRegisterLayout);
        repeatPasswordRegisterLayout = findViewById(R.id.RepeatPasswordRegisterLayout);
        phoneRegisterLayout = findViewById(R.id.PhoneRegisterLayout);
        constraintLayoutRegister = findViewById(R.id.ContraintLayoutRegister);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBarRegister);
        submitRegister = findViewById(R.id.submitRegister);

        nameRegisterEditText = findViewById(R.id.nameRegisterEditText);
        emailRegisterEditText = findViewById(R.id.emaiRegisterlEditText);
        passwordRegisterEditText = findViewById(R.id.PasswordRegisterEditText);
        repeatPasswordRegisterEditText = findViewById(R.id.RepeatPasswordRegisterEditText);
        phoneRegisterEdiText = findViewById(R.id.PhoneRegisterEditText);

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        constraintLayoutRegister.setOnClickListener(null);

        db = FirebaseFirestore.getInstance();

    }

    @Override
    protected void onResume() {
        super.onResume();

        inputCredentialsVerification();

        submitRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitUser();
            }
        });

    }




    private void submitUser() {

        final String name = nameRegisterEditText.getText().toString().trim();
        final String email = emailRegisterEditText.getText().toString().trim();
        final String password = passwordRegisterEditText.getText().toString().trim();
        String repeatPassword = repeatPasswordRegisterEditText.getText().toString().trim();
        final String phone = phoneRegisterEdiText.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        if(TextUtils.isEmpty(name)){
            nameRegisterLayout.setErrorEnabled(true);
            nameRegisterLayout.setError("Please Enter the Name");
            progressBar.setVisibility(View.GONE);
            return;
        }

        if(TextUtils.isEmpty(email)){
            emailRegisterLayout.setErrorEnabled(true);
            emailRegisterLayout.setError("Please Enter the Email");
            progressBar.setVisibility(View.GONE);
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailRegisterLayout.setErrorEnabled(true);
            emailRegisterLayout.setError("Please Enter the Valid Email");
            progressBar.setVisibility(View.GONE);
            return;
        }
        if(TextUtils.isEmpty(password)){
            passwordRegisterLayout.setErrorEnabled(true);
            passwordRegisterLayout.setError("Please Enter The Password");
            progressBar.setVisibility(View.GONE);
            return;
        }
        if(!password.equals(repeatPassword)){
            repeatPasswordRegisterLayout.setErrorEnabled(true);
            repeatPasswordRegisterLayout.setError("Please enter the same Password as above");
            progressBar.setVisibility(View.GONE);
            return;
        }
        if(TextUtils.isEmpty(phone)){
            phoneRegisterLayout.setErrorEnabled(true);
            phoneRegisterLayout.setError("Please Enter your Contact NO.");
            progressBar.setVisibility(View.GONE);
            return;
        }

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString("login_type", "EmailSignIn");
        preferencesEditor.apply();

        addUser(name,email,password,phone);
        signInAccountWithFirebase(getEmailCredentials(email,password));

    }

    public AuthCredential getEmailCredentials(String email, String password) {
        return EmailAuthProvider.getCredential(email, password);
    }

    private void addUser(String name,String email,String password,String phone) {
        User user = new User(name,email,password,phone);
        CollectionReference users = db.collection(USER_COLLECTION_PATH);

        Log.println(Log.INFO,"addUser","add User function running....");
        users.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.println(Log.INFO,"addUser","User added....");
                Toast.makeText(SignUpActivity.this, "User Added successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(SignUpActivity.this, "User not added, error: " + e, Toast.LENGTH_LONG).show();
                Log.println(Log.INFO,"addUser","Error: " + e);
            }
        });

    }



    private void inputCredentialsVerification() {


        emailRegisterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = emailRegisterEditText.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    emailRegisterLayout.setErrorEnabled(true);
                    emailRegisterLayout.setError("Please enter the Email");
                }else{
                    emailRegisterLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordRegisterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = passwordRegisterEditText.getText().toString().trim();
                if(TextUtils.isEmpty(password)){
                    passwordRegisterLayout.setErrorEnabled(true);
                    passwordRegisterLayout.setError("Please enter the Password");
                }else if(password.length()<6){
                    passwordRegisterLayout.setErrorTextAppearance(R.style.errorAppearanceWeak);
                    passwordRegisterLayout.setErrorEnabled(true);
                    passwordRegisterLayout.setError("Password length should be atleast 6");
                }else if(password.length()<=12 && password.length()>=6){
                    passwordRegisterLayout.setErrorTextAppearance(R.style.errorAppearanceGood);
                    passwordRegisterLayout.setErrorEnabled(true);
                    passwordRegisterLayout.setError("Good Password strength");
                }else if(password.length()>12){
                    passwordRegisterLayout.setErrorTextAppearance(R.style.errorAppearanceStrong);
                    passwordRegisterLayout.setErrorEnabled(true);
                    passwordRegisterLayout.setError("String Password Strength");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passwordRegisterEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String password = passwordRegisterEditText.getText().toString().trim();
                if(!TextUtils.isEmpty(password)){
                    passwordRegisterLayout.setErrorEnabled(false);
                }
            }
        });

        repeatPasswordRegisterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String repeatPassword = repeatPasswordRegisterEditText.getText().toString().trim();
                String password = passwordRegisterEditText.getText().toString().trim();
                if(TextUtils.isEmpty(repeatPassword)){
                    repeatPasswordRegisterLayout.setErrorEnabled(true);
                    repeatPasswordRegisterLayout.setError("Please enter the Password to Verify");
                }else if(!repeatPassword.equals(password)){
                    repeatPasswordRegisterLayout.setErrorEnabled(true);
                    repeatPasswordRegisterLayout.setError("Password did not Matched");
                }
                else{
                    repeatPasswordRegisterLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        phoneRegisterEdiText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = phoneRegisterEdiText.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    phoneRegisterLayout.setErrorEnabled(true);
                    phoneRegisterLayout.setError("Please enter your Phone NO.");
                }else{
                    phoneRegisterLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        emailRegisterEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String email = emailRegisterEditText.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    emailRegisterLayout.setErrorEnabled(true);
                    emailRegisterLayout.setError("Please enter the Email");
                }else{
                    emailRegisterLayout.setErrorEnabled(false);
                }
            }
        });

    }

    // --------------------------------------- LINK,MERGE AND SIGN IN WITH CREDENTIAL START ----------------------------------------

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
                            Toast.makeText(SignUpActivity.this, "LinkWithCredential failed.",
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

    public void signInAccountWithFirebase(final AuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            linkAccountsWithCredential(credential);
                            Intent intent = new Intent(SignUpActivity.this,FirstPage.class);
                            startActivity(intent);
                            progressBar.setVisibility(View.INVISIBLE);
                        } else {

                            Toast.makeText(SignUpActivity.this, "Authentication failed !!!" + task.getException(),
                                    Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }

    // --------------------------------------- LINK,MERGE AND SIGN IN WITH CREDENTIAL STOP ----------------------------------------

}
