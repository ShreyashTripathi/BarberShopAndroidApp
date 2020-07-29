package com.example.barbershop.ui.appointment;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.barbershop.models.AppointmentData;
import com.example.barbershop.models.BookingData;
import com.example.barbershop.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class AppointmentViewModel extends ViewModel {

    private static final String USER_COLLECTION_PATH = "Users";
    private static final String SHOP_COLLECTION_PATH = "Shops";
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
                                if(user.getAppointmentData()!=null) {
                                    ArrayList<AppointmentData> appointmentDataArrayList = new ArrayList<>(user.getAppointmentData());
                                    appointmentData.setValue(appointmentDataArrayList);
                                }
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




    public void removeBookingData(final BookingData bookingData, String shop_document_id, final String shop_name, final String hairStylistName, final String emailOrPhone) {


        firestore.collection(SHOP_COLLECTION_PATH).document(shop_document_id)
                .update("bookingDataList", FieldValue.arrayRemove(bookingData))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Log.println(Log.INFO,TAG,"Booking Data is removed from the shop's data");
                            AppointmentData appointmentData = new AppointmentData(shop_name,bookingData.getDate(),bookingData.getTimeSlot(),hairStylistName,true);
                            //removeAppointmentData(appointmentData,emailOrPhone);
                        }
                        else
                        {
                            Log.println(Log.ERROR,TAG,task.getException()+"");
                        }
                    }
                });

    }

    public void removeAppointmentData(AppointmentData appointmentData, final String emailOrPhone)
    {

        firestore.collection(USER_COLLECTION_PATH).document(emailOrPhone)
                .update("appointmentData",FieldValue.arrayRemove(appointmentData))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Log.println(Log.INFO,TAG,"Appointment Data set for: " + emailOrPhone);
                        }
                        else {
                            Log.println(Log.ERROR,TAG,task.getException()+"");
                        }
                    }
                });
    }

    public void setAppointmentStatus(final AppointmentData appointmentData, final String user_doc_id, boolean status) {
        firestore.collection(USER_COLLECTION_PATH).document(user_doc_id)
                .update("appointmentData",FieldValue.arrayRemove(appointmentData))          //first deleting the object
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            appointmentData.setActive(false);
                            setAppointmentStatus_2(appointmentData,user_doc_id);
                            Log.println(Log.INFO,TAG,"Appointment Data deleted for: " + user_doc_id);
                        }
                        else {
                            Log.println(Log.ERROR,TAG,task.getException()+"");
                        }
                    }
                });
    }

    private void setAppointmentStatus_2(final AppointmentData appointmentData, final String user_doc_id) {
        firestore.collection(USER_COLLECTION_PATH).document(user_doc_id)
                .update("appointmentData",FieldValue.arrayUnion(appointmentData))          //now adding the updated object
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Log.println(Log.INFO,TAG,"Appointment Data added n updated active status for: " + user_doc_id);
                        }
                        else {
                            Log.println(Log.ERROR,TAG,task.getException()+"");
                        }
                    }
                });
    }
}