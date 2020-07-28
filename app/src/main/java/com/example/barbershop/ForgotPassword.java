package com.example.barbershop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

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

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseFirestore firestore;
    private static final String USER_COLLECTION_PATH = "Users";

    private final String PASSWORD_FIELD = "password";
    private final String EMAIL_FIELD = "email";
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initialize();


        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        send_otp.setOnClickListener(this);
        buttonResend.setOnClickListener(this);
        verify_otp.setOnClickListener(this);
        change_password.setOnClickListener(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Toast.makeText(ForgotPassword.this, "onVerificationCompleted: " + credential, Toast.LENGTH_SHORT).show();
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                //signInWithPhoneAuthCredential(credential);            //9187654329
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Toast.makeText(ForgotPassword.this, "onVerificationFailed", Toast.LENGTH_SHORT).show();
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    phoneInputLayout.setError("Invalid phone number.");
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

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        //verifyButton.setOnClickListener(null);
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
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        // [START_EXCLUDE]
        if (mVerificationInProgress) {
            startPhoneNumberVerification(phoneInputEditText.getText().toString());
        }
        // [END_EXCLUDE]
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {


        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;


    }

    private void verifyUserWithPhoneNumber(final String phoneNumberInput) {

        firestore.collection(USER_COLLECTION_PATH).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                String phone_number = document.get(PHONE_FIELD) + "";
                                String email = document.get(EMAIL_FIELD) + "";
                                String password = document.get(PASSWORD_FIELD) + "";
                                //Log.println(Log.INFO,"phone_numbers",phone_number);
                                if(phoneNumberInput.equals(phone_number))
                                {
                                    signInFirebaseUser(email,password);
                                    startPhoneNumberVerification(phoneNumberInput);
                                }
                            }
                        } else {
                            Toast.makeText(ForgotPassword.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(ForgotPassword.this, "Data fetch unsuccessful! error: " + e, Toast.LENGTH_SHORT).show();
                        Log.println(Log.ERROR,"Errors","Data fetch unsuccessful! error: " + e);
                    }
                });

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
                //Toast.makeText(ForgotPassword.this, "Firebase sign In Error: " + e, Toast.LENGTH_LONG).show();
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
                // Initialized state, show only the phone number field and start button
                enableViews(send_otp, phoneInputLayout);
                disableViews(buttonResend, verify_otp, otpInputLayout,passwordInputLayout,confirmPasswordInputLayout,change_password);
                //mBinding.detail.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                enableViews(verify_otp, buttonResend, phoneInputLayout, otpInputLayout);
                disableViews(send_otp,passwordInputLayout,confirmPasswordInputLayout,change_password);
                //mBinding.detail.setText(R.string.status_code_sent);
                Toast.makeText(this, "Code Sent", Toast.LENGTH_SHORT).show();
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                enableViews(verify_otp, buttonResend, phoneInputLayout, otpInputLayout);
                disableViews(passwordInputLayout,confirmPasswordInputLayout,send_otp,change_password);
                //mBinding.detail.setText(R.string.status_verification_failed);
                Toast.makeText(this, "Verification Failed", Toast.LENGTH_SHORT).show();
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                enableViews(passwordInputLayout,confirmPasswordInputLayout,change_password,otpInputLayout,phoneInputLayout);
                disableViews(verify_otp, send_otp, buttonResend);

                //mBinding.detail.setText(R.string.status_verification_succeeded);

                Toast.makeText(this, "Verification Succeeded", Toast.LENGTH_SHORT).show();
                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        otpInputEditText.setText(cred.getSmsCode());
                    } else {
                        //mBinding.fieldVerificationCode.setText(R.string.instant_validation);
                        Toast.makeText(this, "SMS not sent!", Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
                //mBinding.detail.setText(R.string.status_sign_in_failed);
                Toast.makeText(this, "Sign in Failed", Toast.LENGTH_SHORT).show();
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                Toast.makeText(this, "Sign in Success", Toast.LENGTH_SHORT).show();
                break;
        }

        /*
        if (user == null) {
            // Signed out
            mBinding.phoneAuthFields.setVisibility(View.VISIBLE);
            mBinding.signedInButtons.setVisibility(View.GONE);

            mBinding.status.setText(R.string.signed_out);
        } else {
            // Signed in
            mBinding.phoneAuthFields.setVisibility(View.GONE);
            mBinding.signedInButtons.setVisibility(View.VISIBLE);

            enableViews(mBinding.fieldPhoneNumber, mBinding.fieldVerificationCode);
            mBinding.fieldPhoneNumber.setText(null);
            mBinding.fieldVerificationCode.setText(null);

            mBinding.status.setText(R.string.signed_in);
            mBinding.detail.setText(getString(R.string.firebase_status_fmt, user.getUid()));
        }

         */
    }

    private void updatePassword(final String phone, final String password) {

        firestore.collection(USER_COLLECTION_PATH)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot document : task.getResult())
                {
                    String phone_no = document.get(PHONE_FIELD) + "";
                    if(phone_no.equals(phone)) {
                        DocumentReference documentReference = document.getReference();
                        String email = document.get(EMAIL_FIELD) + "";
                        updatePasswordUtil(documentReference, password, email);
                    }
                }
            }
        });
    }



    private void updatePasswordUtil(final DocumentReference documentReference, final String password, final String email) {

        firestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(documentReference);
                transaction.update(documentReference, PASSWORD_FIELD, password);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Log.d(TAG, "Transaction success!");
                Toast.makeText(ForgotPassword.this, "Password updated", Toast.LENGTH_SHORT).show();

                signInAfterPasswordUpdate(email,password);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Transaction failure.", e);
                        //Toast.makeText(ForgotPassword.this, "Transaction failure. " + e, Toast.LENGTH_SHORT).show();
                        Log.println(Log.ERROR,"Errors","Transaction error: " + e);
                    }
                });


    }

    private void signInAfterPasswordUpdate(final String email, final String password) {


        //First Sign in user .... then update the firebase password..... then logout........then sign in the user
        //mAuth.signInWithEmailAndPassword()

        mAuth.getCurrentUser().updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ForgotPassword.this, "Password of firebase also updated!", Toast.LENGTH_SHORT).show();
                if(mAuth.getCurrentUser() != null)
                    mAuth.signOut();         //After updating the password , sign out the user
                signInFirebaseUser(email,password);               //Sign In With updated Password

                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                preferencesEditor.putString("login_type", "FirebaseSignIn");
                preferencesEditor.apply();

                startActivity(new Intent(ForgotPassword.this, FirstPage.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.println(Log.ERROR,"Errors","Firebase Update Password Error: " + e);
                reauthenticate(email,password);
            }
        });



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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_otp:
                Toast.makeText(this, "Sending the otp!", Toast.LENGTH_SHORT).show();
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
                updatePassword(phoneInputEditText.getText().toString(),passwordInputEditText.getText().toString());
                break;
        }
    }
}