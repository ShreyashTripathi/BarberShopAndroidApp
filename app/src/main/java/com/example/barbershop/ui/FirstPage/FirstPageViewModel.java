package com.example.barbershop.ui.FirstPage;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.barbershop.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class FirstPageViewModel extends ViewModel {
    private static final String USER_COLLECTION_PATH = "Users";
    private static final String EMAIL_FIELD = "email";
    private static final String PHONE_FIELD = "phone";
    MutableLiveData<String> user_name;
    MutableLiveData<String> user_profile_pic_url;
    SharedPreferences preferences;

    public FirstPageViewModel() {

    }

    public MutableLiveData<String> getUser_name(final String email) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(USER_COLLECTION_PATH).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                            {
                                if(Objects.equals(document.get(EMAIL_FIELD), email))
                                {
                                    User user = document.toObject(User.class);
                                    user_name.setValue(user.getName());
                                }
                            }
                        }
                    }
                });
        return user_name;
    }

    public MutableLiveData<String> getUser_profile_pic_url(final String emailOrPhone) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(USER_COLLECTION_PATH).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                            {
                                if(Objects.equals(document.get(EMAIL_FIELD), emailOrPhone) || Objects.equals(document.get(PHONE_FIELD), emailOrPhone))
                                {
                                    User user = document.toObject(User.class);
                                    user_profile_pic_url.setValue(user.getUser_profile_pic());
                                }
                            }
                        }
                    }
                });
        return user_profile_pic_url;
    }
}
