package com.example.barbershop;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.barbershop.models.User;
import com.example.barbershop.ui.FirstPage.FirstPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private static final String PHONE_FIELD = "phone";
    TextInputLayout phoneInputLayout, otpInputLayout, passwordInputLayout, confirmPasswordInputLayout;
    TextInputEditText phoneInputEditText, otpInputEditText, passwordInputEditText, confirmPasswordInputEditText;
    MaterialButton send_otp,buttonResend,verify_otp,change_password;

    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private FirebaseAuth mAuth;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseFirestore firestore;
    private static final String USER_COLLECTION_PATH = "Users";
    private final String PASSWORD_FIELD = "password";
    private final String EMAIL_FIELD = "email";
    private ProgressBar progressBar;
    private String userEmail;
    private String userPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initialize();
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        send_otp.setOnClickListener(this);
        buttonResend.setOnClickListener(this);
        verify_otp.setOnClickListener(this);
        change_password.setOnClickListener(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

                Toast.makeText(ForgotPassword.this, "onVerificationCompleted: " + credential, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                mVerificationInProgress = false;
                updateUI(STATE_VERIFY_SUCCESS, credential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                Toast.makeText(ForgotPassword.this, "onVerificationFailed", Toast.LENGTH_SHORT).show();
                mVerificationInProgress = false;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    phoneInputLayout.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Toast.makeText(ForgotPassword.this, "onCodeSent: " + verificationId, Toast.LENGTH_SHORT).show();

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]
    }

    private void initialize() {
        phoneInputLayout = findViewById(R.id.phoneResetPassInputLayout);
        otpInputLayout = findViewById(R.id.otpResetPassInputLayout);
        passwordInputLayout = findViewById(R.id.passwordResetPassLayout);
        confirmPasswordInputLayout = findViewById(R.id.repeatPasswordResetPassLayout);

        phoneInputEditText = findViewById(R.id.phoneResetPassEditText);
        otpInputEditText = findViewById(R.id.otpResetPassEditText);
        passwordInputEditText = findViewById(R.id.passwordResetPassEditText);
        confirmPasswordInputEditText = findViewById(R.id.repeatPasswordResetPassEditText);
        send_otp = findViewById(R.id.send_otp);
        buttonResend = findViewById(R.id.resend_code);
        verify_otp = findViewById(R.id.verify_with_otp);
        change_password = findViewById(R.id.change_password);
        progressBar = findViewById(R.id.progressBarResetPass);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        enableViews(phoneInputLayout,send_otp);
        disableViews(otpInputLayout,passwordInputLayout,confirmPasswordInputLayout,verify_otp,buttonResend,change_password);
    }

    @Override
    protected void onResume() {
        super.onResume();

        inputCredentialsVerification();

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        if (mVerificationInProgress) {
            startPhoneNumberVerification(phoneInputEditText.getText().toString());
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_otp:
                Toast.makeText(this, "Sending the otp!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                verifyUserWithPhoneNumber(phoneInputEditText.getText().toString());
                break;
            case R.id.verify_with_otp:
                String code = otpInputEditText.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    otpInputLayout.setError("Cannot be empty.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.resend_code:
                resendVerificationCode(phoneInputEditText.getText().toString(), mResendToken);
                break;
            case R.id.change_password:
                Toast.makeText(this, "Password Change Process Initiated!", Toast.LENGTH_SHORT).show();
                updatePassword(passwordInputEditText.getText().toString());
                break;
        }
    }

    private void startPhoneNumberVerification(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        mVerificationInProgress = true;

    }

    private void verifyUserWithPhoneNumber(final String phoneNumberInput) {

        firestore.collection(USER_COLLECTION_PATH).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean phone_flag = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                User user = document.toObject(User.class);
                                /*String phone_number = document.get(PHONE_FIELD) + "";
                                String email = document.get(EMAIL_FIELD) + "";
                                String password = document.get(PASSWORD_FIELD) + "";*/

                                String phone_number = user.getPhone();
                                String email = user.getEmail();
                                String password = user.getPassword();

                                if(phoneNumberInput.equals(phone_number))
                                {
                                    if(!email.equals("") && !password.equals("")) {
                                        //signInFirebaseUser(email, password);
                                        setEmailAndPassword(email,password);
                                        startPhoneNumberVerification(phoneNumberInput);
                                    }
                                    else
                                    {
                                        Snackbar.make(findViewById(android.R.id.content), "User signed earlier through OTP!\nNo password assigned!",
                                                Snackbar.LENGTH_SHORT).show();
                                    }
                                    phone_flag = true;
                                    break;
                                }
                            }
                            if(!phone_flag)
                            {
                                Snackbar.make(findViewById(android.R.id.content), "No matching phone number found!",
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.println(Log.ERROR,TAG,""+task.getException());
                        }

                    }
                });

    }

    private void setEmailAndPassword(String email, String password) {
        userEmail = email;
        userPassword = password;
    }

    private void signInFirebaseUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(ForgotPassword.this, "Firebase User Signed In!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.println(Log.ERROR,"Errors","Firebase sign In Error: " + e);
            }
        });
    }



    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        //signInWithPhoneAuthCredential(credential);
        
        updateUI(STATE_VERIFY_SUCCESS,credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "91"+phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                enableViews(send_otp, phoneInputLayout);
                disableViews(buttonResend, verify_otp, otpInputLayout,passwordInputLayout,confirmPasswordInputLayout,change_password);
                break;
            case STATE_CODE_SENT:
                enableViews(verify_otp, buttonResend, phoneInputLayout, otpInputLayout,progressBar);
                disableViews(send_otp,passwordInputLayout,confirmPasswordInputLayout,change_password);
                Toast.makeText(this, "Code Sent", Toast.LENGTH_SHORT).show();
                break;
            case STATE_VERIFY_FAILED:
                enableViews(verify_otp, buttonResend, phoneInputLayout, otpInputLayout);
                disableViews(passwordInputLayout,confirmPasswordInputLayout,send_otp,change_password,progressBar);
                Toast.makeText(this, "Verification Failed", Toast.LENGTH_SHORT).show();
                break;
            case STATE_VERIFY_SUCCESS:
                enableViews(passwordInputLayout,confirmPasswordInputLayout,change_password,otpInputLayout,phoneInputLayout);
                disableViews(verify_otp, send_otp, buttonResend,progressBar);

                Toast.makeText(this, "Verification Succeeded", Toast.LENGTH_SHORT).show();
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        otpInputEditText.setText(cred.getSmsCode());
                    } else {
                        Toast.makeText(this, "SMS not sent!", Toast.LENGTH_SHORT).show();
                    }
                }
                signInFirebaseUser(userEmail,userPassword);
                break;
            case STATE_SIGNIN_FAILED:
                Toast.makeText(this, "Sign in Failed", Toast.LENGTH_SHORT).show();
                break;
            case STATE_SIGNIN_SUCCESS:
                Toast.makeText(this, "Sign in Success", Toast.LENGTH_SHORT).show();
                break;
        }


    }

    private void updatePassword(final String password) {

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            firestore.collection(USER_COLLECTION_PATH).document(userID).update(PASSWORD_FIELD, password)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ForgotPassword.this, "Firebase Password updated!", Toast.LENGTH_SHORT).show();
                        updatePasswordFirebase(password);
                    }
                    else {
                        Log.println(Log.ERROR,TAG,task.getException()+"");
                        reauthenticate(userEmail,password);
                    }
                }
            });
        }

    }

    private void updatePasswordFirebase(final String password) {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            FirebaseAuth.getInstance().getCurrentUser().updatePassword(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                signInFirebaseUser(userEmail,password);
                                startActivity(new Intent(ForgotPassword.this, FirstPage.class));
                                finish();
                            }
                            else {
                                Log.println(Log.ERROR,TAG,""+task.getException());
                            }
                        }
                    });
        }
    }

    public void reauthenticate(String email,String password) {
        // [START reauthenticate]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);

        // Prompt the user to re-provide their sign-in credentials
        assert user != null;
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.println(Log.INFO,"info","User reauthenticated!");
                    }
                });
        // [END reauthenticate]
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
            v.setVisibility(View.VISIBLE);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
            v.setVisibility(View.GONE);
        }
    }



    private void inputCredentialsVerification() {

        if(phoneInputLayout.isEnabled())
        {
            phoneInputEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String phone = phoneInputEditText.getText().toString().trim();
                    if(TextUtils.isEmpty(phone)){
                        phoneInputLayout.setErrorEnabled(true);
                        phoneInputLayout.setError("Please enter the Phone Number");
                    }
                    else if(phone.length() != 10) {
                        phoneInputLayout.setErrorEnabled(true);
                        phoneInputLayout.setError("Phone number should be of 10 digits");
                    }
                    else{
                        phoneInputLayout.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        if(passwordInputLayout.isEnabled()) {
            passwordInputEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String password = passwordInputEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(password)) {
                        passwordInputLayout.setErrorEnabled(true);
                        passwordInputLayout.setError("Please enter the Password");
                    } else if (password.length() < 6) {
                        passwordInputLayout.setErrorTextAppearance(R.style.errorAppearanceWeak);
                        passwordInputLayout.setErrorEnabled(true);
                        passwordInputLayout.setError("Password length should be atleast 6");
                    } else if (password.length() <= 12 && password.length() >= 6) {
                        passwordInputLayout.setErrorTextAppearance(R.style.errorAppearanceGood);
                        passwordInputLayout.setErrorEnabled(true);
                        passwordInputLayout.setError("Good Password strength");
                    } else if (password.length() > 12) {
                        passwordInputLayout.setErrorTextAppearance(R.style.errorAppearanceStrong);
                        passwordInputLayout.setErrorEnabled(true);
                        passwordInputLayout.setError("String Password Strength");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            passwordInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    String password = passwordInputEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(password)) {
                        passwordInputLayout.setErrorEnabled(false);
                    }
                }
            });

        }
        if(confirmPasswordInputLayout.isEnabled())
        {
            confirmPasswordInputEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String repeatPassword = confirmPasswordInputEditText.getText().toString().trim();
                    String password = passwordInputEditText.getText().toString().trim();
                    if(TextUtils.isEmpty(repeatPassword)){
                        confirmPasswordInputLayout.setErrorEnabled(true);
                        confirmPasswordInputLayout.setError("Please enter the Password to Verify");
                    }else if(!repeatPassword.equals(password)){
                        confirmPasswordInputLayout.setErrorEnabled(true);
                        confirmPasswordInputLayout.setError("Password did not Matched");
                    }
                    else{
                        confirmPasswordInputLayout.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }


}