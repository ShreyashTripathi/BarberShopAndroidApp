package com.example.barbershop.ui.settings;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.barbershop.R;
import com.example.barbershop.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class UpdateProfileDialogFragment extends BottomSheetDialogFragment {

    private static final String TAG = "Update_Fragment_TAG";
    private static final int PICK_IMAGE = 1;
    private CircleImageView pic_update_civ;
    private TextInputLayout nameUpdateLayout,emailUpdateLayout,passwordUpdateLayout,repeatPasswordUpdateLayout,phoneUpdateLayout;
    private TextInputEditText nameUpdateEditText,emailUpdateEditText,passwordUpdateEditText,repeatPasswordUpdateEditText,phoneUpdateEdiText;
    private Button saveButton;
    private String emailOrPhone;
    private byte[] imgData;
    private View userView;
    private SettingsViewModel settingsViewModel;
    private Context userContext;

    public static UpdateProfileDialogFragment newInstance(){
        return new UpdateProfileDialogFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.layout_update_profile_bottom_sheet, container, false);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        initializeUI(view);

        String sharedPrefFile = "login";
        SharedPreferences mPreferences = requireActivity().getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String email = mPreferences.getString("user_email","");
        String phone = mPreferences.getString("user_phone","");

        if(!email.equals(""))
        {
            emailOrPhone = email;
        }
        else
        {
            emailOrPhone = phone;
        }
        settingsViewModel.getUserData(emailOrPhone, new SettingsViewModel.OnGetUserData() {
            @Override
            public void getUserData(User user) {
                if(isAdded()) {
                    if(user.getUser_profile_pic() != null)
                        Picasso.with(userContext).load(user.getUser_profile_pic()).into(pic_update_civ);
                    else
                        Picasso.with(userContext).load(R.drawable.user).into(pic_update_civ);
                    nameUpdateEditText.setText(user.getName());
                    emailUpdateEditText.setText(user.getEmail());
                    phoneUpdateEdiText.setText(user.getPhone());
                    passwordUpdateEditText.setText(user.getPassword());
                }
            }
        });

        inputCredentialsVerification();
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        pic_update_civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isReadStoragePermissionGranted()) {
                    Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    pickIntent.setType("image/*");
                    startActivityForResult(pickIntent, PICK_IMAGE);
                }
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsViewModel.getUserData(emailOrPhone, new SettingsViewModel.OnGetUserData() {
                    @Override
                    public void getUserData(final User user) {
                        final User tempUser = new User(nameUpdateEditText.getText().toString(),emailUpdateEditText.getText().toString(),passwordUpdateEditText.getText().toString(),phoneUpdateEdiText.getText().toString());
                        settingsViewModel.checkIsUserInfoIsSame(user, tempUser, new SettingsViewModel.SetIsUserInfoSame() {
                            @Override
                            public void IsUserInfoSame(boolean isSame) {
                                if(!isSame)
                                {
                                    settingsViewModel.updateUserProfile(tempUser,emailOrPhone);
                                    Toast.makeText(userContext, "User Data Updated!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(userContext, "User data is same in db!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        checkAndUpdateImage(user.getUser_profile_pic(), new OnUploadUserProfilePic() {
                                @Override
                                public void setImgUrl(String url,boolean isImageDataSet) {
                                    if(isImageDataSet && url != null)
                                        settingsViewModel.setProfilePic(emailOrPhone,url);
                                }
                            });

                    }
                });
                dismiss();
            }
        });
        return view;
    }



    private void initializeUI(View view) {
        pic_update_civ = view.findViewById(R.id.profile_pic_update);
        nameUpdateEditText = view.findViewById(R.id.userNameEditText_update);
        emailUpdateEditText = view.findViewById(R.id.emailEditText_update);
        passwordUpdateEditText = view.findViewById(R.id.passwordEditText_update);
        repeatPasswordUpdateEditText = view.findViewById(R.id.RepeatPasswordEditText_update);
        phoneUpdateEdiText = view.findViewById(R.id.phoneEditText_update);

        nameUpdateLayout = view.findViewById(R.id.userNameInputLayout_update);
        emailUpdateLayout = view.findViewById(R.id.emailInputLayout_update);
        passwordUpdateLayout = view.findViewById(R.id.passwordInputLayout_update);
        repeatPasswordUpdateLayout = view.findViewById(R.id.RepeatPasswordInputLayout_update);
        phoneUpdateLayout = view.findViewById(R.id.phoneInputLayout_update);
        saveButton = view.findViewById(R.id.save_button_update);
    }


    private void inputCredentialsVerification() {

        passwordUpdateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(v.isInTouchMode() && hasFocus)
                {
                    v.performClick();
                    //Toast.makeText(requireActivity(), "Password box focused!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        passwordUpdateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(requireActivity(), "Password box clicked!", Toast.LENGTH_SHORT).show();
                passwordVerification();
            }
        });


        phoneUpdateLayout.setPrefixText("+91  ");
        phoneUpdateEdiText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = phoneUpdateEdiText.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    phoneUpdateLayout.setErrorEnabled(true);
                    phoneUpdateLayout.setError("Please enter your Phone Number");
                }else{
                    phoneUpdateLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }
    private void passwordVerification()
    {
        passwordUpdateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = passwordUpdateEditText.getText().toString().trim();
                repeatPasswordUpdateLayout.setVisibility(View.VISIBLE);
                if(password.length()<6){
                    passwordUpdateLayout.setErrorTextAppearance(R.style.errorAppearanceWeak);
                    passwordUpdateLayout.setErrorEnabled(true);
                    passwordUpdateLayout.setError("Password length should be atleast 6");
                }else if(password.length() <= 12){
                    passwordUpdateLayout.setErrorTextAppearance(R.style.errorAppearanceGood);
                    passwordUpdateLayout.setErrorEnabled(true);
                    passwordUpdateLayout.setError("Good Password strength");
                }else {
                    passwordUpdateLayout.setHelperTextTextAppearance(R.style.helperTextAppearance);
                    passwordUpdateLayout.setHelperTextEnabled(true);
                    passwordUpdateLayout.setHelperText("Strong Password Strength");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        repeatPasswordUpdateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String repeatPassword = repeatPasswordUpdateEditText.getText().toString().trim();
                String password = passwordUpdateEditText.getText().toString().trim();
                if(TextUtils.isEmpty(repeatPassword)){
                    repeatPasswordUpdateLayout.setErrorEnabled(true);
                    repeatPasswordUpdateLayout.setError("Please enter the Password to Verify");
                }else if(!repeatPassword.equals(password)){
                    repeatPasswordUpdateLayout.setErrorEnabled(true);
                    repeatPasswordUpdateLayout.setError("Password did not Matched");
                }
                else{
                    repeatPasswordUpdateLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //---------------------------------------- Image Setting START------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE && data!=null) {
            Log.println(Log.INFO,TAG,"Picked image Result OK");
            startCropImageActivity(data.getData());
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Log.println(Log.INFO,TAG,"Cropped image Result");
            if (resultCode == RESULT_OK) {
                Log.println(Log.INFO,TAG,"Cropped image Result OK");
                Toast.makeText(userContext, "Cropped image Result OK", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(userContext, "Permission not granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted1");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted1");
            return true;
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
        if(img==null)
        {
            Log.println(Log.INFO,TAG,"img array is null!");
        }
        Glide.with(requireActivity()).load(img).into(pic_update_civ);
        //Picasso.with(requireActivity()).load()
        imgData = img;
    }

    private void startCropImageActivity(Uri imgUri) {
        CropImage.activity(imgUri).start(requireActivity(),UpdateProfileDialogFragment.this);
    }

    private void checkAndUpdateImage(String imgUrl, final OnUploadUserProfilePic onUploadUserProfilePic) {
        //TODO: first delete the old pic and then upload the image to a folder named according to user_id instead of UUID
        if(imgData != null && imgUrl != null) {
            FirebaseStorage storage_old = FirebaseStorage.getInstance();
            StorageReference storageReference_old = storage_old.getReferenceFromUrl(imgUrl);
            storageReference_old.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Log.println(Log.INFO,TAG,"Old Pic deleted!");
                    }
                    else {
                        Log.println(Log.ERROR,TAG,""+task.getException());
                    }
                }
            });
        }

        if(imgData!=null)
        {
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
                        Toast.makeText(userContext, "Image uploaded", Toast.LENGTH_SHORT).show();
                        userImagesRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String imgUrl = task.getResult().toString();
                                Log.println(Log.INFO,TAG,"Image url: " + imgUrl);
                                onUploadUserProfilePic.setImgUrl(imgUrl,true);
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
        else
        {
            onUploadUserProfilePic.setImgUrl(null,false);
        }
    }


    interface OnUploadUserProfilePic{
        void setImgUrl(String url,boolean isImageDataSet);
    }

    // ----------------------------- Image Setting END -------------------------------


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        updateUI();
    }

    public void setUserView(View view, Context context)
    {
        userView = view;
        userContext = context;
    }

    protected void updateUI() {
        if(userView != null) {
            ImageView drop_down = userView.findViewById(R.id.drop_down_icon_setting);
            ProgressBar progressBar = userView.findViewById(R.id.progressBarSetting);
            drop_down.setImageResource(R.drawable.ic_outline_create_24);
            String sharedPrefFile = "login";
            SharedPreferences mPreferences = requireActivity().getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
            String email = mPreferences.getString("user_email", "");
            String phone = mPreferences.getString("user_phone", "");
            final String emailOrPhone;
            if (!email.equals("")) {
                emailOrPhone = email;
            } else {
                emailOrPhone = phone;
            }

            progressBar.setVisibility(View.VISIBLE);
            final Handler handler = new Handler();
            final Runnable r = new Runnable() {
                public void run() {
                    settingsViewModel.getUserData(emailOrPhone, new SettingsViewModel.OnGetUserData() {
                        @Override
                        public void getUserData(User user) {
                            setUserData(user);
                        }});
                }
            };
            handler.postDelayed(r, 1500);
        }
        else
        {
            Log.println(Log.ERROR,TAG,"User view is null or is not added!");
        }
    }

    private void setUserData(User user) {
        Log.println(Log.INFO,TAG,"setUserData() running!");
        TextView user_name_tv = userView.findViewById(R.id.profile_name_setting);
        CircleImageView user_pic_civ = userView.findViewById(R.id.profile_pic_setting);
        String name = user.getName();
        String user_profile_pic = user.getUser_profile_pic();

        user_name_tv.setText(name);
        if (user.getUser_profile_pic() != null)
            Picasso.with(userContext).load(user_profile_pic).into(user_pic_civ);
        else
            Picasso.with(userContext).load(R.drawable.user).into(user_pic_civ);

        ProgressBar progressBar = userView.findViewById(R.id.progressBarSetting);
        progressBar.setVisibility(View.INVISIBLE);
    }

}
