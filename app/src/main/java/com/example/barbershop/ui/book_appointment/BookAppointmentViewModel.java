package com.example.barbershop.ui.book_appointment;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.barbershop.models.HairStylist;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import global_class.MyGlobalClass;

import static com.facebook.FacebookSdk.getApplicationContext;

public class BookAppointmentViewModel extends ViewModel {

    private static final String TAG = "book_appointment_view_model";
    private MutableLiveData< ArrayList<HairStylist> > hair_stylists_list;
    public BookAppointmentViewModel(){
        hair_stylists_list = new MutableLiveData<>();
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
}
