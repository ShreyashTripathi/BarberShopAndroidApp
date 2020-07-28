package com.example.barbershop.ui.appointment;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.barbershop.models.AppointmentData;
import com.example.barbershop.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class AppointmentViewModel extends ViewModel {

    private static final String USER_COLLECTION_PATH = "Users";
    private MutableLiveData<ArrayList<AppointmentData>> appointmentData;
    private FirebaseFirestore firestore;
    private String TAG = "appt_view_model";

    public AppointmentViewModel() {
        appointmentData = new MutableLiveData<>();
        firestore = FirebaseFirestore.getInstance();
    }


    public MutableLiveData<ArrayList<AppointmentData>> getAppointmentData(String user_doc_id) {
        firestore.collection(USER_COLLECTION_PATH).document(user_doc_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                            if(user != null) {
                                ArrayList<AppointmentData> appointmentDataArrayList = new ArrayList<>(user.getAppointmentData());
                                appointmentData.setValue(appointmentDataArrayList);
                            }
                        }
                        else
                        {
                            Log.println(Log.ERROR,TAG,""+task.getException());
                        }
                    }
                });
        return appointmentData;
    }
}