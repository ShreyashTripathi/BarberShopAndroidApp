package com.example.barbershop.ui.barber_shop;

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

public class ShopViewModel extends ViewModel {

    private static final String TAG = "shop_view_model";
    private static MutableLiveData<Shops> _shop;
    public ShopViewModel(){
        _shop = new MutableLiveData<>();
    }



    public MutableLiveData<Shops> getShopData(final String shop_name) {

        Log.println(Log.INFO,TAG,"getShopData() running!");
        FirebaseFirestore db  = FirebaseFirestore.getInstance();
        String SHOPS_COLLECTION_PATH = "Shops";
        final CollectionReference shops = db.collection(SHOPS_COLLECTION_PATH);

        shops.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Shops shop = document.toObject(Shops.class);
                                if(shop.getShopName().equals(shop_name)){
                                    _shop.setValue(shop);
                                    break;
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return _shop;
    }


}
