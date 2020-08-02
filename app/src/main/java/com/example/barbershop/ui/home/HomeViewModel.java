package com.example.barbershop.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.barbershop.models.Worker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import global_class.MyGlobalClass;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeViewModel extends ViewModel {

    private MutableLiveData< ArrayList<Worker> > workers_list;
    private MutableLiveData< ArrayList<StorageReference> > advertisement_list;
    private MutableLiveData<String> user_location;
    private final String TAG = "home_view_model";

    public HomeViewModel() {
        Log.println(Log.INFO,TAG,"Home View Model Running!");
        workers_list = new MutableLiveData<>();
        advertisement_list = new MutableLiveData<>();
        user_location = new MutableLiveData<>();
        setAdvertisementList();


    }


    private void setAdvertisementList() {
        final ArrayList<StorageReference> image_ref_list = new ArrayList<>();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("AdvertisementBanner");

        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                image_ref_list.addAll(listResult.getItems());
                advertisement_list.setValue(image_ref_list);
                Log.println(Log.ERROR,TAG,"AdvImages loaded...");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.println(Log.ERROR,TAG,"AdvImage Not loaded... " + exception);
            }
        });

    }





    public MutableLiveData<ArrayList<Worker>> getWorkersList()
    {
        final MyGlobalClass myGlobalClass = (MyGlobalClass) getApplicationContext();
        final String gender_ = myGlobalClass.getGender();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String WORKERS_COLLECTION_PATH = "Workers";
        final CollectionReference hairStylists = firestore.collection(WORKERS_COLLECTION_PATH);
        hairStylists.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //ArrayList<Worker> workerArrayList = new ArrayList<>(task.getResult().toObjects(Worker.class));
                    ArrayList<Worker> workerArrayList = new ArrayList<>();
                    for(DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult()))
                    {
                        Worker worker = snapshot.toObject(Worker.class);
                        if(worker !=null && (worker.getShop_type().equals(gender_+" Salon") || worker.getShop_type().equals("Unisex Salon")))
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

    public MutableLiveData<ArrayList<StorageReference>> getAdvertisementList()
    {
        return advertisement_list;
    }





}