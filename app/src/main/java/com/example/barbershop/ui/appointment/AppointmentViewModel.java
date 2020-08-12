package com.example.barbershop.ui.appointment;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.barbershop.models.AppointmentData;
import com.example.barbershop.models.Shops;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppointmentViewModel extends ViewModel {

    private static final String USER_COLLECTION_PATH = "Users";
    private static final String SHOP_COLLECTION_PATH = "Shops";
    private static final String APPOINTMENT_COLLECTION_PATH = "Appointments";
    private MutableLiveData<ArrayList<AppointmentData>> appointmentData;
    private FirebaseFirestore firestore;
    private String TAG = "appt_view_model";

    public AppointmentViewModel() {
        appointmentData = new MutableLiveData<>();
        firestore = FirebaseFirestore.getInstance();
    }


    public MutableLiveData<ArrayList<AppointmentData>> getAppointmentData(final String user_doc_id, final String gender) {
        firestore.collection(APPOINTMENT_COLLECTION_PATH).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            final ArrayList<AppointmentData> appointmentDataArrayList = new ArrayList<>();
                            for(final AppointmentData ad: Objects.requireNonNull(task.getResult()).toObjects(AppointmentData.class))
                            {
                                if(ad.getUserId().equals(user_doc_id))
                                {
                                    appointmentDataArrayList.add(ad);
                                }
                            }
                            appointmentData.setValue(appointmentDataArrayList);
                        }
                        else {
                            Log.println(Log.ERROR,TAG,""+task.getException());
                        }
                    }
                });
        return appointmentData;
    }

   
    public void setAppointmentStatus(final AppointmentData appointmentData, final String user_doc_id, final boolean status) {
        Log.println(Log.INFO,"app view TAG","set app function");
        firestore.collection(APPOINTMENT_COLLECTION_PATH).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            int flag = 0;
                            appointmentData.setActive_status(true);       //for checking equality of ad's...
                            List<DocumentSnapshot> dsList = Objects.requireNonNull(task.getResult()).getDocuments();
                            for (DocumentSnapshot ds : dsList) {
                                if(checkAppointmentData(appointmentData,ds))
                                {
                                    Log.println(Log.INFO,"app view TAG","Objects are found same!");
                                    setAppointmentStatus_2(ds.getReference(),status);
                                    flag = 1;
                                    break;
                                }
                            }
                            if(flag == 0)
                            {
                                Log.println(Log.INFO,"app view TAG","Objects are NOT found same!");
                            }
                        }
                        else {
                            Log.println(Log.ERROR,TAG,task.getException()+"");
                        }
                    }
                });


    }

    private boolean checkAppointmentData(AppointmentData appointmentData,DocumentSnapshot ds) {
        AppointmentData ad = ds.toObject(AppointmentData.class);
        Log.println(Log.INFO,TAG,""+appointmentData.toString()+" :\n "+ad.toString());
        return ad != null && appointmentData.toString().equals(ad.toString());
    }

    private void setAppointmentStatus_2(final DocumentReference reference, boolean status) {
        Log.println(Log.INFO,"app view TAG",reference.getPath());
        reference.update("active_status",status)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Log.println(Log.INFO,TAG,"Appointment Data status updated!");
                        }
                        else {
                            Log.println(Log.ERROR,TAG,task.getException()+"");
                        }
                    }
                });
    }


}