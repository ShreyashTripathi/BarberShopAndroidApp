package com.example.barbershop.ui.settings;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.barbershop.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class SettingsViewModel extends ViewModel {

    private static final String USER_COLLECTION_PATH = "Users";
    private static final String EMAIL_FIELD = "email";
    private static final String TAG = "SettingsViewModel";
    private FirebaseAuth user;
    private  FirebaseFirestore firestore;
    private MutableLiveData<User> userMutableLiveData;
    public SettingsViewModel()
    {
        user = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userMutableLiveData = new MutableLiveData<>();
    }

    public FirebaseAuth getUser()
    {
        return user;
    }

    // -------------------------- Get User Profile Start -------------------------

    public void getUserData(final String user_doc_id, final OnGetUserData onGetUserData) {

        firestore.collection(USER_COLLECTION_PATH)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                            {
                                if(document.getId().equals(user_doc_id))
                                {
                                    User user = document.toObject(User.class);
                                    onGetUserData.getUserData(user);
                                    break;
                                }
                            }
                        }
                        else
                        {
                            Log.println(Log.ERROR,TAG,""+task.getException());
                        }
                    }
                });
    }



    abstract static class OnGetUserData{
        public abstract void getUserData(User user);
    }

    public MutableLiveData<User> getLiveUserData(final String user_doc_id)
    {
        firestore.collection(USER_COLLECTION_PATH)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                            {
                                if(document.getId().equals(user_doc_id))
                                {
                                    User user = document.toObject(User.class);
                                    userMutableLiveData.setValue(user);
                                    break;
                                }
                            }
                        }
                        else
                        {
                            Log.println(Log.ERROR,TAG,""+task.getException());
                        }
                    }
                });
        return userMutableLiveData;
    }
    // -------------------------- Get User Profile End -------------------------

    //------------------------------- Update User Profile Start --------------------

    public void updateUserProfile(User user, String user_id)
    {
        firestore.collection(USER_COLLECTION_PATH).document(user_id)
                .update("name",user.getName(),
                        "email",user.getEmail(),
                        "phone",user.getPhone(),
                        "password",user.getPassword())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Log.println(Log.INFO,TAG,"Profile Updated!");
                        }
                        else
                        {
                            Log.println(Log.ERROR,TAG, task.getException()+"");
                        }
                    }
                });
    }

    interface SetIsUserInfoSame{
        void IsUserInfoSame(boolean isSame);
    }

    public void checkIsUserInfoIsSame(User user_db,User user_dialog,SetIsUserInfoSame setIsUserInfoSame)
    {
        boolean b1 = user_db.getName().equals(user_dialog.getName());
        boolean b2 = user_db.getEmail().equals(user_dialog.getEmail());
        boolean b3 = user_db.getPhone().equals(user_dialog.getPhone());
        boolean b4 = user_db.getPassword().equals(user_dialog.getPassword());

        if(b1 && b2 && b3 && b4)
            setIsUserInfoSame.IsUserInfoSame(true);
        else
            setIsUserInfoSame.IsUserInfoSame(false);
    }

    public void setProfilePic(String user_id,String imgUrl) {
        firestore.collection(USER_COLLECTION_PATH).document(user_id)
                .update("user_profile_pic",imgUrl)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Log.println(Log.INFO,TAG,"Profile Pic Updated!");
                        }
                        else
                        {
                            Log.println(Log.ERROR,TAG, task.getException()+"");
                        }
                    }
                });
    }

    //------------------------------- Update User Profile End ----------------------

}