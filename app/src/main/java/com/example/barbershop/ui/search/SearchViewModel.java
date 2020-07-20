package com.example.barbershop.ui.search;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.barbershop.models.Shops;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import global_class.MyGlobalClass;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SearchViewModel extends ViewModel {

    private static final String TAG ="search_view_model" ;
    private  static MutableLiveData< ArrayList<Shops> > _shopsArrayList;


    public SearchViewModel() {
        Log.println(Log.INFO,TAG,"SearchViewModel() running!");
        _shopsArrayList = new MutableLiveData<>();

    }


    public MutableLiveData< ArrayList<Shops> > getShopList()
    {
        Log.println(Log.INFO,TAG,"getShopList() running!");
        FirebaseFirestore db  = FirebaseFirestore.getInstance();
        String SHOPS_COLLECTION_PATH = "Shops";
        final CollectionReference shops = db.collection(SHOPS_COLLECTION_PATH);
        final MyGlobalClass myGlobalClass = (MyGlobalClass) getApplicationContext();
        final String gender_ = myGlobalClass.getGender();
        shops.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Shops> shopsArrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Shops shop = document.toObject(Shops.class);
                                if(shop.getType().equals(gender_ + " Salon") || shop.getType().equals("Unisex Salon"))
                                    shopsArrayList.add(shop);
                            }
                            //shopsArrayList               <------------ This arrayList is to be returned from the function.
                            _shopsArrayList.setValue(shopsArrayList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return _shopsArrayList;
    }




}


