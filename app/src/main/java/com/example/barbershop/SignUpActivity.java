package com.example.barbershop;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.barbershop.models.User;
import com.example.barbershop.ui.FirstPage.FirstPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import global_class.MyGlobalClass;

public class SignUpActivity extends AppCompatActivity {

    private static final String USER_COLLECTION_PATH = "Users";
    private static final int PICK_IMAGE = 1;
    TextInputLayout nameRegisterLayout,emailRegisterLayout,passwordRegisterLayout,repeatPasswordRegisterLayout,phoneRegisterLayout;
    TextInputEditText nameRegisterEditText,emailRegisterEditText,passwordRegisterEditText,repeatPasswordRegisterEditText,phoneRegisterEdiText;
    MaterialButton submitRegister;
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
    private MyGlobalClass myGlobalClass;
    private CircleImageView profile_pic;
    private byte[] imgData;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        inititalize();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void inititalize() {

        nameRegisterLayout = findViewById(R.id.nameInputLayout);
        emailRegisterLayout = findViewById(R.id.emailRegisterLayout);
        passwordRegisterLayout = findViewById(R.id.PasswordRegisterLayout);
        repeatPasswordRegisterLayout = findViewById(R.id.RepeatPasswordRegisterLayout);
        phoneRegisterLayout = findViewById(R.id.PhoneRegisterLayout);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBarRegister);
        submitRegister = findViewById(R.id.submitRegister);
        nameRegisterEditText = findViewById(R.id.nameRegisterEditText);
        emailRegisterEditText = findViewById(R.id.emaiRegisterlEditText);
        passwordRegisterEditText = findViewById(R.id.PasswordRegisterEditText);
        repeatPasswordRegisterEditText = findViewById(R.id.RepeatPasswordRegisterEditText);
        phoneRegisterEdiText = findViewById(R.id.PhoneRegisterEditText);
        myGlobalClass = (MyGlobalClass)getApplicationContext();
        profile_pic = findViewById(R.id.profile_pic_upload);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        linearLayout = findViewById(R.id.linear_layout_sign_up);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE && data!=null) {
            startCropImageActivity(data.getData());

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri resultUri = result.getUri();
                compressAndSetImg(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
                Log.println(Log.ERROR,TAG,"Error: 155 : "+error);
            }
        }
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
                    passwordRegisterLayout.setError("Password length should be at least 6");
                }else if(password.length() <= 12){
                    passwordRegisterLayout.setErrorTextAppearance(R.style.errorAppearanceGood);
                    passwordRegisterLayout.setErrorEnabled(true);
                    passwordRegisterLayout.setError("Good Password strength");
                }else {
                    passwordRegisterLayout.setHelperTextTextAppearance(R.style.helperTextAppearance);
                    passwordRegisterLayout.setHelperTextEnabled(true);
                    passwordRegisterLayout.setHelperText("Strong Password Strength");

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
                    repeatPasswordRegisterLayout.setError("Please enter the password to Verify");
                }else if(!repeatPassword.equals(password)){
                    repeatPasswordRegisterLayout.setErrorEnabled(true);
                    repeatPasswordRegisterLayout.setError("Password did not matched");
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


    //-----------------------Img Settings START--------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3) {
            Log.d(TAG, "External storage1");
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                //resume tasks needing this permission
                Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickIntent.setType("image/*");
                startActivityForResult(pickIntent, PICK_IMAGE);

            } else {
                Toast.makeText(SignUpActivity.this, "Permission not granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted1");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted1");
            return true;
        }
    }

    public void choosePic(View view) {
        if(isReadStoragePermissionGranted()) {
            Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pickIntent.setType("image/*");
            startActivityForResult(pickIntent, PICK_IMAGE);
        }
    }


    private void compressAndSetImg(Uri resultImgUri) {
        Log.println(Log.INFO,TAG,resultImgUri.getPath()+"");
        byte[] img = compressImage(resultImgUri);
        setAndSendImg(img);
    }

    private byte[] compressImage(Uri imgUri)
    {
        String file_path = imgUri.getPath();
        Log.println(Log.INFO,TAG,"SecFilePath: " + file_path);
        Bitmap bitmap = BitmapFactory.decodeFile(file_path);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Log.println(Log.INFO,TAG,"Before Compression: "+bitmap.getAllocationByteCount());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); //compress to 50% of original image quality
        Log.println(Log.INFO,TAG,"After Compression: "+stream.toByteArray().length);
        return stream.toByteArray();
    }

    private void setAndSendImg(byte[] img) {
        Glide.with(SignUpActivity.this).load(img).into(profile_pic);
        imgData = img;
    }

    private void startCropImageActivity(Uri imgUri) {
        CropImage.activity(imgUri)
                .start(this);
    }

    private void uploadUserProfilePic(byte[] imgData, final OnUploadUserProfilePic onUploadUserProfilePic) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference userImagesRef = storageRef.child("UserProfilePic/"+ UUID.randomUUID().toString());
        UploadTask uploadTask = userImagesRef.putBytes(imgData);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    String imgSize = Objects.requireNonNull(task.getResult()).getBytesTransferred() + "";
                    Log.println(Log.INFO,TAG,"Image uploaded, size : "+imgSize);
                    Toast.makeText(SignUpActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                    userImagesRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String imgUrl = task.getResult().toString();
                            Log.println(Log.INFO,TAG,"Image url: " + imgUrl);
                            onUploadUserProfilePic.setImgUrl(imgUrl);
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.println(Log.INFO,TAG,"Image upload error: " + e);
            }
        });
    }

    interface OnUploadUserProfilePic{
        void setImgUrl(String url);
    }

    //-----------------------Img Settings END--------------------------------------

    //---------------------- USER Settings START ----------------------------------

    private interface OnCheckAccountExists{
        void checkAccountExists(boolean accountExists,String loginType);
    }

    private void addUser(final String name, final String email, final String password, final String phone) {

        if(imgData != null)
        {
            uploadUserProfilePic(imgData, new OnUploadUserProfilePic() {
                @Override
                public void setImgUrl(final String url) {
                    User user = new User(name,email,password,phone,null,"EmailPassword");
                    user.setUser_profile_pic(url);
                    CollectionReference users = db.collection(USER_COLLECTION_PATH);


                    Log.println(Log.INFO,"addUser","add User function running....");
                    users.document(email).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Log.println(Log.INFO,"addUser","User added....");
                                Toast.makeText(SignUpActivity.this, "User Added successfully!", Toast.LENGTH_SHORT).show();

                                signInAccountWithEmailFirebase(email,password);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.println(Log.INFO,"addUser","Error: " + e);
                        }
                    });

                    /*users.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.println(Log.INFO,"addUser","User added....");
                            Toast.makeText(SignUpActivity.this, "User Added successfully!", Toast.LENGTH_SHORT).show();

                            signInAccountWithEmailFirebase(email,password);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(SignUpActivity.this, "User not added, error: " + e, Toast.LENGTH_LONG).show();
                            Log.println(Log.INFO,"addUser","Error: " + e);
                        }
                    });*/
                }
            });
        }
        else {                //without pic
            User user = new User(name, email, password, phone, null, "EmailPassword");
            CollectionReference users = db.collection(USER_COLLECTION_PATH);

            Log.println(Log.INFO, "addUser", "add User function running....");
            users.document(email).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.println(Log.INFO, "addUser", "User added....");
                    Toast.makeText(SignUpActivity.this, "User Added successfully!", Toast.LENGTH_SHORT).show();

                    signInAccountWithEmailFirebase(email,password);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.println(Log.INFO, "addUser", "Error: " + e);
                }
            });

            /*users.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.println(Log.INFO, "addUser", "User added....");
                    Toast.makeText(SignUpActivity.this, "User Added successfully!", Toast.LENGTH_SHORT).show();

                    signInAccountWithEmailFirebase(email,password);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(SignUpActivity.this, "User not added, error: " + e, Toast.LENGTH_LONG).show();
                    Log.println(Log.INFO, "addUser", "Error: " + e);
                }
            });*/

        }
    }

    //---------------------- USER Settings START ----------------------------------

    //---------------------------------------- SUBMIT User START ----------------------------------

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



        checkAccountExistAlready(email, new OnCheckAccountExists() {
            @Override
            public void checkAccountExists(boolean accountExists,String loginType) {
                if (accountExists) {
                    Snackbar.make(linearLayout,"User with same email already exists OR you signed in earlier using " + loginType,Snackbar.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else
                {
                    addUser(name,email,password,phone);

                }
            }
        });

    }

    private void signInAccountWithEmailFirebase(final String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                            preferencesEditor.putString("login_type", "EmailSignIn");
                            preferencesEditor.putString("user_email",email);
                            preferencesEditor.apply();
                            progressBar.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(SignUpActivity.this, FirstPage.class);
                            startActivity(intent);
                        }
                        else
                        {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.println(Log.ERROR,TAG,""+task.getException());
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


    //---------------------------------------- SUBMIT User END ----------------------------------


}


