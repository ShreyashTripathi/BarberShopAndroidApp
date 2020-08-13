package com.example.barbershop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.barbershop.models.User;
import com.example.barbershop.ui.FirstPage.FirstPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import global_class.MyGlobalClass;

public class PhoneAuthentication extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    private static final String USER_COLLECTION_PATH = "Users";
    private FirebaseFirestore db;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private MaterialButton send_otp_but,verify_otp_but,resend_otp_but;
    private TextInputEditText fieldPhoneNumber,fieldVerificationCode;
    private ProgressBar progressBar;
    private String sharedPrefFile = "login";
    private SharedPreferences mPreferences;
    private String phone_number;
    private MyGlobalClass myGlobalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        initialize();
        send_otp_but.setOnClickListener(this);
        verify_otp_but.setOnClickListener(this);
        resend_otp_but.setOnClickListener(this);

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull final PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
                updateUI(STATE_VERIFY_SUCCESS, credential);

                checkAccountExistAlready(phone_number, new OnCheckAccountExists() {
                    @Override
                    public void checkAccountExists(boolean accountExists, User user) {
                        if(accountExists)
                        {
                            if(user!=null) {
                                LinearLayout linearLayout = findViewById(R.id.phone_auth_container);
                                String loginType = user.getLoginType();
                                Snackbar.make(linearLayout, "User already signed in using " + loginType, Snackbar.LENGTH_LONG).show();
                            }
                        }
                        else {
                            signInWithPhoneAuthCredential(credential);
                        }
                    }
                });

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    fieldPhoneNumber.setError("Invalid phone number.");
                    // [END_EXCLUDE]
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
                Log.d(TAG, "onCodeSent:" + verificationId);

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
        send_otp_but = findViewById(R.id.send_otp_for_auth);
        verify_otp_but = findViewById(R.id.verify_otp_for_auth);
        resend_otp_but = findViewById(R.id.resend_code_for_auth);

        fieldPhoneNumber = findViewById(R.id.phone_number_for_auth);
        fieldVerificationCode = findViewById(R.id.otp_for_auth);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBarOtpAuth);
        db = FirebaseFirestore.getInstance();
        myGlobalClass = (MyGlobalClass)getApplicationContext();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(fieldPhoneNumber.getText().toString());
        }
        // [END_EXCLUDE]
    }
    // [END on_start_check_user]

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void addUser(String phone,String userID) {
        User user = new User("","","",phone,null,"OTP");
        CollectionReference users = db.collection(USER_COLLECTION_PATH);

        Log.println(Log.INFO,"addUser","add User function running....");
        users.document(userID).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Log.println(Log.INFO,"addUser","User added....");
                    Toast.makeText(PhoneAuthentication.this, "User Added successfully!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.println(Log.INFO,"addUser","Error: " + task.getException());
                }
            }
        });

    }

    private void checkAccountExistAlready(final String phone, final OnCheckAccountExists onCheckAccountExists) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection(USER_COLLECTION_PATH);
        users.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    boolean flag = false;
                    for(QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult()))
                    {
                        User user = snapshot.toObject(User.class);
                        if(user.getPhone().equals(phone))
                        {
                            flag = true;
                            onCheckAccountExists.checkAccountExists(true,user);
                            break;
                        }
                    }
                    if(!flag)
                    {
                        onCheckAccountExists.checkAccountExists(false,null);
                    }
                }
            }
        });

    }

    private interface OnCheckAccountExists{
        void checkAccountExists(boolean accountExists,User user);
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                            String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                            addUser(phone_number,userID);
                            SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                            preferencesEditor.putString("userID", userID);
                            preferencesEditor.apply();
                            updateUI(STATE_SIGNIN_SUCCESS, user);
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                fieldVerificationCode.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

    private void signOut() {
        mAuth.signOut();
        updateUI(STATE_INITIALIZED);
    }

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
                // Initialized state, show only the phone number field and start button
                enableViews(send_otp_but, fieldPhoneNumber);
                disableViews(verify_otp_but, resend_otp_but, fieldVerificationCode);
                //mBinding.detail.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                progressBar.setVisibility(View.VISIBLE);
                enableViews(verify_otp_but, resend_otp_but, fieldPhoneNumber, fieldVerificationCode);
                disableViews(send_otp_but);
                //mBinding.detail.setText(R.string.status_code_sent);
                Toast.makeText(this, "OTP Sent to your phone", Toast.LENGTH_SHORT).show();
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                enableViews(send_otp_but, verify_otp_but, resend_otp_but, fieldPhoneNumber, fieldVerificationCode);
                //mBinding.detail.setText(R.string.status_verification_failed);
                Toast.makeText(this, "OTP verification failed", Toast.LENGTH_SHORT).show();
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                disableViews(send_otp_but, verify_otp_but, resend_otp_but);
                enableViews(fieldPhoneNumber,fieldVerificationCode);
                //mBinding.detail.setText(R.string.status_verification_succeeded);
                Toast.makeText(this, "Verification succeeded", Toast.LENGTH_SHORT).show();
                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        fieldVerificationCode.setText(cred.getSmsCode());
                    }
                }
                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
                //mBinding.detail.setText(R.string.status_sign_in_failed);
                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                //Toast.makeText(this, "Signed In.... Moving to First Activity!", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                preferencesEditor.putString("login_type", "OTPSignIn");
                preferencesEditor.apply();
                progressBar.setVisibility(View.INVISIBLE);
                startActivity(new Intent(PhoneAuthentication.this, FirstPage.class));
                break;
        }

    }

    private boolean validatePhoneNumber() {
        String phoneNumber = fieldPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            fieldPhoneNumber.setError("Invalid phone number.");
            return false;
        }

        return true;
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_otp_for_auth:
                if (!validatePhoneNumber()) {
                    return;
                }
                if(fieldPhoneNumber.getText() != null)
                {
                    phone_number = fieldPhoneNumber.getText().toString();
                    startPhoneNumberVerification(fieldPhoneNumber.getText().toString());
                }
                break;
            case R.id.verify_otp_for_auth:
                String code = fieldVerificationCode.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    fieldVerificationCode.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.resend_code_for_auth:
                resendVerificationCode(fieldPhoneNumber.getText().toString(), mResendToken);
                break;

        }
    }
}