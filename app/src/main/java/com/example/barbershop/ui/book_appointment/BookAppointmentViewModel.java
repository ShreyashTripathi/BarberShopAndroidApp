package com.example.barbershop.ui.book_appointment;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.barbershop.models.AppointmentData;
import com.example.barbershop.models.BookingData;
import com.example.barbershop.models.HairStylist;
import com.example.barbershop.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import global_class.MyGlobalClass;

import static com.facebook.FacebookSdk.getApplicationContext;

public class BookAppointmentViewModel extends ViewModel {

    private static final String TAG = "book_appt_view_model";
    private static final String USER_COLLECTION_PATH = "Users";
    private static final String EMAIL_FIELD = "email";
    private static final String PHONE_FIELD = "phone";
    private static final String SHOP_COLLECTION_PATH = "Shops";
    private static final String HAIRSTYLIST_COLLECTION_PATH = "HairStylists";
    private MutableLiveData< ArrayList<HairStylist> > hair_stylists_list;
    private MutableLiveData<User> userData;
    private FirebaseFirestore firestore;

    public BookAppointmentViewModel(){
        hair_stylists_list = new MutableLiveData<>();
        userData = new MutableLiveData<>();
        firestore = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<ArrayList<HairStylist>> getHairStylistList(final String shop_name)
    {
        final MyGlobalClass myGlobalClass = (MyGlobalClass) getApplicationContext();
        final String gender_ = myGlobalClass.getGender();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String HAIR_STYLISTS_COLLECTION_PATH = "HairStylists";
        final CollectionReference hairStylists = firestore.collection(HAIR_STYLISTS_COLLECTION_PATH);
        hairStylists.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //ArrayList<HairStylist> hairStylistArrayList = new ArrayList<>(task.getResult().toObjects(HairStylist.class));
                    ArrayList<HairStylist> hairStylistArrayList = new ArrayList<>();
                    for(DocumentSnapshot snapshot : task.getResult())
                    {
                        HairStylist hairStylist = snapshot.toObject(HairStylist.class);
                        if(hairStylist!=null && hairStylist.getShop_name().equals(shop_name))
                            hairStylistArrayList.add(hairStylist);
                    }
                    hair_stylists_list.setValue(hairStylistArrayList);
                }
                else
                {
                    Log.println(Log.ERROR,TAG,""+task.getException());
                }
            }
        });

        return hair_stylists_list;
    }

    public ArrayList<String> getAvailableTimings()
    {
        ArrayList<String> timingsAvailable = new ArrayList<>();
        timingsAvailable.add("10:00 am to 11:00 am ");
        timingsAvailable.add("11:00 am to 12:00 am ");
        timingsAvailable.add("12:00 pm to 1:00 pm ");
        timingsAvailable.add("1:00 pm to 2:00 pm ");
        timingsAvailable.add("2:00 pm to 3:00 pm ");
        timingsAvailable.add("3:00 pm to 4:00 pm ");
        timingsAvailable.add("4:00 pm to 5:00 pm ");

        return timingsAvailable;
    }

    public MutableLiveData<User> getUserData(final String emailOrPhone)
    {
        //FirebaseFirestore firestore = FirebaseFirestore.getInstance();
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
                                    userData.setValue(user);
                                }
                            }
                        }
                    }
                });
        return userData;
    }

    public void setBookingData(final BookingData bookingData, String shop_document_id, final String shop_name, final String hairStylistName, final String emailOrPhone) {


        firestore.collection(SHOP_COLLECTION_PATH).document(shop_document_id)
                .update("bookingDataList", FieldValue.arrayUnion(bookingData))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Log.println(Log.INFO,TAG,"Booking Data is set in the shop's data");
                            AppointmentData appointmentData = new AppointmentData(shop_name,bookingData.getDate(),bookingData.getTimeSlot(),hairStylistName,true);
                            setAppointmentData(appointmentData,emailOrPhone);
                        }
                        else
                        {
                            Log.println(Log.ERROR,TAG,task.getException()+"");
                        }
                    }
                });

    }

    public void setAppointmentData(AppointmentData appointmentData, final String emailOrPhone)
    {

        firestore.collection(USER_COLLECTION_PATH).document(emailOrPhone)
                .update("appointmentData",FieldValue.arrayUnion(appointmentData))
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
}
