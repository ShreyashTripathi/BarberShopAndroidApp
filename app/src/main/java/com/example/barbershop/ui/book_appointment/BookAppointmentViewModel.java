package com.example.barbershop.ui.book_appointment;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.barbershop.models.AppointmentData;
import com.example.barbershop.models.User;
import com.example.barbershop.models.Worker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import global_class.MyGlobalClass;

import static com.facebook.FacebookSdk.getApplicationContext;

public class BookAppointmentViewModel extends ViewModel {

    private static final String TAG = "book_appt_view_model";
    private static final String USER_COLLECTION_PATH = "Users";
    private static final String EMAIL_FIELD = "email";
    private static final String PHONE_FIELD = "phone";
    private static final String SHOP_COLLECTION_PATH = "Shops";
    private static final String APPOINTMENT_COLLECTION_PATH = "Appointments";
    private MutableLiveData< ArrayList<Worker> > workers_list;
    private MutableLiveData<User> userData;
    private FirebaseFirestore firestore;

    public BookAppointmentViewModel(){
        workers_list = new MutableLiveData<>();
        userData = new MutableLiveData<>();
        firestore = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<ArrayList<Worker>> getWorkers(final String shop_name)
    {
        final MyGlobalClass myGlobalClass = (MyGlobalClass) getApplicationContext();
        final String gender_ = myGlobalClass.getGender();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String WORKERS_COLLECTION_PATH = "Workers";
        final CollectionReference workers = firestore.collection(WORKERS_COLLECTION_PATH);
        workers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //ArrayList<Worker> workerArrayList = new ArrayList<>(task.getResult().toObjects(Worker.class));
                    ArrayList<Worker> workerArrayList = new ArrayList<>();
                    for(DocumentSnapshot snapshot : task.getResult())
                    {
                        Worker worker = snapshot.toObject(Worker.class);
                        if(worker !=null && worker.getShop_name().equals(shop_name))
                            workerArrayList.add(worker);
                    }
                    workers_list.setValue(workerArrayList);
                }
                else
                {
                    Log.println(Log.ERROR,TAG,""+task.getException());
                }
            }
        });

        return workers_list;
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

   /* public MutableLiveData<User> getUserData(final String emailOrPhone)
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
    }*/

    public void setAppointmentData(final AppointmentData appointmentData) {

        firestore.collection(APPOINTMENT_COLLECTION_PATH).add(appointmentData)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful())
                        {
                            Log.println(Log.INFO,TAG,"Appointment Data is set !");
                        }
                        else {
                            Log.println(Log.ERROR,TAG,task.getException()+"");
                        }
                    }
                });


    }

}
